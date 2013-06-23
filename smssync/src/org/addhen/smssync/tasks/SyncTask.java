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
import org.addhen.smssync.MessageType;
import org.addhen.smssync.Prefs;
import org.addhen.smssync.ProcessSms;
import org.addhen.smssync.R;
import org.addhen.smssync.exceptions.ConnectivityException;
import org.addhen.smssync.models.MessagesModel;
import org.addhen.smssync.models.SyncUrlModel;
import org.addhen.smssync.net.MainHttpClient;
import org.addhen.smssync.services.SyncPendingMessagesService;
import org.addhen.smssync.tasks.state.MessageSyncState;
import org.addhen.smssync.tasks.state.SyncState;
import org.addhen.smssync.util.Logger;
import org.addhen.smssync.util.ServicesConstants;
import org.addhen.smssync.util.Util;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.squareup.otto.Subscribe;

/**
 * Provide a background service for asynchronous synchronizing of huge messages
 */
public class SyncTask extends AsyncTask<SyncConfig, MessageSyncState, MessageSyncState> {

    private final SyncPendingMessagesService mService;

    private final static String CLASS_TAG = SyncTask.class.getSimpleName();

    private final MessagesModel messagesModel;

    private SyncUrlModel model;

    private MessageType messageType;

    private int itemsToSync;

    private ProcessSms processSms;

    /**
     * Default constructor
     * 
     * @param service The sync service
     * @param messageType The message type being synchronize
     */
    public SyncTask(SyncPendingMessagesService service, MessageType messageType) {
        this.mService = service;
        this.messageType = messageType;
        this.messagesModel = new MessagesModel();
        this.model = new SyncUrlModel();

    }

    @Override
    protected MessageSyncState doInBackground(SyncConfig... params) {
        final SyncConfig config = params[0];
        if (config.skip) {
            Logger.log(CLASS_TAG, "Sync skipped");
            // In case a user decides to skip the sync process
            return new MessageSyncState(FINISHED_SYNC, 0, 0, SyncType.MANUAL, null, null);
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
    protected void onPostExecute(MessageSyncState result) {
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

    private void post(MessageSyncState state) {
        MainApplication.bus.post(state);
    }

    private MessageSyncState transition(SyncState state, Exception exception) {
        return mService.getState().transition(state, exception);
    }

    @Override
    protected void onProgressUpdate(MessageSyncState... progress) {
        if (progress != null && progress.length > 0 && !isCancelled()) {
            post(progress[0]);
        }
    }

    private MessageSyncState sync(SyncConfig config) {
        Logger.log(CLASS_TAG, "syncToWeb(): push pending messages to the Sync URL");
        publishState(INITIAL);

        int syncdItems = 0;
        switch (messageType) {
            case PENDING:
                syncdItems = syncPending(config);
                break;
            case TASK:
                syncdItems = syncTask(config);
        }

        if (syncdItems == 0) {

            Logger.log(CLASS_TAG, "Nothing to do.");
            return transition(FINISHED_SYNC, null);
        }
        messageType.setLastSyncedDate(mService.getApplicationContext(), System.currentTimeMillis());
        return new MessageSyncState(FINISHED_SYNC,
                syncdItems,
                itemsToSync,
                config.syncType, messageType, null);

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

        if (messagesModel.totalMessages() > 0) {
            itemsToSync = messagesModel.totalMessages();
            Logger.log(CLASS_TAG,
                    String.format(Locale.ENGLISH, "Starting to sync (%d messages)", itemsToSync));

            // keep the sync running as long as the service is not cancelled and
            // the syncd items is less than
            // the items to be syncd.
            while (!isCancelled() && syncdItems < itemsToSync) {

                // determine if syncing by message UUID
                if (config.messageUuid != null && !TextUtils.isEmpty(config.messageUuid)) {
                    messagesModel.loadByUuid(config.messageUuid);
                    listMessages = messagesModel.listMessages;

                } else {
                    // load all messages
                    messagesModel.load();
                    listMessages = messagesModel.listMessages;

                }

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
                        publishProgress(new MessageSyncState(SYNC, syncdItems, itemsToSync,
                                config.syncType, messageType, null));

                    }
                }
            }

        }
        return syncdItems;
    }

    private int syncTask(SyncConfig config) {
        int syncdItems = 0;
        JSONObject jsonObject;

        JSONArray jsonArray;
        Logger.log(CLASS_TAG, "checkTaskService: check if a task has been enabled.");

        Logger.log(CLASS_TAG,
                String.format(Locale.ENGLISH, "Starting to sync task (%d messages)", itemsToSync));

        // keep the sync running as long as the service is not cancelled and
        // the syncd items is less than
        // the items to be syncd.
        while (!isCancelled() && syncdItems < itemsToSync) {
            // load Prefs
            Prefs.loadPreferences(mService.getApplicationContext());
            for (SyncUrlModel syncUrl : model
                    .loadByStatus(ServicesConstants.ACTIVE_SYNC_URL)) {
                // validate configured url
                int status = Util.validateCallbackUrl(syncUrl.getUrl());
                if (status == 1) {
                    Util.showToast(mService.getApplicationContext(), R.string.no_configured_url);
                } else if (status == 2) {
                    Util.showToast(mService.getApplicationContext(), R.string.invalid_url);
                } else if (status == 3) {
                    Util.showToast(mService.getApplicationContext(), R.string.no_connection);
                } else {

                    StringBuilder uriBuilder = new StringBuilder(syncUrl.getUrl());

                    uriBuilder.append("?task=send");

                    String response = MainHttpClient.getFromWebService(uriBuilder
                            .toString());
                    Log.d(CLASS_TAG, "TaskCheckResponse: " + response);
                    String task = "";
                    String secret = "";
                    if (!TextUtils.isEmpty(response) && response != null) {

                        try {

                            jsonObject = new JSONObject(response);
                            JSONObject payloadObject = jsonObject
                                    .getJSONObject("payload");

                            if (payloadObject != null) {
                                task = payloadObject.getString("task");
                                secret = payloadObject.getString("secret");
                                if ((task.equals("send")) && (secret.equals(syncUrl.getSecret()))) {
                                    jsonArray = payloadObject.getJSONArray("messages");

                                    for (int index = 0; index < jsonArray.length(); ++index) {
                                        jsonObject = jsonArray.getJSONObject(index);

                                        processSms.sendSms(jsonObject.getString("to"),
                                                jsonObject.getString("message"));
                                        // increment the number of syncd items
                                        syncdItems++;

                                        // update the UI with progress of the
                                        // sync
                                        // progress
                                        publishProgress(new MessageSyncState(SYNC, syncdItems,
                                                itemsToSync,
                                                config.syncType, messageType, null));
                                    }

                                } else {
                                    Logger.log(CLASS_TAG,
                                            mService.getApplicationContext()
                                                    .getString(R.string.no_task));
                                }

                            } else { // 'payload' data may not be present in
                                     // JSON
                                     // response
                                Logger.log(CLASS_TAG,
                                        mService.getApplicationContext()
                                                .getString(R.string.no_task));
                            }

                        } catch (JSONException e) {
                            Logger.log(CLASS_TAG, "Error: " + e.getMessage());
                        }
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
