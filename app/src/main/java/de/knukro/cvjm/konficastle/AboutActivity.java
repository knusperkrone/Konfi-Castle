package de.knukro.cvjm.konficastle;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import de.knukro.cvjm.konficastle.helper.BootReceiver;
import de.knukro.cvjm.konficastle.helper.DbOpenHelper;

public class AboutActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private Context context;
    private int check = 6;
    private DbOpenHelper dbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        final ImageButton button = (ImageButton) findViewById(R.id.about_logo);
        context = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        dbOpenHelper = DbOpenHelper.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (check-- == 0) {
                    String message;
                    if (!preferences.getBoolean(getString(R.string.key_ma), false)) {
                        message = getString(R.string.activity_about_ma_pos);
                        preferences.edit().putBoolean(getString(R.string.key_ma), true).apply();
                    } else {
                        message = getString(R.string.activity_about_ma_neg);
                        preferences.edit().putBoolean(getString(R.string.key_ma), false).apply();
                    }
                    Toast.makeText(AboutActivity.this, message, Toast.LENGTH_SHORT).show();

                    dbOpenHelper.updateDbData(context);
                    BootReceiver.resetNotifications(context);
                    check = 6;
                }
            }
        });
    }

}
