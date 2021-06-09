package org.hdm.app.timetracker.main;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.hdm.app.timetracker.R;
import org.hdm.app.timetracker.datastorage.ActivityObject;
import org.hdm.app.timetracker.datastorage.DataManager;
import org.hdm.app.timetracker.listener.PreferenceListener;
import org.hdm.app.timetracker.util.FileLoader;
import org.hdm.app.timetracker.util.Variables;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import static org.hdm.app.timetracker.util.Consts.ACTIVE_LIST;
import static org.hdm.app.timetracker.util.Consts.ACTIVITY_STATE;
import static org.hdm.app.timetracker.util.Consts.CALENDAR_MAP;
import static org.hdm.app.timetracker.util.Consts.CONFIG_FOLDER;
import static org.hdm.app.timetracker.util.Consts.DEBUGMODE;
import static org.hdm.app.timetracker.util.Consts.IMAGE_FOLDER;
import static org.hdm.app.timetracker.util.Consts.LOGS_FOLDER;

//import android.support.v7.app.AlertDialog;


// Version 0.9 - 07.10.2016

public class MainActivity extends AppCompatActivity implements
        PreferenceListener {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String[] PERMISSION = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private final String TAG = "MainActivity";

    // ToDo Add Logik for Dayshift
    // ToDo Add Logik to display Activitys on Dayview only after 1 Minute recording
    /**
     * Constants
     */
    Handler handler = new Handler();
    private Variables var;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkReadWriteExternalStorage();

        //initConfiguration();
        //initCalendar();
        //initDataLogger();
//        initResetRecordedData();
        //loadSavedObjectState();
        initLayout();

    }

    /**
     * There are to options for the Read and write external storage
     */
    private void checkReadWriteExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (allPermissionCheck(PERMISSION, MainActivity.this) == PackageManager.PERMISSION_GRANTED) {
                initConfiguration();
                initCalendar();
                initDataLogger();
                loadSavedObjectState();
            } else {
                MainActivity.this.requestPermissions(PERMISSION, 1);
            }
        } else {
            initConfiguration();
            initCalendar();
            initDataLogger();
            loadSavedObjectState();
        }
    }

    private int allPermissionCheck(String[] permissions, MainActivity mainActivity) {
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (String permission : permissions) {
            permissionCheck += ContextCompat.checkSelfPermission(mainActivity, permission);
        }
        return permissionCheck;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0) {
                boolean readExternalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean writeExternalStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (readExternalStorage && writeExternalStorage) {
                    initConfiguration();
                    initCalendar();
                    initDataLogger();
                    loadSavedObjectState();
                }else {
                    checkReadWriteExternalStorage();
                }
            }
        }
    }

    private void initCalendar() {

        DataManager.getInstance().calenderMap = new TreeMap<>();

        Calendar calEndTime = Calendar.getInstance();
        Variables.getInstance().fistDay = calEndTime.getTime();
        initCalenderMap(calEndTime.getTime());

//        calEndTime.add(Calendar.DAY_OF_MONTH, 1);
//        Variables.getInstance().secondDay = calEndTime.getTime();
//
//        initCalenderMap(calEndTime.getTime());
//
//        calEndTime.add(Calendar.DAY_OF_MONTH, 1);
//        Variables.getInstance().thirdDay = calEndTime.getTime();
//        initCalenderMap(calEndTime.getTime());
    }


    /**
     * There are to options for the Backpress Button
     * 1. if backPress flag is true that allow to use this button as backpress
     * 2. if backPressDialog is true then show a AlertDialog before quit the app
     */
    @Override
    public void onBackPressed() {

        // ToDo write test cases
        if (Variables.getInstance().backPress) {

            // if backPressDialog is true then show a AlertDialog before quit the app
            if (Variables.getInstance().backPressDialog) {
                new AlertDialog.Builder(this)
                        .setTitle("Really Exit?")
                        .setMessage("Are you sure you want to exit?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                MainActivity.super.onBackPressed();
                            }
                        }).create().show();
            } else {
                MainActivity.super.onBackPressed();
            }
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (DEBUGMODE) Log.d(TAG, "onStop");
        saveLogFile();
        saveCurrentState();
