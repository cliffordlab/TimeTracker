package org.hdm.app.timetracker.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.text.style.IconMarginSpan;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Preconditions;

import org.hdm.app.timetracker.datastorage.ActivityObject;
import org.hdm.app.timetracker.datastorage.DataManager;
import org.hdm.app.timetracker.main.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

import static org.hdm.app.timetracker.util.Consts.ACTIVITIES;
import static org.hdm.app.timetracker.util.Consts.CONFIG_FOLDER;
import static org.hdm.app.timetracker.util.Consts.DEBUGMODE;
import static org.hdm.app.timetracker.util.Consts.FOOD;
import static org.hdm.app.timetracker.util.Consts.IMAGEFOLDER;
import static org.hdm.app.timetracker.util.Consts.IMAGE_FOLDER;
import static org.hdm.app.timetracker.util.Consts.LOGS_FOLDER;
import static org.hdm.app.timetracker.util.Consts.MAIN_FOLDER;
import static org.hdm.app.timetracker.util.Consts.PORTIONS;
import static org.hdm.app.timetracker.util.Consts.PROPERTIESFILE;


/**
 * Created by Hannes on 09.05.2016.
 */
public class FileLoader {

    private static final String TAG = "FileLoader";
    private final File enviroment = Environment.getExternalStorageDirectory();
    private final Context context;
    String state = Environment.getExternalStorageState();
    private Properties properties;


    /**************************
     * Constructor
     *************************/
    public FileLoader(MainActivity mainActivity) {
        context = mainActivity;
    }


    /**************************
     * Init File Prozess
     *************************/

    public void initFiles() {


        initFolder();
        initConfiguration();
        initJson();

    }


    public void initFolder() {

        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+ MAIN_FOLDER);
        if (!folder.exists()){
            folder.mkdirs();
        }

