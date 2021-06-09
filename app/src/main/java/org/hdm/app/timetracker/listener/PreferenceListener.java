package org.hdm.app.timetracker.listener;

/**
 * Created by Hannes on 27.05.2016.
 */
public interface PreferenceListener {
    void resetActivities();

    void sendLogFile();

    void reloadData();

}
