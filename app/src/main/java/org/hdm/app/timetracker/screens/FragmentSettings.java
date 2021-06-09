package org.hdm.app.timetracker.screens;

/**
 * Created by Hannes on 13.05.2016.
 */

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hdm.app.timetracker.R;


/**
 * This fragment representing the front of the card.
 */
public class FragmentSettings extends BaseFragemnt {

    private final String TAG = "FragmentSettings";


    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        initMenu(view);
        initLayout();

        Settings settings = new Settings();

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.fragment_settings, settings).commit();
        }

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        setMenuTitle("Settings");
        setMenuBackground(android.R.color.holo_red_light);
        setMenuBtn(R.drawable.ic_forward);
        menuView.findViewById(R.id.menu_tv).setOnClickListener(null);
        Log.d(TAG, "Settings on Resume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Settings on Pause");

    }


    /*******************
     * Life Cycle Ende
     ***********************/


    /**
     * Init the SettingsLayout
     */
    private void initLayout() {

    }


    /*******************
     * Init Ende
     ***********************/

}