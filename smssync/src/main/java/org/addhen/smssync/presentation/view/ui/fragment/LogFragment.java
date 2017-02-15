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

package org.addhen.smssync.presentation.view.ui.fragment;

import com.addhen.android.raiburari.presentation.ui.fragment.BaseRecyclerViewFragment;
import com.addhen.android.raiburari.presentation.ui.widget.BloatedRecyclerView;

import org.addhen.smssync.R;
import org.addhen.smssync.data.PrefsFactory;
import org.addhen.smssync.data.cache.FileManager;
import org.addhen.smssync.presentation.di.component.LogComponent;
import org.addhen.smssync.presentation.model.LogModel;
import org.addhen.smssync.presentation.model.PhoneStatusInfoModel;
import org.addhen.smssync.presentation.presenter.DeleteLogPresenter;
import org.addhen.smssync.presentation.presenter.ListLogPresenter;
import org.addhen.smssync.presentation.util.Utility;
import org.addhen.smssync.presentation.view.log.DeleteLogView;
import org.addhen.smssync.presentation.view.log.ListLogView;
import org.addhen.smssync.presentation.view.ui.activity.MainActivity;
import org.addhen.smssync.presentation.view.ui.adapter.LogAdapter;
import org.addhen.smssync.presentation.view.ui.widget.DividerItemDecoration;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnCheckedChanged;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class LogFragment extends BaseRecyclerViewFragment<LogModel, LogAdapter>
        implements ListLogView {

    @BindView(android.R.id.list)
    BloatedRecyclerView mLogRecyclerView;

    @BindView(R.id.data_connection_status)
    TextView mDataConnection;

    @BindView(R.id.phone_status_label)
    TextView mPhoneStatusLabel;

    @BindView(R.id.battery_level_status)
    TextView mBatteryLevelStatus;

    @BindView(R.id.log_location)
    TextView mLogLocation;

    @BindView(R.id.start_logs)
    SwitchCompat mStartCheckBox;

    @Inject
    ListLogPresenter mListLogPresenter;

    @Inject
    DeleteLogPresenter mDeleteLogPresenter;

    @Inject
    PrefsFactory mPrefsFactory;

    @Inject
    FileManager mFileManager;

    private LogAdapter mLogAdapter;

    private static LogFragment mLogFragment;

    private int mRemovedItemPosition = 0;

    private LogModel mRemovedLog;

    public LogFragment() {
        super(LogAdapter.class, R.layout.fragment_list_log, R.menu.log_menu);
    }

    public static LogFragment newInstance() {
        return new LogFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    @Override
    public void onResume() {
        super.onResume();
        mListLogPresenter.resume();
        getActivity().registerReceiver(batteryLevelReceiver,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onPause() {
        super.onPause();
        mListLogPresenter.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mListLogPresenter != null) {
            mListLogPresenter.destroy();
        }
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

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.log_entries));
        shareIntent.putExtra(Intent.EXTRA_TEXT, makeShareableMessage());
        return shareIntent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.delete_log_menu) {
            mDeleteLogPresenter.deleteLogs();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initialize() {
        getLogComponent(LogComponent.class).inject(this);
        mListLogPresenter.setView(this);
        initializeDeleteUseCase();
        initRecyclerView();
    }

    private void initRecyclerView() {
        mLogAdapter = new LogAdapter();
        mLogRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mLogRecyclerView.setFocusable(true);
        mLogRecyclerView.setFocusableInTouchMode(true);
        mLogRecyclerView.setAdapter(mLogAdapter);
        mLogRecyclerView.addItemDividerDecoration(getActivity());
        mLogRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mLogRecyclerView.enableDefaultSwipeRefresh(false);
        mLogRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null));
        mStartCheckBox.setChecked(mPrefsFactory.enableLog().get());
    }

    private void initializeDeleteUseCase() {
        mDeleteLogPresenter.setView(new DeleteLogView() {
            @Override
            public void onDeleted(Long row) {
                mListLogPresenter.loadLogs();
            }

            @Override
            public void showError(String s) {
                showSnackbar(getView(), s);
            }

            @Override
            public Context getAppContext() {
                return getActivity().getApplicationContext();
            }
        });
    }

    @Override
    public void showLogs(List<LogModel> logModelList) {
        if (!Utility.isEmpty(logModelList)) {
            mLogAdapter.setItems(logModelList);
        }
    }

    @Override
    public void showLoading() {
        // Do nothing
    }

    @Override
    public void hideLoading() {
        // Do nothing
    }

    @Override
    public void showRetry() {
        // Do nothing
    }

    @Override
    public void hideRetry() {
        // Do nothing
    }

    @Override
    public void showError(String s) {
        Snackbar snackbar = Snackbar.make(getView(), s, Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(getAppContext().getResources().getColor(R.color.red));
        snackbar.show();
    }

    @OnCheckedChanged(R.id.start_logs)
    void onChecked(boolean checked) {
        if (checked) {
            mPrefsFactory.enableLog().set(true);
            mLogLocation.setVisibility(View.VISIBLE);
        } else {
            mPrefsFactory.enableLog().set(false);
            mLogLocation.setVisibility(View.GONE);
        }
    }

    @Override
    public Context getAppContext() {
        return getActivity();
    }

    protected <C> C getLogComponent(Class<C> componentType) {
        return componentType.cast(((MainActivity) getActivity()).getLogComponent());
    }

    private String makeShareableMessage() {

        StringBuilder build = new StringBuilder();

        final String newLine = "\n";

        if ((Utility.getPhoneNumber(getActivity(), mPrefsFactory) != null) && (!TextUtils
                .isEmpty(Utility.getPhoneNumber(getActivity(), mPrefsFactory)))) {
            build.append(getString(R.string.log_message_from,
                    Utility.getPhoneNumber(getActivity(), mPrefsFactory)));
            build.append(newLine);
        }

        build.append(getString(R.string.phone_status));
        build.append(newLine);
        build.append(getString(R.string.battery_level));
        build.append(getString(R.string.battery_level_status, mPrefsFactory.batteryLevel().get()));
        build.append(newLine);
        build.append(getString(R.string.data_connection));
        build.append(getString(R.string.data_connection_status,
                Utility.isConnected(getActivity()) ? getString(R.string.confirm_yes)
                        : getString(R.string.confirm_no)));

        // Get the log entries if they exist
        final String logs = mFileManager.readLogs(FileManager.LOG_NAME);
        if ((logs != null) && (!TextUtils.isEmpty(logs))) {
            build.append(newLine);
            build.append(newLine);
            build.append(getString(R.string.log_entries_below));
            build.append(newLine);
            build.append(logs);
        }

        return build.toString();
    }

    private void setPhoneStatus(PhoneStatusInfoModel info) {
        // Save this in prefs for later retrieval
        // Not elegant but works
        mPrefsFactory.batteryLevel().set(info.getBatteryLevel());
        mBatteryLevelStatus
                .setText(getString(R.string.battery_level_status, info.getBatteryLevel() + "%"));
        final String status = info.isDataConnection() ? getString(R.string.confirm_yes)
                : getString(R.string.confirm_no);
        mDataConnection.setText(getString(R.string.data_connection_status, status));
        mPhoneStatusLabel.setText(info.getPhoneNumber());

        // Set text colors
        if (info.getBatteryLevel() < 30) {
            mBatteryLevelStatus.setTextColor(getResources().getColor(R.color.red));
        }
        if (!info.isDataConnection()) {
            mDataConnection.setTextColor(getResources().getColor(R.color.red));
        }
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
            final PhoneStatusInfoModel info = new PhoneStatusInfoModel();
            info.setBatteryLevel(level);
            info.setDataConnection(Utility.isConnected(getActivity()));
            info.setPhoneNumber(Utility.getPhoneNumber(getActivity(), mPrefsFactory));
            setPhoneStatus(info);
        }
    };
}