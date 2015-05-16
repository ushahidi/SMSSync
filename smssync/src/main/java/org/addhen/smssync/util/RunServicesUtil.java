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

package org.addhen.smssync.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import org.addhen.smssync.prefs.Prefs;
import org.addhen.smssync.receivers.AutoSyncScheduledReceiver;
import org.addhen.smssync.receivers.CheckTaskScheduledReceiver;
import org.addhen.smssync.receivers.MessageResultsScheduledReceiver;
import org.addhen.smssync.services.ScheduleServices;

/**
 * This will run all enabled and scheduled services.
 */
public class RunServicesUtil {

    public static final String CLASS_TAG = RunServicesUtil.class
            .getSimpleName();

    private Prefs prefs;

    private Context context;

    public RunServicesUtil(Prefs prefs) {
        this.prefs = prefs;
        context = prefs.getContext();
    }

    /**
     * Runs the {@link org.addhen.smssync.services.AutoSyncScheduledService
     * AutoSyncScheduledService}
     *
     * @return ScheduleServices
     */
    public void runAutoSyncService() {

        // Push any pending messages now that we have connectivity
        if (prefs.enableAutoSync().get() && prefs.serviceEnabled().get()) {

            // start the scheduler for auto sync service
            final long interval = TimeFrequencyUtil.calculateInterval(prefs.autoTime().get());
            final Intent intent = new Intent(context, AutoSyncScheduledReceiver.class);
            Logger.log(CLASS_TAG, "Auto sync service started");
            // run the service
            runServices(intent, ServicesConstants.AUTO_SYNC_SCHEDULED_SERVICE_REQUEST_CODE,
                    interval);

        }

    }

    /**
     * Stops the {@link org.addhen.smssync.services.CheckTaskScheduledService
     * CheckTaskScheduledService}
     *
     * @return void
     */
    public void stopCheckTaskService() {
        // Push any pending messages now that we have connectivity
        final Intent intent = new Intent(context, CheckTaskScheduledReceiver.class);

        // stop the scheduled service
        Logger.log(CLASS_TAG, "Stop CheckTaskScheduledService");
        stopServices(intent,
                ServicesConstants.CHECK_TASK_SCHEDULED_SERVICE_REQUEST_CODE);

    }

    /**
     * Stops the {@link org.addhen.smssync.services.AutoSyncScheduledService
     * AutoSyncScheduledService}
     *
     * @return void
     */
    public void stopAutoSyncService() {
        final Intent intent = new Intent(context,
                AutoSyncScheduledReceiver.class);

        // stop the scheduled service
        Logger.log(CLASS_TAG, "Stop AutoSyncScheduledService");
        stopServices(intent,
                ServicesConstants.AUTO_SYNC_SCHEDULED_SERVICE_REQUEST_CODE);

    }

    /**
     * Runs the {@link org.addhen.smssync.services.MessageResultsScheduledService}
     */
    public void runMessageResultsService() {
        Logger.log(CLASS_TAG, "Running MessageResultService " + prefs.taskCheckTime().get());
        // load preferences
        if (prefs.messageResultsAPIEnable().get() && prefs.serviceEnabled().get()) {
            // start the scheduler for 'message results' service
            final long interval = TimeFrequencyUtil.calculateInterval(prefs.taskCheckTime().get());
            final Intent intent = new Intent(context,
                    MessageResultsScheduledReceiver.class);
            Logger.log(CLASS_TAG, "Message Results service started - interval: " + interval);
            Util.logActivities(context, "Message Results service started - interval: " + interval);
            // run the service
            runServices(intent,
                    ServicesConstants.MESSAGE_RESULTS_SCHEDULED_SERVICE_REQUEST_CODE,
                    interval);
        }
    }

    /**
     * Stops the {@link org.addhen.smssync.services.MessageResultsScheduledService
     *
     * @param context the calling context
     */
    public void stopMessageResultsService() {
        final Intent intent = new Intent(context,
                MessageResultsScheduledReceiver.class);

        stopServices(intent, ServicesConstants.MESSAGE_RESULTS_SCHEDULED_SERVICE_REQUEST_CODE);

    }

    /**
     * Runs any enabled services. Making sure the device has internet connection before it attempts
     * to start any of the enabled services.
     *
     * @param intent      The intent to be started.
     * @param requestCode The private request code
     * @param interval    The interval in which to run the scheduled service.
     * @return void
     */
    public void runServices(Intent intent,
                            int requestCode, long interval) {
        // load current setting
        // is smssync enabled
        if (prefs.serviceEnabled().get()) {

            // show notification
            Util.showNotification(context);

            // start pushing pending messages

            // do we have data network?
            if (isConnected()) {
                Scheduler.INSTANCE.getScheduler(context, intent, requestCode)
                        .updateScheduler(interval);
            }
        }

    }

    /**
     * stop any enabled services.
     *
     * @param intent      The intent to be started.
     * @param requestCode The private request code
     * @return void
     */
    public void stopServices(Intent intent, int requestCode) {
        Logger.log(CLASS_TAG, "Stopping services");
        Scheduler.INSTANCE.getScheduler(prefs.getContext(), intent, requestCode).stopScheduler();
    }

    /**
     * Runs the {@link org.addhen.smssync.services.CheckTaskScheduledService
     * CheckTaskScheduledService}
     *
     * @return ScheduleServices
     */
    public void runCheckTaskService() {
        // Check for tasks now that we have connectivity
        Logger.log(CLASS_TAG, "Running CheckTaskService " + prefs.taskCheckTime().get());

        if (prefs.enableTaskCheck().get() && prefs.serviceEnabled().get()) {

            // start the scheduler for 'task check' service
            final long interval = TimeFrequencyUtil.calculateInterval(prefs.taskCheckTime().get());

            final Intent intent = new Intent(context,
                    CheckTaskScheduledReceiver.class);

            Logger.log(CLASS_TAG, "Check task service started - interval: " + interval);
            // run the service
            runServices(
                    intent,
                    ServicesConstants.CHECK_TASK_SCHEDULED_SERVICE_REQUEST_CODE,
                    interval);

        }

    }

    public boolean isConnected() {
        return Util.isConnected(context);
    }

    public enum Scheduler {
        INSTANCE;

        private ScheduleServices getScheduler(Context context, Intent intent, int requestCode) {
            return new ScheduleServices(context, intent, requestCode,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }
}
