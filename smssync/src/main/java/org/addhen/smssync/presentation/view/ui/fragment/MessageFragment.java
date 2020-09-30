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
import org.addhen.smssync.presentation.presenter.message.ImportMessagePresenter;
import org.addhen.smssync.presentation.presenter.message.ListMessagePresenter;
import org.addhen.smssync.presentation.presenter.message.PublishAllMessagesPresenter;
import org.addhen.smssync.presentation.presenter.message.PublishMessagePresenter;
import org.addhen.smssync.presentation.service.ServiceConstants;
import org.addhen.smssync.presentation.util.Utility;
import org.addhen.smssync.presentation.view.message.DeleteMessageView;
import org.addhen.smssync.presentation.view.message.ImportMessageView;
import org.addhen.smssync.presentation.view.message.ListMessageView;
import org.addhen.smssync.presentation.view.message.PublishMessageView;
import org.addhen.smssync.presentation.view.message.PublishAllMessagesView;
import org.addhen.smssync.presentation.view.ui.activity.MainActivity;
import org.addhen.smssync.presentation.view.ui.adapter.MessageAdapter;
import org.addhen.smssync.presentation.view.ui.widget.DividerItemDecoration;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;

import static org.addhen.smssync.presentation.service.ServiceConstants.ACTIVE_SYNC;
import static org.addhen.smssync.presentation.service.ServiceConstants.SYNC_STATUS;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class MessageFragment extends BaseRecyclerViewFragment<MessageModel, MessageAdapter>
        implements ListMessageView {

    private static final int HONEYCOMB = 11;

    /** List of items pending to to be deleted **/
    public List<PendingMessage> mPendingMessages;

    @BindView(R.id.messages_fab)
    FloatingActionButton mFab;

    @BindView(android.R.id.empty)
    View mEmptyView;

    @Inject
    ListMessagePresenter mListMessagePresenter;

    @Inject
    PublishMessagePresenter mPublishMessagePresenter;

    @Inject
    PublishAllMessagesPresenter mPublishAllMessagesPresenter;

    @Inject
    DeleteMessagePresenter mDeleteMessagePresenter;

    @Inject
    ImportMessagePresenter mImportMessagePresenter;

    private int mRemovedItemPosition = 0;

    private MessageModel mRemovedMessage;

    private ActionMode mActionMode;

    private boolean mIsPermanentlyDeleted = true;

    private CompositeSubscription mSubscriptions = new CompositeSubscription();

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getIntExtra(SYNC_STATUS, 0) == ACTIVE_SYNC) {
                    mBloatedRecyclerView.enableDefaultSwipeRefresh(false);
                    mFab.setVisibility(View.GONE);
                }
                mListMessagePresenter.loadMessages();
            }
        }
    };

    public MessageFragment() {
        super(MessageAdapter.class, R.layout.fragment_list_message, R.menu.menu_messages);
    }

    public static MessageFragment newInstance() {
        return new MessageFragment();
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
        mPublishAllMessagesPresenter.pause();
        mPublishMessagePresenter.pause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mListMessagePresenter != null) {
            mListMessagePresenter.destroy();
        }
        if (mPublishAllMessagesPresenter != null) {
            mPublishAllMessagesPresenter.destroy();
        }

        if (mPublishMessagePresenter != null) {
            mPublishMessagePresenter.destroy();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.import_sms) {
            mImportMessagePresenter.importMessages();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initialize() {
        mListMessagePresenter.setView(this);
        initializePublishPresenter();
        initializeDeletePresenter();
        initializeImportPresenter();
        initializePublishMessagesPresenter();
        initRecyclerView();
    }

    private void initializePublishPresenter() {
        mPublishMessagePresenter.setView(new PublishMessageView() {
            @Override
            public void successfullyPublished(boolean status) {
                reloadMessages();
            }

            @Override
            public void showLoading() {
                MessageFragment.this.showLoading();
            }

            @Override
            public void hideLoading() {
                MessageFragment.this.hideLoading();
            }

            @Override
            public void showRetry() {
                MessageFragment.this.showRetry();
            }

            @Override
            public void hideRetry() {
                MessageFragment.this.hideRetry();
            }

            @Override
            public void showError(String s) {
                showSnackbar(mFab, s);
            }

            @Override
            public void showEnableServiceMessage(String s) {
                Snackbar snackbar = Snackbar.make(mFab, s, Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.enable, e -> {
                    ((MainActivity) getActivity()).launchIntegration();
                }).show();
            }

            @Override
            public Context getAppContext() {
                return getActivity();
            }
        });
    }

    private void initializePublishMessagesPresenter() {
        mPublishAllMessagesPresenter.setView(new PublishAllMessagesView() {
            @Override
            public void successfullyPublished(boolean status) {
                reloadMessages();
            }

            @Override
            public void showLoading() {
                MessageFragment.this.showLoading();
            }

            @Override
            public void hideLoading() {
                MessageFragment.this.hideLoading();
            }

            @Override
            public void showRetry() {
                MessageFragment.this.showRetry();
            }

            @Override
            public void hideRetry() {
                MessageFragment.this.hideRetry();
            }

            @Override
            public void showError(String s) {
                showSnackbar(mFab, s);
            }

            @Override
            public void showEnableServiceMessage(String s) {
                Snackbar snackbar = Snackbar.make(mFab, s, Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.enable, e -> {
                    ((MainActivity) getActivity()).launchIntegration();
                }).show();
            }

            @Override
            public Context getAppContext() {
                return getActivity();
            }
        });
    }

    private void initializeDeletePresenter() {
        mDeleteMessagePresenter.setView(new DeleteMessageView() {
            @Override
            public void onMessageDeleted() {
                reloadMessages();
            }

            @Override
            public void showLoading() {
                MessageFragment.this.showLoading();
            }

            @Override
            public void hideLoading() {
                MessageFragment.this.hideLoading();
            }

            @Override
            public void showRetry() {
                MessageFragment.this.showRetry();
            }

            @Override
            public void hideRetry() {
                MessageFragment.this.hideRetry();
            }

            @Override
            public void showError(String s) {
                showSnackbar(mFab, s);
            }

            @Override
            public Context getAppContext() {
                return getContext().getApplicationContext();
            }
        });
    }

    private void initializeImportPresenter() {
        mImportMessagePresenter.setView(new ImportMessageView() {
            @Override
            public void showMessages(List<MessageModel> messageModelList) {
                if (!Utility.isEmpty(messageModelList)) {
                    mRecyclerViewAdapter.setItems(messageModelList);
                    mFab.setVisibility(View.VISIBLE);
                    return;
                }
                mFab.setVisibility(View.GONE);
            }

            @Override
            public void showLoading() {
                MessageFragment.this.showLoading();
            }

            @Override
            public void hideLoading() {
                MessageFragment.this.hideLoading();
            }

            @Override
            public void showRetry() {
                MessageFragment.this.showRetry();
            }

            @Override
            public void hideRetry() {
                MessageFragment.this.hideRetry();
            }

            @Override
            public void showError(String s) {
                showSnackbar(getView(), s);
            }

            @Override
            public Context getAppContext() {
                return getContext().getApplicationContext();
            }
        });
    }

    private void initRecyclerView() {
        mPendingMessages = new ArrayList<>();
        mBloatedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBloatedRecyclerView.setFocusable(true);
        mBloatedRecyclerView.setFocusableInTouchMode(true);
        mBloatedRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null));
        mRecyclerViewAdapter.setOnCheckedListener(position -> setItemChecked(position));
        mBloatedRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerViewAdapter
                .setOnMoreActionListener(position -> new BottomSheet.Builder(getActivity())
                        .sheet(R.menu.menu_messages_more_actions)
                        .listener((dialog, which) -> {
                            switch (which) {
                                case R.id.menu_messages_more_actions_delete:
                                    deleteItem(position);
                                    break;
                                default:
                                    publishItem(position);
                            }
                        }).show());
        if (Build.VERSION.SDK_INT >= HONEYCOMB) {
            enableSwipeToPerformAction();
        }
    }

    public void requestQuery(final String query) {
        Handler handler = new Handler();
        final Runnable filterDeployments = new Runnable() {
            public void run() {
                try {
                    mRecyclerViewAdapter.getFilter().filter(query);
                } catch (Exception e) {
                    reloadMessages();
                }
            }
        };

        handler.post(filterDeployments);
    }

    public void reloadMessages() {
        mListMessagePresenter.loadMessages();
    }

    private void drawSwipeListItemBackground(Canvas c, int dX, View itemView, int actionState) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Fade out the view as it is swiped out of the parent's bounds
            final float alpha = 1.0f - Math.abs(dX) / (float) itemView.getWidth();
            ViewHelper.setAlpha(itemView, alpha);
            ViewHelper.setTranslationX(itemView, dX);
            Drawable d;
            // Swiping right
            Paint p = new Paint();
            if (dX > 0) {
                d = ContextCompat
                        .getDrawable(getAppContext(), R.drawable.swipe_right_list_item_background);
                d.setBounds(itemView.getLeft(), itemView.getTop(), dX, itemView.getBottom());
                c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                        (float) itemView.getBottom(), p);
            } else { // Swiping left
                d = ContextCompat
                        .getDrawable(getAppContext(), R.drawable.swipe_left_list_item_background);
                d.setBounds(itemView.getRight() + dX, itemView.getTop(), itemView.getRight(),
                        itemView.getBottom());
                c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                        (float) itemView.getRight(), (float) itemView.getBottom(), p);
            }
            d.draw(c);
        }
    }

    private void remove(int position) {
        mRemovedItemPosition = position;
        publishItem(mRemovedItemPosition);
    }

    private void removeItems() {
        if (!Utility.isEmpty(mPendingMessages)) {
            for (PendingMessage message : mPendingMessages) {
                mRecyclerViewAdapter.removeItem(message.messageModel);
            }
        }
    }

    @TargetApi(HONEYCOMB)
    private void enableSwipeToPerformAction() {
        // Swiping doesn't work well on API 11 and below because the android support lib ships
        // with buggy APIs that makes it hard to implement on older devices.
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
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDismiss);
        itemTouchHelper.attachToRecyclerView(mBloatedRecyclerView.recyclerView);
    }

    @OnClick(R.id.messages_fab)
    void syncItems() {
        mPublishAllMessagesPresenter.publishMessages();
    }

    @OnClick(android.R.id.empty)
    void importItems() {
        mImportMessagePresenter.importMessages();
    }

    @Override
    public void showMessages(List<MessageModel> messageModelList) {
        if (!Utility.isEmpty(messageModelList)) {
            mRecyclerViewAdapter.setItems(messageModelList);
            mFab.setVisibility(View.VISIBLE);
            return;
        }
        mRecyclerViewAdapter.setItems(new ArrayList<>());
        mFab.setVisibility(View.GONE);
        if (mRecyclerViewAdapter.getAdapterItemCount() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showLoading() {
        mBloatedRecyclerView.setRefreshing(true);
        mFab.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        mBloatedRecyclerView.setRefreshing(false);
        mFab.setVisibility(View.VISIBLE);
    }

    @Override
    public void showRetry() {
        // Do nothing
    }

    @Override
    public void hideRetry() {
        mBloatedRecyclerView.setRefreshing(false);
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

    private void showUndoSnackbar(String s) {
        Snackbar snackbar = Snackbar.make(mFab, s, Snackbar.LENGTH_LONG);
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
            mPendingMessages.add(new PendingMessage(position,
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
        if (mPendingMessages != null) {
            mPendingMessages.clear();
        }
    }

    private void deleteItems() {
        //Sort in ascending order for restoring deleted items
        Comparator cmp = Collections.reverseOrder();
        Collections.sort(mPendingMessages, cmp);
        removeItems();
        Snackbar snackbar = Snackbar.make(mFab, getActivity()
                        .getString(R.string.item_deleted, mPendingMessages.size()),
                Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.undo, e -> {
            // Restore items
            for (PendingMessage pendingDeletedDeployment
                    : mPendingMessages) {
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
                    if (mPendingMessages.size() > 0) {
                        for (PendingMessage pendingDeletedDeployment : mPendingMessages) {
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

    private void publishItems() {
        //Sort in ascending order for restoring deleted items
        Comparator cmp = Collections.reverseOrder();
        Collections.sort(mPendingMessages, cmp);
        removeItems();
        Snackbar snackbar = Snackbar.make(mFab, getActivity()
                        .getString(R.string.item_deleted, mPendingMessages.size()),
                Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.undo, e -> {
            // Restore items
            for (PendingMessage pendingDeletedDeployment
                    : mPendingMessages) {
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
                    if (mPendingMessages.size() > 0) {
                        List<MessageModel> messageModels = new ArrayList<MessageModel>();
                        for (PendingMessage pendingDeletedDeployment : mPendingMessages) {
                            mPublishMessagePresenter
                                    .publishMessage(pendingDeletedDeployment.messageModel);
                        }
                        clearItems();
                    }
                }
            }
        });
        snackbar.show();
    }

    private void publishItem(int position) {
        //Sort in ascending order for restoring deleted items
        Snackbar snackbar = Snackbar.make(mFab, getActivity().getString(R.string.published),
                Snackbar.LENGTH_LONG);
        mRemovedMessage = mRecyclerViewAdapter.getItem(position);
        mRecyclerViewAdapter.removeItem(mRemovedMessage);
        snackbar.setAction(R.string.undo, e -> {
            // Restore items
            mRecyclerViewAdapter.addItem(mRemovedMessage, position);
        });
        View view = snackbar.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(getAppContext().getResources().getColor(R.color.red));
        snackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                    mPublishMessagePresenter.publishMessage(mRemovedMessage);
                }
            }
        });
        snackbar.show();
    }

    private void deleteItem(int position) {
        //Sort in ascending order for restoring deleted items
        Snackbar snackbar = Snackbar.make(mFab, getActivity()
                        .getString(R.string.item_deleted, mPendingMessages.size()),
                Snackbar.LENGTH_LONG);
        mRemovedMessage = mRecyclerViewAdapter.getItem(position);
        mRecyclerViewAdapter.removeItem(mRemovedMessage);
        snackbar.setAction(R.string.undo, e -> {
            // Restore items
            mRecyclerViewAdapter.addItem(mRemovedMessage, position);
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

    private static class PendingMessage implements Comparable<PendingMessage> {

        /** The message model to be deleted */
        public MessageModel messageModel;

        private int mPosition;

        public PendingMessage(int position, MessageModel messageModel) {
            mPosition = position;
            this.messageModel = messageModel;
        }

        @Override
        public int compareTo(PendingMessage other) {
            // Sort by descending position
            return other.mPosition - mPosition;
        }

        public int getPosition() {
            return mPosition;
        }
    }

    private class ActionBarModeCallback implements ActionMode.Callback {

        private boolean mIsActionTaken = false;

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.context_menu_messages, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

            if (menuItem.getItemId() == R.id.context_menu_delete) {
                deleteItems();
                mIsActionTaken = true;
            } else if (menuItem.getItemId() == R.id.context_menu_import_sms) {
                publishItems();
                mIsActionTaken = true;
            }

            if (mActionMode != null) {
                mActionMode.finish();
            }
            return mIsActionTaken;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            if (!mIsActionTaken) {
                clearItems();
            }
            mActionMode = null;
        }
    }
}