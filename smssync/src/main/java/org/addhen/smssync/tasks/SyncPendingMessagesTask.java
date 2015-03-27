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

package org.addhen.smssync.tasks;

import com.squareup.otto.Subscribe;

import org.addhen.smssync.App;
import org.addhen.smssync.SyncDate;
import org.addhen.smssync.exceptions.ConnectivityException;
import org.addhen.smssync.messages.ProcessMessage;
import org.addhen.smssync.messages.ProcessSms;
import org.addhen.smssync.models.Message;
import org.addhen.smssync.prefs.Prefs;
import org.addhen.smssync.services.SyncPendingMessagesService;
import org.addhen.smssync.tasks.state.SyncPendingMessagesState;
import org.addhen.smssync.tasks.state.SyncState;
import org.addhen.smssync.util.Logger;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.addhen.smssync.tasks.state.SyncState.CANCELED_SYNC;
import static org.addhen.smssync.tasks.state.SyncState.ERROR;
import static org.addhen.smssync.tasks.state.SyncState.FINISHED_SYNC;
import static org.addhen.smssync.tasks.state.SyncState.INITIAL;
import static org.addhen.smssync.tasks.state.SyncState.SYNC;

/**
 * Provide a background service for asynchronous synchronizing of huge messages
 */
public class SyncPendingMessagesTask extends
        AsyncTask<SyncConfig, SyncPendingMessagesState, SyncPendingMessagesState> {

    private final SyncPendingMessagesService mService;

    private final static String CLASS_TAG = SyncPendingMessagesTask.class.getSimpleName();

    private int itemsToSync;

    ProcessMessage mProcessMessage;

    /**
     * Default constructor
     *
     * @param service The sync service
     */
    public SyncPendingMessagesTask(SyncPendingMessagesService service) {
        this.mService = service;
    }

    @Override
    protected SyncPendingMessagesState doInBackground(SyncConfig... params) {
        final SyncConfig config = params[0];
        if (config.skip) {
            Logger.log(CLASS_TAG, "Sync skipped");
            // In case a user decides to skip the sync process
            return new SyncPendingMessagesState(FINISHED_SYNC, 0, 0, 0, 0, SyncType.MANUAL, null);
        }

        return sync(config);

    }

    @Override
    protected void onPreExecute() {
        // register bus for passing events around
        App.bus.register(this);
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
        // un-register bus to stop listening for events
        App.bus.unregister(this);
    }

    @Override
    protected void onCancelled() {
        post(transition(CANCELED_SYNC, null));
        App.bus.unregister(this);
    }

    private void post(SyncPendingMessagesState state) {
        App.bus.post(state);
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

        final SyncStatus syncdStatus = syncPending(config);

        if (itemsToSync == 0) {
            Logger.log(CLASS_TAG, "Nothing to do.");
            return transition(FINISHED_SYNC, null);
        }

        new SyncDate(new Prefs(mService.getApplicationContext())).setLastSyncedDate(
                System.currentTimeMillis());

        Logger.log("SyncPendingMessages", "successful: " + syncdStatus.successful + " failed: "
                + syncdStatus.failed);

        return new SyncPendingMessagesState(FINISHED_SYNC,
                syncdStatus.successful,
                syncdStatus.failed,
                syncdStatus.progress,
                itemsToSync,
                config.syncType, null);

    }

    /**
     * Sync pending messages to the enabled sync URL.
     *
     * @param config The sync configuration.
     * @return int The number of syncd items
     */
    private SyncStatus syncPending(SyncConfig config) {
        // sync pending messages
        int syncdItems = 0;
        int failedItems = 0;
        int progress = 0;
        SyncStatus syncStatus = new SyncStatus();
        mProcessMessage = new ProcessMessage(mService.getApplicationContext(),new ProcessSms(mService.getApplicationContext()));
        List<Message> listMessages = new ArrayList<>();

        // determine if syncing by message UUID
        if (config.messageUuids != null && config.messageUuids.size() > 0) {
            for (String messageUuid : config.messageUuids) {
                Message msg = App.getDatabaseInstance().getMessageInstance().fetchByUuid(messageUuid);
                listMessages.add(msg);

            }

        } else {

            // load all messages
            listMessages = App.getDatabaseInstance().getMessageInstance().fetchPending();
        }

        if (listMessages.size() > 0) {
            itemsToSync = listMessages.size();
            Logger.log(CLASS_TAG,
                    String.format(Locale.ENGLISH, "Starting to sync (%d messages)", itemsToSync));

            // keep the sync running as long as the service is not cancelled and
            // the syncd items is less than
            // the items to be syncd.

            while (!isCancelled() && progress < itemsToSync) {

                // iterate through the loaded messages and push to the web
                // service
                for (Message m : listMessages) {
                    progress++;
                    // route the message to the appropriate enabled sync URL
                    if (mProcessMessage.routePendingMessage(m)) {
                        // / if it successfully pushes message, purge the
                        // message from the db
                        // increment the number of syncd items
                        syncdItems++;

                    } else {
                        failedItems++;

                    }

                    // update the UI with progress of the sync progress
                    publishProgress(new SyncPendingMessagesState(SYNC, syncdItems, failedItems,
                            progress,
                            itemsToSync,
                            config.syncType, null));
                }
            }

        }

        return syncStatus.setSuccessfulCount(syncdItems).setFailedCount(failedItems)
                .setProgress(progress);

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
     * @param e     The exception
     */
    private void publishState(SyncState state, Exception e) {
        publishProgress(mService.getState().transition(state, e));
    }

    private class SyncStatus {

        int successful;

        int failed;

        int progress;

        public SyncStatus setSuccessfulCount(int count) {
            this.successful = count;
            return this;
        }

        public SyncStatus setFailedCount(int count) {
            this.failed = count;
            return this;
        }

        public SyncStatus setProgress(int progress) {
            this.progress = progress;
            return this;
        }
    }
}
