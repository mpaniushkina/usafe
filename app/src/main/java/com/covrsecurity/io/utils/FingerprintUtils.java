package com.covrsecurity.io.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.covrsecurity.io.ui.dialog.FingerprintAuthenticationDialogFragment;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class FingerprintUtils {

    private static FingerprintUtils sFingerprintUtils;
    private static FingerprintManager sFingerprintManager;
    private static KeyguardManager sKeyguardManager;

    public FingerprintManager getFingerprintManager() {
        return sFingerprintManager;
    }

    public KeyguardManager getKeyguardManager() {
        return sKeyguardManager;
    }

    private FingerprintUtils(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            sFingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
            sKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        }
    }

    public static FingerprintUtils getInstance(Context context) {
        if (sFingerprintUtils == null) {
            sFingerprintUtils = new FingerprintUtils(context);
        }
        return sFingerprintUtils;
    }

    public boolean canUseFingerprintScanner(Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && sFingerprintManager.isHardwareDetected()
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean readyToUseFingerprintScanner(Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && sFingerprintManager.isHardwareDetected()
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED
                && sFingerprintManager.hasEnrolledFingerprints() && sKeyguardManager.isKeyguardSecure();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasEnrolledFingerprints() {
        return sFingerprintManager != null && sFingerprintManager.hasEnrolledFingerprints();
    }

    public boolean isKeyguardSecure() {
        return sKeyguardManager != null && sKeyguardManager.isKeyguardSecure();
    }

    public KeyStore getKeystore() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            return keyStore;
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException("Failed to get an instance of KeyStore", e);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public KeyGenerator getGenerator() {
        try {
            return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get an instance of KeyGenerator", e);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public Cipher generateCipher(int cipherMode, @Nullable byte[] ivBytes) {
        try {
            Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);

            SecretKey key = (SecretKey) getKeystore().getKey(FingerprintAuthenticationDialogFragment.DEFAULT_KEY_NAME,
                    null);

            if (Cipher.ENCRYPT_MODE == cipherMode) {
                cipher.init(Cipher.ENCRYPT_MODE, key);
            } else if (Cipher.DECRYPT_MODE == cipherMode) {
                cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(ivBytes));
            } else {
                throw new IllegalArgumentException("cipherMode should be either Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE");
            }
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | UnrecoverableKeyException | KeyStoreException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new RuntimeException("Failed to get an instance of Cipher", e);
        }
    }

    /**
     * Creates a symmetric key in the Android Key Store which can only be used after the user has
     * authenticated with fingerprint.
     *
     * @param keyName                          the name of the key to be created
     * @param invalidatedByBiometricEnrollment if {@code false} is passed, the created key will not
     *                                         be invalidated even if a new fingerprint is enrolled.
     *                                         The default value is {@code true}, so passing
     *                                         {@code true} doesn't change the behavior
     *                                         (the key will be invalidated if a new fingerprint is
     *                                         enrolled.). Note that this parameter is only valid if
     *                                         the app works on Android N developer preview.
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void generateKey(String keyName, boolean invalidatedByBiometricEnrollment, int cipherMode) {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            KeyStore keystore = getKeystore();
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder
            if (Cipher.DECRYPT_MODE == cipherMode /*&& keystore.containsAlias(keyName)*/) {
                return;
            }
            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec
                    .Builder(keyName, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setKeySize(256)
                    // Require the user to authenticate with a fingerprint to authorize every use
                    // of the key
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

            // This is a workaround to avoid crashes on devices whose API level is < 24
            // because KeyGenParameterSpec.Builder#setInvalidatedByBiometricEnrollment is only
            // visible on API level +24.
            // Ideally there should be a compat library for KeyGenParameterSpec.Builder but
            // which isn't available yet.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment);
            }
            KeyGenerator generator = getGenerator();
            generator.init(builder.build());
            generator.generateKey();
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }
}
