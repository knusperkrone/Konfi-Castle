package de.knukro.cvjm.konficastle;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import de.knukro.cvjm.konficastle.helper.DbOpenHelper;
import de.knukro.cvjm.konficastle.structs.SchedulerObject;

public class SharedValues {

    public static final String NOTIZ_ = "00NOTIZ::";
    /*BootReceiver <-> NotificationService*/
    public static final String NOTIFICATION_ID = "notification_id";
    public static final String NOTIFICATION_TEXT = "notification_text";
    /*MainActivity <-> NotificationService*/
    public static final String KEY_TO_EXPAND = "to_expand";
    public static String toExpand = "";
    /*GetImages/AsyncAdapterSet <-> ProgrammRecyclerFragment*/
    private static LinkedBlockingQueue<AsyncTaskObject> runningTasks;
    /*ProgrammFragment <-> ProgrammRecycleFragment*/
    private static int currPosition = -1;
    private static long currDay = -1;
    private static int currScrollPosition = -1;
    private static ExpandableRecyclerAdapter adapter = null;

    public static void addAsyncTask(AsyncTask task, Object callingClass) {
        if (runningTasks == null)
            runningTasks = new LinkedBlockingQueue<>();
        runningTasks.add(new AsyncTaskObject(task, callingClass));
    }

    public static void removeAsyncTask(AsyncTask task) {
        if (runningTasks != null) {
            for (AsyncTaskObject taskObject : runningTasks) {
                if (!taskObject.task.equals(task)) {
                    runningTasks.remove(taskObject);
                }
            }
        }
    }

    public static void killRunningAsyncTasks(Object callingClass) {
        if (runningTasks != null && !runningTasks.isEmpty()) {
            for (AsyncTaskObject taskObject : runningTasks) {
                if (!taskObject.callerClass.equals(callingClass)) {
                    taskObject.task.cancel(true);
                }
            }
        }
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

    public static int getAndResetProgrammScrollPosition() {
        if (currScrollPosition == -1)
            return -1;
        int tmp = currScrollPosition;
        currScrollPosition = -1;
        return tmp;
    }

    public static long getCurrProgrammDay(Context context) {
        if (currDay == -1) {
            SchedulerObject toCheck = DbOpenHelper.getInstance().getDate(context);
            currDay = TimeUnit.DAYS.convert(System.currentTimeMillis() - toCheck.event.getTime(), TimeUnit.MILLISECONDS);
        }
        return currDay;
    }

    public static void initTablayout(Activity activity, ViewPager viewPager) {
        TabLayout tabLayout = (TabLayout) activity.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        if (tabLayout.getTabCount() < 6) {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        } else {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
        tabLayout.setVisibility(View.VISIBLE);
    }

    public static ExpandableRecyclerAdapter getAdapter() {
        ExpandableRecyclerAdapter tmp = adapter;
        adapter = null;
        return tmp;
    }

    public static void setAdapter(ExpandableRecyclerAdapter adapterNew) {
        adapter = adapterNew;
    }

    private static class AsyncTaskObject {
        final AsyncTask task;
        final Object callerClass;

        AsyncTaskObject(AsyncTask task, Object callerClass) {
            this.task = task;
            this.callerClass = callerClass;
        }
    }

}


