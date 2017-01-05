package de.knukro.cvjm.konficastle.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import de.knukro.cvjm.konficastle.R;
import de.knukro.cvjm.konficastle.SettingsActivity;
import de.knukro.cvjm.konficastle.helper.DbOpenHelper;
import de.knukro.cvjm.konficastle.helper.NotificationHelper;

import static de.knukro.cvjm.konficastle.R.string.help_key;

public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private String VIBRATE_KEY, HELP_KEY, INSTANZ_KEY, ACTIVE_KEY;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        Preference button = findPreference(getString(help_key));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new WelcomeDialog().show(SettingsActivity.ft, "settings");
                return true;
            }
        });

        VIBRATE_KEY = getString(R.string.vibrate_key);
        HELP_KEY = getString(help_key);
        INSTANZ_KEY = getString(R.string.instanz_key);
        ACTIVE_KEY = getString(R.string.active_notifcations);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (isAdded() && !(s.equals(VIBRATE_KEY) || s.equals(HELP_KEY))) {
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            Context context = getActivity();

            if (s.equals(INSTANZ_KEY)) {
                DbOpenHelper.getInstance(context).updateProgramm(context);
            }

            PreferenceManager.getDefaultSharedPreferences(context)
                    .edit().putBoolean(ACTIVE_KEY, false).apply();

            NotificationHelper.setupNotifications(context);
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }
    }

}
