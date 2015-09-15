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

package org.addhen.smssync.presentation.task;

import com.squareup.otto.Subscribe;

import org.addhen.smssync.data.message.PostMessage;
import org.addhen.smssync.data.util.Logger;
import org.addhen.smssync.presentation.App;
import org.addhen.smssync.presentation.model.MessageModel;
import org.addhen.smssync.presentation.service.SyncPendingMessagesService;
import org.addhen.smssync.presentation.task.state.SyncPendingMessagesState;
import org.addhen.smssync.presentation.task.state.SyncState;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.addhen.smssync.presentation.task.state.SyncState.CANCELED_SYNC;
import static org.addhen.smssync.presentation.task.state.SyncState.FINISHED_SYNC;
import static org.addhen.smssync.presentation.task.state.SyncState.INITIAL;
import static org.addhen.smssync.presentation.task.state.SyncState.SYNC;


/**
 * @author Henry Addo
 */
public class SyncPendingMessagesTask extends AsyncTask<SyncConfig, SyncPendingMessagesState,
        SyncPendingMessagesState> {

    private final static String CLASS_TAG = SyncPendingMessagesTask.class.getSimpleName();

    private final SyncPendingMessagesService mService;

    private PostMessage mProcessMessage;

    private int itemsToSync;

    /**
     * Default constructor
     *
     * @param service The sync service
     */
    public SyncPendingMessagesTask(SyncPendingMessagesService service) {
        mService = service;
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

        Logger.log("SyncPendingMessages", "successful: " + syncdStatus.successful + " failed: "
                + syncdStatus.failed);

        return new SyncPendingMessagesState(FINISHED_SYNC, syncdStatus.successful,
                syncdStatus.failed, syncdStatus.progress, itemsToSync, config.syncType, null);

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
        List<MessageModel> listMessages = new ArrayList<>();

        // determine if syncing by message UUID
        if (config.messageUuids != null && config.messageUuids.size() > 0) {
            // TODO: get messages to sync

            if (listMessages.size() > 0) {
                itemsToSync = listMessages.size();
                Logger.log(CLASS_TAG,
                        String.format(Locale.ENGLISH, "Starting to sync (%d messages)",
                                itemsToSync));

                // keep the sync running as long as the service is not cancelled and
                // the syncd items is less than
                // the items to be syncd.

                while (!isCancelled() && progress < itemsToSync) {

                    // iterate through the loaded messages and push to the web
                    // service
                    for (MessageModel m : listMessages) {
                        progress++;
                        // route the message to the appropriate enabled sync URL
                        // TODO: Update successfully sync'd items and failed ones

                        // update the UI with progress of the sync progress
                        publishProgress(new SyncPendingMessagesState(SYNC, syncdItems, failedItems,
                                progress,
                                itemsToSync,
                                config.syncType, null));
                    }
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

    public class TaskCanceled {

    }
}
