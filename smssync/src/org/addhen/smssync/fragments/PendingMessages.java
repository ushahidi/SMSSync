/*****************************************************************************
 ** Copyright (c) 2010 - 2012 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 **
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.
 **
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 **
 *****************************************************************************/

package org.addhen.smssync.fragments;

import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedHashSet;

import org.addhen.smssync.MainApplication;
import org.addhen.smssync.Prefs;
import org.addhen.smssync.ProcessSms;
import org.addhen.smssync.R;
import org.addhen.smssync.SyncDate;
import org.addhen.smssync.adapters.PendingMessagesAdapter;
import org.addhen.smssync.listeners.PendingMessagesActionModeListener;
import org.addhen.smssync.models.MessagesModel;
import org.addhen.smssync.services.SyncPendingMessagesService;
import org.addhen.smssync.tasks.ProgressTask;
import org.addhen.smssync.tasks.SyncType;
import org.addhen.smssync.tasks.TaskCanceled;
import org.addhen.smssync.tasks.state.State;
import org.addhen.smssync.tasks.state.SyncPendingMessagesState;
import org.addhen.smssync.tasks.state.SyncState;
import org.addhen.smssync.util.ServicesConstants;
import org.addhen.smssync.util.Util;
import org.addhen.smssync.views.PendingMessagesView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.SparseBooleanArray;
import android.widget.ListView;

import com.actionbarsherlock.view.MenuItem;
import com.squareup.otto.Subscribe;