//        startActivity(new Intent(this, MainActivity.class));
    }


    /**
     * Use the App as Homescreen App
     * with this feature is the home button override
     * That means when the user is pressing the homebutton the app will start again
     * and the user can´t get to the regular homescreen of the smartphone
     *
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_MAIN.equals(intent.getAction())) {
        }
    }


    /*****************************
     * Init
     *******************************************/

    /**
     * Init DataManagement Singleton
     * Init Variables Singleton
     * Load all used files like activity.json, images
     */
    private void initConfiguration() {
        // Init the Data Structure - there all the created where hosted
        DataManager.init();
        Variables.init();
        var = Variables.getInstance();

        Calendar cal = Calendar.getInstance();

        var.currentTime = cal.getTime();

        FileLoader fl = new FileLoader(this);
        fl.initFiles();


        // Datenabgleich zwischen SharedMemory und Variables
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(this).edit();
        //prefs.putBoolean(getString(R.string.pref_key_preferences_editable_mode), var.editableMode);
        //prefs.putString(getString(R.string.pref_user_ID), var.user_ID);
        //prefs.putString(getString(R.string.pref_key_user_dropoff_date), var.user_Dropoff_Date);
        //prefs.putBoolean(getString(R.string.pref_key_user_consent), var.user_Consent);
        //prefs.putString(getString(R.string.pref_key_connection_ip), var.serverIP);
        //prefs.putString(getString(R.string.pref_key_connection_port), var.serverPort);

        //prefs.putString(getString(R.string.pref_key_logtime_interval), String.valueOf(var.logTimeInterval));
        prefs.commit();

        //Get settings from SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Variables.getInstance().user_ID = preferences.getString(getString(R.string.pref_user_ID), var.user_ID);
        Variables.getInstance().serverIP = preferences.getString(getString(R.string.pref_key_connection_ip), var.serverIP);
        Variables.getInstance().serverPort = preferences.getString(getString(R.string.pref_key_connection_port), var.serverPort);
        Variables.getInstance().editableMode = preferences.getBoolean(getString(R.string.pref_key_preferences_editable_mode), var.editableMode);
        Variables.getInstance().maxRecordedActivity = Integer.parseInt(preferences.getString(getString(R.string.pref_key_max_active_activity), String.valueOf(var.maxRecordedActivity)));
        Variables.getInstance().logTimeInterval = Long.valueOf(preferences.getString(getString(R.string.pref_key_logtime_interval), String.valueOf(var.logTimeInterval)));
        Variables.getInstance().thresholdTime = Integer.parseInt(preferences.getString(getString(R.string.pref_key_threshold_minute), String.valueOf(var.thresholdTime)));
        Variables.getInstance().emailId = preferences.getString(getString(R.string.pref_key_email_id), "");
    }