        if (folder.exists()){
            File imageFolder = new File(Environment.getExternalStorageDirectory() + File.separator + IMAGE_FOLDER);
            imageFolder.mkdir();

            File confiFolder = new File(Environment.getExternalStorageDirectory() + File.separator + CONFIG_FOLDER);
            confiFolder.mkdir();

            File logFolder = new File(Environment.getExternalStorageDirectory() + File.separator + LOGS_FOLDER);
            logFolder.mkdir();
        }else {
            Log.d(TAG, MAIN_FOLDER + "Folder not Create");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                folder = new File(context.getFilesDir(), MAIN_FOLDER);
                folder.mkdir();
                if (!folder.exists()){
                    Log.d(TAG, "Folder Created");
                }else {
                  Log.d(TAG, "Folder not Created");
                }
            }
        }

        //createExternalFolder(MAIN_FOLDER);
        //createExternalFolder(IMAGE_FOLDER);
        //createExternalFolder(CONFIG_FOLDER);
        //createExternalFolder(LOGS_FOLDER);
    }


    public void initConfiguration() {

        String path = enviroment + "/" + CONFIG_FOLDER;

        if (!isExternalFileExists(path + PROPERTIESFILE)) {
            copyFileFromAssetToExternal(PROPERTIESFILE, path);
        }

        Properties properties = getPropertiesFromExternal(path + PROPERTIESFILE);

//        Log.d(TAG, "variables "
//                + Variables.getInstance().user_ID + " // "
//                + Variables.getInstance().setup + " // "
//                + Variables.getInstance().maxRecordedActivity + " // "
//                + Variables.getInstance().listRows + " // "
//                + Variables.getInstance().vibrationTime + " // "
//                + Variables.getInstance().notificationPeriode + " // "
//        );

        if (properties.get("user_ID") != null) {
            Variables.getInstance().user_ID = (String) properties.get("user_ID");
        }

        if (properties.get("user_Dropoff_Date") != null) {
            Variables.getInstance().user_Dropoff_Date = (String) properties.get("user_Dropoff_Date");
        }

        if (properties.get("user_Consent") != null) {
            Variables.getInstance().user_Consent = (Boolean) properties.get("user_Consent");
        }

        if (properties.get("setup") != null) {
            Variables.getInstance().setup = (String) properties.get("setup");
        }

        if (properties.get("maxRecordedActivity") != null) {
            try {
                int value = Integer.parseInt((String) properties.get("maxRecordedActivity"));
                Variables.getInstance().maxRecordedActivity = value;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (properties.get("listRows") != null) {

            try {
                int value = Integer.parseInt((String) properties.get("listRows"));
                Variables.getInstance().listRows = value;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }


    }


    private void initJson() {

        String path = enviroment + "/" + CONFIG_FOLDER;

        // Copy all Json files from Intern to External Folder if they not exist
        String fileName = "activity.json";
        if (!isExternalFileExists(path + fileName)) {
            copyFileFromAssetToExternal(fileName, path);
        }
        loadActivityObjects(ACTIVITIES, path, fileName);
        loadActivityObjects(PORTIONS, path, fileName);
        loadActivityObjects(FOOD, path, fileName);


    }


    /**************************
     * Assets
     *************************/


    public String readFromAssets(Context context, String filename) {

        BufferedReader reader = null;
        StringBuilder sb = null;
        String mLine = null;

        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(filename)));
            sb = new StringBuilder();
            mLine = reader.readLine();
            while (mLine != null) {
                sb.append(mLine); // process line
                mLine = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return sb.toString();
    }


    private String readStringFromExternalFolder(String folderPath, String fileName) {

        if (!isExternalFileExists(folderPath + fileName)) return null;

        BufferedReader reader;
        StringBuilder sb;
        String mLine;
        File file = new File(folderPath, fileName);

        try {

            FileInputStream fileInputStream = new FileInputStream(file);
            reader = new BufferedReader(
                    new InputStreamReader(fileInputStream));
            sb = new StringBuilder();
            mLine = reader.readLine();
            while (mLine != null) {
                sb.append(mLine); // process line
                mLine = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return sb.toString();

    }


    public boolean copyFileFromAssetToExternal(String fileName, String path) {
        Log.e("File Name : ", fileName);
        Log.e("path : ", path);

        if (fileName != null && path != null) {
            InputStream in = null;
            OutputStream out = null;

            try {
                in = context.getAssets().open(fileName);
                File outFile = new File(path, fileName);
                out = new FileOutputStream(outFile);

                copyFile(in, out);

            } catch (IOException e) {
                Log.d(TAG, "Failed to copy asset file: " + fileName, e);
                return false;
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                if (out != null) {
                    try {
                        out.flush();
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }
        }
        return true;
    }


    public boolean CopyImagesFromResourceToExternal(int[] resources) {

        if (resources.length != 0) {
            String imageFolder = getPropertiesFromAssets(PROPERTIESFILE)
                    .getProperty(IMAGEFOLDER);
            for (int i = 0; i < resources.length; i++) {
                String fileName = enviroment + "/" + imageFolder + "/" +
                        context.getResources().getResourceEntryName(resources[i]) +
                        ".png";
                Bitmap bm = BitmapFactory.decodeResource(context.getResources(), resources[i]);
                try {
                    FileOutputStream out = new FileOutputStream(fileName);
                    bm.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    public Drawable getDrawableFromPath(String filePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        //Here you can make logic for decode bitmap for ignore oom error.
        return new BitmapDrawable(bitmap);
    }


    /**************************
     * Create External Folder
     *************************/
    public String createExternalFolder(String folderName) {

        if (!isExternalStorageWritable()) {
            Toast.makeText(context, " External Storage is not writeble -" +
                    "folder could not be created", Toast.LENGTH_SHORT).show();
            return null;
        }

        File f = new File(enviroment, folderName);
        if (!f.exists()) {
            f.mkdirs();
            if (f.exists()) return f.toString();
        }
//        Toast.makeText(context, folderName + " already exists", Toast.LENGTH_SHORT).show();
        return null;
    }

    public String deleteExternalFolder(String folderName) {

        if (!isExternalStorageWritable()) {
            Toast.makeText(context, " External Storage is not writeble -" +
                    "folder could not be created", Toast.LENGTH_SHORT).show();
            return null;
        }

        File f = new File(enviroment, folderName);
        if (f.exists()){
            if (f.isDirectory()){
                for (File file: f.listFiles()) {
                    file.delete();
                }
            }
            f.delete();
        }
        //f.mkdirs();

        return f.toString();

    }


    /**************************
     * Property File
     *************************/


    public Properties getPropertiesFromAssets(String file) {

        properties = new Properties();

        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(file);
            properties.load(inputStream);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            return null;
        }
        return properties;
    }


    public Properties getPropertiesFromExternal(String file) {

        Properties properties = new Properties();
        try {
            InputStream input = new FileInputStream(file);
            properties.load(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return properties;
    }


    // Load Content
    public void loadActivityObjects(String object, String folderPath, String fileName) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ALPHA_8;
        options.inSampleSize = 2; //reduce quality

        Log.d(TAG, "listtttt" + fileName + " " + folderPath);

        // Check if Json File is in External Folder
        // if not than copy Json file from Asset to external Folder
        if (!isExternalFileExists(folderPath + fileName)) {
            copyFileFromAssetToExternal(fileName, folderPath);
        }


        if (DEBUGMODE) Log.d(TAG, "loadActivityObjects " + folderPath + fileName);


        // Read out JsonFile from External Folder
        String jsonString = readStringFromExternalFolder(folderPath, fileName);
        if (DEBUGMODE) Log.d(TAG, "jasonString " + jsonString);

        MyJsonParser jParser = new MyJsonParser();
        ArrayList<ActivityObject> list = jParser.createObjectFromJson(object, jsonString);


        if (list == null) {
            jsonString = readFromAssets(context, "activity.json");
            list = jParser.createObjectFromJson(object, jsonString);
        }


        String imgPath = enviroment.toString() + "/" + IMAGE_FOLDER;

        if (list != null) {

            for (int i = 0; i < list.size(); i++) {
                ActivityObject activityObject = list.get(i);

                Log.d(TAG, "imageName " + activityObject.imageName);
                String objectImgPath = imgPath + activityObject.imageName;

                // check if Image is in externalFolder available
                // if not than save it from asset to external load again
                if (!isExternalFileExists(objectImgPath)) {
                    // Save Image from Asset to External
                    copyFileFromAssetToExternal(activityObject.imageName, imgPath);
                }

                DataManager.getInstance().imageMap.put(
                        activityObject.imageName,
                        BitmapFactory.decodeFile(objectImgPath, options));


                switch (object) {
                    case ACTIVITIES:
                        DataManager.getInstance().setActivityObject(activityObject);
                        break;

                    case PORTIONS:
                        DataManager.getInstance().setPortionObject(activityObject);
                        break;
                    case FOOD:
                        DataManager.getInstance().setFoodObject(activityObject);
                        break;

                    default:
                        break;
                }

                ActivityObject o = DataManager.getInstance().getActivityObject(activityObject.title);

                if (o != null) {
                    Log.d(TAG, "sizzeee " + o.imageName + " " + o.title);
                }
            }
        }
    }


    /**************************
     * File Permission Check
     *************************/


    public boolean isExternalStorageWritable() {
        return (Environment.MEDIA_MOUNTED.equals(state));
    }


    public boolean isExternalFileExists(String filePath) {
        File f = new File(filePath);
        return f.exists();
    }


    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }


    public void saveLogsOnExternal(String fileName) {

//        MyJsonParser parser = new MyJsonParser();
//        String logFile = parser.createLogJsonFromActivityObjects();

        String path = enviroment.toString() + "/" + LOGS_FOLDER;
        Gson gson = new Gson();
        String s = gson.toJson(DataManager.getInstance().logList);
        writeStringOnExternal(s, fileName, path);
        DataManager.getInstance().lastLog = s;
        Log.d(TAG, "logFile " + s);
    }


    private void writeStringOnExternal(String stringFile, String fileName, String path) {

        if (stringFile != null && fileName != null && path != null) {

            File f = new File(path);
            if (!f.exists()) {
                initFolder();
            }


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
