package org.hdm.app.timetracker.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.hdm.app.timetracker.R;
import org.hdm.app.timetracker.adapter.DialogFoodListAdapter;
import org.hdm.app.timetracker.adapter.DialogPortionListAdapter;
import org.hdm.app.timetracker.datastorage.ActivityObject;
import org.hdm.app.timetracker.datastorage.DataManager;
import org.hdm.app.timetracker.datastorage.Stamp;
import org.hdm.app.timetracker.datastorage.TimeFrame;
import org.hdm.app.timetracker.listener.DialogPortionListOnClickListener;
import org.hdm.app.timetracker.util.FileHandler;
import org.hdm.app.timetracker.util.Logs;
import org.hdm.app.timetracker.util.Variables;
import org.hdm.app.timetracker.util.View_Holder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hannes on 03.06.2016.
 */
public class DialogFoodFragment extends DialogFragment implements DialogPortionListOnClickListener, View.OnLongClickListener {


    private static final String TAG = "DialogFoodFragment";
    private ActivityObject activityObject;
    private DialogFoodListAdapter foodAdapter;
    private RecyclerView recyclerView;

    public Variables var = Variables.getInstance();
    public DataManager dataManager = DataManager.getInstance();

    private String foodCustomFile = "food.json";
    private FileHandler fileHandler = new FileHandler();

    View view;
    private Button btnDialogFood;
    public DialogFoodFragment(){}

    public DialogFoodFragment(ActivityObject activityObject) {
        this.activityObject = activityObject;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_food, container,
                false);
        initLayout();
        return view;
    }

    private void initLayout() {
        getDialog().setTitle("Choose Types of Food");
        String countrySetting = Variables.getInstance().country.toLowerCase();
        foodAdapter = new DialogFoodListAdapter((List) new ArrayList<>(fileHandler.getCustomList(countrySetting, this.foodCustomFile).keySet()));
        foodAdapter.setListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_listt);
        recyclerView.setAdapter(foodAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(
                var.listRows, StaggeredGridLayoutManager.VERTICAL));

        btnDialogFood = (Button) view.findViewById(R.id.btn_dialog);
        btnDialogFood.setVisibility(View.GONE);
        btnDialogFood.setOnLongClickListener(this);
    }

    @Override
    public void didClickOnPortionListItem(String title, View_Holder holder) {

    }

    @Override
    public void didLongClickOnPortionListItem(String title, View_Holder view_holder) {
        Log.i(TAG, "click on Dialog " + title);
        handleObjectClick(title, view_holder);
    }

    private void handleObjectClick(String title, View_Holder view_holder) {
        // get clicked PortionObject
        ActivityObject foodObject = dataManager.getFoodObject(title);
        if(foodObject.activeState){
            foodObject.activeState = false;
            if(activityObject.food.contains(foodObject.title)){
                activityObject.food.remove(foodObject.title);
                activityObject.foodSelection.remove(foodObject.title);
            }
        } else {
            foodObject.activeState = true;
            if(!activityObject.food.contains(foodObject.title)){
                activityObject.food.add(foodObject.title);
                activityObject.foodSelection.add(foodObject.title);
            }
        }
        Log.i(TAG, "DialogFoodFragment handleObjectClick: " + activityObject.food);
        // set Background to green
        view_holder.setBackground(foodObject.activeState);

        Log.d(TAG, "foodObject " + foodObject.title);
        Log.d(TAG, "foodObject a" + activityObject.food);

        if(activityObject.food.size()>=1){
            btnDialogFood.setVisibility(View.VISIBLE);
        } else {
            btnDialogFood.setVisibility(View.GONE);
        }
    }

    private void resetFoodItemState() {
        LinkedHashMap<String, ActivityObject> foodMap = dataManager.getFoodMap();
        for (Map.Entry<String, ActivityObject> entry : foodMap.entrySet()) {
            entry.getValue().activeState = false;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        Log.i(TAG, "**************** onLongClick: ");
        Log.i(TAG, "Here I´m " + dataManager.getActivityObject("Eating + Drinking").food.toString());
        Log.i(TAG, "Here I´m " + dataManager.getActivityObject("Eating + Drinking").portion);
        Log.i(TAG, "Here I´m " + dataManager.getActivityObject("Eating + Drinking").startTime.toString());
        Log.i(TAG, "Here I´m " + dataManager.getActivityObject("Eating + Drinking").endTime.toString());

        // Save Timestamp and SubCategory in ActivityObject
        activityObject.setFoodTimeFrame("user");
        boolean returnSetActivityObject = dataManager.setActivityObject(activityObject);
//        Log.i(TAG, "returnSetActivityObject: " + returnSetActivityObject);
//        Log.i(TAG, "portion and food: " + dataManager.getActivityObject("Eating + Drinking"));

        // Save log file for portion and food
//        Log.i(TAG, "timeFrameList array: " + activityObject.timeFrameList.toString());
        Logs logs =  new Logs();;
        logs.saveStateToLogList(dataManager, "Eating + Drinking");
        logs.saveLogFile("eat_drink");
        Log.i(TAG, "clear foodSelection: ");
        activityObject.foodSelection.clear();

        resetFoodItemState();
        this.dismiss();
        return true;
    }
}