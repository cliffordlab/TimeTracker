package org.hdm.app.timetracker.util;

import static org.hdm.app.timetracker.util.Consts.LOGS_FOLDER;

import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import org.hdm.app.timetracker.datastorage.ActivityObject;
import org.hdm.app.timetracker.datastorage.DataManager;
import org.hdm.app.timetracker.datastorage.Stamp;
import org.hdm.app.timetracker.datastorage.TimeFrame;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Hannes on 30.07.2016.
 */
public class Logs {
    public String title;
    public String _id;
    public List<TimeStamp> timeStamps = new ArrayList<>();
    public Variables var = Variables.getInstance();

    private Stamp stamp;
    private String TAG = "Logs";
    private DataManager dataManager;
    private File enviroment = Environment.getExternalStorageDirectory();

    public void saveStateToLogList(DataManager dataManager, String title) {
        this.dataManager = dataManager;
        ActivityObject activityObject = dataManager.getActivityObject(title);

        stamp = new Stamp();
        stamp.user = var.user_ID;
        stamp.activity = activityObject.title;
        stamp.date = Calendar.getInstance().getTime().toString();

        if(activityObject.title.equals("Eating + Drinking")) {
            TimeFrame timeFrame = activityObject.foodTimeFrame;
            stamp.startTime = timeFrame.startTime.toString();
            stamp.endTime = timeFrame.endTime.toString();
            stamp.time = String.valueOf(timeFrame.endTime.getTime() - timeFrame.startTime.getTime());
            stamp.contractWork = activityObject.service;
            stamp.author = "user";
            stamp.delete = "no";
            Log.i(TAG, "saveStateToLogList food: " + activityObject.foodSelection.toString());
            stamp.portion = timeFrame.portion + activityObject.foodSelection.toString();
        } else {
            stamp.startTime = activityObject.startTime.toString();
            stamp.endTime = activityObject.endTime.toString();
            stamp.time = String.valueOf(activityObject.endTime.getTime() - activityObject.startTime.getTime());
            stamp.contractWork = activityObject.service;
            stamp.author = "user";
            stamp.delete = "no";
            stamp.portion = "";
        }

        this.dataManager.logList.add(stamp);
        Log.i(TAG, "Log stamp: " + dataManager.lastLog);
        Log.i(TAG, "Log stamp: " + stamp.toString());
    }

    public void saveLogFile(String name) {
        String currentDate = Calendar.getInstance().getTime().toString();
        int size = currentDate.length();
        String year = currentDate.substring(size - 4, size);
        String date = currentDate.substring(4, 19);
        String fileName = var.user_ID + "_" + Variables.getInstance().country + "_" + year + "_" + date + "_" + name + ".txt";
        fileName = fileName.replaceAll(" ", "_");
        Log.d(TAG, "currentDate " + fileName);
        saveLogsOnExternal(fileName);
    }

    private void saveLogsOnExternal(String fileName) {
        String path = enviroment.toString() + "/" + LOGS_FOLDER;
        Gson gson = new Gson();
        String s = gson.toJson(stamp);
        writeStringOnExternal(s, fileName, path);
        DataManager.getInstance().lastLog = s;
        Log.d(TAG, "logFile " + s);
    }

    private void writeStringOnExternal(String stringFile, String fileName, String path) {
        if (stringFile != null && fileName != null && path != null) {
            File file = new File(path, fileName);
            FileOutputStream stream = null;

            try {
                stream = new FileOutputStream(file);
                stream.write(stringFile.getBytes());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
