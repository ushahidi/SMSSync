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
import com.addhen.android.raiburari.presentation.ui.listener.SwipeToDismissTouchListener;
import com.addhen.android.raiburari.presentation.ui.widget.BloatedRecyclerView;
import com.addhen.android.raiburari.presentation.ui.widget.DividerItemDecoration;
import com.nineoldandroids.view.ViewHelper;

import org.addhen.smssync.R;
import org.addhen.smssync.data.PrefsFactory;
import org.addhen.smssync.presentation.di.component.LogComponent;
import org.addhen.smssync.presentation.model.LogModel;
import org.addhen.smssync.presentation.model.PhoneStatusInfoModel;
import org.addhen.smssync.presentation.presenter.ListLogPresenter;
import org.addhen.smssync.presentation.util.Utility;
import org.addhen.smssync.presentation.view.log.ListLogView;
import org.addhen.smssync.presentation.view.ui.activity.MainActivity;
import org.addhen.smssync.presentation.view.ui.adapter.LogAdapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class LogFragment extends BaseRecyclerViewFragment<LogModel, LogAdapter>
        implements
        ListLogView {

    @Bind(android.R.id.list)
    BloatedRecyclerView mLogRecyclerView;

    @Bind(R.id.data_connection_status)
    TextView mDataConnection;

    @Bind(R.id.phone_status_label)
    TextView mPhoneStatusLabel;

    @Bind(R.id.battery_level_status)
    TextView mBatteryLevelStatus;

    @Bind(R.id.log_location)
    TextView mLogLocation;

    @Inject
    ListLogPresenter mListLogPresenter;

    @Inject
    PrefsFactory mPrefs;

    private LogAdapter mLogAdapter;

    private static LogFragment mLogFragment;

    private int mRemovedItemPosition = 0;

    private LogModel mRemovedLog;

    public LogFragment() {
        super(LogAdapter.class, R.layout.fragment_list_log, 0);
    }

    public static LogFragment newInstance() {
        if (mLogFragment == null) {
            mLogFragment = new LogFragment();
        }
        return mLogFragment;
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
        mListLogPresenter.destroy();
    }

    private void initialize() {
        getLogComponent(LogComponent.class).inject(this);
        mListLogPresenter.setView(this);
        initRecyclerView();
    }

    private void initRecyclerView() {
        mLogAdapter = new LogAdapter();
        mLogRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mLogRecyclerView.setFocusable(true);
        mLogRecyclerView.setFocusableInTouchMode(true);
        mLogAdapter.setHasStableIds(true);
        mLogRecyclerView.setAdapter(mLogAdapter);
        mLogRecyclerView.addItemDividerDecoration(getActivity());
        mLogRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mLogRecyclerView.addItemDecoration(
                new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL_LIST));
        mLogRecyclerView.setSwipeToDismissCallback(
                new SwipeToDismissTouchListener.DismissCallbacks() {
                    @Override
                    public SwipeToDismissTouchListener.SwipeDirection canDismiss(int position) {
                        return SwipeToDismissTouchListener.SwipeDirection.BOTH;
                    }

                    @Override
                    public void onDismiss(RecyclerView view,
                            List<SwipeToDismissTouchListener.PendingDismissData> dismissData) {
                        // Implement swipe to delete
                    }
                });
        mLogRecyclerView.enableDefaultSwipeRefresh(true);
    }

    private void drawSwipeListItemBackground(Canvas c, int dX, View itemView, int actionState) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Fade out the view as it is swiped out of the parent's bounds
            final float alpha = 1.0f - Math.abs(dX) / (float) itemView.getWidth();
            ViewHelper.setAlpha(itemView, alpha);
            ViewHelper.setTranslationX(itemView, dX);
            Drawable d;
            // Swiping right
            if (dX > 0) {
                d = ContextCompat
                        .getDrawable(getAppContext(),
                                R.drawable.swipe_right_publish_list_item_background);
                d.setBounds(itemView.getLeft(), itemView.getTop(), dX, itemView.getBottom());
            } else { // Swiping left
                d = ContextCompat
                        .getDrawable(getAppContext(),
                                R.drawable.swipe_left_publish_list_item_background);
                d.setBounds(itemView.getRight() + dX, itemView.getTop(), itemView.getRight(),
                        itemView.getBottom());
            }
            d.draw(c);
        }
    }

    private void remove(int position) {
        mRemovedItemPosition = position;
        mRemovedLog = mLogAdapter.getItem(position);
        mLogAdapter.removeItem(mRemovedLog);
        showUndoSnackbar(1);
    }

    private void enableSwipeToPerformAction() {
        // Swiping doesn't work well on API 11 and below because the android support lib ships
        // with buggy APIs that makes it hard to implement on older devices.
        // See http://b.android/181858
        ItemTouchHelper.SimpleCallback swipeToDismiss = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                    RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                remove(viewHolder.getAdapterPosition());
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView,
                    RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState,
                    boolean isCurrentlyActive) {
                drawSwipeListItemBackground(c, (int) dX, viewHolder.itemView, actionState);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState,
                        isCurrentlyActive);
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                ViewHelper.setAlpha(viewHolder.itemView, 1.0f);
                viewHolder.itemView.setBackgroundColor(0);
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDismiss);
        itemTouchHelper.attachToRecyclerView(mLogRecyclerView.recyclerView);
    }

    private void showUndoSnackbar(int count) {
        Snackbar snackbar = Snackbar
                .make(getView(), getString(R.string.item_deleted, count), Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.undo, v -> {
            // Restore item
            mLogAdapter.addItem(mRemovedLog, mRemovedItemPosition);
        });
        snackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                    // TODO: Perform deletion
                    //mDeleteWebServicePresenter.deleteWebService(mRemovedWebServiceModel._id);
                }
            }
        });
        snackbar.show();
    }

    @Override
    public void showLogs(List<LogModel> logModelList) {
        if (Utility.isEmpty(logModelList)) {
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

    @Override
    public Context getAppContext() {
        return getActivity().getApplicationContext();
    }

    protected <C> C getLogComponent(Class<C> componentType) {
        return componentType.cast(((MainActivity) getActivity()).getLogComponent());
    }

    private String makeShareableMessage() {

        StringBuilder build = new StringBuilder();

        final String newLine = "\n";

        if ((Utility.getPhoneNumber(getActivity(), mPrefs) != null) && (!TextUtils
                .isEmpty(Utility.getPhoneNumber(getActivity(), mPrefs)))) {
            build.append(getString(R.string.log_message_from,
                    Utility.getPhoneNumber(getActivity(), mPrefs)));
            build.append(newLine);
        }

        build.append(getString(R.string.phone_status));
        build.append(newLine);
        build.append(getString(R.string.battery_level));
        build.append(getString(R.string.battery_level_status, mPrefs.batteryLevel().get()));
        build.append(newLine);
        build.append(getString(R.string.data_connection));
        build.append(getString(R.string.data_connection_status,
                Utility.isConnected(getActivity()) ? getString(R.string.confirm_yes)
                        : getString(R.string.confirm_no)));

        // Get the log entries if they exist
        final String logs = "";

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
        mPrefs.batteryLevel().set(info.getBatteryLevel());
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
            info.setPhoneNumber(Utility.getPhoneNumber(getActivity(), mPrefs));
            setPhoneStatus(info);
        }
    };
}