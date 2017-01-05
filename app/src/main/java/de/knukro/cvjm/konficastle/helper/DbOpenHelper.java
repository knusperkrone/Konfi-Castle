package de.knukro.cvjm.konficastle.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import de.knukro.cvjm.konficastle.R;
import de.knukro.cvjm.konficastle.structs.ExpandableDescription;
import de.knukro.cvjm.konficastle.structs.ExpandableTermin;
import de.knukro.cvjm.konficastle.structs.SchedulerObject;


public class DbOpenHelper extends SQLiteOpenHelper {

    private static DbOpenHelper instance;

    private static final String DB_NAME = "KonfiCastle.db";
    private static String DB_PATH;
    private final SharedPreferences sp;
    private SQLiteDatabase mDatabase;

    private ArrayList<ArrayList<ExpandableTermin>> programList = null;

    private static final String queryDate =
            "SELECT Startdatum, Dauer\n" +
                    "from Veranstaltung\n" +
                    "JOIN Instanz ON Veranstaltung.Id = Instanz.Id_Veranstaltung\n" +
                    "where Instanz.id == ? and Veranstaltung.Kürzel == ?";

    private static final String queryProgramm =
            "SELECT Termin.Uhrzeit, Termin.Titel, Termin.Gruppe, Beschreibung.TerminBeschreibung, Termin.Tag\n" +
                    "FROM Veranstaltung\n" +
                    "JOIN Instanz\n" +
                    "\tON Instanz.Id_Veranstaltung == Veranstaltung.Id\n" +
                    "JOIN Termin\n" +
                    "\tON Termin.Id_Veranstaltung == Veranstaltung.Id\n" +
                    "LEFT JOIN Beschreibung\n" +
                    "\tON Beschreibung.Id_Instanz == Instanz.Id and Beschreibung.Tag_Termin == Termin.Tag and Beschreibung.Uhrzeit_Termin == Termin.Uhrzeit\n" +
                    "where Instanz.Id == ?\n" +
                    "and Kürzel == ?\n" +
                    "Order by Tag, Uhrzeit";


    private DbOpenHelper(Context context) {
        super(context, DB_NAME, null, 1);

        sp = PreferenceManager.getDefaultSharedPreferences(context);
        if (DB_PATH == null) {
            DB_PATH = context.getDatabasePath(DB_NAME).getPath();
        }

        if (checkUpgraded(context) || !new File(DB_PATH).exists()) {
            getReadableDatabase(); // Somehow necessary
            if (!copyDatabase(context)) {
                Toast.makeText(context, "Oh, ein Fehler mit der Datenbank. Starte die App neu!"
                        , Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static DbOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DbOpenHelper(context);
        }
        return instance;
    }

    private boolean checkUpgraded(Context context) {
        String VERSION_KEY = "version_key";
        int versionCheck, currVersion;
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            versionCheck = packageInfo.versionCode;
        }  catch (PackageManager.NameNotFoundException e) {
            return true;
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        currVersion =  sp.getInt(VERSION_KEY, 1);
        if (currVersion != versionCheck) {
            sp.edit().putInt(VERSION_KEY, versionCheck).apply();
            return true;
        }
        return false;
    }

    private boolean copyDatabase(Context context) {
        try {
            InputStream inputStream = context.getAssets().open(DB_NAME);
            String outFileName = DB_PATH;
            OutputStream outputStream = new FileOutputStream(outFileName);
            byte[] buff = new byte[1024];
            int length;
            while ((length = inputStream.read(buff)) > 0) {
                outputStream.write(buff, 0, length);
            }
            outputStream.flush();
            outputStream.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void openDatabase() {
        if (mDatabase != null && mDatabase.isOpen()) {
            return;
        }
        mDatabase = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }

    private void closeDatabase() {
        if (mDatabase != null) {
            mDatabase.close();
        }
    }


    public ArrayList<ArrayList<ExpandableTermin>> getProgramm(Context context) {
        if (programList == null) {
            updateProgramm(context);
        }
        return programList;
    }


    @SuppressWarnings("WeakerAccess")
    public SchedulerObject getDates(Context mContext) {
        openDatabase();
        Cursor cursor = mDatabase.rawQuery(queryDate, new String[]{
                sp.getString(mContext.getString(R.string.instanz_key), "1"),
                sp.getString(mContext.getString(R.string.event_key), "KC")
        });

        cursor.moveToFirst();
        String startDate = cursor.getString(0); /* DD/MM/YYYY */
        int length = cursor.getInt(1);
        cursor.close();
        closeDatabase();

        GregorianCalendar c = new GregorianCalendar(Integer.valueOf(startDate.substring(6)), //Year
                Integer.valueOf(startDate.substring(3, 5)) - 1, //Month
                Integer.valueOf(startDate.substring(0, 2))); //Day

        Date start = c.getTime();
        return new SchedulerObject(start, length);
    }


    /*Necessary through a database design flaw*/
    private String unescape(String description) {
        return description.replaceAll("\\\\n", "\\\n");
    }

    public void updateProgramm(Context mContext) {
        programList = new ArrayList<>();
        ExpandableTermin currTermin;
        ArrayList<ExpandableTermin> listDay = new ArrayList<>();

        boolean ma = sp.getBoolean(mContext.getString(R.string.ma_key), false);
        int index = 1;

        openDatabase();
        Cursor cursor = mDatabase.rawQuery(queryProgramm, new String[]{
                sp.getString(mContext.getString(R.string.instanz_key), "1"),
                sp.getString(mContext.getString(R.string.event_key), "KC")
        });

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            currTermin = new ExpandableTermin(cursor.getString(0), cursor.getString(1), cursor.getString(2));

            if (cursor.getString(3) != null) { //Get the expandable Descriptions

                if (ma) {
                    do {
                        currTermin.details.add(new ExpandableDescription(unescape(cursor.getString(3))));
                        cursor.moveToNext();
                    } while (!cursor.isAfterLast() && cursor.getString(0).equals(currTermin.time));
                } else {
                    do {
                        cursor.moveToNext();
                    } while (!cursor.isAfterLast() && cursor.getString(0).equals(currTermin.time));
                }
                cursor.moveToPrevious();
            }

            if (ma || !(currTermin.group.equals("ma") || currTermin.group.equals("aufbau"))) {
                listDay.add(currTermin);
            }
            cursor.moveToNext();

            if (cursor.isAfterLast()) {
                programList.add(listDay);
                break;
            } else if (cursor.getInt(4) != index) { //New day
                programList.add(listDay);
                listDay = new ArrayList<>();
                index++;
            }
        }
        cursor.close();
        closeDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

}