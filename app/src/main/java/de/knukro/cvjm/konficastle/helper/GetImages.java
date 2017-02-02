package de.knukro.cvjm.konficastle.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import de.knukro.cvjm.konficastle.R;
import de.knukro.cvjm.konficastle.SharedValues;
import de.knukro.cvjm.konficastle.structs.ParsedEvent;


public class GetImages extends AsyncTask<Object, Object, Object> {

    private static final String baseUrl = "https://www.cvjm-bayern.de/";
    private final ImageView view;
    private final ParsedEvent event;
    private final Context context;
    private Bitmap bitmap;
    private volatile boolean success = true;


    public GetImages(ImageView view, ParsedEvent event, Context context) {
        this.view = view;
        this.event = event;
        this.context = context;
    }

    private String parseImage(String url) {
        try {
            URLConnection connection = (new URL(url)).openConnection();
            connection.setConnectTimeout(500); //1/2 Second to connect
            connection.setReadTimeout(1000); //1 Second to get the image url
            connection.connect();
            String line;
            int index = 0;
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            while ((line = reader.readLine()) != null) {
                if ((index = line.lastIndexOf(".jpg")) != -1)
                    break;
            }
            in.close();
            if (index == -1) {
                return null;
            }
            if (line != null) {
                return line.substring(line.indexOf("uploads/"), index + 4);
            }
            return null;
        } catch (Exception e) {
            success = false;
            return null;
        }
    }

    @Override
    protected Object doInBackground(Object... objects) {
        SharedValues.addAsyncTask(this, GetImages.class);
        String savedImage = ImageStorage.getImagePath(event.eventTitle, context);
        if (savedImage == null) {
            /*Need to check the web*/
            event.imagename = parseImage(event.link);
            if (event.imagename != null) {
                /*The web knows*/
                try {
                    URL url = new URL(baseUrl + event.imagename);
                    URLConnection conn = url.openConnection();
                    conn.setConnectTimeout(500); //1/2 Seconds to connect.
                    conn.setReadTimeout(2000); //1 Seconds to get Image;
                    bitmap = BitmapFactory.decodeStream(conn.getInputStream());
                } catch (Exception e) {
                    success = false;
                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.onlineplaceholder);
                    e.printStackTrace();
                }
            } else {
                /*The web is stupid. Save the placeholder then!*/
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.onlineplaceholder);
            }
        } else {
            /*We already have that file!*/
            try {
                bitmap = BitmapFactory.decodeFile(savedImage);
            } catch (Exception e) {
                success = false;
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.onlineplaceholder);
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        SharedValues.removeAsyncTask(this);
        if (success) {
            ImageStorage.saveToSdCard(bitmap, event.eventTitle, context);
        }
        try {
            view.setImageBitmap(bitmap);
        } catch (Exception e) {
            String path = ImageStorage.getImagePath(event.eventTitle, context);
            if (path != null) {
                new File(path).delete();
            }
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.onlineplaceholder);
        }
    }

}

