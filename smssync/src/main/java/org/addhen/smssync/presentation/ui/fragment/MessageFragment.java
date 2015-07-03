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
import com.addhen.android.raiburari.presentation.ui.widget.BloatedRecyclerView;

import org.addhen.smssync.R;
import org.addhen.smssync.presentation.di.component.MessageComponent;
import org.addhen.smssync.presentation.model.MessageModel;
import org.addhen.smssync.presentation.presenter.ListMessagePresenter;
import org.addhen.smssync.presentation.ui.activity.MainActivity;
import org.addhen.smssync.presentation.ui.adapter.MessageAdapter;
import org.addhen.smssync.presentation.ui.listener.OnSwipeableRecyclerViewTouchListener;
import org.addhen.smssync.presentation.util.Utility;
import org.addhen.smssync.presentation.view.message.ListMessageView;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class MessageFragment extends BaseRecyclerViewFragment<MessageModel, MessageAdapter>
        implements
        ListMessageView {

    @Bind(R.id.messages_fab)
    FloatingActionButton mFab;

    @Bind(android.R.id.list)
    BloatedRecyclerView mMessageRecyclerView;

    @Bind(R.id.empty_list_view)
    TextView mEmptyView;

    @Inject
    ListMessagePresenter mListMessagePresenter;

    private MessageAdapter mMessageAdapter;

    private static MessageFragment mMessageFragment;

    public MessageFragment() {
        super(MessageAdapter.class, R.layout.fragment_list_message, R.menu.menu_messages);
    }

    public static MessageFragment newInstance() {
        if (mMessageFragment == null) {
            mMessageFragment = new MessageFragment();
        }
        return mMessageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    @Override
    public void onResume() {
        super.onResume();
        mListMessagePresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mListMessagePresenter.pause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListMessagePresenter.destroy();
    }

    private void initialize() {
        getMessageComponent(MessageComponent.class).inject(this);
        mListMessagePresenter.setView(this);
        initRecyclerView();
    }

    private void initRecyclerView() {
        mMessageAdapter = new MessageAdapter(mEmptyView);
        mMessageRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMessageRecyclerView.setFocusable(true);
        mMessageRecyclerView.setFocusableInTouchMode(true);
        mMessageAdapter.setHasStableIds(true);
        mMessageRecyclerView.setAdapter(mMessageAdapter);
        mMessageRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mMessageRecyclerView.addItemDividerDecoration(getActivity());
        OnSwipeableRecyclerViewTouchListener swipeTouchListener =
                new OnSwipeableRecyclerViewTouchListener(mMessageRecyclerView.recyclerView,
                        new OnSwipeableRecyclerViewTouchListener.SwipeListener() {
                            @Override
                            public boolean canSwipe(int position) {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView,
                                    int[] reverseSortedPositions) {
                                // TODO: Implement swipe action

                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView,
                                    int[] reverseSortedPositions) {
                                // TODO: Implement swipe action

                            }
                        });
        mMessageRecyclerView.recyclerView.addOnItemTouchListener(swipeTouchListener);
        mMessageRecyclerView.enableDefaultSwipeRefresh(true);
    }

    @OnClick(R.id.messages_fab)
    void syncItems() {
        // TODO: Perform message sync. For now reload the messages list
        mListMessagePresenter.loadMessages();
    }

    @Override
    public void showMessages(List<MessageModel> messageModelList) {
        if (!Utility.isEmpty(messageModelList)) {
            mMessageAdapter.setItems(messageModelList);
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
        Snackbar snackbar = Snackbar.make(mFab, s, Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(getAppContext().getResources().getColor(R.color.red));
        snackbar.show();
    }

    @Override
    public Context getAppContext() {
        return getActivity().getApplicationContext();
    }

    protected <C> C getMessageComponent(Class<C> componentType) {
        return componentType.cast(((MainActivity) getActivity()).getMessageComponent());
    }
}