//    /**
//     * Init Content for CalendarList
//     * Every hour is seperatetd in two half hour periods
//     */
//    public void initCalenderMap() {
//
//        Calendar cal = Calendar.getInstance();
//        Date time = cal.getTime();
//
//        // Reset Time to 00:00:00
//        time.setHours(var.startHour);
//        time.setMinutes(var.startMin);
//        time.setSeconds(var.startMin);
//
//        // Set Calendar with reseted time
//        cal.setTime(time);
//
//
//        Calendar calEndTime = Calendar.getInstance();
//        Date endTime = calEndTime.getTime();
//        endTime.setHours(var.endHour);
//        endTime.setMinutes(var.startMin);
//        endTime.setSeconds(var.startMin);
//        calEndTime.setTime(endTime);
//        calEndTime.add(Calendar.MINUTE, -var.timeFrame);
//        endTime = calEndTime.getTime();
//
//        Log.d(TAG, "Timeweee " + endTime + "  "  + time);
//
//        DataManager.getInstance().calenderMap = new TreeMap<>();
//
//        while(time.before(endTime)) {
//            time = cal.getTime();
//            DataManager.getInstance().setCalenderMapEntry(time.toString(), null);
//            // add 30 minutes to setTime
//            cal.add(Calendar.MINUTE, var.timeFrame);
//        }
//    }


    /*
     * Init CalendarList with new Entries
     *
     * @param calendar current Time in Date
     */
    public void initCalenderMap(Date currentTime) {

        Log.d(TAG, "calendar eingang " + currentTime);

        Calendar cal = Calendar.getInstance();
        Log.d(TAG, "calendar new Instance " + cal.getTime());
        cal.setTime(currentTime);
        Log.d(TAG, "calendar set with eingang " + cal.getTime());


        Calendar calEndTime = Calendar.getInstance();
        calEndTime.setTime(currentTime);
        Log.d(TAG, "calendar end time " + calEndTime.getTime());


        Date time = cal.getTime();
        // Reset Time to 00:00:00
        time.setHours(var.startHour);
        time.setMinutes(var.startMin);
        time.setSeconds(var.startMin);

        // Set Calendar with reseted time
        cal.setTime(time);


        Date endTime = calEndTime.getTime();
        endTime.setHours(var.endHour);
        endTime.setMinutes(var.startMin);
        endTime.setSeconds(var.startMin);
        calEndTime.setTime(endTime);
        calEndTime.add(Calendar.MINUTE, -var.timeFrame);
        endTime = calEndTime.getTime();

        Log.d(TAG, "calendar end time " + endTime + "  " + time);


        while (time.before(endTime)) {
            time = cal.getTime();
            DataManager.getInstance().setCalenderMapEntry(time.toString(), null);
            // add 30 minutes to setTime
            cal.add(Calendar.MINUTE, var.timeFrame);
            Log.d(TAG, "calendar time new " + time.toString() + " " + DataManager.getInstance().calenderMap.size());
        }


    }


    /**
     * Save current State every 15 min
     * Run in TimerTask Thread not on UI Thread
     */
    private void initDataLogger() {

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        saveLogFile();
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, (var.logTimeInterval * 60 * 1000));
    }


    private void initResetRecordedData() {


        // ToDo Check hole function --- last edit

        // Set the alarm to start at approximately 2:00 p.m.
        final Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.HOUR_OF_DAY, 23);
//        calendar.set(Calendar.MINUTE, 59);
//        calendar.set(Calendar.SECOND, 56);
        calendar.add(Calendar.SECOND, 10);

        Date currentDate = calendar.getTime();
        Log.d(TAG, "calendar5 " + currentDate);


//        Calendar currentCalendar = Calendar.getInstance();
//        currentCalendar.add(Calendar.DAY_OF_MONTH, +1);
//        initCalenderMap(currentCalendar.getTime()); // only for Testing

        Log.d(TAG, "calendar4 " + calendar.getTime());
        Log.d(TAG, "calendar5 " + currentDate);


        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                //add 24 hours to the calendar Time for the next
