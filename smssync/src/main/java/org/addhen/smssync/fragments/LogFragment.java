/*******************************************************************************
 *  Copyright (c) 2010 - 2013 Ushahidi Inc
 *  All rights reserved
 *  Contact: team@ushahidi.com
 *  Website: http://www.ushahidi.com
 *  GNU Lesser General Public License Usage
 *  This file may be used under the terms of the GNU Lesser
 *  General Public License version 3 as published by the Free Software
 *  Foundation and appearing in the file LICENSE.LGPL included in the
 *  packaging of this file. Please review the following information to
 *  ensure the GNU Lesser General Public License version 3 requirements
 *  will be met: http://www.gnu.org/licenses/lgpl.html.
 *
 * If you have questions regarding the use of this file, please contact
 * Ushahidi developers at team@ushahidi.com.
 ******************************************************************************/

package org.addhen.smssync.fragments;

import org.addhen.smssync.Prefs;
import org.addhen.smssync.R;
import org.addhen.smssync.adapters.FilterAdapter;
import org.addhen.smssync.adapters.LogAdapter;
import org.addhen.smssync.controllers.LogController;
import org.addhen.smssync.models.Filter;
import org.addhen.smssync.models.Log;
import org.addhen.smssync.models.PhoneStatusInfo;
import org.addhen.smssync.util.ServicesConstants;
import org.addhen.smssync.util.Util;
import org.addhen.smssync.views.ILogView;
import org.addhen.smssync.views.LogView;
import org.addhen.smssync.views.WhitelistView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;

public class LogFragment extends BaseListFragment<LogView, Log, LogAdapter> implements
        View.OnClickListener, AdapterView.OnItemClickListener, ILogView {

    private Log model;

    private LogController mLogController;

    private PhoneStatusInfo info;

    public LogFragment() {
        super(LogView.class, LogAdapter.class, R.layout.list_logs,
                R.menu.log_menu, android.R.id.list);
        model = new Log();
        mLogController = new LogController();
        info = new PhoneStatusInfo();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        listView.setItemsCanFocus(false);
        listView.setOnItemClickListener(this);

        view.enableLogs.setChecked(Prefs.enableWhitelist);
        view.enableLogs.setOnClickListener(this);
        mLogController.setView(this);
    }

    @Override
    public void onResume() {
        log("onResume()");
        super.onResume();
        getActivity().registerReceiver(batteryLevelReceiver,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

    }

    @Override
    public void onDestroy() {
        log("onDestroy()");
        super.onDestroy();
        getActivity().unregisterReceiver(batteryLevelReceiver);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    /**
     * Receiver for getting battery state.
     */
    private BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            context.unregisterReceiver(this);

            int extraLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            int level = -1;

            if (extraLevel >= 0 && scale > 0) {
                level = (extraLevel * 100) / scale;
            }

            info.setBatteryLevel(level);
            info.setDataConnection(Util.isConnected(getActivity()));
            info.setPhoneNumber(Util.getPhoneNumber(getActivity()));
            mLogController.setPhoneStatusInfo(info);
        }
    };

    @Override
    public void setPhoneStatus(PhoneStatusInfo info) {
        view.batteryLevelStatus.setText(getString(R.string.battery_level_status, info.getBatteryLevel()+ "%"));
        final String status = info.isDataConnection() ? getString(R.string.confirm_yes) : getString(R.string.confirm_no);
        view.dataConnection.setText(getString(R.string.data_connection_status, status));
        view.phoneStatusLable.setText(info.getPhoneNumber());
        // Set text colors
        if(info.getBatteryLevel() < 30)
            view.batteryLevelStatus.setTextColor(getResources().getColor(R.color.status_error));
        if(!info.isDataConnection())
            view.dataConnection.setTextColor(getResources().getColor(R.color.status_error));
    }
}
