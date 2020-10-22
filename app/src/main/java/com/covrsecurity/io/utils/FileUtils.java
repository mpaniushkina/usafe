package com.covrsecurity.io.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import static com.covrsecurity.io.utils.BitmapUtils.rotateBitmap;

/**
 * Created by Lenovo on 12.09.2017.
 */

public class FileUtils {

    private static final String TAG = "FileUtils";
    private static final int NORMAL_ROTATE_ANGLE = 0;
    private static final int COMPRESSED_QUALITY = 100;
    private static final String ACCESS_MODE = "r";
    private static final String JPEG_SUFFIX = ".jpeg";
    private static final String ROTATED_PREFIX = "ROTATED";

    public static File rotateImage(File srcAbsolute) throws IOException {

        final String absolutePath = srcAbsolute.getAbsolutePath();
        final int rotation = getRotation(absolutePath);

        if (rotation != NORMAL_ROTATE_ANGLE) {

            final File convertedImage = File.createTempFile(ROTATED_PREFIX, JPEG_SUFFIX, srcAbsolute.getParentFile());
            final FileOutputStream outStream = new FileOutputStream(convertedImage);

            final Bitmap bmp = BitmapFactory.decodeFile(absolutePath);
            final Bitmap rotateBitmap = rotateBitmap(bmp, rotation);
            rotateBitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSED_QUALITY, outStream);

            srcAbsolute.delete();

            convertedImage.renameTo(srcAbsolute);

            outStream.flush();
            outStream.close();
        }
        return srcAbsolute;
    }

    private static int getRotation(String filePath) throws IOException {
        int rotationExif = getRotationExif(filePath);
        return exifToDegrees(rotationExif);
    }

    private static int getRotationExif(String srcAbsolutePath) throws IOException {
        ExifInterface exif = new ExifInterface(srcAbsolutePath);
        return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public static byte[] readAllBytes(File file) throws IOException {
        RandomAccessFile f = new RandomAccessFile(file, ACCESS_MODE);
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        return bytes;
    }

    private FileUtils() {
    }
}
