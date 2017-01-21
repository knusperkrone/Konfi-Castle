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
import de.knukro.cvjm.konficastle.SharedValues;
import de.knukro.cvjm.konficastle.structs.ExpandableDescription;
import de.knukro.cvjm.konficastle.structs.ExpandableTermin;
import de.knukro.cvjm.konficastle.structs.SchedulerObject;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;


public class DbOpenHelper extends SQLiteOpenHelper {

    private static DbOpenHelper instance;

    private static final String DB_NAME = "KonfiCastle.db";
    private static String DB_PATH;
    private final SharedPreferences sp;
    private SQLiteDatabase mDatabase;
    private SchedulerObject programDate;
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
                    "\tON Beschreibung.Id_Instanz == Instanz.Id AND Beschreibung.Tag_Termin == Termin.Tag AND Beschreibung.Uhrzeit_Termin == Termin.Uhrzeit\n" +
                    "WHERE Instanz.Id == ?\n" +
                    "AND Kürzel == ?\n" +
                    "ORDER BY Tag, Uhrzeit, TerminBeschreibung";


    private DbOpenHelper(Context context) {
        super(context, DB_NAME, null, 1);

        sp = getDefaultSharedPreferences(context);
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

    public static void initInstance(Context context) {
        if (instance == null) {
            instance = new DbOpenHelper(context);
        }
    }

    public static DbOpenHelper getInstance() {
        if (instance == null) {
            throw new IllegalAccessError("Singleton failed!");
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
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }
        SharedPreferences sp = getDefaultSharedPreferences(context);
        currVersion = sp.getInt(VERSION_KEY, 1);
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
            inputStream.close();
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

    public void updateDbData(Context mContext) {
        updateDate(mContext);
        updateProgramm(mContext);
        SharedValues.resetCurrProgrammDay();
    }

    public SchedulerObject getDate(Context mContext) {
        if (programDate == null)
            updateDbData(mContext);
        return programDate;
    }

    public ArrayList<ArrayList<ExpandableTermin>> getProgramm(Context context) {
        if (programList == null) {
            updateDbData(context);
        }
        return programList;
    }

    private void updateDate(Context mContext) {
        openDatabase();
        Cursor cursor = mDatabase.rawQuery(queryDate, new String[]{
                sp.getString(mContext.getString(R.string.instanz_key), "1"),
                sp.getString(mContext.getString(R.string.event_key), "KC")
        });

        cursor.moveToFirst();
        String startDate = cursor.getString(0); /* DD/MM/YYYY */
        int length = cursor.getInt(1);
        closeDatabase();
        cursor.close();

        GregorianCalendar c = new GregorianCalendar(Integer.valueOf(startDate.substring(6)), //Year
                Integer.valueOf(startDate.substring(3, 5)) - 1, //Month
                Integer.valueOf(startDate.substring(0, 2))); //Day

        Date start = c.getTime();
        programDate = new SchedulerObject(start, length);
    }

    /*Necessary through a database design flaw*/
    private String unescape(String description) {
        return description.replaceAll("\\\\n", "\\\n");
    }

    private void updateProgramm(Context mContext) {
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

            if (cursor.getString(3) != null) { //Get the expandableDescriptions
                if (ma) {
                    /*We are allowed to see everything*/
                    do {
                        currTermin.details.add(new ExpandableDescription(unescape(cursor.getString(3)),
                                cursor.getString(0)));
                        cursor.moveToNext();
                    } while (!cursor.isAfterLast() && cursor.getString(0).equals(currTermin.time));
                } else {
                    do {
                        /*We only can see our notes*/
                        if (cursor.getString(3).startsWith("00NOTIZ::")) {
                            currTermin.details.add(new ExpandableDescription(unescape(cursor.getString(3)),
                                    cursor.getString(0)));
                        } else { //No more notes
                            while (!cursor.isAfterLast() && cursor.getString(0).equals(currTermin.time)) {
                                cursor.moveToNext();
                            }
                            break;
                        }
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
        closeDatabase();
    }

    private void makeDbAction(String action, Context context) {
        openDatabase();
        mDatabase.beginTransaction();
        mDatabase.execSQL(action);
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
        closeDatabase();
        updateProgramm(context);
    }

    public void deleteNote(Context context, String day, String time, String content) {
        String id_Instanz;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        id_Instanz = sp.getString(context.getString(R.string.instanz_key), "1");

        makeDbAction("DELETE FROM Beschreibung WHERE Id_Instanz == \"" + id_Instanz + "\" AND Tag_Termin == \"" + day + "\" AND Uhrzeit_Termin ==\"" + time + "\" AND TerminBeschreibung LIKE \"%" + content + "\"", context);
    }

    public void updateNote(Context context, String day, String time, String origContent, String newContent) {
        String id_Instanz;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        id_Instanz = sp.getString(context.getString(R.string.instanz_key), "1");

        makeDbAction("UPDATE Beschreibung SET TerminBeschreibung = \"00NOTIZ::" + newContent + "\" WHERE Id_Instanz == \"" + id_Instanz + "\" AND Tag_Termin == \"" + day + "\" AND Uhrzeit_Termin ==\"" + time + "\" AND TerminBeschreibung LIKE \"%" + origContent + "\"", context);
    }

    public void putNote(Context context, String day, String time, String content) {
        String id_Instanz;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        id_Instanz = sp.getString(context.getString(R.string.instanz_key), "1");

        makeDbAction("INSERT INTO Beschreibung VALUES (\"" + id_Instanz + "\",\"" + day + "\",\"" + time + "\",\"00NOTIZ::" + content + "\")", context);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

}