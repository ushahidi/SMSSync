
package org.addhen.smssync.tasks;

import org.addhen.smssync.services.SyncPendingMessagesService;
import org.addhen.smssync.tasks.state.SyncState;
import org.addhen.smssync.util.Logger;

import android.os.AsyncTask;

import static org.addhen.smssync.tasks.state.SyncState.FINISHED_SYNC;

/**
 * Provide a background service for synchronizing huge messages
 */
public class SyncTask extends AsyncTask<SyncConfig, SyncState, SyncState> {

    private final SyncPendingMessagesService mService;
    
    private final static String TAG = SyncTask.class.getSimpleName();
    SyncTask(SyncPendingMessagesService service) {
        this.mService = service;
    }

    @Override
    protected SyncState doInBackground(SyncConfig... params) {
        final SyncConfig config = params[0];
        if (config.skip) {
            Logger.log(TAG, "Backup skipped");
            appLog(R.string.app_log_skip_backup_skip_messages);
            for (DataType type : new DataType[] {
                    SMS, MMS, CALLLOG
            }) {
                type.setMaxSyncedDate(service, fetcher.getMaxData(type));
            }
            Log.i(TAG, "All messages skipped.");
            return new SyncState(FINISHED_SYNC, 0, 0, SyncType.MANUAL, null, null);
        }

        Cursor smsItems = null;
        Cursor mmsItems = null;
        Cursor callLogItems = null;
        Cursor whatsAppItems = null;
        final int smsCount, mmsCount, callLogCount, whatsAppItemsCount;
        try {
            service.acquireLocks();
            int max = config.maxItemsPerSync;

            smsItems = fetcher.getItemsForDataType(SMS, config.groupToBackup, max);
            smsCount = smsItems != null ? smsItems.getCount() : 0;
            max -= smsCount;

            mmsItems = fetcher.getItemsForDataType(MMS, config.groupToBackup, max);
            mmsCount = mmsItems != null ? mmsItems.getCount() : 0;
            max -= mmsCount;

            callLogItems = fetcher.getItemsForDataType(CALLLOG, config.groupToBackup, max);
            callLogCount = callLogItems != null ? callLogItems.getCount() : 0;
            max -= callLogCount;

            whatsAppItems = fetcher.getItemsForDataType(DataType.WHATSAPP, config.groupToBackup,
                    max);
            whatsAppItemsCount = whatsAppItems != null ? whatsAppItems.getCount() : 0;

            final int itemsToSync = smsCount + mmsCount + callLogCount + whatsAppItemsCount;

            if (itemsToSync > 0) {
                if (!AuthPreferences.isLoginInformationSet(service)) {
                    appLog(R.string.app_log_missing_credentials);
                    return transition(ERROR, new RequiresLoginException());
                } else {
                    appLog(R.string.app_log_backup_messages, smsCount, mmsCount, callLogCount);
                    return backup(config, smsItems, mmsItems, callLogItems, whatsAppItems,
                            itemsToSync);
                }
            } else {
                appLog(R.string.app_log_skip_backup_no_items);

                if (Preferences.isFirstBackup(service)) {
                    // If this is the first backup we need to write something to
                    // MAX_SYNCED_DATE
                    // such that we know that we've performed a backup before.
                    SMS.setMaxSyncedDate(service, Defaults.MAX_SYNCED_DATE);
                    MMS.setMaxSyncedDate(service, Defaults.MAX_SYNCED_DATE);
                }
                Log.i(TAG, "Nothing to do.");
                return transition(FINISHED_BACKUP, null);
            }
        } catch (XOAuth2AuthenticationFailedException e) {
            if (e.getStatus() == 400) {
                Log.d(TAG, "need to perform xoauth2 token refresh");
                if (config.tries < 1 && refreshOAuth2Token(service)) {
                    try {
                        // we got a new token, let's retry one more time - we
                        // need to pass in a new store object
                        // since the auth params on it are immutable
                        return doInBackground(config.retryWithStore(service.getBackupImapStore()));
                    } catch (MessagingException ignored) {
                        Log.w(TAG, ignored);
                    }
                } else {
                    Log.w(TAG, "no new token obtained, giving up");
                }
            } else {
                Log.w(TAG, "unexpected xoauth status code " + e.getStatus());
            }
            return transition(ERROR, e);
        } catch (AuthenticationFailedException e) {
            return transition(ERROR, e);
        } catch (MessagingException e) {
            return transition(ERROR, e);
        } catch (ConnectivityException e) {
            return transition(ERROR, e);
        } finally {
            service.releaseLocks();
            try {
                if (smsItems != null)
                    smsItems.close();
                if (mmsItems != null)
                    mmsItems.close();
                if (callLogItems != null)
                    callLogItems.close();
                if (whatsAppItems != null)
                    whatsAppItems.close();
            } catch (Exception ignore) {
                Log.e(TAG, "error", ignore);
            }
        }
    }
}
