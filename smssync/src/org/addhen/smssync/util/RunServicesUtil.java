
package org.addhen.smssync.util;

import org.addhen.smssync.Prefs;
import org.addhen.smssync.receivers.AutoSyncScheduledReceiver;
import org.addhen.smssync.receivers.CheckTaskScheduledReceiver;
import org.addhen.smssync.services.AutoSyncScheduledService;
import org.addhen.smssync.services.AutoSyncService;
import org.addhen.smssync.services.CheckTaskScheduledService;
import org.addhen.smssync.services.CheckTaskService;
import org.addhen.smssync.services.ScheduleServices;
import org.addhen.smssync.services.SmsSyncServices;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * This will run all enabled and scheduled services.
 */
public class RunServicesUtil {

    public static final int FLAGS = PendingIntent.FLAG_UPDATE_CURRENT;

    public static final String CLASS_TAG = RunServicesUtil.class
            .getSimpleName();

    /**
     * Runs any enabled services. Making sure the device has internet connection
     * before it attempts to start any of the enabled services.
     * 
     * @param context The calling context.
     * @param intent The intent to be started.
     * @param cls The scheduler's receiver.
     * @param requestCode The private request code
     * @param interval The interval in which to run the scheduled service.
     * @return void
     */
    public static void runServices(Context context, Intent intent,
            Class<?> cls, int requestCode, long interval) {
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
                new ScheduleServices(context, intent, cls, requestCode, FLAGS)
                        .updateScheduler(interval);
            }
        }

    }

    /**
     * stop any enabled services.
     * 
     * @param context The calling context.
     * @param intent The intent to be started.
     * @param cls The scheduler's receiver.
     * @param requestCode The private request code
     * @param interval The interval in which to run the scheduled service.
     * @return void
     */
    public static void stopServices(Context context, Intent intent,
            Class<?> cls, int requestCode) {
        Logger.log(CLASS_TAG, "Stopping services");
        new ScheduleServices(context, intent, cls, requestCode, FLAGS)
                .stopScheduler();
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

            SmsSyncServices.sendWakefulTask(context, CheckTaskService.class);

            // start the scheduler for 'task check' service
            final long interval = (Prefs.taskCheckTime * 60000);

            final Intent intent = new Intent(context,
                    CheckTaskScheduledService.class);

            Logger.log(CLASS_TAG, "Check task service started");
            // run the service
            RunServicesUtil
                    .runServices(
                            context,
                            intent,
                            CheckTaskScheduledReceiver.class,
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
        if (Prefs.enableAutoSync) {

            SmsSyncServices.sendWakefulTask(context, AutoSyncService.class);

            // start the scheduler for auto sync service
            final long interval = (Prefs.autoTime * 60000);
            final Intent intent = new Intent(context,
                    AutoSyncScheduledService.class);
            Logger.log(CLASS_TAG, "Auto sync service started");
            // run the service
            RunServicesUtil.runServices(context, intent,
                    AutoSyncScheduledReceiver.class,
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
                AutoSyncScheduledService.class);

        // stop the scheduled service
        context.stopService(new Intent(context, AutoSyncService.class));
        RunServicesUtil.stopServices(context, intent,
                CheckTaskScheduledReceiver.class,
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
        // Push any pending messages now that we have connectivity
        Prefs.loadPreferences(context);
        final Intent intent = new Intent(context,
                AutoSyncScheduledService.class);

        // stop the scheduled service
        context.stopService(new Intent(context, AutoSyncService.class));
        RunServicesUtil.stopServices(context, intent,
                AutoSyncScheduledService.class,
                ServicesConstants.AUTO_SYNC_SCHEDULED_SERVICE_REQUEST_CODE);

    }
}
