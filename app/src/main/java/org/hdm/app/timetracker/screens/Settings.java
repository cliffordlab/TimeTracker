package org.hdm.app.timetracker.screens;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.DatePicker;

import androidx.appcompat.app.AppCompatActivity;

import org.hdm.app.timetracker.BuildConfig;
import org.hdm.app.timetracker.R;
import org.hdm.app.timetracker.listener.PreferenceListener;
import org.hdm.app.timetracker.util.Variables;

import java.util.Calendar;


/**
 * Created by Hannes on 14.09.2016.
 */
public class Settings extends PreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    private static final String TAG = "Settings";

    private PreferenceListener listener;
    private AppCompatActivity mainActivity;

    //private Preference prefActivitiesReset;
    private Preference prefEditableMode;
    private Preference prefUserID;
    private Preference prefUserDropoffDate;
    private Preference prefUserConsent;
    private Preference prefConnectionSend;
    //private Preference prefConnectionIP;
    //private Preference prefConnectionPort;
    private Preference prefEmailId;

    private Preference prefMaxActiveActivities;
    private Preference prefThresholdMinutes;
    private Preference prefLogTimeInterval;
    private Preference prefReLoad;
    private Preference prefReStore;
    private Preference prefAppVersion;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mainActivity = (AppCompatActivity) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initXML();
        initListener();
    }


    private void initXML() {
        addPreferencesFromResource(R.xml.settings);

        //prefActivitiesReset = getPreferenceManager().findPreference(getString(R.string.pref_key_preferences_reset_activities));
        prefEditableMode = getPreferenceManager().findPreference(getString(R.string.pref_key_preferences_editable_mode));
        prefUserID = getPreferenceManager().findPreference(getString(R.string.pref_key_user_user_id));
        prefUserDropoffDate = getPreferenceManager().findPreference(getString(R.string.pref_key_user_dropoff_date));
        prefUserConsent = getPreferenceManager().findPreference(getString(R.string.pref_key_user_consent));
        prefConnectionSend = getPreferenceManager().findPreference(getString(R.string.pref_key_connection_send));
        //prefConnectionIP = getPreferenceManager().findPreference(getString(R.string.pref_key_connection_ip));
        //prefConnectionPort = getPreferenceManager().findPreference(getString(R.string.pref_key_connection_port));
        prefEmailId = getPreferenceManager().findPreference(getString(R.string.pref_key_email_id));

        prefMaxActiveActivities = getPreferenceManager().findPreference(getString(R.string.pref_key_max_active_activity));
        prefThresholdMinutes = getPreferenceManager().findPreference(getString(R.string.pref_key_threshold_minute));
        prefLogTimeInterval = getPreferenceManager().findPreference(getString(R.string.pref_key_logtime_interval));
        prefReLoad = getPreferenceManager().findPreference(getString(R.string.pref_key_reload));
        prefReStore = getPreferenceManager().findPreference(getString(R.string.pref_key_restore));
        prefAppVersion = getPreferenceManager().findPreference(getString(R.string.pref_key_app_version));
    }

    private void initListener() {

        if (mainActivity != null) listener = (PreferenceListener) mainActivity;

        //prefActivitiesReset.setOnPreferenceClickListener(this);
        prefConnectionSend.setOnPreferenceClickListener(this);
        prefReLoad.setOnPreferenceClickListener(this);
        prefReStore.setOnPreferenceClickListener(this);
        prefUserDropoffDate.setOnPreferenceClickListener(this);

        prefEditableMode.setOnPreferenceChangeListener(this);
        prefUserID.setOnPreferenceChangeListener(this);
        prefUserConsent.setOnPreferenceChangeListener(this);
        //prefConnectionIP.setOnPreferenceChangeListener(this);
        //prefConnectionPort.setOnPreferenceChangeListener(this);
        prefEmailId.setOnPreferenceChangeListener(this);

        prefMaxActiveActivities.setOnPreferenceChangeListener(this);
        prefThresholdMinutes.setOnPreferenceChangeListener(this);
        prefLogTimeInterval.setOnPreferenceChangeListener(this);
    }


    private void initConditions() {

        if (prefUserID != null) {
            prefUserID.setTitle("User ID: " + Variables.getInstance().user_ID);
        }

        if (prefUserDropoffDate != null) {
            prefUserDropoffDate.setTitle("User Dropff Date: " + Variables.getInstance().user_Dropoff_Date);
        }

        if (prefUserConsent != null) {
            prefUserConsent.setTitle("User Consent: " + Variables.getInstance().user_Consent);

        }

        if (prefConnectionSend != null) {
            prefConnectionSend.setEnabled(Variables.getInstance().serverConnection);
        }

        if (prefEmailId != null) {
            prefEmailId.setTitle("Email: " + Variables.getInstance().emailId);
        }

        /*if (prefConnectionIP != null) {
            prefConnectionIP.setTitle("Port: " + Variables.getInstance().serverIP);
        }*/

        /*if (prefConnectionPort != null) {
            prefConnectionPort.setTitle("Port: " + Variables.getInstance().serverPort);
        }*/

        if (prefLogTimeInterval != null) {
            prefLogTimeInterval.setTitle("Log Time Interval: " + Variables.getInstance().logTimeInterval);
        }

        if (prefMaxActiveActivities != null) {
            prefMaxActiveActivities.setTitle("Max. active Activities: " + Variables.getInstance().maxRecordedActivity);
        }

        if (prefThresholdMinutes != null) {
            prefThresholdMinutes.setTitle("Threshold Minutes: " + Variables.getInstance().thresholdTime);
        }

        if (prefAppVersion != null) {
            prefAppVersion.setSummary("Current Version " + BuildConfig.VERSION_NAME);
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


        /*if (preference.equals(prefActivitiesReset)) {
            if (listener != null) listener.resetActivities();
        }*/

        if (preference.equals(prefReStore)) {
            if (listener != null) listener.resetActivities();
        }

        if (preference.equals(prefConnectionSend)) {
            if (listener != null) listener.sendLogFile();
        }

        if (preference.equals(prefReLoad)) {
            if (listener != null) listener.reloadData();
        }

        if (preference.equals(prefUserDropoffDate)){
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    String changeDate = year + "/" + (month + 1) + "/"+ dayOfMonth;
                    Variables.getInstance().user_Dropoff_Date = changeDate;
                    prefUserDropoffDate.setTitle("User Dropoff Date: " + Variables.getInstance().user_Dropoff_Date);
                }
            }, mYear, mMonth, mDay);
            dialog.show();
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
        } else if (preference.equals(prefUserID)) {
            if (newValue instanceof String) {
                Variables.getInstance().user_ID = (String) newValue;
                prefUserID.setTitle("User ID: " + Variables.getInstance().user_ID);
            }
        } /*else if (preference.equals(prefUserDropoffDate)) {
            if (newValue instanceof String) {
                Variables.getInstance().user_Dropoff_Date = (String) newValue;
                prefUserDropoffDate.setTitle("User Dropoff Date: " + Variables.getInstance().user_Dropoff_Date);
            }
        }*/ else if (preference.equals(prefUserConsent)) {
            if (newValue instanceof String) {
                Variables.getInstance().user_Consent = (Boolean) newValue;
                prefUserConsent.setTitle("User Consent: " + Variables.getInstance().user_Consent);
            }
        }
        /*else if (preference.equals(prefConnectionIP)) {
            if (newValue instanceof String) {
                Variables.getInstance().serverIP = (String) newValue;
                prefConnectionIP.setTitle("IP: " + Variables.getInstance().serverIP);
            }
        }*/ /*else if (preference.equals(prefConnectionPort)) {
            if (newValue instanceof String) {
                Variables.getInstance().serverPort = (String) newValue;
                prefConnectionPort.setTitle("Port: " + Variables.getInstance().serverPort);
            }
        }*/
        else if (preference.equals(prefLogTimeInterval)) {
            if (newValue instanceof String) {
                Log.d(TAG, "prefLogTimeInterval : " + newValue);
                Variables.getInstance().logTimeInterval = Long.valueOf(String.valueOf(newValue));
                prefLogTimeInterval.setTitle("Log Time Interval: " + Variables.getInstance().logTimeInterval);
            }
        } else if (preference.equals(prefMaxActiveActivities)) {
            if (newValue instanceof String) {
                Variables.getInstance().maxRecordedActivity = Integer.parseInt(String.valueOf(newValue));
                prefMaxActiveActivities.setTitle("Max. active Activities: " + Variables.getInstance().maxRecordedActivity);
            }
        } else if (preference.equals(prefThresholdMinutes)) {
            if (newValue instanceof String) {
                Variables.getInstance().thresholdTime = Integer.parseInt(String.valueOf(newValue));
                prefThresholdMinutes.setTitle("Threshold Minutes: " + Variables.getInstance().thresholdTime);
            }
        } else if (preference.equals(prefEmailId)) {
            if (newValue instanceof String) {
                Variables.getInstance().emailId = String.valueOf(newValue);
                prefEmailId.setTitle("Email: " + Variables.getInstance().emailId);
            }
        }

        return true;
    }
}
