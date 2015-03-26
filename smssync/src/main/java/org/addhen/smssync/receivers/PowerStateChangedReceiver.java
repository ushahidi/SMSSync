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

package org.addhen.smssync.receivers;

import org.addhen.smssync.prefs.Prefs;
import org.addhen.smssync.R;
import org.addhen.smssync.controllers.AlertCallbacks;
import org.addhen.smssync.services.CheckTaskService;
import org.addhen.smssync.services.SmsSyncServices;
import org.addhen.smssync.services.SyncPendingMessagesService;
import org.addhen.smssync.util.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

/**
 * The manifest Receiver is used to detect changes in battery state. When the system broadcasts a
 * "Battery Low" warning we turn off we stop all enabled services. When the system broadcasts
 * "Battery OK" to indicate the battery has returned to an okay state, we start all enabled
 * services
 */

public class PowerStateChangedReceiver extends BroadcastReceiver {

    private boolean batteryLow;

    private Intent smsSyncAutoSyncServiceIntent;

    private Intent smsSyncTaskCheckServiceIntent;

    @Override
    public void onReceive(final Context context, Intent intent) {
        batteryLow = intent.getAction().equals(Intent.ACTION_BATTERY_LOW);
        boolean batteryOkay = intent.getAction().equals(Intent.ACTION_BATTERY_OKAY);

        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        final int percentage = Util.calculateBatteryLevel(level,scale);

        Prefs prefs = new Prefs(context);
        if (batteryLow) {
            // is smssync enabled
            Util.logActivities(context, context.getString(R.string.battery_low));
            if (prefs.serviceEnabled().get()) {

                // clear all notifications
                Util.clearNotify(context);

                // Stop the service that pushes pending messages
                if (prefs.enableAutoSync().get()) {
                    smsSyncAutoSyncServiceIntent = new Intent(context,
                            SyncPendingMessagesService.class);
                    context.stopService(smsSyncAutoSyncServiceIntent);
                }

                // Stop the service that checks for tasks
                if (prefs.enableTaskCheck().get()) {
                    smsSyncTaskCheckServiceIntent = new Intent(context,
                            CheckTaskService.class);
                    context.stopService(smsSyncTaskCheckServiceIntent);
                }
            }

            final AlertCallbacks alertCallbacks = new AlertCallbacks(prefs);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    alertCallbacks.lowBatteryLevelRequest(percentage);
                }
            }).start();
        }

        if (batteryOkay) {
            Util.logActivities(context, context.getString(R.string.battery_okay));
            // is smssync enabled
            if (prefs.serviceEnabled().get()) {

                // clear all notifications
                Util.clearNotify(context);

                // Stop the service that pushes pending messages
                if (prefs.enableAutoSync().get()) {
                    smsSyncAutoSyncServiceIntent = new Intent(context,
                            SyncPendingMessagesService.class);
                    SmsSyncServices.sendWakefulWork(context, smsSyncAutoSyncServiceIntent);
                }

                // Stop the service that checks for tasks
                if (prefs.enableTaskCheck().get()) {
                    smsSyncTaskCheckServiceIntent = new Intent(context,
                            CheckTaskService.class);
                    SmsSyncServices.sendWakefulWork(context, smsSyncTaskCheckServiceIntent);
                }
            }
        }

    }
}
