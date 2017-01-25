package de.knukro.cvjm.konficastle.helper;

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
import java.util.HashMap;

import de.knukro.cvjm.konficastle.R;
import de.knukro.cvjm.konficastle.SharedValues;
import de.knukro.cvjm.konficastle.structs.ParsedEvent;


public class GetImages extends AsyncTask<Object, Object, Object> {

    private static final HashMap<String, String> map = new HashMap<>();
    private final ImageView view;
    private final ParsedEvent event;
    private final String eventUrl;
    private static final String baseUrl = "https://www.cvjm-bayern.de/";
    private String imagename;
    private Bitmap bitmap;


    public GetImages(ImageView view, ParsedEvent event) {
        this.view = view;
        this.event = event;
        this.eventUrl = event.link;
    }

    private static String parseImage(String url) {
        try {
            URLConnection connection = (new URL(url)).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
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
            return null;
        }
    }

    @Override
    protected Object doInBackground(Object... objects) {
        SharedValues.addAsyncTask(this);
        if (event.imagename == null) {
            if (map.containsKey(event.eventTitle)) {
                event.imagename = map.get(event.eventTitle);
            } else {
                event.imagename = parseImage(eventUrl);
                map.put(event.eventTitle, event.imagename);
            }
        }
        imagename = event.imagename;
        if (imagename != null && !ImageStorage.checkifImageExists(imagename)) {
            try {
                URL url = new URL(baseUrl + imagename);
                URLConnection conn = url.openConnection();
                conn.setConnectTimeout(500); //1/2 Seconds to connect.
                conn.setReadTimeout(1000); //1 Second to get Image;
                bitmap = BitmapFactory.decodeStream(conn.getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        SharedValues.removeAsyncTask(this);
        if (imagename != null) {
            if (!ImageStorage.checkifImageExists(imagename)) {
                view.setImageBitmap(bitmap);
                ImageStorage.saveToSdCard(bitmap, imagename);
            } else {
                File file = ImageStorage.getImage(imagename);
                if (file == null) {
                    view.setImageResource(R.drawable.onlineplaceholder);
                } else {
                    try {
                        String path = file.getAbsolutePath();
                        bitmap = BitmapFactory.decodeFile(path);
                        view.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        view.setImageResource(R.drawable.onlineplaceholder);
                    }
                }
            }
        } else {
            view.setImageResource(R.drawable.onlineplaceholder);
        }
    }

}

