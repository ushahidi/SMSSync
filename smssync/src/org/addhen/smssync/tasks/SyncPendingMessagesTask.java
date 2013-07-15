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

package org.addhen.smssync.tasks;

import static org.addhen.smssync.tasks.state.SyncState.CANCELED_SYNC;
import static org.addhen.smssync.tasks.state.SyncState.ERROR;
import static org.addhen.smssync.tasks.state.SyncState.FINISHED_SYNC;
import static org.addhen.smssync.tasks.state.SyncState.INITIAL;
import static org.addhen.smssync.tasks.state.SyncState.SYNC;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.addhen.smssync.MainApplication;
import org.addhen.smssync.ProcessSms;
import org.addhen.smssync.SyncDate;
import org.addhen.smssync.exceptions.ConnectivityException;
import org.addhen.smssync.models.MessagesModel;
import org.addhen.smssync.services.SyncPendingMessagesService;
import org.addhen.smssync.tasks.state.SyncPendingMessagesState;
import org.addhen.smssync.tasks.state.SyncState;
import org.addhen.smssync.util.Logger;

import android.os.AsyncTask;

import com.squareup.otto.Subscribe;

/**
 * Provide a background service for asynchronous synchronizing of huge messages
 */
public class SyncPendingMessagesTask extends
        AsyncTask<SyncConfig, SyncPendingMessagesState, SyncPendingMessagesState> {

    private final SyncPendingMessagesService mService;

    private final static String CLASS_TAG = SyncPendingMessagesTask.class.getSimpleName();

    private final MessagesModel messagesModel;

    private int itemsToSync;

    private ProcessSms processSms;

    /**
     * Default constructor
     * 
     * @param service The sync service
     * @param messageType The message type being synchronize
     */
    public SyncPendingMessagesTask(SyncPendingMessagesService service) {
        this.mService = service;
        this.messagesModel = new MessagesModel();
    }

    @Override
    protected SyncPendingMessagesState doInBackground(SyncConfig... params) {
        final SyncConfig config = params[0];
        if (config.skip) {
            Logger.log(CLASS_TAG, "Sync skipped");
            // In case a user decides to skip the sync process
            return new SyncPendingMessagesState(FINISHED_SYNC, 0, 0, SyncType.MANUAL, null);
        }

        try {
            // lock resources need to keep this sync alive
            mService.acquireLocks();

            return sync(config);

        } catch (ConnectivityException e) {
            Logger.log(CLASS_TAG, "No internet connection");
            return transition(ERROR, e);

        } finally {
            // release resources
            mService.releaseLocks();
        }
    }

    @Override
    protected void onPreExecute() {
        // register bus for passing events around
        MainApplication.bus.register(this);
    }

    @Subscribe
    public void taskCanceled(TaskCanceled canceled) {
        // cancel the sync process when the user hit to cancel button
        cancel(false);
    }

    @Override
    protected void onPostExecute(SyncPendingMessagesState result) {
        if (result != null) {
            post(result);
        }
        // unregister bus to stop listening for events
        MainApplication.bus.unregister(this);
    }

    @Override
    protected void onCancelled() {
        post(transition(CANCELED_SYNC, null));
        MainApplication.bus.unregister(this);
    }

    private void post(SyncPendingMessagesState state) {
        MainApplication.bus.post(state);
    }

    private SyncPendingMessagesState transition(SyncState state, Exception exception) {
        return mService.getState().transition(state, exception);
    }

    @Override
    protected void onProgressUpdate(SyncPendingMessagesState... progress) {
        if (progress != null && progress.length > 0 && !isCancelled()) {
            post(progress[0]);
        }
    }

    private SyncPendingMessagesState sync(SyncConfig config) {
        Logger.log(CLASS_TAG, "syncToWeb(): push pending messages to the Sync URL");
        publishState(INITIAL);

        int syncdItems = 0;

        syncdItems = syncPending(config);

        if (syncdItems == 0) {
            Logger.log(CLASS_TAG, "Nothing to do.");
            return transition(FINISHED_SYNC, null);
        }

        new SyncDate().setLastSyncedDate(mService.getApplicationContext(),
                System.currentTimeMillis());

        return new SyncPendingMessagesState(FINISHED_SYNC,
                syncdItems,
                itemsToSync,
                config.syncType, null);

    }

    /**
     * Sync pending messages to the enabled sync URL.
     * 
     * @param config The sync configuration.
     * @return int The number of syncd items
     */
    private int syncPending(SyncConfig config) {
        // sync pending messages
        int syncdItems = 0;

        processSms = new ProcessSms(mService.getApplicationContext());

        List<MessagesModel> listMessages = new ArrayList<MessagesModel>();

        // determine if syncing by message UUID
        if (config.messageUuids != null && config.messageUuids.size() > 0) {
            for (String messageUuid : config.messageUuids) {
                if (messagesModel.loadByUuid(messageUuid)) {
                    listMessages.add(messagesModel.listMessages.get(0));
                }
            }

        } else {
            // load all messages
            messagesModel.load();
            listMessages = messagesModel.listMessages;

        }

        if (listMessages.size() > 0) {
            itemsToSync = listMessages.size();
            Logger.log(CLASS_TAG,
                    String.format(Locale.ENGLISH, "Starting to sync (%d messages)", itemsToSync));

            // keep the sync running as long as the service is not cancelled and
            // the syncd items is less than
            // the items to be syncd.
            while (!isCancelled() && syncdItems < itemsToSync) {

                // iterate through the loaded messages and push to the web
                // service
                for (MessagesModel messages : listMessages) {

                    // route the message to the appropriate enabled sync URL
                    if (processSms.routePendingMessages(messages.getMessageFrom(),
                            messages.getMessage(), messages.getMessageDate(),
                            messages.getMessageUuid())) {

                        // / if it successfully pushes message, purge the
                        // message from the db
                        new MessagesModel().deleteMessagesByUuid(messages
                                .getMessageUuid());
                        // increment the number of syncd items
                        syncdItems++;

                        // update the UI with progress of the sync progress
                        publishProgress(new SyncPendingMessagesState(SYNC, syncdItems, itemsToSync,
                                config.syncType, null));

                    }
                }
            }

        }
        return syncdItems;
    }

    /**
     * Send the state of the sync to the UI
     * 
     * @param state The sync state
     */
    private void publishState(SyncState state) {
        publishState(state, null);
    }

    /**
     * Send the state of the sync to the UI
     * 
     * @param state The sync state
     * @param e The exception
     */
    private void publishState(SyncState state, Exception e) {
        publishProgress(mService.getState().transition(state, e));
    }
}
