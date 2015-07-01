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

package org.addhen.smssync.presentation.ui.fragment;

import com.addhen.android.raiburari.presentation.ui.fragment.BaseRecyclerViewFragment;
import com.addhen.android.raiburari.presentation.ui.listener.SwipeToDismissTouchListener;
import com.addhen.android.raiburari.presentation.ui.widget.BloatedRecyclerView;

import org.addhen.smssync.R;
import org.addhen.smssync.presentation.di.component.LogComponent;
import org.addhen.smssync.presentation.model.LogModel;
import org.addhen.smssync.presentation.presenter.ListLogPresenter;
import org.addhen.smssync.presentation.ui.activity.MainActivity;
import org.addhen.smssync.presentation.ui.adapter.LogAdapter;
import org.addhen.smssync.presentation.util.Utility;
import org.addhen.smssync.presentation.view.log.ListLogView;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    @Bind(R.id.empty_list_view)
    TextView mEmptyView;

    @Inject
    ListLogPresenter mListLogPresenter;

    private LogAdapter mLogAdapter;

    private static LogFragment mLogFragment;

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
        mLogAdapter = new LogAdapter(mEmptyView);
        mLogRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mLogRecyclerView.setFocusable(true);
        mLogRecyclerView.setFocusableInTouchMode(true);
        mLogAdapter.setHasStableIds(true);
        mLogRecyclerView.setAdapter(mLogAdapter);
        mLogRecyclerView.addItemDividerDecoration(getActivity());
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
}