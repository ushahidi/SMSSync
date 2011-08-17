
package org.addhen.smssync.receivers;

import org.addhen.smssync.SmsSyncPref;
import org.addhen.smssync.Util;
import org.addhen.smssync.services.SmsSyncAutoSyncService;
import org.addhen.smssync.services.SmsSyncTaskCheckService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This Receiver class listens for system boot. If smssync has been enabled run
 * the app.
 */
public class BootReceiver extends BroadcastReceiver {

    private Intent smsSyncAutoSyncServiceIntent;

    private Intent smsSyncTaskCheckServiceIntent;

    private boolean isConnected;

    @Override
    public void onReceive(Context context, Intent intent) {

        // load current settings
        SmsSyncPref.loadPreferences(context);

        // is smssync enabled
        if (SmsSyncPref.enabled) {

            // show notification
            Util.showNotification(context);

            // start pushing pending messages
            isConnected = Util.isConnected(context);

            // do we have data network?
            if (isConnected) {
                // Push any pending messages now that we have connectivity
                if (SmsSyncPref.enableAutoSync) {
                    smsSyncAutoSyncServiceIntent = new Intent(context, SmsSyncAutoSyncService.class);
                    context.startService(smsSyncAutoSyncServiceIntent);
                }

                // Check for tasks now that we have connectivity
                if (SmsSyncPref.enableTaskCheck) {
                    smsSyncTaskCheckServiceIntent = new Intent(context,
                            SmsSyncTaskCheckService.class);
                    context.startService(smsSyncTaskCheckServiceIntent);
                }
            }
        }
    }
}
