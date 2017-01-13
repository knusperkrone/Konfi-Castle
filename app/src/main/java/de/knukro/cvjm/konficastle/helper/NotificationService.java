package de.knukro.cvjm.konficastle.helper;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import de.knukro.cvjm.konficastle.MainActivity;
import de.knukro.cvjm.konficastle.R;
import de.knukro.cvjm.konficastle.structs.ExpandableTermin;

import static de.knukro.cvjm.konficastle.helper.NotificationHelper.NotificationPublisher.VIBRATE_KEY;


public class NotificationService extends IntentService {

    public static final String NOTIFICATION_ID = "notifiaction_id";
    public static final String NOTIFICATION_TEXT = "notifacation_text";
    private static final long[] VIBRATIONS = {250, 250};

    public static String NOTICATION_TIME_KEY;
    public static String VIBRATE_KEY;


    public NotificationService() {
        super("Notification service");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        int notification_id = intent.getIntExtra(NOTIFICATION_ID, -1);

        if (notification_id != -1) {
            Context context = getBaseContext();
            if (NOTICATION_TIME_KEY == null || VIBRATE_KEY == null) {
                NOTICATION_TIME_KEY = context.getString(R.string.notification_time_key);
                VIBRATE_KEY = context.getString(R.string.vibrate_key);
            }

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

            Intent i = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            String text = intent.getStringExtra(NOTIFICATION_TEXT);
            ExpandableTermin.toExpand = text;

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getBaseContext());
            mBuilder.setSmallIcon(R.drawable.icon)
                    .setContentTitle("Es geht in " + sp.getString(NOTICATION_TIME_KEY, "5") + " weiter")
                    .setContentText(text)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            if (sp.getBoolean(VIBRATE_KEY, false)) {
                mBuilder.setVibrate(VIBRATIONS);
            }

            ((NotificationManager) getSystemService(context.NOTIFICATION_SERVICE))
                    .notify(notification_id, mBuilder.build());
        }
    }
}