public class PendingMessages
        extends
        BaseListFragment<PendingMessagesView, MessagesModel, PendingMessagesAdapter> implements
        android.view.View.OnClickListener {

    private Intent syncPendingMessagesServiceIntent;

    private final Handler mHandler;

    private MessagesModel model;

    private String messageUuid;

    private static final String STATE_CHECKED = "org.addhen.smssync.fragments.STATE_CHECKED";

    public PendingMessages() {
        super(PendingMessagesView.class, PendingMessagesAdapter.class,
                R.layout.list_messages, R.menu.pending_messages_menu,
                android.R.id.list);
        log("PendingMessages()");
        mHandler = new Handler();
        model = new MessagesModel();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        log("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
        Prefs.loadPreferences(getActivity());
        // show notification
        if (Prefs.enabled) {
            Util.showNotification(getActivity());
        }

        listView.setItemsCanFocus(false);
        listView.setLongClickable(true);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemLongClickListener(new PendingMessagesActionModeListener(
                this, listView));

        if (savedInstanceState != null) {
            int position = savedInstanceState.getInt(STATE_CHECKED, -1);

            if (position > -1) {
                listView.setItemChecked(position, true);
            }
        }
        view.sync.setOnClickListener(this);
        Util.setupStrictMode();
        MainApplication.bus.register(this);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        log("onSaveInstanceState()");
        super.onSaveInstanceState(state);
        state.putInt(STATE_CHECKED, listView.getCheckedItemPosition());
    }

    @Override
    public void onResume() {
        log("onResume()");
        super.onResume();
        getActivity().registerReceiver(failedReceiver,
                new IntentFilter(ServicesConstants.FAILED_ACTION));
        getActivity().registerReceiver(smsSentReceiver,
                new IntentFilter(ServicesConstants.SENT));
        getActivity().registerReceiver(smsDeliveredReceiver,
                new IntentFilter(ServicesConstants.DELIVERED));
        idle();
        mHandler.post(mUpdateListView);

        MainApplication.bus.register(this);
    }

    @Override
    public void onStart() {
        log("onStart()");
        super.onStart();
    }

    @Override
    public void onPause() {
        log("onPause()");
        super.onPause();

    }

    @Override
    public void onDestroy() {
        log("onDestroy()");
        super.onDestroy();
        getActivity().unregisterReceiver(failedReceiver);
        getActivity().unregisterReceiver(smsSentReceiver);
        getActivity().unregisterReceiver(smsDeliveredReceiver);
        MainApplication.bus.unregister(this);
    }

    private void idle() {

        view.details.setText(getLastSyncText(new SyncDate().getLastSyncedDate(getActivity())));
        view.status.setText(R.string.idle);
        view.status
                .setTextColor(getActivity().getResources().getColor(R.color.status_idle));
    }

    @Override
    public void onClick(android.view.View v) {
        if (v == view.sync) {
            if (!SyncPendingMessagesService.isServiceWorking()) {
                log("Sync in action");
                startSync();
            } else {
                log("Sync canceled by the user");
                // Sync button will be restored on next status update.
                view.sync.setText(R.string.stopping);
                view.sync.setEnabled(false);
                MainApplication.bus.post(new TaskCanceled());
            }
        }
    }

    private void startSync() {
        startSync("");
    }

    private void startSync(String messagesUuid) {
        log("syncMessages messagesUuid: " + messagesUuid);

        syncPendingMessagesServiceIntent = new Intent(getActivity(),
                SyncPendingMessagesService.class);

        syncPendingMessagesServiceIntent.putExtra(
                ServicesConstants.MESSAGE_UUID, messagesUuid);
        syncPendingMessagesServiceIntent.putExtra(SyncType.EXTRA,
                SyncType.MANUAL.name());
        getActivity().startService(syncPendingMessagesServiceIntent);

    }

    /**
     * The last time the sync item was done.
     * 
     * @param lastSync
     * @return
     */
    private String getLastSyncText(final long lastSync) {
        return getString(R.string.idle_details,
                lastSync < 0 ? getString(R.string.never) :
                        DateFormat.getDateTimeInstance().format(new Date(lastSync)));

    }

    public boolean performAction(MenuItem item, LinkedHashSet<Integer> selectedItemPositions) {
        log("performAction()");
       // messageUuid = adapter.getItem(position).getMessageUuid();
        toastLong("total: "+selectedItemPositions.size());
        if (item.getItemId() == R.id.context_delete) {

            performDeleteById();
            return (true);

        } else if (item.getItemId() == R.id.context_sync) {
            // Synchronize by ID
            startSync(messageUuid);
        }
        return (false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        log("onOptionsItemSelected()");
        if (item.getItemId() == R.id.import_sms) {
            importAllSms();
        } else if (item.getItemId() == R.id.delete) {
            performDeleteAll();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Delete all messages
     */
    private void performDeleteAll() {
        log("perofrmDeleteAll()");
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
                                mHandler.post(mDeleteAllMessages);
                                adapter.refresh();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Import all messages from the Android messaging inbox
     */
    private void importAllSms() {
        log("importAllSms()");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.confirm_sms_import))
                .setCancelable(false)
                .setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ImportMessagesTask importMessagesTask = new ImportMessagesTask(
                                        getActivity());
                                importMessagesTask.execute();

                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Delete message by it's id
     */
    public void performDeleteById() {
        log("performDeleteById()");
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
                                mHandler.post(mDeleteMessagesById);
                                adapter.refresh();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // Display pending messages.
    final Runnable mUpdateListView = new Runnable() {
        public void run() {
            log("mUpdateListView()");
            showMessages();
        }
    };

    // Synchronize all pending messages.
    final Runnable mSyncMessages = new Runnable() {
        public void run() {
            log("mSyncMessages()");
            Prefs.loadPreferences(getActivity());
            if (Prefs.enabled) {
                int result = 0;
                try {
                    if (result == 0) {
                        toastLong(R.string.sending_succeeded);
                    } else if (result == 1) {
                        toastLong(R.string.sending_failed);
                    }
                } catch (Exception e) {
                    return;
                }
            } else {
                toastLong(R.string.smssync_not_enabled);
            }
        }
    };

    /**
     * Synchronize all pending messages by message id. Which means it
     * synchronizes messages individually.
     */
    final Runnable mSyncMessagesById = new Runnable() {
        public void run() {
            log("mSyncMessagesById()");
            Prefs.loadPreferences(getActivity());
            if (Prefs.enabled) {
                int result = 0;
                try {
                    if (result == 0) {
                        toastLong(R.string.sending_succeeded);
                        showMessages();
                    } else if (result == 1) {
                        toastLong(R.string.sync_failed);
                    }
                } catch (Exception e) {
                    return;
                }
            } else {
                toastLong(R.string.smssync_not_enabled);
            }
        }
    };

    /**
     * Delete all messages. 0 - Successfully deleted. 1 - There is nothing to be
     * deleted.
     */
    final Runnable mDeleteAllMessages = new Runnable() {
        public void run() {
            log("mDeleteAllMessages()");
            getActivity().setProgressBarIndeterminateVisibility(true);
            boolean result = false;

            int deleted = 0;

            if (adapter.getCount() == 0) {
                deleted = 1;
            } else {
                result = model.deleteAllMessages();
            }

            try {
                if (deleted == 1) {
                    toastLong(R.string.no_messages_to_delete);
                } else {
                    if (result) {

                        toastLong(R.string.messages_deleted);
                        showMessages();
                    } else {
                        toastLong(R.string.messages_deleted_failed);
                    }
                }
                getActivity().setProgressBarIndeterminateVisibility(false);
            } catch (Exception e) {
                return;
            }
        }
    };

    /**
     * Delete individual messages 0 - Successfully deleted. 1 - There is nothing
     * to be deleted.
     */
    final Runnable mDeleteMessagesById = new Runnable() {
        public void run() {
            log("mDeleteMessagesById()");
            getActivity().setProgressBarIndeterminateVisibility(true);
            boolean result = false;

            int deleted = 0;

            if (adapter.getCount() == 0) {
                deleted = 1;
            } else {
                result = model.deleteMessagesByUuid(messageUuid);
            }

            try {
                if (deleted == 1) {
                    toastLong(R.string.no_messages_to_delete);
                } else {

                    if (result) {
                        toastLong(R.string.messages_deleted);

                    } else {
                        toastLong(R.string.messages_deleted_failed);
                    }
                }
                showMessages();
                getActivity().setProgressBarIndeterminateVisibility(false);
            } catch (Exception e) {
                return;
            }
        }
    };

    /**
     * Get messages from the database.
     * 
     * @return void
     */
    public void showMessages() {
        log("showMessages()");
        if (adapter != null) {
            adapter.refresh();
        }
    }

    @Subscribe
    public void syncStateChanged(final SyncPendingMessagesState newState) {

        log("syncChanged:" + newState);
        if (view == null || newState.syncType.isBackground())
            return;

        stateChanged(newState);

        switch (newState.state) {
            case FINISHED_SYNC:
                finishedSync(newState);
                break;
            case SYNC:
                log("In sync state " + " items to sync: " + newState.itemsToSync + " syncdItems "
                        + newState.currentSyncedItems);
                view.sync.setText(R.string.cancel);

                view.status.setText(R.string.working);
                view.details.setText(newState
                        .getNotification(getActivity().getResources()));
                view.progressStatus.setIndeterminate(false);
                view.progressStatus.setProgress(newState.currentSyncedItems);
                view.progressStatus.setMax(newState.itemsToSync);
                break;
            case CANCELED_SYNC:
                view.status.setText(R.string.canceled);

                view.details.setText(getString(R.string.sync_canceled_details,
                        newState.currentSyncedItems,
                        newState.itemsToSync));
                break;
        }

    }

    private void finishedSync(SyncPendingMessagesState state) {
        int syncCount = state.currentSyncedItems;
        String text = null;
        if (syncCount > 0) {
            text = getActivity().getResources().getQuantityString(
                    R.plurals.sync_done_details, syncCount,
                    syncCount);
        } else if (syncCount == 0) {
            text = getActivity().getString(R.string.empty_list);
        }
        view.status.setText(R.string.done);
        view.status.setTextColor(getActivity().getResources().getColor(R.color.status_done));
        view.details.setText(text);
        showMessages();
    }

    private void stateChanged(State state) {
        setViewAttributes(state.state);
        switch (state.state) {
            case INITIAL:
                idle();

                break;
            case ERROR:
                final String errorMessage = state.getError(getActivity().getResources());
                view.status.setText(R.string.error);
                view.status.setText(getActivity().getString(
                        R.string.sync_error_details,
                        errorMessage == null ? "N/A" : errorMessage));
                break;
        }
    }

    private void setViewAttributes(final SyncState state) {

        switch (state) {
            case SYNC:
                view.status
                        .setTextColor(getActivity().getResources().getColor(R.color.status_sync));

                break;
            case ERROR:
                view.progressStatus.setProgress(0);
                view.progressStatus.setIndeterminate(false);
                view.status.setTextColor(getActivity().getResources()
                        .getColor(R.color.status_error));

                setButtonsToDefault();
                break;
            default:
                view.progressStatus.setProgress(0);
                view.progressStatus.setIndeterminate(false);
                view.status
                        .setTextColor(getActivity().getResources().getColor(R.color.status_idle));
                setButtonsToDefault();
                break;
        }
    }

    private void setButtonsToDefault() {

        view.sync.setEnabled(true);
        view.sync.setText(R.string.sync);
    }

    @Override
    protected void onLoaded(boolean success) {
        // TODO Auto-generated method stub
    }

    // Thread class to handle synchronous execution of message importation task.
    private class ImportMessagesTask extends ProgressTask {

        protected Integer status;

        protected Context appContext;

        public ImportMessagesTask(Activity activity) {
            super(activity, R.string.please_wait);
            appContext = activity;
        }

        @Override
        protected Boolean doInBackground(String... args) {

            status = new ProcessSms(appContext).importMessages();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                if (status == 0) {
                    showMessages();
                } else if (status == 1) {
                    toastLong(R.string.nothing_to_import);
                }
            }
        }
    }

    /**
     * This will refresh content of the listview aka the pending messages when
     * smssync successfully syncs pending messages.
     */
    private BroadcastReceiver failedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                int status = intent.getIntExtra("failed", 1);

                if (status == 0) {
                    mHandler.post(mUpdateListView);
                }
            }
        }
    };

    // when sms has been sent
    private BroadcastReceiver smsSentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int result = getResultCode();
            log("smsSentReceiver onReceive result: " + result);
            switch (result) {
                case Activity.RESULT_OK:
                    toastLong(R.string.sms_status_success);
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    toastLong(R.string.sms_delivery_status_failed);
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    toastLong(R.string.sms_delivery_status_no_service);
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    toastLong(R.string.sms_delivery_status_null_pdu);
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    toastLong(R.string.sms_delivery_status_radio_off);
                    break;
            }
        }
    };
    // when sms has been delivered
    private BroadcastReceiver smsDeliveredReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            int result = getResultCode();
            log("smsDeliveredReceiver onReceive result: "
                    + result);
            switch (result) {
                case Activity.RESULT_OK:
                    toastLong(R.string.sms_delivered);
                    break;
                case Activity.RESULT_CANCELED:
                    toastLong(R.string.sms_not_delivered);
                    break;
            }
        }
    };

}
