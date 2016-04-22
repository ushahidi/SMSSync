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

package org.addhen.smssync.presentation.receiver;

import org.addhen.smssync.R;
import org.addhen.smssync.data.PrefsFactory;
import org.addhen.smssync.data.cache.FileManager;
import org.addhen.smssync.presentation.App;
import org.addhen.smssync.presentation.service.CheckTaskService;
import org.addhen.smssync.presentation.service.Scheduler;
import org.addhen.smssync.presentation.service.ServiceConstants;
import org.addhen.smssync.presentation.service.ServiceControl;
import org.addhen.smssync.presentation.service.SyncPendingMessagesService;
import org.addhen.smssync.presentation.task.SyncType;
import org.addhen.smssync.presentation.util.TimeFrequencyUtil;
import org.addhen.smssync.presentation.util.Utility;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

/**
 * This Receiver class listens for system boot. If SMSsync has been enabled run the app.
 *
 * @author Henry Addo
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean rebooted = intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED);
        boolean shutdown = intent.getAction().equals(Intent.ACTION_SHUTDOWN);
        FileManager fileManager = App.getAppComponent().fileManager();
        PrefsFactory prefsFactory = App.getAppComponent().prefsFactory();
        if (shutdown) {
            final long currentTime = System.currentTimeMillis();
            final String time = Utility.formatTimestamp(context, currentTime);
            fileManager.append(context.getString(R.string.device_shutdown, time));
        }

        if (rebooted) {
            fileManager.append(context.getString(R.string.device_reboot));
            // Is SMSsync enabled
            if (prefsFactory.serviceEnabled().get()) {

                // show notification
                Utility.showNotification(context);

                // Push any pending messages now that we have connectivity
                if (prefsFactory.enableAutoSync().get()) {

                    Intent syncPendingMessagesServiceIntent = new Intent(context,
                            SyncPendingMessagesService.class);

                    syncPendingMessagesServiceIntent.putStringArrayListExtra(
                            ServiceConstants.MESSAGE_UUID, new ArrayList<String>());
                    syncPendingMessagesServiceIntent.putExtra(SyncType.EXTRA,
                            SyncType.MANUAL.name());
                    context.startService(syncPendingMessagesServiceIntent);

                    // start the scheduler for auto sync service
                    long interval = TimeFrequencyUtil
                            .calculateInterval(prefsFactory.autoTime().get());
                    new Scheduler(context, fileManager,
                            new Intent(context, AutoSyncScheduledReceiver.class),
                            ServiceConstants.AUTO_SYNC_SCHEDULED_SERVICE_REQUEST_CODE,
                            PendingIntent.FLAG_UPDATE_CURRENT).updateScheduler(
                            interval);
                }

                // Enable Task service
                if (prefsFactory.enableTaskCheck().get()) {
                    CheckTaskService.sendWakefulWork(context, CheckTaskService.class);

                    // start the scheduler for 'task check' service
                    long interval = TimeFrequencyUtil.calculateInterval(
                            prefsFactory.taskCheckTime().get());
                    new Scheduler(context, fileManager,
                            new Intent(context, CheckTaskScheduledReceiver.class),
                            ServiceConstants.CHECK_TASK_SCHEDULED_SERVICE_REQUEST_CODE,
                            PendingIntent.FLAG_UPDATE_CURRENT).updateScheduler(
                            interval);
                }

                // Start the service message results api service
                new ServiceControl(prefsFactory, context, fileManager).runMessageResultsService();
            }
        }
    }
}
