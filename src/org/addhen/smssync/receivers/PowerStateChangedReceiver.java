
package org.addhen.smssync.receivers;

import org.addhen.smssync.SmsSyncPref;
import org.addhen.smssync.Util;
import org.addhen.smssync.services.SmsSyncAutoSyncService;
import org.addhen.smssync.services.SmsSyncTaskCheckService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * The manifest Receiver is used to detect changes in battery state. When the
 * system broadcasts a "Battery Low" warning we turn off we stop all enabled
 * services When the system broadcasts "Battery OK" to indicate the battery has
 * returned to an okay state, we start all enabled services
 */

public class PowerStateChangedReceiver extends BroadcastReceiver {

    private boolean batteryLow;

    private Intent smsSyncAutoSyncServiceIntent;

    private Intent smsSyncTaskCheckServiceIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        batteryLow = intent.getAction().equals(Intent.ACTION_BATTERY_LOW);
        // load current settings
        SmsSyncPref.loadPreferences(context);
        if (batteryLow) {
            // is smssync enabled
            if (SmsSyncPref.enabled) {

                // clear all notifications
                Util.clearNotify(context);

                // Stop the service that pushes pending messages
                if (SmsSyncPref.enableAutoSync) {
                    smsSyncAutoSyncServiceIntent = new Intent(context, SmsSyncAutoSyncService.class);
                    context.stopService(smsSyncAutoSyncServiceIntent);
                }

                // Stop the service that checks for tasks
                if (SmsSyncPref.enableTaskCheck) {
                    smsSyncTaskCheckServiceIntent = new Intent(context,
                            SmsSyncTaskCheckService.class);
                    context.stopService(smsSyncTaskCheckServiceIntent);
                }
            }
        }
    }
}
