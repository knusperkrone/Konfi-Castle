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

import static de.knukro.cvjm.konficastle.SharedValues.NOTIFICATION_ID;
import static de.knukro.cvjm.konficastle.SharedValues.NOTIFICATION_TEXT;


public class NotificationService extends IntentService {

    private static final long[] VIBRATIONS = {250, 250};
    private static final int DEFAULT_ID = 0;
    private static String NOTICATION_TIME_KEY, VIBRATE_KEY, SPAM_KEY;


    public NotificationService() {
        super(NotificationService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int intentId = intent.getIntExtra(NOTIFICATION_ID, -1);

        if (intentId != -1) {
            Context context = getBaseContext();
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

            if (NOTICATION_TIME_KEY == null || VIBRATE_KEY == null || SPAM_KEY == null) {
                NOTICATION_TIME_KEY = context.getString(R.string.notification_time_key);
                VIBRATE_KEY = context.getString(R.string.vibrate_key);
                SPAM_KEY = context.getString(R.string.spam_key);
            }

            Intent i = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, DEFAULT_ID, i, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setSmallIcon(R.drawable.icon)
                    .setContentTitle("Es geht in " + sp.getString(NOTICATION_TIME_KEY, "5") + " Minuten weiter")
                    .setContentText(intent.getStringExtra(NOTIFICATION_TEXT))
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            if (sp.getBoolean(VIBRATE_KEY, false)) {
                mBuilder.setVibrate(VIBRATIONS);
            }

            if (sp.getBoolean(SPAM_KEY, false)) {
                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                        .notify(DEFAULT_ID, mBuilder.build()); //Only one notification at a time
            } else {
                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                        .notify(intentId, mBuilder.build()); //Lot's of notifications
            }
        }
    }

}
