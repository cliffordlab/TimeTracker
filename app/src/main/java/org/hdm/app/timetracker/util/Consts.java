 package org.hdm.app.timetracker.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Hannes on 25.06.2016.
 */
public final class Consts {

    public final static boolean DEBUGMODE = true;
    public final static int CALENDARITEMROW = 1;

    public final static String JSONFILE = "standard.json";
    public final static String TEMPACTIVITIES = "temp-activities.json";
    public final static String PARENTPATH = "Rwandapp";
    public final static String IMAGEPATH = "Images";
    public final static String CONFIGPATH = "Config";
    public final static String LOGPATH = "Logs";


    public final static String MAINFOLDER = "mainFolder";
    public final static String IMAGEFOLDER = "imageFolder";
    public final static String CONFIGFOLDER = "configFolder";
    public final static String LOGFOLDER = "logFolder";

    public final static String PROPERTIESFILE = "config.properties";


    public final static String MAIN_FOLDER = "RwandaApp/";
    public final static String IMAGE_FOLDER = MAIN_FOLDER + "Images/";
    public final static String CONFIG_FOLDER = MAIN_FOLDER + "Config/";
    public final static String LOGS_FOLDER = MAIN_FOLDER + "Logs/";

    // Name from the Json Object
    public final static String ACTIVITIES = "activitys";
    public final static String PORTIONS = "portions";
    public final static String FOOD = "food";


    public final static String ACTIVITY_STATE = "ActivityState";
    public final static String ACTIVE_LIST = "ActiveList";
    public final static String CALENDAR_MAP = "CalendarMap";

    //API URL
    public final static String BASE_URL = "http://13.57.155.198:8082/apis/";
    public final static String SEND_MAIL_URL =  BASE_URL + "corey";

    //Image Url
    public final static String BASE_IMAGE_URL = "https://timetracker2021.s3.us-west-1.amazonaws.com/";
    public final static String BASE_ACTIVITY_URL = "https://timetracker2021.s3.us-west-1.amazonaws.com/activity/";

    /**
     * The caller references the constants using <tt>Consts.EMPTY_STRING</tt>,
     * and so on. Thus, the caller should be prevented from constructing objects of
     * this class, by declaring this private constructor.
     */
    private Consts() {
        //this prevents even the native class from
        //calling this ctor as well :
        throw new AssertionError();
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected() && netInfo.isAvailable())
            return true;

        return false;
    }
}
