package de.knukro.cvjm.konficastle.helper;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import de.knukro.cvjm.konficastle.R;
import de.knukro.cvjm.konficastle.SharedValues;
import de.knukro.cvjm.konficastle.structs.ExpandableTermin;
import de.knukro.cvjm.konficastle.structs.SchedulerObject;

public class BootReceiver extends BroadcastReceiver {

    private static final int DELAY = 0;
    private static AlarmManager aManager;

    public static void resetNotifications(Context context) {
        AlarmManager aManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, BootReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Start onReceive now
        long notification_time = System.currentTimeMillis() + DELAY;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            aManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, notification_time, alarmIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            aManager.setExact(AlarmManager.RTC_WAKEUP, notification_time, alarmIntent);
        } else {
            aManager.set(AlarmManager.RTC_WAKEUP, notification_time, alarmIntent);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        intent.getAction(); //Don't care about the action yet, doing the same thing anyway - but compiler o.O
        DbOpenHelper.initInstance(context); //Just to be sure
        DbOpenHelper dbOpenHelper = DbOpenHelper.getInstance();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> set;
        SchedulerObject toCheck = dbOpenHelper.getDate(context);

        aManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        set = sp.getStringSet(context.getString(R.string.key_notification), new HashSet<>(Arrays.asList(context.getResources().getStringArray(R.array.notification_values))));

        int day = (int) TimeUnit.DAYS.convert(System.currentTimeMillis() - toCheck.event.getTime(), TimeUnit.MILLISECONDS);

        if (day > toCheck.length || set.isEmpty()) {
            /*It's already gone or no Notification are wanted*/
            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
        } else {

            int offset = Integer.valueOf(sp.getString(context.getString(R.string.key_notification_time), "5"));
            int notification_id = 1; //minimum notificationID

            ArrayList<ArrayList<ExpandableTermin>> query = dbOpenHelper.getProgramm(context);
            for (int listDay = (day >= 0) ? day : 0; listDay < query.size(); listDay++) {
                for (ExpandableTermin termin : query.get(listDay)) {
                    if (set.contains(termin.group)) {
                        scheduleNotification(getSecondOffset(termin.time, toCheck, listDay, offset),
                                termin.name, context, notification_id++);
                    }
                }
            }

            if (sp.getBoolean(context.getString(R.string.key_ma), false)) { //Visit out guestbook?
                ExpandableTermin lastTermin = query.get(query.size() - 1).get(query.get(query.size() - 1).size() - 1);
                scheduleNotification(getSecondOffset(lastTermin.time, toCheck, (int) toCheck.length - 1, -150), "", context, 0);
            }
        }
    }

    private long getSecondOffset(String time, SchedulerObject eventCheck, int day, int offset) {
        Calendar eventTime = Calendar.getInstance();
        eventTime.setTime(eventCheck.event); //Set on the where the event starts
        eventTime.add(Calendar.DAY_OF_MONTH, day); //Add the day offset
        eventTime.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time.substring(0, 2)));
        eventTime.set(Calendar.MINUTE, Integer.valueOf(time.substring(3)) - offset);
        eventTime.set(Calendar.MILLISECOND, 0);
        eventTime.set(Calendar.SECOND, 0);
        return (eventTime.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());
    }

    private void scheduleNotification(long delay, String notiText, Context context, int notification_id) {
        if (delay < 0) {
            return;
        }
        Intent i = new Intent(context, NotificationService.class);
        i.putExtra(SharedValues.NOTIFICATION_ID, notification_id);
        i.putExtra(SharedValues.NOTIFICATION_TEXT, notiText); //Saving the text in intent
        PendingIntent pi = PendingIntent.getService(context, notification_id, i, PendingIntent.FLAG_UPDATE_CURRENT);

        long notification_time = System.currentTimeMillis() + delay;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            aManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, notification_time, pi);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            aManager.setExact(AlarmManager.RTC_WAKEUP, notification_time, pi);
        } else {
            aManager.set(AlarmManager.RTC_WAKEUP, notification_time, pi);
        }
    }

}