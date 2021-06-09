package org.hdm.app.timetracker.screens;

/**
 * Created by Hannes on 13.05.2016.
 */

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.hdm.app.timetracker.R;
import org.hdm.app.timetracker.adapter.CalendarListAdapter;
import org.hdm.app.timetracker.datastorage.DataManager;
import org.hdm.app.timetracker.datastorage.TimeFrame;
import org.hdm.app.timetracker.listener.CalendarItemOnClickListener;
import org.hdm.app.timetracker.util.Variables;
import org.hdm.app.timetracker.util.View_Holder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import static org.hdm.app.timetracker.util.Consts.DEBUGMODE;


/**
 * A fragment representing the front of the card.
 */
public class FragmentCalender extends BaseFragemnt implements
        CalendarItemOnClickListener,
        View.OnClickListener,
        View.OnLongClickListener {


    private final String TAG = "DayView";
    private final int rows = 1;
    private final DataManager manager = DataManager.getInstance();
    public Variables var = Variables.getInstance();
    private View view;
    private RecyclerView rv_calender;
    private CalendarListAdapter adapter;
    private LinkedHashMap data;
    private TreeMap calendar;
    private FloatingActionButton fab_calendar;
    private int overallXScroll;
    private int lastFirstVisiblePosition = 0;
    private Button btn_settings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calender, container, false);
        initMenu(view);
        initCalenderList();
        initFloatingButton();
        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onResume() {
        super.onResume();
        setMenuTitle("Calender");
        setMenuBackground(android.R.color.holo_blue_light);
        setMenuBtn(R.drawable.ic_back);
        showCalendarIcon();
        if (!var.editable) scrollListToCurrentTime();

        if (var.editableMode) {
            fab_calendar.setVisibility(View.VISIBLE);
        } else {
            fab_calendar.setVisibility(View.GONE);
        }
    }


    @SuppressLint("RestrictedApi")
    private void initFloatingButton() {
        fab_calendar = view.findViewById(R.id.fab_calendar);
        //  fab_calendar.setOnClickListener(this);
        fab_calendar.setOnLongClickListener(this);
        fab_calendar.setVisibility(View.INVISIBLE);
    }

    private void initCalenderList() {
        data = DataManager.getInstance().activityMap;
        calendar = DataManager.getInstance().calenderMap;
        adapter = new CalendarListAdapter((AppCompatActivity) getActivity(), data, calendar);
        Log.d(TAG, "Jsonnnnnn " + "Calendar " + calendar.size());
        Log.d(TAG, "Jsonnnnnn " + "Calendar " + calendar);

        adapter.setListener(this);
        rv_calender = view.findViewById(R.id.rv_calender);
        rv_calender.setAdapter(adapter);
        rv_calender.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_calender.setOnClickListener(this);
        scrollListToCurrentTime();
    }


    @Override
    public void didOnClick(String time, String s, View_Holder holder) {

        if (DEBUGMODE) Log.d(TAG, "holder " + time + " // String " + s + " " + holder.id);

        if (var.editable) {
            // Delete Entry in CalendarMap
            dataManager.deleteCalenderMapEntry(time, s);

            // Delete Entry in ActivityObject TimeFrame
            // ToDo Discuss if the TimeFrameList is realy helpful - better way CalendarMap?
            ArrayList<TimeFrame> list = dataManager.getActivityObject(s).timeFrameList;
            if (DEBUGMODE) Log.d(TAG, "activity " + list.size());
        }
    }


    @Override
    public void didOnClickAddBtn(View_Holder holder) {
        if (DEBUGMODE) Log.d(TAG, "holder " + holder.id);
        var.selectedTime = holder.id;
        listener.flip();
    }

    @Override
    public void didOnChangeTitle(String hours, int position) {
        if (position >= 0 && position <= 48) {
            setMenuTitle(getDate(1));
        } else if (position >= 48 && position <= 96) {
            setMenuTitle(getDate(2));
        } else if (position >= 96 && position <= 144) {
            setMenuTitle(getDate(3));
        }
    }


    // scroll in Calendarlist to current Time
    private void scrollListToCurrentTime() {
        Date currentTime = Calendar.getInstance().getTime();
        int hour = currentTime.getHours();
        if (hour > 2) hour = hour * 2 - 2;
        rv_calender.scrollToPosition(hour);
        setMenuTitle(getDate(1));
        Log.d(TAG, "scrollHour " + hour + " " + calendar.size());
    }

    //Get current date as title show
    private String getDate(int dayCount) {
        DateFormat dfTime = new SimpleDateFormat("dd. EEE");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        if (dayCount == 1) {
            c.add(Calendar.DATE, 0);
        } else if (dayCount == 2) {
            c.add(Calendar.DATE, 1);
        } else if (dayCount == 3) {
            c.add(Calendar.DATE, 2);
        }
        String time = dfTime.format(c.getTime());
        return time;
    }


    // FloatingActionButton Listener
    @Override
    public void onClick(View v) {


        Log.d(TAG, "click " + v.getId());

//        lastFirstVisiblePosition = ((LinearLayoutManager) rv_calender.getLayoutManager()).findFirstVisibleItemPosition();
//
//        if (var.editable) {
//            var.editable = false;
//            fab_calendar.setImageResource(android.R.drawable.ic_menu_edit);
//
//        } else {
//            var.editable = true;
//            fab_calendar.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
//        }
//        // Invalidate new
//        adapter.notifyDataSetChanged();
    }


    @Override
    public boolean onLongClick(View v) {
        lastFirstVisiblePosition = ((LinearLayoutManager) rv_calender.getLayoutManager()).findFirstVisibleItemPosition();

        if (var.editable) {
            var.editable = false;
            fab_calendar.setImageResource(android.R.drawable.ic_menu_edit);

        } else {
            var.editable = true;
            fab_calendar.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        }
        // Invalidate new
        adapter.notifyDataSetChanged();
        return true;
    }
}