//                calendar.add(Calendar.DAY_OF_MONTH, +1);
                calendar.add(Calendar.SECOND, 10);


                // Stop every active Activity and save the time

                // Get ActiveList
                ArrayList<String> list = DataManager.getInstance().activeList;

                // iterate through the complete list and save the active Activity to Activity Object
                for (int i = 0; i < list.size() - 1; i++) {

                    String title = list.get(i);

                    ActivityObject activityObject = DataManager.getInstance().getActivityObject(title);

                    // Deactivate Activity
                    activityObject.activeState = false;
                    activityObject.count = 0;

                    // set temporary end time
                    activityObject.endTime = Calendar.getInstance().getTime();


                    //Count how many activities are active
                    var.activeCount--;

                    // Save Timestamp and SubCategory in ActivityObject
                    activityObject.saveTimeStamp("user");
                    DataManager.getInstance().setActivityObject(activityObject);

                    // ToDo check background of Activity in objectList and activeList like ActivityFragment
                    // DataManager.getInstance().activeList.remove(activityObject.title);
                }

                // Save Log File
                saveLogFile();

                // Save active Files
                var.activeActivities = DataManager.getInstance().activeList;

                // Reset all Lists, CalendarList, ObjectActivityList, ActiveActivity
                initConfiguration();


                // Add new CalendarItems
                calendar.add(Calendar.DAY_OF_MONTH, +1);
                initCalenderMap(calendar.getTime());


                //Get active Activities and set them back to active
                DataManager.getInstance().activeList = var.activeActivities;
                ArrayList<String> listt = DataManager.getInstance().activeList;

                Log.d(TAG, "listt size " + listt.size());

                // iterate through the complete list and save the active Activity to Activity Object
                for (int i = 0; i < listt.size(); i++) {

                    String title = listt.get(i);

                    ActivityObject activityObject = DataManager.getInstance().getActivityObject(title);

                    // Deactivate Activity
                    activityObject.activeState = true;
                    activityObject.count = 1;

                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.HOUR, 0);
                    activityObject.startTime = calendar.getTime();
                    Log.d(TAG, "listt size " + activityObject.startTime);

                    //Count how many activities are active
                    var.activeCount++;

                    DataManager.getInstance().setActivityObject(activityObject);
                    // ToDo check background of Activity in objectList and activeList like ActivityFragment

                    Log.d(TAG, "listt size " + DataManager.getInstance().getActivityObject(activityObject.title).startTime);
                }


                Log.d(TAG, "restart");
                initResetRecordedData();
            }
        };
        timer.schedule(timerTask, calendar.getTime());

    }


    /**
     * Init the Layout
     * activate FullScreen Mode
     */
    private void initLayout() {
        setFullScreen(true);
        setContentView(R.layout.activity_main);
    }


    /**
     * Set App to fullscreen mode if boolean == true
     *
     * @param fullscreen a flag for setting the app in fullscreen mode or not
     */
    private void setFullScreen(boolean fullscreen) {
        if (fullscreen) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }


    /**
     * Save the current activities State on local storage as json format
     */
    private void saveLogFile() {
        String currentDate = Calendar.getInstance().getTime().toString();
        int size = currentDate.length();
        String year = currentDate.substring(size - 4, size);
        String date = currentDate.substring(4, 19);
        String fileName = var.user_ID + "_" + year + "_" + date + "_activities.txt";
        fileName = fileName.replaceAll(" ", "_");
        Log.d(TAG, "currentDate " + fileName);
        new FileLoader(this).saveLogsOnExternal(fileName);
    }


    /**
     * Save current activity state, and CalenderMap in internal storage
     */
    private void saveCurrentState() {

        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();


        // Save ObjectActivity Map
        Map<String, ActivityObject> map = DataManager.getInstance().getObjectMap();
        String json = gson.toJson(map);
        prefsEditor.putString(ACTIVITY_STATE, json);


        // Save ActiveList
        ArrayList<String> activeList = DataManager.getInstance().activeList;
        json = gson.toJson(activeList);
        prefsEditor.putString(ACTIVE_LIST, json);


        // Save CalendarMap
        TreeMap<String, ArrayList<String>> calendarMap = DataManager.getInstance().calenderMap;
        json = gson.toJson(calendarMap);
        prefsEditor.putString(CALENDAR_MAP, json);
        prefsEditor.commit();

    }


    /**
     * Load the stored current states after the app is opened
     */
    private void loadSavedObjectState() {
        Gson gson = new Gson();

        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
//        editor.remove(ACTIVITY_STATE);
//        editor.remove(ACTIVE_LIST);
//        editor.remove(CALENDAR_MAP);
//        editor.commit();

        // Datenabgleich zwischen SharedMemory und Variables
        editor.putBoolean(getString(R.string.pref_key_preferences_editable_mode), var.editableMode);
        editor.putBoolean(getString(R.string.pref_key_user_consent), var.user_Consent);
        editor.apply();

        if (mPrefs.contains(ACTIVITY_STATE)) {

            String json = mPrefs.getString(ACTIVITY_STATE, "");
            Log.d(TAG, "Jsonnnnnn " + json);


            Type typeOfHashMap = new TypeToken<LinkedHashMap<String, ActivityObject>>() {
            }.getType();
            LinkedHashMap<String, ActivityObject> newMap = gson.fromJson(json, typeOfHashMap); // This type must match TypeToken
            DataManager.getInstance().activityMap = newMap;


            if (mPrefs.contains(ACTIVE_LIST)) {
                json = mPrefs.getString(ACTIVE_LIST, "");
                Type type = new TypeToken<List<String>>() {
                }.getType();
                ArrayList<String> activeList = gson.fromJson(json, type);
                DataManager.getInstance().activeList = activeList;

            }

            if (mPrefs.contains(CALENDAR_MAP)) {
                json = mPrefs.getString(CALENDAR_MAP, "");
                Log.d(TAG, "Jsonnnnnn " + json);
                Type type = new TypeToken<TreeMap<String, ArrayList<String>>>() {
                }.getType();
                TreeMap<String, ArrayList<String>> calendarMap = gson.fromJson(json, type);
                DataManager.getInstance().calenderMap = calendarMap;
                Log.d(TAG, "Jsonnnnnn " + DataManager.getInstance().calenderMap);
                Log.d(TAG, "Jsonnnnnn " + DataManager.getInstance().calenderMap.size());
            }

        }
        if (DataManager.getInstance().activeList != null) {
            Variables.getInstance().activeCount = DataManager.getInstance().activeList.size();
        }
    }


    /**
     * Listener from SettingsView
     */

    @Override
    public void resetActivities() {
        // Activity reset process;
        new AlertDialog.Builder(this)
                .setTitle("Restore")
                .setMessage("Are you realy want to restore?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveLogFile();
                        resetAll();
                        initConfiguration();
                        initCalendar();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();

        /*saveLogFile();
        resetAll();
        initConfiguration();
//        Calendar calEndTime = Calendar.getInstance();
//        initCalenderMap(calEndTime.getTime());
        initCalendar();*/
    }


    @Override
    public void sendLogFile() {
        saveLogFile();
        Log.d(TAG, "Click on Send Files : " + DataManager.getInstance().lastLog);

        if (Variables.getInstance().emailId.equals("")) {
            Toast.makeText(this, "Please enter your Email address", Toast.LENGTH_SHORT).show();
        } else {
            AndroidNetworking.post("http://64.225.117.112:8082/apis/corey")
                    .addBodyParameter("email", Variables.getInstance().emailId)
                    .addBodyParameter("user_id", Variables.getInstance().user_ID)
                    .addBodyParameter("user_date", Variables.getInstance().user_Dropoff_Date)
                    .addBodyParameter("user_consent", String.valueOf(Variables.getInstance().user_Consent))
                    .addBodyParameter("log_data", String.valueOf(DataManager.getInstance().lastLog))
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "Response: " + response.toString());
                            try {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Email send")
                                        .setMessage(response.getString("message"))
                                        .setPositiveButton("Ok", null)
                                        .show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d(TAG, "ANError: " + anError.getMessage());
                            Toast.makeText(MainActivity.this, anError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }


        /*Thread sendThread = new Thread(new Runnable() {
            @Override
            public void run() {

                Socket socket = null;

                try {
                    socket = new Socket(var.serverIP, Integer.parseInt(var.serverPort));

                    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                    writer.write(DataManager.getInstance().lastLog);
                    writer.flush();
                    writer.close();
                    socket.close();


                } catch (IOException e) {
                    Log.d(TAG, "Send File : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        sendThread.start();*/
    }

    @Override
    public void reloadData() {
        new AlertDialog.Builder(this)
                .setTitle("Reload")
                .setMessage("Are you realy want to reload?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetAll();
                        initConfiguration();
                        initDataLogger();
                        loadSavedObjectState();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void resetAll() {


        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.remove(ACTIVITY_STATE);
        editor.remove(ACTIVE_LIST);
        editor.remove(CALENDAR_MAP);
        editor.commit();


        FileLoader fl = new FileLoader(this);
        fl.deleteExternalFolder(IMAGE_FOLDER);
        fl.deleteExternalFolder(CONFIG_FOLDER);
        fl.deleteExternalFolder(LOGS_FOLDER);
    }
}
