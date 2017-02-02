package de.knukro.cvjm.konficastle.helper;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;

import static java.lang.System.out;


public class ImageStorage {

    private static String prepareFilename(String eventName) {
        return eventName.trim().replace(".", "").replace("/", "") + ".jpg";
    }

    static void saveToSdCard(Bitmap bitmap, String eventName, Context context) {
        String filename = prepareFilename(eventName);
        File outFile;
        outFile = new File(context.getCacheDir(), filename);
        if (!outFile.exists()) {
            try {
                if (outFile.createNewFile()) {
                    FileOutputStream outStream = new FileOutputStream(outFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
                    out.flush();
                    out.close();
                }
            } catch (Exception e) {
                outFile.delete();
                e.printStackTrace();
            }
        }
    }

    public static String getImagePath(String eventName, Context context) {
        File image = new File(context.getCacheDir(), prepareFilename(eventName));
        return (image.exists()) ? image.getAbsolutePath() : null;
    }

}

