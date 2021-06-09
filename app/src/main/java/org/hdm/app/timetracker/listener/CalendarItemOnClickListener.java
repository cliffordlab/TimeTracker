package org.hdm.app.timetracker.listener;

import org.hdm.app.timetracker.util.View_Holder;

/**
 * Created by Hannes on 10.06.2016.
 */
public interface CalendarItemOnClickListener {

    void didOnClick(String time, String s, View_Holder holder);

    void didOnClickAddBtn(View_Holder holder);

    void didOnChangeTitle(String hours, int position);
}
