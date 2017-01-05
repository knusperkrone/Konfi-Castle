package de.knukro.cvjm.konficastle.helper;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import de.knukro.cvjm.konficastle.MainActivity;
import de.knukro.cvjm.konficastle.R;
import de.knukro.cvjm.konficastle.structs.ExpandableTermin;
import de.knukro.cvjm.konficastle.structs.SchedulerObject;

import static android.content.Context.ALARM_SERVICE;
import static de.knukro.cvjm.konficastle.helper.NotificationHelper.NotificationPublisher.NOTIFICATION_ID;
import static de.knukro.cvjm.konficastle.helper.NotificationHelper.NotificationPublisher.NOTIFICATION_TEXT;

public class NotificationHelper {

    private static AlarmManager aManager;
    private static Set<String> defaultSet;

    private final static int nID = 10;

    /*public static void testNotification(Context context) {
        if (aManager == null || defaultSet == null) {
            aManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

            defaultSet = new TreeSet<>();
            defaultSet.add("aufbau");
            defaultSet.add("essen");
            defaultSet.add("freiwillig");
            defaultSet.add("programm");
            defaultSet.add("ma");
        }
        scheduleNotification(1000, "TestNotification", context);
    }*/

    public static void setupNotifications(Context context) {
        int day, offset;
        SharedPreferences sp;
        Set<String> set;
        SchedulerObject toCheck = DbOpenHelper.getInstance(context).getDates(context);

        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();

        if (aManager == null || defaultSet == null) {
            aManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            defaultSet = new TreeSet<>();
            defaultSet.add("aufbau");
            defaultSet.add("essen");
            defaultSet.add("freiwillig");
            defaultSet.add("programm");
            defaultSet.add("ma");
        }

        if ((day = (int) SchedulerHelper.getDayDiff(toCheck)) == -1) {
            return;
        }
        //scheduleGaestebuch(toCheck, day);

        sp = PreferenceManager.getDefaultSharedPreferences(context);
        set = sp.getStringSet(context.getString(R.string.notification_key), defaultSet);

        if (set.isEmpty()) {
            return;
        }

        sp.edit().putBoolean(context.getString(R.string.active_notifcations), true).apply();

        offset = Integer.valueOf(sp.getString(context.getString(R.string.notification_time_key), "5"));

        ArrayList<ArrayList<ExpandableTermin>> query = DbOpenHelper.getInstance(context).getProgramm(context);
        for (; day < query.size(); day++) {
            for (ExpandableTermin termin : query.get(day)) {
                if (set.contains(termin.group)) {
                    scheduleNotification(
                            SchedulerHelper.getSecondOffset(termin.time, toCheck, day, offset),
                            termin.name, context);
                }
            }
        }

    }

    private static void scheduleNotification(long delay, String notiText, Context context) {

        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NOTIFICATION_ID, nID);
        notificationIntent.putExtra(NOTIFICATION_TEXT, notiText);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, nID, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        aManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pendingIntent);
        //Log.d("scheduleNotification", "Made notification: " + notiText + " - at " + new Date(System.currentTimeMillis() + delay));
    }

    /*private static void scheduleGaestebuch(SchedulerObject eventStart, int dayDiff) {
        //TODO: Schedule a notification that opens Browser to the visitor's book in browser
        int inFutureDays = (int) eventStart.length - dayDiff - 1;
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_WEEK, inFutureDays);
        c.set(Calendar.HOUR_OF_DAY, 17);
        c.set(Calendar.MINUTE, 30);
        c.set(Calendar.SECOND, 0);
    }*/


    public static class NotificationPublisher extends BroadcastReceiver {

        public static final String NOTIFICATION_ID = "notification_id";
        public static final String NOTIFICATION_TEXT = "notification_text";
        public static String PREFERENCE_KEY;
        public static String VIBRATE_KEY;
        private static final long[] VIBRATIONS = {250,250};

        public static NotificationCompat.Builder builder;

        @Override
        public void onReceive(final Context context, Intent intent) {
            String notificationText = intent.getStringExtra(NOTIFICATION_TEXT);
            int notificationId = intent.getIntExtra(NOTIFICATION_ID, 0);

            if (PREFERENCE_KEY == null || VIBRATE_KEY == null) {
                PREFERENCE_KEY = context.getString(R.string.notification_time_key);
                VIBRATE_KEY = context.getString(R.string.vibrate_key);
            }

            ExpandableTermin.toExpand = notificationText;

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

            Intent notificationIntent = new Intent(context, MainActivity.class);
            PendingIntent activity = PendingIntent.getActivity(
                    context, notificationId, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);


            builder = new NotificationCompat.Builder(context)
                        .setContentIntent(activity)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.icon)
                        .setColor(Color.rgb(228, 1, 58));

            builder.setContentText(notificationText)
                    .setContentTitle("Es geht in " + sp.getString(PREFERENCE_KEY, "5") +
                            " Minuten weiter!");

            if (sp.getBoolean(VIBRATE_KEY, false)) {
                //((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(350);
                builder.setVibrate(VIBRATIONS);
            }

            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
                    .notify(nID, builder.build());
        }
    }

}
