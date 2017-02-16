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
import com.cocosw.bottomsheet.BottomSheet;
import com.nineoldandroids.view.ViewHelper;

import org.addhen.smssync.R;
import org.addhen.smssync.presentation.di.component.MessageComponent;
import org.addhen.smssync.presentation.model.MessageModel;
import org.addhen.smssync.presentation.presenter.message.DeleteMessagePresenter;
import org.addhen.smssync.presentation.presenter.message.ListPublishedMessagePresenter;
import org.addhen.smssync.presentation.service.ServiceConstants;
import org.addhen.smssync.presentation.util.Utility;
import org.addhen.smssync.presentation.view.message.DeleteMessageView;
import org.addhen.smssync.presentation.view.message.ListMessageView;
import org.addhen.smssync.presentation.view.ui.activity.MainActivity;
import org.addhen.smssync.presentation.view.ui.adapter.MessageAdapter;
import org.addhen.smssync.presentation.view.ui.widget.DividerItemDecoration;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class PublishedMessageFragment extends BaseRecyclerViewFragment<MessageModel, MessageAdapter>
        implements ListMessageView {

    private static final int HONEYCOMB = 11;

    @BindView(android.R.id.empty)
    ViewGroup mEmptyView;

    @Inject
    ListPublishedMessagePresenter mListMessagePresenter;

    @Inject
    DeleteMessagePresenter mDeleteMessagePresenter;

    private int mRemovedItemPosition = 0;

    private MessageModel mRemovedMessage;

    private ActionMode mActionMode;

    private boolean mIsPermanentlyDeleted = true;

    /** List of items pending to be deleted **/
    public List<PendingDeletedMessage> mPendingDeletedMessages;

    public PublishedMessageFragment() {
        super(MessageAdapter.class, R.layout.fragment_list_published_message, 0);
    }

    public static PublishedMessageFragment newInstance() {
        return new PublishedMessageFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getMessageComponent(MessageComponent.class).inject(this);
        initialize();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(broadcastReceiver,
                new IntentFilter(ServiceConstants.AUTO_SYNC_ACTION));
        mListMessagePresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(broadcastReceiver);
        mListMessagePresenter.pause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mListMessagePresenter != null) {
            mListMessagePresenter.destroy();
        }
    }

    private void initialize() {
        getMessageComponent(MessageComponent.class).inject(this);
        mListMessagePresenter.setView(this);
        initializeDeletePresenter();
        initRecyclerView();
    }

    private void initRecyclerView() {
        mPendingDeletedMessages = new ArrayList<>();
        mBloatedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBloatedRecyclerView.setFocusable(true);
        mBloatedRecyclerView.setFocusableInTouchMode(true);
        mBloatedRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null));
        mBloatedRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mBloatedRecyclerView.enableDefaultSwipeRefresh(false);
        mRecyclerViewAdapter.setOnCheckedListener(position -> setItemChecked(position));
        mBloatedRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerViewAdapter
                .setOnMoreActionListener(position -> new BottomSheet.Builder(getActivity())
                        .sheet(R.menu.menu_published_messages_more_actions)
                        .listener((dialog, which) -> {
                            switch (which) {
                                case R.id.menu_published_messages_more_actions_delete:
                                    showUndoSnackbar(position);
                                    break;
                            }
                        }).show());
        if (Build.VERSION.SDK_INT >= HONEYCOMB) {
            enableSwipeToPerformAction();
        }
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
        mRemovedMessage = mRecyclerViewAdapter.getItem(position);
        mRecyclerViewAdapter.removeItem(mRemovedMessage);
        showUndoSnackbar(1);
    }

    private void removeItems() {
        if (!Utility.isEmpty(mPendingDeletedMessages)) {
            for (PendingDeletedMessage pendingDeletedMessage : mPendingDeletedMessages) {
                mRecyclerViewAdapter.removeItem(pendingDeletedMessage.messageModel);
            }
        }
    }

    private void initializeDeletePresenter() {
        mDeleteMessagePresenter.setView(new DeleteMessageView() {
            @Override
            public void onMessageDeleted() {
                mListMessagePresenter.loadMessages();
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
                showSnackbar(getView(), s);
            }

            @Override
            public Context getAppContext() {
                return getContext();
            }
        });
    }

    @TargetApi(HONEYCOMB)
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
        itemTouchHelper.attachToRecyclerView(mBloatedRecyclerView.recyclerView);
    }

    @Override
    public void showMessages(List<MessageModel> messageModelList) {
        if (!Utility.isEmpty(messageModelList)) {
            mRecyclerViewAdapter.setItems(messageModelList);
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

    protected <C> C getMessageComponent(Class<C> componentType) {
        return componentType.cast(((MainActivity) getActivity()).getMessageComponent());
    }

    private void showUndoSnackbar(int count) {
        Snackbar snackbar = Snackbar
                .make(getView(), getString(R.string.item_deleted, count), Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.undo, v -> {
            // Restore item
            mRecyclerViewAdapter.addItem(mRemovedMessage, mRemovedItemPosition);
        });
        View view = snackbar.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(getAppContext().getResources().getColor(R.color.red));
        snackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                    mDeleteMessagePresenter.deleteMessage(mRemovedMessage.getMessageUuid());
                }
            }
        });
        snackbar.show();
    }

    public void setItemChecked(int position) {

        mRecyclerViewAdapter.toggleSelection(position);

        int checkedCount = mRecyclerViewAdapter.getSelectedItemCount();

        if (checkedCount == 0) {
            if (mActionMode != null) {
                mActionMode.finish();
            }
            return;
        }
        if (mActionMode == null) {
            mActionMode = ((MainActivity) getActivity())
                    .startSupportActionMode(new ActionBarModeCallback());
        }

        if (mRecyclerViewAdapter != null) {
            mPendingDeletedMessages.add(new PendingDeletedMessage(position,
                    mRecyclerViewAdapter.getItem(position)));

        }

        // Set the CAB title with the number of selected items
        mActionMode.setTitle(getAppContext().getString(R.string.selected, checkedCount));

    }

    /**
     * Clear all checked items in the list and the selected {@link MessageModel}
     */
    private void clearItems() {
        mRecyclerViewAdapter.clearSelections();
        if (mPendingDeletedMessages != null) {
            mPendingDeletedMessages.clear();
        }
    }

    private void deleteItems() {
        //Sort in ascending order so deleted items can be easily restored
        Comparator cmp = Collections.reverseOrder();
        Collections.sort(mPendingDeletedMessages, cmp);
        removeItems();
        Snackbar snackbar = Snackbar.make(getView(), getActivity()
                        .getString(R.string.item_deleted, mPendingDeletedMessages.size()),
                Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.undo, e -> {
            mIsPermanentlyDeleted = false;
            // Restore items
            for (PendingDeletedMessage pendingDeletedDeployment
                    : mPendingDeletedMessages) {
                mRecyclerViewAdapter.addItem(pendingDeletedDeployment.messageModel,
                        pendingDeletedDeployment.getPosition());
            }
            clearItems();
        });
        View view = snackbar.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(getAppContext().getResources().getColor(R.color.red));
        snackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                    if (mPendingDeletedMessages.size() > 0) {
                        for (PendingDeletedMessage pendingDeletedDeployment : mPendingDeletedMessages) {
                            mDeleteMessagePresenter
                                    .deleteMessage(
                                            pendingDeletedDeployment.messageModel.getMessageUuid());
                        }
                        clearItems();
                    }
                }
            }
        });
        snackbar.show();
    }

    public static class PendingDeletedMessage implements Comparable<PendingDeletedMessage> {

        /** The message model to be deleted */
        public MessageModel messageModel;

        private int mPosition;

        public PendingDeletedMessage(int position, MessageModel messageModel) {
            mPosition = position;
            this.messageModel = messageModel;
        }

        @Override
        public int compareTo(PendingDeletedMessage other) {
            // Sort by descending position
            return other.mPosition - mPosition;
        }

        public int getPosition() {
            return mPosition;
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                mListMessagePresenter.loadMessages();
            }
        }
    };

    private class ActionBarModeCallback implements ActionMode.Callback {

        private boolean isDeleted = false;

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.context_menu_publish_messages, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

            if (menuItem.getItemId() == R.id.context_menu_delete_published_messages) {
                deleteItems();
                isDeleted = true;
            }

            if (mActionMode != null) {
                mActionMode.finish();
            }
            return isDeleted;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            if (!isDeleted) {
                clearItems();
            }
            mActionMode = null;
        }
    }
}