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

import org.addhen.smssync.Prefs;
import org.addhen.smssync.R;
import org.addhen.smssync.services.CheckTaskService;
import org.addhen.smssync.services.ScheduleServices;
import org.addhen.smssync.services.SmsSyncServices;
import org.addhen.smssync.services.SyncPendingMessagesService;
import org.addhen.smssync.tasks.SyncType;
import org.addhen.smssync.util.ServicesConstants;
import org.addhen.smssync.util.Util;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This Receiver class listens for system boot. If smssync has been enabled run the app.
 */
public class BootReceiver extends BroadcastReceiver {

    private boolean isConnected;

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean rebooted = intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED);
        boolean shutdown = intent.getAction().equals(Intent.ACTION_SHUTDOWN);
        // load current settings
        Prefs.loadPreferences(context);
        if (shutdown) {
            Util.logActivities(context, context.getString(R.string.device_shutdown));
        }
        if (rebooted) {
            Util.logActivities(context, context.getString(R.string.device_reboot));
            // is smssync enabled
            if (Prefs.enabled) {

                // show notification
                Util.showNotification(context);

                // start pushing pending messages
                isConnected = Util.isConnected(context);

                // do we have data network?
                if (isConnected) {
                    // Push any pending messages now that we have connectivity
                    if (Prefs.enableAutoSync) {

                        Intent syncPendingMessagesServiceIntent = new Intent(context,
                                SyncPendingMessagesService.class);

                        syncPendingMessagesServiceIntent.putExtra(
                                ServicesConstants.MESSAGE_UUID, "");
                        syncPendingMessagesServiceIntent.putExtra(SyncType.EXTRA,
                                SyncType.MANUAL.name());
                        context.startService(syncPendingMessagesServiceIntent);

                        // start the scheduler for auto sync service
                        long interval = (Prefs.autoTime * 60000);
                        new ScheduleServices(
                                context,
                                new Intent(context, AutoSyncScheduledReceiver.class),
                                ServicesConstants.AUTO_SYNC_SCHEDULED_SERVICE_REQUEST_CODE,
                                PendingIntent.FLAG_UPDATE_CURRENT)
                                .updateScheduler(interval);
                    }

                    // Check for tasks now that we have connectivity
                    if (Prefs.enableTaskCheck) {
                        SmsSyncServices.sendWakefulTask(context,
                                CheckTaskService.class);

                        // start the scheduler for 'task check' service
                        long interval = (Prefs.taskCheckTime * 60000);
                        new ScheduleServices(
                                context,
                                new Intent(context, CheckTaskScheduledReceiver.class),
                                ServicesConstants.CHECK_TASK_SCHEDULED_SERVICE_REQUEST_CODE,
                                PendingIntent.FLAG_UPDATE_CURRENT)
                                .updateScheduler(interval);
                    }
                }
            }
        }
    }
}
