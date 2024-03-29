package org.hdm.app.timetracker.screens;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import org.hdm.app.timetracker.R;
import org.hdm.app.timetracker.listener.PreferenceListener;
import org.hdm.app.timetracker.util.Variables;


/**
 * Created by Hannes on 14.09.2016.
 */
public class Settings extends PreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    private static final String TAG = "Settings";

    private PreferenceListener listener;
    private Activity mainActivity;

    private Preference prefActivitiesReset;
    private Preference prefEditableMode;
    private Preference prefUserID;
    private Preference prefUserDropoffDate;
    private Preference prefUserConsent;
    private Preference prefConnectionSend;
    private Preference prefConnectionIP;
    private Preference prefConnectionPort;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mainActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initXML();
        initListener();
    }


    private void initXML() {
        addPreferencesFromResource(R.xml.settings);

        prefActivitiesReset = getPreferenceManager().findPreference(getString(R.string.pref_key_preferences_reset_activities));
        prefEditableMode = getPreferenceManager().findPreference(getString(R.string.pref_key_preferences_editable_mode));
        prefUserID = getPreferenceManager().findPreference(getString(R.string.pref_key_user_user_id));
        prefUserDropoffDate = getPreferenceManager().findPreference(getString(R.string.pref_key_user_dropoff_date));
        prefUserConsent = getPreferenceManager().findPreference(getString(R.string.pref_key_user_consent));
        prefConnectionSend = getPreferenceManager().findPreference(getString(R.string.pref_key_connection_send));
        prefConnectionIP = getPreferenceManager().findPreference(getString(R.string.pref_key_connection_ip));
        prefConnectionPort = getPreferenceManager().findPreference(getString(R.string.pref_key_connection_port));

    }

    private void initListener() {

        if (mainActivity != null) listener = (PreferenceListener) mainActivity;

        prefActivitiesReset.setOnPreferenceClickListener(this);
        prefConnectionSend.setOnPreferenceClickListener(this);

        prefEditableMode.setOnPreferenceChangeListener(this);
        prefUserID.setOnPreferenceChangeListener(this);
        prefUserDropoffDate.setOnPreferenceChangeListener(this);
        prefUserConsent.setOnPreferenceChangeListener(this);
        prefConnectionIP.setOnPreferenceChangeListener(this);
        prefConnectionPort.setOnPreferenceChangeListener(this);

    }


    private void initConditions() {

        if (prefUserID != null) {
            prefUserID.setTitle("User ID: " + Variables.getInstance().user_ID);
        }

        if (prefUserDropoffDate != null) {
            prefUserDropoffDate.setTitle("User Dropff Date: " + Variables.getInstance().user_Dropoff_Date);
        }

//        if (prefUserConsent != null) {
//            prefUserConsent.setTitle("User Consent: " + Variables.getInstance().user_Consent);
//        }

        if (prefConnectionSend != null) {
            prefConnectionSend.setEnabled(Variables.getInstance().serverConnection);
        }

        if (prefConnectionIP != null) {
            prefConnectionIP.setTitle("Port: " + Variables.getInstance().serverIP);
        }

        if (prefConnectionPort != null) {
            prefConnectionPort.setTitle("Port: " + Variables.getInstance().serverPort);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initConditions();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public boolean onPreferenceClick(Preference preference) {


        if (preference.equals(prefActivitiesReset)){
            if (listener != null) listener.resetActivities();
        }

        if (preference.equals(prefConnectionSend)) {
            if (listener != null) listener.sendLogFile();
        }


        return true;
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        if (preference.equals(prefEditableMode)) {
            if (newValue instanceof Boolean) {
                Variables.getInstance().editableMode = (Boolean) newValue;
                Variables.getInstance().backPress = (Boolean) newValue;
            }
        }

        else if (preference.equals(prefUserID)) {
            if (newValue instanceof String) {
                Variables.getInstance().user_ID = (String) newValue;
                prefUserID.setTitle("User ID: " + Variables.getInstance().user_ID);
            }
        }

        else if (preference.equals(prefUserDropoffDate)) {
            if (newValue instanceof String) {
                Variables.getInstance().user_Dropoff_Date = (String) newValue;
                prefUserDropoffDate.setTitle("User Dropoff Date: " + Variables.getInstance().user_Dropoff_Date);
            }
        }

        else if (preference.equals(prefUserConsent)) {
            if (newValue instanceof String) {
                Variables.getInstance().user_Consent = (Boolean) newValue;
            }
        }


        else if (preference.equals(prefConnectionIP)) {
            if (newValue instanceof String) {
                Variables.getInstance().serverIP = (String) newValue;
                prefConnectionIP.setTitle("IP: " + Variables.getInstance().serverIP);
            }
        }

        else if (preference.equals(prefConnectionPort)) {
            if (newValue instanceof String) {
                Variables.getInstance().serverPort = (String) newValue;
                prefConnectionPort.setTitle("Port: " + Variables.getInstance().serverPort);
            }
        }

        return true;
    }
}
