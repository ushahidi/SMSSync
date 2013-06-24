/*****************************************************************************
 ** Copyright (c) 2010 - 2012 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 **
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.
 **
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 **
 *****************************************************************************/

package org.addhen.smssync.services;

import static org.addhen.smssync.tasks.SyncType.UNKNOWN;
import static org.addhen.smssync.tasks.SyncType.SCHEDULE;

import org.addhen.smssync.Prefs;
import org.addhen.smssync.tasks.SyncType;
import org.addhen.smssync.util.Logger;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

/**
 * This class schedules the various tasks that needs to be executed periodically
 * 
 * @author eyedol
 */
public class ScheduleServices {

    private static final String CLASS_TAG = ScheduleServices.class
            .getSimpleName();

    /**
     * Stops the schedule service or task
     * 
     * @param context The calling context
     */
    public static void stopScheduler(Context context) {
        getAlarmManager(context).cancel(createPendingIntent(context, UNKNOWN));
    }

    private static AlarmManager getAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    private static PendingIntent createPendingIntent(Context context, SyncType backupType) {
        final Intent intent = (new Intent(context, BaseService.class))
                .putExtra(SyncType.EXTRA, backupType.name());
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static long updateScheduler(Context context, long interval, SyncType syncType) {
        Logger.log(CLASS_TAG, "executing scheduleSyncService: seconds: " + interval + " SyncType: "
                + syncType);
        // load saved prefrences
        Prefs.loadPreferences(context);
        if (Prefs.enabled) {
            final AlarmManager mgr = getAlarmManager(context);
            PendingIntent pendingIntent = createPendingIntent(context, syncType);

            if (mgr != null && pendingIntent != null) {
                // schedule the service to execute
                final long triggerAtMillis = SystemClock.elapsedRealtime() + 60000;
                mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        triggerAtMillis, interval,
                        pendingIntent);
                return triggerAtMillis;
            }

        } else {
            Logger.log(CLASS_TAG,
                    "Cannot schedule any service because SMSSynce service is disabled");
        }

        return -1;

    }

    /**
     * Schedule the auto sync service to periodically send pending messages
     * 
     * @param context The Context calling this method
     * @return
     */
    public static long scheduleAutoSync(Context context) {
        return updateScheduler(context, Prefs.autoTime * 60000, SCHEDULE);

    }
    
    public static long scheduleCheckTask(Context context) {
        return updateScheduler(context,Prefs.taskCheckTime * 60000, SCHEDULE);
    }
}
