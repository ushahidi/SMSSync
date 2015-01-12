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

import com.squareup.otto.Subscribe;

import org.addhen.smssync.MainApplication;
import org.addhen.smssync.R;
import org.addhen.smssync.adapters.SentMessagesAdapter;
import org.addhen.smssync.listeners.SentMessagesActionModeListener;
import org.addhen.smssync.models.SentMessagesModel;
import org.addhen.smssync.tasks.ProgressTask;
import org.addhen.smssync.tasks.state.SyncPendingMessagesState;
import org.addhen.smssync.util.ServicesConstants;
import org.addhen.smssync.util.Util;
import org.addhen.smssync.views.SentMessagesView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;

public class SentMessageFragment
        extends
        BaseListFragment<SentMessagesView, SentMessagesModel, SentMessagesAdapter> {

    private final Handler mHandler = new Handler();

    private String messageUuid = "";

    private SentMessagesModel model;

    private MenuItem refresh;

    private boolean refreshState = false;

    /**
     * Delete all messages. 0 - Successfully deleted. 1 - There is nothing to be deleted.
     */
    final Runnable mDeleteAllSentMessages = new Runnable() {
        public void run() {

            boolean result = false;

            int deleted = 0;

            if (adapter.getCount() == 0) {
                deleted = 1;
            } else {
                result = model.deleteAllSentMessages();
            }

            try {

                if (deleted == 1) {
                    toastLong(R.string.no_messages_to_delete);
                } else {
                    if (result) {
                        toastLong(R.string.messages_deleted);
                        refresh();
                    } else {
                        toastLong(R.string.messages_deleted_failed);
                    }
                }
                refreshState = false;
                updateRefreshStatus();
                refresh();
            } catch (Exception e) {
                return;
            }
        }
    };

    /**
     * Delete individual messages 0 - Successfully deleted. 1 - There is nothing to be deleted.
     */
    final Runnable mDeleteMessagesById = new Runnable() {
        public void run() {
            getActivity().setProgressBarIndeterminateVisibility(true);
            boolean result = false;

            int deleted = 0;

            if (adapter.getCount() == 0) {
                deleted = 1;
            } else {
                result = model.deleteSentMessagesByUuid(messageUuid);
            }

            try {
                if (deleted == 1) {
                    toastLong(R.string.no_messages_to_delete);

                } else {

                    if (result) {
                        toastLong(R.string.messages_deleted);
                        refreshState = false;

                    } else {
                        toastLong(R.string.messages_deleted_failed);

                    }
                }
                refreshState = true;
                updateRefreshStatus();
                refresh();
            } catch (Exception e) {
                return;
            }
        }
    };

    /**
     * This will refresh content of the listview aka the pending messages when smssync syncs pending
     * messages.
     */

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                refresh();
            }
        }
    };

    public SentMessageFragment() {
        super(SentMessagesView.class, SentMessagesAdapter.class,
                R.layout.sent_messages, R.menu.sent_messages_menu,
                android.R.id.list);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        model = new SentMessagesModel();
        // show notification
        if (prefs.serviceEnabled().get()) {
            Util.showNotification(getActivity());
        }
        listView.setItemsCanFocus(false);
        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new SentMessagesActionModeListener(
                this, listView));
        MainApplication.bus.register(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        log("OnResume is called");
        getActivity().registerReceiver(broadcastReceiver,
                new IntentFilter(ServicesConstants.AUTO_SYNC_ACTION));
        refresh();
        MainApplication.bus.register(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
        MainApplication.bus.unregister(this);
    }

    public boolean performAction(MenuItem item, int position) {
        messageUuid = adapter.getItem(position).getMessageUuid();
        if (item.getItemId() == R.id.sent_messages_context_delete) {
            // Delete by ID
            performDeleteById();
            return (true);
        }

        return (false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.delete) {
            refresh = item;
            performDeleteAll();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Delete all messages
     */
    public void performDeleteAll() {
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
                                refreshState = true;
                                updateRefreshStatus();
                                mHandler.post(mDeleteAllSentMessages);
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Delete message by it's id
     */
    public void performDeleteById() {
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
                                // Delete by ID
                                refreshState = true;
                                updateRefreshStatus();
                                mHandler.post(mDeleteMessagesById);
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void updateRefreshStatus() {
        if (refresh != null) {
            if (refreshState) {
                refresh.setActionView(R.layout.indeterminate_progress_action);
            } else {
                refresh.setActionView(null);
            }
        }

    }

    public void refresh() {
        new LoadingTask(getActivity()).execute((String) null);
    }

    @Subscribe
    public void syncStateChanged(final SyncPendingMessagesState newState) {

        switch (newState.state) {
            case FINISHED_SYNC:
            case CANCELED_SYNC:
                refresh();
                break;

        }

    }

    private class LoadingTask extends ProgressTask {

        public LoadingTask(Activity activity) {
            super(activity);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.cancel();
            view.emptyView.setVisibility(android.view.View.GONE);
        }

        @Override
        protected Boolean doInBackground(String... args) {
            return model.load();
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            view.listLoadingProgress.setVisibility(android.view.View.GONE);
            view.emptyView.setVisibility(View.VISIBLE);
            if (success) {

                adapter.setItems(model.listMessages);
                listView.setAdapter(adapter);
            }
        }
    }
}
