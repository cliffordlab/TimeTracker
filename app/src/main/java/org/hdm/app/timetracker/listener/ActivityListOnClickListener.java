package org.hdm.app.timetracker.listener;

import org.hdm.app.timetracker.util.View_Holder;

/**
 * Created by Hannes on 10.06.2016.
 */
public interface ActivityListOnClickListener {
    void didClickOnActivityListItem(String title, View_Holder holder);

    void didLongClickOnActivityListItem(String title, View_Holder view_holder);

}
