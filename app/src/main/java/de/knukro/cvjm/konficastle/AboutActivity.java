package de.knukro.cvjm.konficastle;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import de.knukro.cvjm.konficastle.helper.DbOpenHelper;

public class AboutActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private Context context;
    private int check = 6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        final ImageButton button = (ImageButton) findViewById(R.id.about_logo);
        context = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (check-- == 0) {
                    String message;
                    if (!preferences.getBoolean(getString(R.string.ma_key), false)) {
                        message = "Glückwunsch";
                        preferences.edit().putBoolean(getString(R.string.ma_key), true).apply();
                    } else {
                        message = "Nicht so übermütig";
                        preferences.edit().putBoolean(getString(R.string.ma_key), false).apply();
                    }
                    Toast.makeText(AboutActivity.this, message, Toast.LENGTH_SHORT).show();

                    DbOpenHelper.getInstance(context).updateProgramm(context);

                    check = 6;
                }
            }
        });
    }

}
