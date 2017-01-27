package de.knukro.cvjm.konficastle.helper;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

import de.knukro.cvjm.konficastle.structs.ParsedEvent;

import static java.lang.System.out;


public class ImageStorage {

    private static final String PATH = "/Konfi_Castle/";


    private static String prepareFilename(ParsedEvent event) {
        return event.eventTitle.trim().replace(".", "").replace("/", "") + ".jpg";
    }

    static void saveToSdCard(Bitmap bitmap, ParsedEvent event) {
        String filename = prepareFilename(event);
        File outFile, saveDir, nomedia;

        saveDir = new File(Environment.getExternalStorageDirectory(), PATH);
        if (!saveDir.exists() && !saveDir.mkdirs()) {
            return;
        }
        nomedia = new File(saveDir.getAbsoluteFile(), ".nomedia");
        outFile = new File(saveDir.getAbsoluteFile(), filename);

        if (outFile.exists()) {
            return;
        }

        try {
            if (!nomedia.exists() && !nomedia.createNewFile()) {
                return;
            }
            if (outFile.createNewFile()) {
                FileOutputStream outStream = new FileOutputStream(outFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            outFile.delete();
            Log.d("FILENAME", outFile.getAbsolutePath());
            e.printStackTrace();
        }
    }


    public static String getImagePath(ParsedEvent event) {
        String filename = prepareFilename(event);
        File saveDir = new File(Environment.getExternalStorageDirectory(), PATH);
        File image = new File(saveDir.getAbsolutePath(), filename);
        return (image.exists()) ? image.getAbsolutePath() : null;
    }

}

