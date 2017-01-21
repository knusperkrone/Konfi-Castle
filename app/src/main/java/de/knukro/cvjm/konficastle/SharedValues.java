package de.knukro.cvjm.konficastle;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import de.knukro.cvjm.konficastle.helper.DbOpenHelper;
import de.knukro.cvjm.konficastle.structs.SchedulerObject;

public class SharedValues {


    /*GetImages/AsyncAdapterSet <-> ProgrammRecyclerFragment*/
    private static ConcurrentHashMap<AsyncTask, Boolean> runningTasks;

    /*BootReceiver <-> NotificationService*/
    public static final String NOTIFICATION_ID = "notification_id";
    public static final String NOTIFICATION_TEXT = "notification_text";

    /*ProgrammFragment <-> ProgrammRecycleFragment*/
    private static int currPosition = -1;
    private static long currDay = -1;
    private static int currScrollPosition = -1;


    public static void addAsyncTask(AsyncTask task) {
        if (runningTasks == null)
            runningTasks = new ConcurrentHashMap<>();
        runningTasks.put(task, true);
    }

    public static void removeAsyncTask(AsyncTask task) {
        runningTasks.remove(task);
    }

    public static void killRunningAsyncTasks() {
        if (runningTasks != null) {
            for (AsyncTask task : runningTasks.keySet()) {
                task.cancel(true);
            }
        }
    }

    public static boolean checkRunningTasks() {
        return runningTasks != null && !runningTasks.isEmpty();
    }

    public static void setCurrProgrammViewPagerPosition(int pos) {
        currPosition = pos;
    }


    public static int getAndResetCurrProgrammViewPagerPosition() {
        if (currPosition == -1)
            return -1;
        int tmp = currPosition;
        currPosition = -1;
        return tmp;
    }

    public static void setProgrammScrollPosition(int pos) {
        currScrollPosition = pos;
    }

    public static int getAndResetProgrammScrollPosition() {
        if (currScrollPosition == -1)
            return -1;
        int tmp = currScrollPosition;
        currScrollPosition = -1;
        return tmp;
    }

    public static void resetCurrProgrammDay() {
        currDay = -1;
    }

    public static long getCurrProgrammDay(Context context) {
        if (currDay == -1) {
            SchedulerObject toCheck = DbOpenHelper.getInstance().getDate(context);
            currDay = TimeUnit.DAYS.convert(System.currentTimeMillis() - toCheck.start.getTime(), TimeUnit.MILLISECONDS);
        }
        return currDay;
    }

}
