
package org.addhen.smssync.services;

import org.addhen.smssync.MainApplication;
import org.addhen.smssync.util.ServicesConstants;
import org.addhen.smssync.util.Util;

import android.content.Intent;
import android.util.Log;

/**
 * This will sync pending messages as it's commanded by the user.
 * 
 * @author eyedol
 */
public class SyncPendingMessagesService extends SmsSyncServices {

    private static String CLASS_TAG = SyncPendingMessagesService.class.getSimpleName();

    private Intent statusIntent; // holds the status of the sync and sends it to

    private int messageId = 0;

    public SyncPendingMessagesService() {
        super(CLASS_TAG);
        statusIntent = new Intent(ServicesConstants.AUTO_SYNC_ACTION);
    }

    @Override
    protected void executeTask(Intent intent) {
        // SmsSyncPref.loadPreferences(SmsSyncAutoSyncService.this);
        Log.i(CLASS_TAG, "executeTask() executing this task");

        if (intent != null) {
            // get Id
            messageId = intent.getIntExtra(ServicesConstants.MESSEAGE_ID, messageId);
            if (MainApplication.mDb.fetchMessagesCount() > 0) {
                int status = Util.snycToWeb(SyncPendingMessagesService.this, messageId);
                statusIntent.putExtra("status", status);
                sendBroadcast(statusIntent);
            }
        }

    }

}
