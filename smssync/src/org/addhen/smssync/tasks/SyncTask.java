
package org.addhen.smssync.tasks;

import org.addhen.smssync.models.MessagesModel;
import org.addhen.smssync.models.SyncUrlModel;
import org.addhen.smssync.services.SyncPendingMessagesService;
import org.addhen.smssync.tasks.state.SyncState;
import org.addhen.smssync.tasks.state.MessageSyncState;
import org.addhen.smssync.util.Logger;
import org.addhen.smssync.util.MessageSyncUtil;
import org.addhen.smssync.util.ServicesConstants;

import android.os.AsyncTask;

import static org.addhen.smssync.tasks.state.SyncState.FINISHED_SYNC;
import static org.addhen.smssync.tasks.state.SyncState.ERROR;

/**
 * Provide a background service for synchronizing huge messages
 */
public class SyncTask extends AsyncTask<SyncConfig, MessageSyncState, MessageSyncState> {

    private final SyncPendingMessagesService mService;

    private final static String TAG = SyncTask.class.getSimpleName();

    private final MessagesModel messagesModel;

    private SyncUrlModel model;

    public SyncTask(SyncPendingMessagesService service) {
        this.mService = service;
        this.messagesModel = new MessagesModel();
        this.model = new SyncUrlModel();
    }

    @Override
    protected MessageSyncState doInBackground(SyncConfig... params) {
        final SyncConfig config = params[0];
        if (config.skip) {
            Logger.log(TAG, "Backup skipped");

            /*
             * for (DataType type : new DataType[] { SMS, MMS, CALLLOG }) {
             * type.setMaxSyncedDate(service, fetcher.getMaxData(type)); }
             */

            Logger.log(TAG, "All messages skipped.");
            return new MessageSyncState(FINISHED_SYNC, 0, 0, SyncType.MANUAL, null, null);
        }

        final int pendingMsgCount;
        try {

            final int itemsToSync = messagesModel.totalMessages();

            if (itemsToSync > 0) {
                // Sync pending messages
                Logger.log(TAG, "Sync pending messages: " + itemsToSync);

                return backup(config, smsItems, mmsItems, callLogItems, whatsAppItems,
                        itemsToSync);

            } else {

                Logger.log(TAG, "Nothing to do.");
                return transition(FINISHED_SYNC, null);
            }
        } catch (Exception e) {

            return transition(ERROR, e);
        }
    }

    private MessageSyncState transition(SyncState state, Exception exception) {
        return null;
        // return service.getState().transition(state, exception);
    }

    private void sync(String messageUuid) {

        if (messagesModel.totalMessages() > 0) {

            // This code is a bit retard
            for (SyncUrlModel syncUrl : model
                    .loadByStatus(ServicesConstants.ACTIVE_SYNC_URL)) {

                new MessageSyncUtil(mService, syncUrl.getUrl())
                        .syncToWeb(messageUuid);

            }

        }
    }

    private void publishState(SyncState state) {
        publishState(state, null);
    }

    private void publishState(SyncState state, Exception e) {
        publishProgress(mService.getState().transition(state, e));
    }
}
