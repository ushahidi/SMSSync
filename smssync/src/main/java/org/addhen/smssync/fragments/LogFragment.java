/*
 * Copyright (c) 2010 - 2015 Ushahidi Inc
 * All rights reserved
 * Contact: team@ushahidi.com
 * Website: http://www.ushahidi.com
 * GNU Lesser General Public License Usage
 * This file may be used under the terms of the GNU Lesser
 * General Public License version 3 as published by the Free Software
 * Foundation and appearing in the file LICENSE.LGPL included in the
 * packaging of this file. Please review the following information to
 * ensure the GNU Lesser General Public License version 3 requirements
 * will be met: http://www.gnu.org/licenses/lgpl.html.
 *
 * If you have questions regarding the use of this file, please contact
 * Ushahidi developers at team@ushahidi.com.
 */

package org.addhen.smssync.fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.squareup.otto.Subscribe;

import org.addhen.smssync.App;
import org.addhen.smssync.R;
import org.addhen.smssync.adapters.LogAdapter;
import org.addhen.smssync.controllers.LogController;
import org.addhen.smssync.models.Log;
import org.addhen.smssync.models.PhoneStatusInfo;
import org.addhen.smssync.prefs.Prefs;
import org.addhen.smssync.state.LogEvent;
import org.addhen.smssync.util.LogUtil;
import org.addhen.smssync.util.Util;
import org.addhen.smssync.views.ILogView;
import org.addhen.smssync.views.LogView;

public class LogFragment extends BaseListFragment<LogView, Log, LogAdapter> implements
        View.OnClickListener, AdapterView.OnItemClickListener, ILogView {

    private static PhoneStatusInfo info;
    private LogController mLogController;
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

    public LogFragment() {
        super(LogView.class, LogAdapter.class, R.layout.list_logs,
                R.menu.log_menu, android.R.id.list);
        mLogController = new LogController();
        info = new PhoneStatusInfo();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        listView.setItemsCanFocus(false);
        listView.setOnItemClickListener(this);
        view.enableLogs.setChecked(prefs.enableLog().get());
        view.enableLogs.setOnClickListener(this);
        mLogController.setView(this);
    }

    @Override
    public void onResume() {
        log("onResume()");
        super.onResume();
        App.bus.register(this);
        getActivity().registerReceiver(batteryLevelReceiver,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        loadLogs();
    }

    @Override
    public void onPause() {
        log("onStop()");
        super.onPause();
        App.bus.unregister(this);
    }

    @Override
    public void onClick(View v) {

        if (view.enableLogs.isChecked()) {
            prefs.enableLog().set(true);
            view.enableLogs.setChecked(true);
            view.logLcation.setVisibility(View.VISIBLE);
        } else {
            prefs.enableLog().set(false);
            view.enableLogs.setChecked(false);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem actionItem = menu.findItem(R.id.share_menu);
        ShareActionProvider actionProvider = (ShareActionProvider) MenuItemCompat
                .getActionProvider(actionItem);
        actionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
        actionProvider.setShareIntent(createShareIntent());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.delete_log_menu) {
            // load all blacklisted phone numbers
            performDelete();
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void reloadLog(LogEvent event) {
        adapter.setItems(LogUtil.readLogFile(LogUtil.LOG_NAME));

        // Set the location of the log file
        if (adapter.getCount() > 0) {
            view.logLcation.setText(getString(R.string.log_saved_at,
                    LogUtil.getFile(LogUtil.LOG_NAME).getAbsolutePath()));
            view.logLcation.setVisibility(View.VISIBLE);
        }
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.log_entries));
        shareIntent.putExtra(Intent.EXTRA_TEXT, makeShareableMessage());
        return shareIntent;
    }

    public PhoneStatusInfo getPhoneStatus() {
        return info;
    }

    @Override
    public void setPhoneStatus(PhoneStatusInfo info) {
        // Save this in prefs for later retrieval
        // Not elegant but works
        prefs.batteryLevel().set(info.getBatteryLevel());
        view.batteryLevelStatus
                .setText(getString(R.string.battery_level_status, info.getBatteryLevel() + "%"));
        final String status = info.isDataConnection() ? getString(R.string.confirm_yes)
                : getString(R.string.confirm_no);
        view.dataConnection.setText(getString(R.string.data_connection_status, status));
        view.phoneStatusLable.setText(info.getPhoneNumber());

        // Set text colors
        if (info.getBatteryLevel() < 30) {
            view.batteryLevelStatus.setTextColor(getResources().getColor(R.color.status_error));
        }
        if (!info.isDataConnection()) {
            view.dataConnection.setTextColor(getResources().getColor(R.color.status_error));
        }
    }

    private void loadLogs() {
        adapter.setItems(LogUtil.readLogFile(LogUtil.LOG_NAME));

        // Set the location of the log file
        if (adapter.getCount() > 0) {
            view.logLcation.setText(getString(R.string.log_saved_at,
                    LogUtil.getFile(LogUtil.LOG_NAME).getAbsolutePath()));
            view.logLcation.setVisibility(View.VISIBLE);
        }
    }

    private void deleteLogs() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                LogUtil.deleteLog(LogUtil.LOG_NAME);
                adapter.setItems(LogUtil.readLogFile(LogUtil.LOG_NAME));
                view.logLcation.setVisibility(View.GONE);
            }
        });
    }

    private void performDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.confirm_message))
                .setCancelable(false)
                .setNegativeButton(getString(R.string.confirm_no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setPositiveButton(getString(R.string.confirm_yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // delete all messages
                                deleteLogs();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private String makeShareableMessage() {

        // On some devices this is never initialized.
        if (prefs == null) {
            prefs = new Prefs(getActivity());
        }

        StringBuilder build = new StringBuilder();

        final String newLine = "\n";

        if ((Util.getPhoneNumber(getActivity()) != null) && (!TextUtils
                .isEmpty(Util.getPhoneNumber(getActivity())))) {
            build.append(getString(R.string.log_message_from, Util.getPhoneNumber(getActivity())));
            build.append(newLine);
        }

        build.append(getString(R.string.phone_status));
        build.append(newLine);
        build.append(getString(R.string.battery_level));
        build.append(getString(R.string.battery_level_status, prefs.batteryLevel().get()));
        build.append(newLine);
        build.append(getString(R.string.data_connection));
        build.append(getString(R.string.data_connection_status,
                Util.isConnected(getActivity()) ? getString(R.string.confirm_yes)
                        : getString(R.string.confirm_no)));

        // Get the log entries if they exist
        final String logs = LogUtil.readLogs(LogUtil.LOG_NAME);

        if ((logs != null) && (!TextUtils.isEmpty(logs))) {
            build.append(newLine);
            build.append(newLine);
            build.append(getString(R.string.log_entries_below));
            build.append(newLine);
            build.append(logs);
        }

        return build.toString();
    }
}
