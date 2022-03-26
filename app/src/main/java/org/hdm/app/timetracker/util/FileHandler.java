package org.hdm.app.timetracker.util;

import static org.hdm.app.timetracker.util.Consts.CONFIG_FOLDER;

import android.os.Environment;
import android.util.Log;

import org.hdm.app.timetracker.datastorage.ActivityObject;
import org.hdm.app.timetracker.datastorage.DataManager;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileHandler {
    private final String TAG = "FileHandler";
    private DataManager dataManager = DataManager.getInstance();

    private MyJsonParser jParser = new MyJsonParser();
    private FileLoader fileLoader = new FileLoader();
    private Map<String, ActivityObject> map = new LinkedHashMap<>();
    private String folder = CONFIG_FOLDER;

    public FileHandler() {}

    // Read custom countries.json, food.json and portions.json
    public LinkedHashMap<String, ActivityObject> getCustomList(String selectedCountry, String customFile) {
        Log.i(TAG, "FileHandler");

        LinkedHashMap<String, ActivityObject> pictureKeySet = new LinkedHashMap<>();

        // Read custom country json file
        Log.i(TAG, "Read " + customFile + " file from device");
        String folderPath = Environment.getExternalStorageDirectory() + "/" + folder;
        String jsonString = fileLoader.readStringFromExternalFolder(folderPath, customFile);

        Pattern dataRegex = Pattern.compile("(\"([^\"]*)\").(\\[([^\"]*)\\])");
        Matcher m = dataRegex.matcher(jsonString.replaceAll("\\s+", ""));
        String selectedCountryValue = "";
        while (m.find()) {
            // Check with the selected country in setting
            if (m.group(2).equals(selectedCountry)) {
                selectedCountryValue = m.group(4);
                break;
            }
        }

        // Convert string to array
        String[] cPictureNumberList = selectedCountryValue.split(",");
        Log.i(TAG, "str size: " + cPictureNumberList.length);

        // Check matched countries and activity
        switch (customFile) {
            case "countries.json":
                map = dataManager.getInstance().getObjectMap();
                break;
            case "portions.json":
                map = dataManager.getInstance().getPortionMap();
                break;
            case "food.json":
                map = dataManager.getInstance().getFoodMap();
                break;
        }

        for (ActivityObject aObj : map.values()) {
            String activityId = aObj._id;
            boolean contains = Arrays.stream(cPictureNumberList).anyMatch(activityId::equals);
            if (contains) {
                pictureKeySet.put(aObj.title, aObj);
            }
        }
        return pictureKeySet;
    }
}
