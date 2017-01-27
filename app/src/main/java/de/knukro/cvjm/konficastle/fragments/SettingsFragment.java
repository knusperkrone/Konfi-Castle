package de.knukro.cvjm.konficastle.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import de.knukro.cvjm.konficastle.R;
import de.knukro.cvjm.konficastle.SettingsActivity;
import de.knukro.cvjm.konficastle.dialogs.WelcomeDialog;
import de.knukro.cvjm.konficastle.helper.BootReceiver;
import de.knukro.cvjm.konficastle.helper.DbOpenHelper;

import static de.knukro.cvjm.konficastle.R.string.help_key;

public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private String VIBRATE_KEY, HELP_KEY, INSTANZ_KEY, NOTFICATIONTIME_KEY, SPAM_KEY;
    private boolean VIRBRATE_VALUE, SPAM_VALUE;
    private Preference vibrateButton, spamButton, notficationTimeButton, instanzButton;
    private DbOpenHelper dbOpenHelper;
    private SharedPreferences sp;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        Preference helpButton = findPreference(getString(help_key));
        helpButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new WelcomeDialog().show(SettingsActivity.ft, "settings");
                return true;
            }
        });

        dbOpenHelper = DbOpenHelper.getInstance();

        VIBRATE_KEY = getString(R.string.vibrate_key);
        HELP_KEY = getString(help_key);
        INSTANZ_KEY = getString(R.string.instanz_key);
        SPAM_KEY = getString(R.string.spam_key);
        NOTFICATIONTIME_KEY = getString(R.string.notification_time_key);

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        VIRBRATE_VALUE = sp.getBoolean(VIBRATE_KEY, false);
        SPAM_VALUE = sp.getBoolean(SPAM_KEY, false);

        vibrateButton = findPreference(getString(R.string.vibrate_key));
        spamButton = findPreference(getString(R.string.spam_key));
        notficationTimeButton = findPreference(getString(R.string.notification_time_key));
        instanzButton = findPreference(getString(R.string.instanz_key));

        specialAction(INSTANZ_KEY);
        specialAction(VIBRATE_KEY);
        specialAction(NOTFICATIONTIME_KEY);
        specialAction(SPAM_KEY);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (isAdded()) {
            sp = sharedPreferences;
            specialAction(s);

            if (!(s.equals(HELP_KEY) || s.equals(VIBRATE_KEY) || s.equals(SPAM_KEY))) {
                getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
                Context context = getActivity();

                dbOpenHelper.updateDbData(context);

                BootReceiver.resetNotifications(context);
                getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
            }
        }
    }

    private void specialAction(String key) {
        if (key.equals(VIBRATE_KEY)) {
            vibrateButton.setSummary((VIRBRATE_VALUE) ? R.string.vibrate_summary_pos : R.string.vibrate_summary_neg);
            VIRBRATE_VALUE = !VIRBRATE_VALUE;
        } else if (key.equals(SPAM_KEY)) {
            spamButton.setSummary((!SPAM_VALUE) ? R.string.spam_summary_pos : R.string.spam_summary_neg);
            SPAM_VALUE = !SPAM_VALUE;
        } else if (key.equals(INSTANZ_KEY)) {
            String instance = sp.getString(getString(R.string.instanz_key), "1");
            if (instance.equals("13")) {
                instanzButton.setSummary("Du bist auf dem ÖC");
            } else {
                instanzButton.setSummary("Du bist auf dem Konfi Castle " + instance);
            }
        } else if (key.equals(NOTFICATIONTIME_KEY)) {
            notficationTimeButton.setSummary("Du bekommst " + sp.getString(getString(R.string.notification_time_key), "5") + " Minuten vorher Bescheid");
        }
    }

}
