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
import com.addhen.android.raiburari.presentation.ui.listener.RecyclerViewItemTouchListenerAdapter;
import com.addhen.android.raiburari.presentation.ui.widget.BloatedRecyclerView;

import org.addhen.smssync.R;
import org.addhen.smssync.presentation.di.component.MessageComponent;
import org.addhen.smssync.presentation.model.MessageModel;
import org.addhen.smssync.presentation.presenter.ListMessagePresenter;
import org.addhen.smssync.presentation.util.Utility;
import org.addhen.smssync.presentation.view.message.ListMessageView;
import org.addhen.smssync.presentation.view.ui.activity.MainActivity;
import org.addhen.smssync.presentation.view.ui.adapter.MessageAdapter;
import org.addhen.smssync.presentation.view.ui.animators.SlideInLeftAnimator;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;
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

    @Bind(android.R.id.empty)
    ViewGroup mEmptyView;

    @Inject
    ListMessagePresenter mListMessagePresenter;

    private MessageAdapter mMessageAdapter;

    private static MessageFragment mMessageFragment;

    private int mRemovedItemPosition = 0;

    private MessageModel mRemovedMessage;

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
        mMessageRecyclerView.setItemAnimator(new SlideInLeftAnimator());
        mMessageRecyclerView.addItemDividerDecoration(getActivity());
        mMessageRecyclerView.recyclerView.getItemAnimator().setRemoveDuration(0);
        mMessageRecyclerView.enableDefaultSwipeRefresh(false);
        RecyclerViewItemTouchListenerAdapter recyclerViewItemTouchListenerAdapter
                = new RecyclerViewItemTouchListenerAdapter(mMessageRecyclerView.recyclerView,
                new RecyclerViewItemTouchListenerAdapter.RecyclerViewOnItemClickListener() {
                    @Override
                    public void onItemClick(RecyclerView recyclerView, View view, int i) {
                        // Do nothing
                    }

                    @Override
                    public void onItemLongClick(RecyclerView recyclerView, View view, int i) {

                    }
                });
        mMessageRecyclerView.recyclerView
                .addOnItemTouchListener(recyclerViewItemTouchListenerAdapter);
        enableSwipeToPerformAction();
    }

    private void drawSwipeListItemBackground(Canvas c, int dX, View itemView, int actionState) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            Drawable d;
            // Swiping right
            if (dX > 0) {
                d = ContextCompat
                        .getDrawable(getAppContext(), R.drawable.swipe_right_list_item_background);
                d.setBounds(itemView.getLeft(), itemView.getTop(), dX, itemView.getBottom());
            } else { // Swiping left
                d = ContextCompat
                        .getDrawable(getAppContext(), R.drawable.swipe_left_list_item_background);
                d.setBounds(itemView.getRight() + dX, itemView.getTop(), itemView.getRight(),
                        itemView.getBottom());
            }
            d.draw(c);
        }
    }

    private void remove(int position) {
        mRemovedItemPosition = position;
        mRemovedMessage = mMessageAdapter.getItem(position);
        mMessageAdapter.removeItem(mRemovedMessage);
        showUndoSnackbar(1);
    }

    @TargetApi(11)
    private void enableSwipeToPerformAction() {
        // Swiping works well on API 11 and above because the android support lib ships
        // with buggy APIs that makes it hard to implement on older devices
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
            public long getAnimationDuration(RecyclerView recyclerView, int animationType,
                    float animateDx, float animateDy) {
                return animationType == ItemTouchHelper.ANIMATION_TYPE_DRAG
                        ? DEFAULT_DRAG_ANIMATION_DURATION
                        : DEFAULT_SWIPE_ANIMATION_DURATION;
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDismiss);
        itemTouchHelper.attachToRecyclerView(mMessageRecyclerView.recyclerView);
    }

    @OnClick(R.id.messages_fab)
    void syncItems() {
        // TODO: Perform message sync. For now reload the messages list
        mListMessagePresenter.loadMessages();
    }

    @OnClick(android.R.id.empty)
    void importItems() {
        // TODO: perform SMS import
        showSnabackar(mFab, "Empty view was clicked");
    }

    @Override
    public void showMessages(List<MessageModel> messageModelList) {
        if (!Utility.isEmpty(messageModelList)) {
            mMessageAdapter.setItems(messageModelList);
            mFab.setVisibility(View.VISIBLE);
            return;
        }
        mFab.setVisibility(View.GONE);
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

    private void showUndoSnackbar(int count) {
        Snackbar snackbar = Snackbar
                .make(mFab, getString(R.string.item_deleted, count), Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.undo, v -> {
            // Restore item
            mMessageAdapter.addItem(mRemovedMessage, mRemovedItemPosition);
        });
        snackbar.show();
    }
}