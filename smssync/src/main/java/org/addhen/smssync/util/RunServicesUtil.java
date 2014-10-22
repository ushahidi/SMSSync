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

package org.addhen.smssync.util;

import org.addhen.smssync.Prefs;
import org.addhen.smssync.receivers.AutoSyncScheduledReceiver;
import org.addhen.smssync.receivers.CheckTaskScheduledReceiver;
import org.addhen.smssync.receivers.MessageResultsScheduledReceiver;
import org.addhen.smssync.services.ScheduleServices;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * This will run all enabled and scheduled services.
 */
public class RunServicesUtil {

    public static final String CLASS_TAG = RunServicesUtil.class
            .getSimpleName();

    private ScheduleServices mScheduleServices;

    /**
     * Runs any enabled services. Making sure the device has internet connection before it attempts
     * to start any of the enabled services.
     *
     * @param context     The calling context.
     * @param intent      The intent to be started.
     * @param requestCode The private request code
     * @param interval    The interval in which to run the scheduled service.
     * @return void
     */
    public static void runServices(Context context, Intent intent,
            int requestCode, long interval) {
        // load current settings
        Prefs.loadPreferences(context);

        // is smssync enabled
        if (Prefs.enabled) {

            // show notification
            Util.showNotification(context);

            // start pushing pending messages
            final boolean isConnected = Util.isConnected(context);

            // do we have data network?
            if (isConnected) {
                Scheduler.INSTANCE.getScheduler(context, intent, requestCode)
                        .updateScheduler(interval);
            }
        }

    }

    /**
     * stop any enabled services.
     *
     * @param context     The calling context.
     * @param intent      The intent to be started.
     * @param requestCode The private request code
     * @return void
     */
    public static void stopServices(Context context, Intent intent, int requestCode) {
        Logger.log(CLASS_TAG, "Stopping services");
        Scheduler.INSTANCE.getScheduler(context, intent, requestCode).stopScheduler();
    }

    /**
     * Runs the {@link org.addhen.smssync.services.CheckTaskScheduledService
     * CheckTaskScheduledService}
     *
     * @param context the calling context
     * @return ScheduleServices
     */
    public static void runCheckTaskService(Context context) {
        // Check for tasks now that we have connectivity
        Logger.log(CLASS_TAG, "Running CheckTaskService " + Prefs.taskCheckTime);

        // load preferences
        Prefs.loadPreferences(context);
        if (Prefs.enableTaskCheck && Prefs.enabled) {

            // start the scheduler for 'task check' service
            final long interval = TimeFrequencyUtil.calculateInterval(Prefs.taskCheckTime);

            final Intent intent = new Intent(context,
                    CheckTaskScheduledReceiver.class);

            Logger.log(CLASS_TAG, "Check task service started");
            // run the service
            RunServicesUtil
                    .runServices(
                            context,
                            intent,

                            ServicesConstants.CHECK_TASK_SCHEDULED_SERVICE_REQUEST_CODE,
                            interval);

        }

    }

    /**
     * Runs the {@link org.addhen.smssync.services.AutoSyncScheduledService
     * AutoSyncScheduledService}
     *
     * @param context the calling context
     * @return ScheduleServices
     */
    public static void runAutoSyncService(Context context) {
        // Push any pending messages now that we have connectivity
        Prefs.loadPreferences(context);
        if (Prefs.enableAutoSync && Prefs.enabled) {
            // start the scheduler for auto sync service
            final long interval = TimeFrequencyUtil.calculateInterval(Prefs.autoTime);
            final Intent intent = new Intent(context, AutoSyncScheduledReceiver.class);
            Logger.log(CLASS_TAG, "Auto sync service started");
            // run the service
            RunServicesUtil.runServices(context, intent,
                    ServicesConstants.AUTO_SYNC_SCHEDULED_SERVICE_REQUEST_CODE,
                    interval);

        }
    }

    /**
     * Stops the {@link org.addhen.smssync.services.CheckTaskScheduledService
     * CheckTaskScheduledService}
     *
     * @param context the calling context
     * @return void
     */
    public static void stopCheckTaskService(Context context) {
        // Push any pending messages now that we have connectivity
        Prefs.loadPreferences(context);
        final Intent intent = new Intent(context,
                CheckTaskScheduledReceiver.class);

        // stop the scheduled service
        RunServicesUtil.stopServices(context, intent,
                ServicesConstants.CHECK_TASK_SCHEDULED_SERVICE_REQUEST_CODE);

    }

    /**
     * Stops the {@link org.addhen.smssync.services.AutoSyncScheduledService
     * AutoSyncScheduledService}
     *
     * @param context the calling context
     * @return void
     */
    public static void stopAutoSyncService(Context context) {
        Prefs.loadPreferences(context);
        final Intent intent = new Intent(context,
                AutoSyncScheduledReceiver.class);

        // stop the scheduled service
        RunServicesUtil.stopServices(context, intent,
                ServicesConstants.AUTO_SYNC_SCHEDULED_SERVICE_REQUEST_CODE);

    }


    /**
     * Runs the {@link org.addhen.smssync.services.MessageResultsScheduledService}
     *
     * @param context the calling context
     */
    public static void runMessageResultsService(Context context) {
        Logger.log(CLASS_TAG, "Running CheckTaskService " + Prefs.taskCheckTime);

        // load preferences
        Prefs.loadPreferences(context);
        if (Prefs.messageResultsAPIEnable && Prefs.enabled) {

            // start the scheduler for 'message results' service
            final long interval = TimeFrequencyUtil.calculateInterval(Prefs.taskCheckTime) * 60000;

            final Intent intent = new Intent(context,
                    MessageResultsScheduledReceiver.class);

            Logger.log(CLASS_TAG, "Message Results service started");
            // run the service
            RunServicesUtil
                    .runServices(
                            context,
                            intent,

                            ServicesConstants.MESSAGE_RESULTS_SCHEDULED_SERVICE_REQUEST_CODE,
                            interval);

        }
    }

    /**
     * Stops the {@link org.addhen.smssync.services.MessageResultsScheduledService
     *
     * @param context the calling context
     */
    public static void stopMessageResultsService(Context context) {
        Prefs.loadPreferences(context);
        final Intent intent = new Intent(context,
                MessageResultsScheduledReceiver.class);

        RunServicesUtil.stopServices(context, intent,
                ServicesConstants.MESSAGE_RESULTS_SCHEDULED_SERVICE_REQUEST_CODE);

    }

    public enum Scheduler {
        INSTANCE;

        private ScheduleServices getScheduler(Context context, Intent intent, int requestCode) {
            return new ScheduleServices(context, intent, requestCode,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }
}
