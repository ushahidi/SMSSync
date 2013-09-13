package org.addhen.smssync.fragments;

import org.addhen.smssync.Prefs;
import org.addhen.smssync.R;
import org.addhen.smssync.adapters.FilterAdapter;
import org.addhen.smssync.adapters.LogAdapter;
import org.addhen.smssync.models.Filter;
import org.addhen.smssync.models.Log;
import org.addhen.smssync.views.LogView;
import org.addhen.smssync.views.WhitelistView;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;

public class LogFragment extends BaseListFragment<LogView, Log, LogAdapter> implements
        View.OnClickListener, AdapterView.OnItemClickListener {

    Log model;
    public LogFragment() {
        super(LogView.class, LogAdapter.class, R.layout.list_logs,
                R.menu.log_menu, android.R.id.list);
        model = new Log();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        listView.setItemsCanFocus(false);
        listView.setOnItemClickListener(this);

        view.enableLogs.setChecked(Prefs.enableWhitelist);
        view.enableLogs.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
