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

import static de.knukro.cvjm.konficastle.R.string.activity_settings_spam_summary_neg;
import static de.knukro.cvjm.konficastle.R.string.activity_settings_spam_summary_pos;
import static de.knukro.cvjm.konficastle.R.string.activity_settings_vibrate_summary_neg;
import static de.knukro.cvjm.konficastle.R.string.activity_settings_vibrate_summary_pos;
import static de.knukro.cvjm.konficastle.R.string.key_help;
import static de.knukro.cvjm.konficastle.R.string.key_instanz;
import static de.knukro.cvjm.konficastle.R.string.key_notification_time;
import static de.knukro.cvjm.konficastle.R.string.key_spam;
import static de.knukro.cvjm.konficastle.R.string.key_vibrate;

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
        Preference helpButton = findPreference(getString(key_help));
        helpButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new WelcomeDialog().show(SettingsActivity.ft, "settings");
                return true;
            }
        });

        dbOpenHelper = DbOpenHelper.getInstance();

        VIBRATE_KEY = getString(key_vibrate);
        HELP_KEY = getString(key_help);
        INSTANZ_KEY = getString(key_instanz);
        SPAM_KEY = getString(key_spam);
        NOTFICATIONTIME_KEY = getString(key_notification_time);

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        VIRBRATE_VALUE = sp.getBoolean(VIBRATE_KEY, false);
        SPAM_VALUE = sp.getBoolean(SPAM_KEY, false);

        vibrateButton = findPreference(getString(key_vibrate));
        spamButton = findPreference(getString(key_spam));
        notficationTimeButton = findPreference(getString(key_notification_time));
        instanzButton = findPreference(getString(key_instanz));

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
            vibrateButton.setSummary((VIRBRATE_VALUE) ? activity_settings_vibrate_summary_pos : activity_settings_vibrate_summary_neg);
            VIRBRATE_VALUE = !VIRBRATE_VALUE;
        } else if (key.equals(SPAM_KEY)) {
            spamButton.setSummary((!SPAM_VALUE) ? activity_settings_spam_summary_pos : activity_settings_spam_summary_neg);
            SPAM_VALUE = !SPAM_VALUE;
        } else if (key.equals(INSTANZ_KEY)) {
            String instance = sp.getString(getString(key_instanz), "1");
            if (instance.equals("13")) {
                instanzButton.setSummary(getString(R.string.fragment_settings_titleOC));
            } else {
                instanzButton.setSummary(getString(R.string.fragment_settings_titleKC) + " " + instance);
            }
        } else if (key.equals(NOTFICATIONTIME_KEY)) {
            notficationTimeButton.setSummary(getString(R.string.fragment_settings_notification1) + " " +
                    sp.getString(getString(key_notification_time), "5") + " " +
                    getString(R.string.fragment_settings_notification2));
        }
    }

}
