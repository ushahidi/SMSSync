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

package org.addhen.smssync.presentation.service;

import org.addhen.smssync.data.PrefsFactory;
import org.addhen.smssync.data.cache.FileManager;
import org.addhen.smssync.data.util.Logger;
import org.addhen.smssync.presentation.receiver.AutoSyncScheduledReceiver;
import org.addhen.smssync.presentation.receiver.CheckTaskScheduledReceiver;
import org.addhen.smssync.presentation.receiver.MessageResultsScheduledReceiver;
import org.addhen.smssync.presentation.util.TimeFrequencyUtil;
import org.addhen.smssync.presentation.util.Utility;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;

/**
 * A convenient class for providing methods for controlling services
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class ServiceControl {

    public static final String CLASS_TAG = ServiceControl.class.getSimpleName();

    private PrefsFactory mPrefsFactory;

    private Context mContext;

    private FileManager mFileManager;

    @Inject
    public ServiceControl(PrefsFactory prefsFactory, Context context, FileManager fileManager) {
        mPrefsFactory = prefsFactory;
        mContext = context;
        mFileManager = fileManager;
    }

    /**
     * Runs the {@link org.addhen.smssync.presentation.service.AutoSyncScheduledService}
     *
     * @return ScheduleServices
     */
    public void runAutoSyncService() {

        // Push any pending messages now that we have connectivity
        if (mPrefsFactory.enableAutoSync().get() && mPrefsFactory.serviceEnabled().get()) {

            // Start the scheduler for auto sync service
            final long interval = TimeFrequencyUtil
                    .calculateInterval(mPrefsFactory.autoTime().get());
            final Intent intent = new Intent(mContext, AutoSyncScheduledReceiver.class);
            Logger.log(CLASS_TAG, "Auto sync service started");
            // Run the service
            runServices(intent, ServiceConstants.AUTO_SYNC_SCHEDULED_SERVICE_REQUEST_CODE,
                    interval);

        }

    }

    /**
     * Stops the {@link org.addhen.smssync.presentation.service.CheckTaskService}
     *
     * @return void
     */
    public void stopCheckTaskService() {
        // Push any pending messages now that we have connectivity
        final Intent intent = new Intent(mContext, CheckTaskScheduledReceiver.class);

        // Stop the scheduled service
        Logger.log(CLASS_TAG, "Stop CheckTaskScheduledService");
        stopServices(intent, ServiceConstants.CHECK_TASK_SCHEDULED_SERVICE_REQUEST_CODE);
    }

    /**
     * Stops the {@link org.addhen.smssync.presentation.service.AutoSyncScheduledService}
     *
     * @return void
     */
    public void stopAutoSyncService() {
        final Intent intent = new Intent(mContext, AutoSyncScheduledReceiver.class);

        // Stop the scheduled service
        Logger.log(CLASS_TAG, "Stop AutoSyncScheduledService");
        stopServices(intent, ServiceConstants.AUTO_SYNC_SCHEDULED_SERVICE_REQUEST_CODE);
    }

    /**
     * Runs the {@link org.addhen.smssync.presentation.service.MessageResultsService}
     */
    public void runMessageResultsService() {
        Logger.log(CLASS_TAG,
                "Running MessageResultService " + mPrefsFactory.taskCheckTime().get());
        // Load preferences
        if (mPrefsFactory.messageResultsAPIEnable().get() && mPrefsFactory.serviceEnabled().get()) {
            // start the scheduler for 'message results' service
            final long interval = TimeFrequencyUtil
                    .calculateInterval(mPrefsFactory.taskCheckTime().get());
            final Intent intent = new Intent(mContext,
                    MessageResultsScheduledReceiver.class);
            Logger.log(CLASS_TAG, "Message Results service started - interval: " + interval);
            mFileManager.append("Message Results service started - interval: " + interval);
            // run the service
            runServices(intent, ServiceConstants.MESSAGE_RESULTS_SCHEDULED_SERVICE_REQUEST_CODE,
                    interval);
        }
    }

    /**
     * Stops the {@link org.addhen.smssync.presentation.service.MessageResultsService
     *
     * @param context the calling context
     */
    public void stopMessageResultsService() {
        final Intent intent = new Intent(mContext, MessageResultsScheduledReceiver.class);
        stopServices(intent, ServiceConstants.MESSAGE_RESULTS_SCHEDULED_SERVICE_REQUEST_CODE);
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
        if (mPrefsFactory.serviceEnabled().get()) {

            // show notification
            Utility.showNotification(mContext);

            // start pushing pending messages

            // do we have data network?
            if (isConnected()) {
                SchedulerInstance.INSTANCE.getScheduler(mContext, mFileManager, intent, requestCode)
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
        SchedulerInstance.INSTANCE.getScheduler(mContext, mFileManager, intent, requestCode)
                .stopScheduler();
    }

    /**
     * Runs the {@link org.addhen.smssync.presentation.service.CheckTaskService}
     *
     * @return ScheduleServices
     */
    public void runCheckTaskService() {
        // Check for tasks now that we have connectivity
        Logger.log(CLASS_TAG, "Running CheckTaskService " + mPrefsFactory.taskCheckTime().get());

        if (mPrefsFactory.enableTaskCheck().get() && mPrefsFactory.serviceEnabled().get()) {

            // start the scheduler for 'task check' service
            final long interval = TimeFrequencyUtil
                    .calculateInterval(mPrefsFactory.taskCheckTime().get());

            final Intent intent = new Intent(mContext, CheckTaskScheduledReceiver.class);

            Logger.log(CLASS_TAG, "Check task service started - interval: " + interval);
            // run the service
            runServices(intent, ServiceConstants.CHECK_TASK_SCHEDULED_SERVICE_REQUEST_CODE,
                    interval);

        }

    }

    public boolean isConnected() {
        return Utility.isConnected(mContext);
    }

    public enum SchedulerInstance {
        INSTANCE;

        private Scheduler getScheduler(Context context, FileManager fileManager, Intent intent,
                int requestCode) {
            return new Scheduler(context, fileManager, intent, requestCode,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }
}
