package com.covrsecurity.io.utils;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Log;

import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.app.IamApp;
import com.covrsecurity.io.greendao.model.database.CovrVaultDbModel;
import com.covrsecurity.io.model.EncryptedEnvelope;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.instacart.library.truetime.TrueTime;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.x500.X500Principal;

import timber.log.Timber;

/**
 * Created by Aleksey on 02.12.2015.
 */
public class DefaultEncryptHelper {

    public static final String TAG = DefaultEncryptHelper.class.getSimpleName();

    private static final int KEY_SIZE_BYTES = 16;
    private static final int DB_KEY_SIZE_BYTES = 32;

    private static final String KEYSTORE_KEY_ALIAS_RSA = "ALIAS_RSA";
    private static final Type HASH_MAP_TYPE = new TypeToken<HashMap<String, String>>() {
    }.getType();

    private static final String ENCODING = "UTF-8";
    private static final String ALGORITHM = "RSA";
    private static final String KEY_STORE_PROVIDER = "AndroidKeyStore";
    private static final String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";

    private static final String TRANSFORMATION_RSA = "RSA/ECB/PKCS1Padding";
    private static final String CIPHER_PROVIDER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? "AndroidKeyStoreBCWorkaround" : "AndroidOpenSSL";
    private static final String COVR_KEY_STORE_NAME = "keystore";
    private static final String ENCRYPTED_DATA_FILE_PATH = IamApp.getInstance().getFilesDir().getAbsolutePath() + File.separator + COVR_KEY_STORE_NAME;
    private static final String KEYSTORE_CIPHER = "KEYSTORE_CIPHER";

    private KeyStore keyStore;
    private Gson mGson = new Gson();

    public DefaultEncryptHelper() {
        try {
            keyStore = KeyStore.getInstance(KEY_STORE_PROVIDER);
            keyStore.load(null);
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            Log.e(TAG, "DefaultEncryptHelper()" + e.getMessage());
        }
    }

