package de.knukro.cvjm.konficastle.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import de.knukro.cvjm.konficastle.MainActivity;
import de.knukro.cvjm.konficastle.R;
import de.knukro.cvjm.konficastle.structs.DbDescription;


public class DbUpdater extends AsyncTask<Void, Void, Void> {

    private static final String TMP_NAME = "KonfiCastle2.db";
    private static final String ORIG_DATABASE_NAME = "KonfiCastle.db";
    private static String DOWNLOADED_PATH;

    private final Context context;
    private final SharedPreferences sp;
    private final DbOpenHelper dbOpenHelper;
    private boolean success = false;


    public DbUpdater(Context context) {
        this.context = context;
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        dbOpenHelper = DbOpenHelper.getInstance();
    }


    private static void streamWrite(InputStream input, OutputStream output) throws Exception {
        byte data[] = new byte[1024];
        int count;
        while ((count = input.read(data)) != -1) {
            output.write(data, 0, count);
        }
        output.flush();
        output.close();
        input.close();
    }


    @Override
    protected Void doInBackground(Void... voids) {
        URL url;
        int newVersion;
        try {
            url = new URL("https://raw.githubusercontent.com/knusperkrone/Konfi-Castle/master/DatenBankSync/version.txt");
            URLConnection conn = url.openConnection();
            /*Read the first line of document as int*/
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            newVersion = Integer.valueOf(in.readLine());
            in.close();
            if (newVersion > sp.getInt("db_version", 0)) {
                /*We have a new Version*/
                /*Download the Database as tempFile*/
                File database = File.createTempFile(TMP_NAME, null, context.getCacheDir());
                url = new URL("https://raw.githubusercontent.com/knusperkrone/Konfi-Castle/master/DatenBankSync/KonfiCastle.db");
                streamWrite(new BufferedInputStream(url.openStream(), 8192), new FileOutputStream(database));
                DOWNLOADED_PATH = database.getPath();

                /*Init the downloaded database, save/restore user-Data and overwrite old Database*/
                DatabaseRestore dbRestore = new DatabaseRestore(context);
                success = dbRestore.restoreNotes(dbOpenHelper.getNotes()) &&
                        dbRestore.overwriteDatabase() &&
                        sp.edit().putInt("db_version", newVersion).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (success) {
            Toast.makeText(context, context.getString(R.string.helper_dbupdater_success), Toast.LENGTH_LONG).show();
            dbOpenHelper.updateDbData(context);
            ((MainActivity) context).onResume();
        }
    }


    private static class DatabaseRestore extends SQLiteOpenHelper {

        private final String TMPDB_PATH;

        private DatabaseRestore(Context context) {
            super(context, TMP_NAME, null, 1);
            TMPDB_PATH = context.getDatabasePath(TMP_NAME).getPath();
            getReadableDatabase();
            try {
                streamWrite(new FileInputStream(DOWNLOADED_PATH), new FileOutputStream(TMPDB_PATH));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private boolean restoreNotes(List<DbDescription> notes) {
            if (notes == null) {
                return false;
            }
            if (!notes.isEmpty()) {
                StringBuilder sqlAction = new StringBuilder("INSERT INTO Beschreibung VALUES ");
                for (DbDescription note : notes) {
                    sqlAction.append(note.toString());
                }
                sqlAction.deleteCharAt(sqlAction.lastIndexOf(","));
                try {
                    SQLiteDatabase mDatabase = SQLiteDatabase.openDatabase(TMPDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
                    mDatabase.beginTransaction();
                    mDatabase.execSQL(sqlAction.toString());
                    mDatabase.setTransactionSuccessful();
                    mDatabase.endTransaction();
                    mDatabase.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        boolean overwriteDatabase() {
            File oldDatabase = null, newDatabase;
            String Path = new File(TMPDB_PATH).getParent() + File.separator;
            try {
                oldDatabase = new File(Path + ORIG_DATABASE_NAME);
                newDatabase = new File(TMPDB_PATH);
                oldDatabase.renameTo(new File(Path + "old.db"));
                newDatabase.renameTo(new File(Path + ORIG_DATABASE_NAME));
            } catch (Exception e) {
                if (oldDatabase != null && oldDatabase.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    oldDatabase.renameTo(new File(Path + ORIG_DATABASE_NAME));
                }
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }

}
