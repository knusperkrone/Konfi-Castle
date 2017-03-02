package de.knukro.cvjm.konficastle;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Locale;

import de.knukro.cvjm.konficastle.R;
import de.knukro.cvjm.konficastle.helper.DbOpenHelper;
import de.knukro.cvjm.konficastle.structs.SchedulerObject;

public class CurrEvent {

    public static int eventId;
    public static int eventYear;
    public static String eventName;
    public static int instanzId;
    public static String eventLanguage;
    public static SchedulerObject instanzInfo;

    public static void updateData(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        eventId = Integer.valueOf(sp.getString(context.getString(R.string.key_veranstaltung), "1"));
        eventYear = Integer.valueOf(sp.getString(context.getString(R.string.key_year), "2017"));
        eventName = DbOpenHelper.getInstance().getEventName(context);
        eventLanguage = (Locale.getDefault().toString().startsWith("de")) ? "de" : "en";
        instanzId = Integer.valueOf(sp.getString(context.getString(R.string.key_instanz), "1"));
        instanzInfo = DbOpenHelper.getInstance().getDates(); //Has to be last

    }

}
