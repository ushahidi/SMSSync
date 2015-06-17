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

import com.squareup.otto.Subscribe;

import org.addhen.smssync.App;
import org.addhen.smssync.R;
import org.addhen.smssync.UiThread;
import org.addhen.smssync.adapters.SentMessagesAdapter;
import org.addhen.smssync.database.BaseDatabseHelper;
import org.addhen.smssync.listeners.SentMessagesActionModeListener;
import org.addhen.smssync.models.Message;
import org.addhen.smssync.state.ReloadMessagesEvent;
import org.addhen.smssync.tasks.state.SyncPendingMessagesState;
import org.addhen.smssync.util.ServicesConstants;
import org.addhen.smssync.util.Util;
import org.addhen.smssync.views.SentMessagesView;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

public class SentMessageFragment
        extends
        BaseListFragment<SentMessagesView, Message, SentMessagesAdapter> {

    private String messageUuid = "";

    private MenuItem refresh;

    private boolean refreshState = false;


    /**
     * This will refresh content of the listview aka the pending messages when smssync syncs
     * pending
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
        // show notification
        if (prefs.serviceEnabled().get()) {
            Util.showNotification(getActivity());
        }
        listView.setItemsCanFocus(false);
        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new SentMessagesActionModeListener(
                this, listView));
    }

    @Override
    public void onResume() {
        super.onResume();
        log("OnResume is called");
        getActivity().registerReceiver(broadcastReceiver,
                new IntentFilter(ServicesConstants.AUTO_SYNC_ACTION));
        refresh();
        App.bus.register(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);

    }

    @Override
    public void onPause() {
        super.onPause();
        App.bus.unregister(this);
    }

    public boolean performAction(MenuItem item, int position) {
        messageUuid = adapter.getItem(position).getUuid();
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
                                deleteAllSentMessages();
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
                                deleteMessage(messageUuid);

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

    @Subscribe
    public void reloadMessages(final ReloadMessagesEvent event) {
        refresh();
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

    private void refresh() {
        view.emptyView.setVisibility(android.view.View.GONE);
        App.getDatabaseInstance().getMessageInstance().fetchSent(
                new BaseDatabseHelper.DatabaseCallback<List<Message>>() {
                    @Override
                    public void onFinished(final List<Message> result) {
                        UiThread.getInstance().post(new Runnable() {
                            @Override
                            public void run() {
                                view.listLoadingProgress.setVisibility(android.view.View.GONE);
                                view.emptyView.setVisibility(View.VISIBLE);
                                adapter.setItems(result);
                                listView.setAdapter(adapter);
                            }
                        });

                    }

                    @Override
                    public void onError(Exception exception) {
                    }
                });
    }

    private void deleteMessage(String uuid) {
        getActivity().setProgressBarIndeterminateVisibility(true);

        if (adapter.getCount() == 0) {
            toastLong(R.string.no_messages_to_delete);
        } else {

            refreshState = true;
            App.getDatabaseInstance().getMessageInstance()
                    .deleteByUuid(uuid,
                            new BaseDatabseHelper.DatabaseCallback<Void>() {
                                @Override
                                public void onFinished(Void result) {
                                    UiThread.getInstance().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            toastLong(R.string.messages_deleted);
                                            refreshState = false;
                                            updateRefreshStatus();
                                            refresh();
                                        }
                                    });

                                }

                                @Override
                                public void onError(Exception exception) {
                                    UiThread.getInstance().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            toastLong(R.string.messages_deleted_failed);
                                        }
                                    });

                                }
                            });
        }

    }

    private void deleteAllSentMessages() {
        getActivity().setProgressBarIndeterminateVisibility(true);

        if (adapter.getCount() == 0) {
            toastLong(R.string.no_messages_to_delete);
        } else {

            refreshState = true;
            App.getDatabaseInstance().getMessageInstance()
                    .deleteAllSentMessages(
                            new BaseDatabseHelper.DatabaseCallback<Void>() {
                                @Override
                                public void onFinished(Void result) {
                                    UiThread.getInstance().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            toastLong(R.string.messages_deleted);
                                            refreshState = false;
                                            updateRefreshStatus();
                                            refresh();
                                        }
                                    });

                                }

                                @Override
                                public void onError(Exception exception) {
                                    UiThread.getInstance().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            toastLong(R.string.messages_deleted_failed);
                                        }
                                    });

                                }
                            });
        }

    }
}