    public boolean createRsaKeyIfNecessary(Context context) throws NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, InvalidAlgorithmParameterException {
        if (!keyStore.containsAlias(KEYSTORE_KEY_ALIAS_RSA)) {
            AlgorithmParameterSpec spec;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                spec = getKeyGenParameterSpecVer23();
            } else {
                spec = getKeyGenParameterSpecPre23(context);
            }
            retrieveKeys(spec);
            return true;
        }
        return false;
    }

    @SuppressWarnings({"deprecation", "WrongConstant"})
    private KeyPairGeneratorSpec getKeyGenParameterSpecPre23(Context context) throws NoSuchAlgorithmException {
        Date dateNow = TrueTime.now();
        Calendar notBefore = Calendar.getInstance();
        Calendar notAfter = Calendar.getInstance();
        notBefore.setTimeInMillis(dateNow.getTime());
        notAfter.setTimeInMillis(dateNow.getTime());
        notAfter.add(Calendar.YEAR, 1);
        return new KeyPairGeneratorSpec.Builder(context)
                .setAlias(KEYSTORE_KEY_ALIAS_RSA)
                .setKeyType("RSA")
//                        .setKeySize(2048)
                .setSubject(new X500Principal("CN=" + KEYSTORE_KEY_ALIAS_RSA))
                .setSerialNumber(BigInteger.ONE)
                .setStartDate(notBefore.getTime())
                .setEndDate(notAfter.getTime())
                .build();
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private KeyGenParameterSpec getKeyGenParameterSpecVer23() {
        return new KeyGenParameterSpec.Builder(KEYSTORE_KEY_ALIAS_RSA, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setDigests(KeyProperties.DIGEST_SHA256)
                .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .build();
    }

    private void retrieveKeys(AlgorithmParameterSpec spec) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM, KEY_STORE_PROVIDER);
        generator.initialize(spec);
        generator.generateKeyPair();
    }

    public <T> T saveToKeystore(String key, T value) {
        HashMap<String, String> keystoreContent = getKeystoreContent();
        if (keystoreContent == null) {
            keystoreContent = new HashMap<>();
        }
        Type type = new TypeToken<T>() {
        }.getType();
        String oldValue = keystoreContent.put(key, mGson.toJson(value));
        saveToKeystore(mGson.toJson(keystoreContent));
        return mGson.fromJson(oldValue, type);
    }

    public <T> T getFromKeystore(String key, TypeToken<T> typeToken) {
        HashMap<String, String> keystoreContent = getKeystoreContent();
        if (keystoreContent != null) {
            String value = keystoreContent.get(key);
            if (TextUtils.isEmpty(value)) {
                return null;
            } else {
                return mGson.fromJson(value, typeToken.getType());
            }
        } else {
            return null;
        }
    }

    @Nullable
    private HashMap<String, String> getKeystoreContent() {
        String jsonContent = readFromKeystore();
        Timber.w(TAG, "jsonContent: %s", jsonContent);
        return mGson.fromJson(jsonContent, HASH_MAP_TYPE);
    }

    private void saveToKeystore(String content) {
        byte[] cipheredKeyBytes = null;
        byte[] key = null;
        CipherOutputStream cipherOutputStream = null;
        try {
            cipheredKeyBytes = AppAdapter.settings().getCipheredBytes(KEYSTORE_CIPHER);
            if (cipheredKeyBytes == null || cipheredKeyBytes.length == 0) {
                SecureRandom secureRandom = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);
                key = secureRandom.generateSeed(KEY_SIZE_BYTES);
                AppAdapter.settings().setStringCiphered(KEYSTORE_CIPHER, key);
            } else {
                key = cipheredKeyBytes;
            }
            Cipher inCipher = AESCrypt.getCipher(key, Cipher.ENCRYPT_MODE);
            File yourFile = new File(ENCRYPTED_DATA_FILE_PATH);
            yourFile.createNewFile();
            cipherOutputStream = new CipherOutputStream(new FileOutputStream(ENCRYPTED_DATA_FILE_PATH), inCipher);
            cipherOutputStream.write(content.getBytes(StringUtils.UTF8_CHARSET));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IOException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            Log.e(TAG, "saveToKeystore() " + Log.getStackTraceString(e));
        } finally {
            if (cipheredKeyBytes != null) {
                Arrays.fill(cipheredKeyBytes, (byte) 0);
            }
            if (key != null) {
                Arrays.fill(key, (byte) 0);
            }
            if (cipherOutputStream != null) {
                try {
                    cipherOutputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "saveToKeystore() finally:  " + Log.getStackTraceString(e));
                }
            }
        }
    }

    private String readFromKeystore() {
        byte[] cipheredKeyBytes = null;
        byte[] key = null;
        try {
            cipheredKeyBytes = AppAdapter.settings().getCipheredBytes(KEYSTORE_CIPHER);
            if (cipheredKeyBytes == null || cipheredKeyBytes.length == 0) {
                SecureRandom secureRandom = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);
                key = secureRandom.generateSeed(KEY_SIZE_BYTES);
                AppAdapter.settings().setStringCiphered(KEYSTORE_CIPHER, key);
            } else {
                key = cipheredKeyBytes;
            }
            Cipher outCipher = AESCrypt.getCipher(key, Cipher.DECRYPT_MODE);
            File yourFile = new File(ENCRYPTED_DATA_FILE_PATH);
            yourFile.createNewFile();
            CipherInputStream cipherInputStream = new CipherInputStream(new FileInputStream(ENCRYPTED_DATA_FILE_PATH), outCipher);
            return IOUtils.toString(cipherInputStream, ENCODING);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IOException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            Log.e(TAG, "saveToKeystore() " + Log.getStackTraceString(e));
        } finally {
            if (cipheredKeyBytes != null) {
                Arrays.fill(cipheredKeyBytes, (byte) 0);
            }
            if (key != null) {
                Arrays.fill(key, (byte) 0);
            }
        }
        return null;
    }

    public void deleteRsaKeys() throws KeyStoreException {
        keyStore.deleteEntry(KEYSTORE_KEY_ALIAS_RSA);
    }

    public String encryptRSAText(final String initialText) throws NoSuchPaddingException,
            NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IOException, UnrecoverableEntryException, KeyStoreException {
        return encryptRSABytes(initialText.getBytes(StringUtils.UTF8_CHARSET));
    }

    public String encryptRSABytes(final byte[] initialTextBytes) throws NoSuchPaddingException,
            NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IOException, UnrecoverableEntryException, KeyStoreException {
        return StringUtils.base64UrlEncode(encryptRSA(initialTextBytes));
    }

    private byte[] encryptRSA(final byte[] plainTextBytes) throws NoSuchPaddingException,
            NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IOException, UnrecoverableEntryException, KeyStoreException {

        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(KEYSTORE_KEY_ALIAS_RSA, null);
        RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();
        Cipher inCipher = Cipher.getInstance(TRANSFORMATION_RSA, CIPHER_PROVIDER);
        inCipher.init(Cipher.ENCRYPT_MODE, publicKey);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CipherOutputStream cipherOutputStream = new CipherOutputStream(
                outputStream, inCipher);
        cipherOutputStream.write(plainTextBytes);
        cipherOutputStream.close();

        return outputStream.toByteArray();
    }

    @Deprecated
    public String decryptRSAString(final String cipheredText) throws InvalidKeyException,
            NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, IOException, UnrecoverableEntryException, KeyStoreException {
        byte[] bytes = decryptRSABytes(cipheredText);
        return new String(bytes, 0, bytes.length, StringUtils.UTF8_CHARSET);
    }

    public byte[] decryptRSABytes(final String cipheredText) throws InvalidKeyException,
            NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, IOException, UnrecoverableEntryException, KeyStoreException {
        return decryptRSA(StringUtils.base64UrlDecode(cipheredText));
    }

    private byte[] decryptRSA(final byte[] cipheredText) throws InvalidKeyException,
            NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, IOException, UnrecoverableEntryException, KeyStoreException {

        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(KEYSTORE_KEY_ALIAS_RSA, null);
        Cipher output = Cipher.getInstance(TRANSFORMATION_RSA, CIPHER_PROVIDER);
        output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());

        CipherInputStream cipherInputStream = new CipherInputStream(
                new ByteArrayInputStream(cipheredText), output);
        ArrayList<Byte> values = new ArrayList<>();
        int nextByte;
        while ((nextByte = cipherInputStream.read()) != -1) {
            values.add((byte) nextByte);
        }

        byte[] bytes = new byte[values.size()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = values.get(i);
        }
        return bytes;
    }

    @WorkerThread
    public EncryptedEnvelope getEncryptedEnvelope(Object object) throws GeneralSecurityException, IOException {
        return getEncryptedEnvelope(mGson.toJson(object));
    }

    @WorkerThread
    public EncryptedEnvelope getEncryptedEnvelope(String jsonData) throws GeneralSecurityException, IOException {
//        byte[] oneTimeKey;
//        try {
//            oneTimeKey = AESRequestResponseHelper.getRawKey();
//        } catch (NoSuchAlgorithmException e) {
//            throw new IOException(e);
//        }
//        CryptManager cryptManager = new CryptManager();
//        String encryptedEnvelope = cryptManager.encryptMessage(oneTimeKey, jsonData);
//        byte[] encryptedKey = cryptManager.encryptOneTimeKeyLocal(CovrApp.sClientPublicKey, oneTimeKey);
//        return new EncryptedEnvelope(Base64.encodeToString(encryptedKey, Base64.NO_WRAP), encryptedEnvelope);
        return null;
    }

    @WorkerThread
    public <T> T getDecryptedModel(CovrVaultDbModel dbModel, Class<T> clazz) throws GeneralSecurityException, IOException {
//        CryptManager cryptManager = new CryptManager();
//        byte[] decryptedOneTimeKey = cryptManager.decryptOneTimeKey(dbModel.getMKey(), cryptManager.getKeyVersion(CovrApp.sClientPublicKey));
//        String decryptedMessage = AESRequestResponseHelper.decrypt(decryptedOneTimeKey, AzureBase64.decode(dbModel.getMValue()));
//        return new Gson().fromJson(decryptedMessage, clazz);
        return null;
    }

}