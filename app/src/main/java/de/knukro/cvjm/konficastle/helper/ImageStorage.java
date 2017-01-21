package de.knukro.cvjm.konficastle.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;


public class ImageStorage {

    private static final String PATH = "/Konfi_Castle/";

    static void saveToSdCard(Bitmap bitmap, String filename) {
        filename = filename.substring(filename.indexOf("/") + 1); //trim
        File sdcard = Environment.getExternalStorageDirectory();

        File file, folder, nomedia;
        folder = new File(sdcard.getAbsoluteFile(), PATH);//the dot makes this directory hidden to the user
        if (!folder.exists() && !folder.mkdirs()) {
            return;
        }

        nomedia = new File(folder.getAbsoluteFile(),".nomedia");
        file = new File(folder.getAbsoluteFile(), filename);
        if (file.exists()) {
            return;
        }
        try {
            if (!nomedia.exists() && !nomedia.createNewFile()) {
                return ;
            }
            if (file.createNewFile()) {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
            }
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    public static File getImage(String imagename) {
        imagename = imagename.substring(imagename.indexOf("/") + 1); //trim
        File mediaImage = null;
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root);
            if (!myDir.exists())
                return null;

            mediaImage = new File(myDir.getPath() + PATH + imagename);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaImage;
    }


    public static boolean checkifImageExists(String imagename) {
        imagename = imagename.substring(imagename.indexOf("/") + 1); //trim
        File file = ImageStorage.getImage("/" + imagename);
        try {
            String path = file.getAbsolutePath();
            Bitmap b = BitmapFactory.decodeFile(path);
            return !(b == null || b.equals(""));
        } catch (Exception e) {
            return false;
        }
    }
}

