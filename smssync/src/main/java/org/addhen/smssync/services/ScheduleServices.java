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

package org.addhen.smssync.services;

import org.addhen.smssync.Prefs;
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

    private AlarmManager mgr;

    private PendingIntent pendingIntent;

    private Intent i;

    private static final String CLASS_TAG = ScheduleServices.class
            .getSimpleName();

    public ScheduleServices(Context context, Intent intent, Class<?> cls,
            int requestCode, int flags) {
        Logger.log(CLASS_TAG,
                "ScheduleServices() executing scheduled services ");
        Prefs.loadPreferences(context);

        mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        i = new Intent(context, cls);
        pendingIntent = PendingIntent.getBroadcast(context, requestCode, i,
                flags);
    }

    /**
     * Stops the schedule service or task
     *
     * @param context The calling context
     */
    public void stopScheduler() {
        if (mgr != null && pendingIntent != null) {
            Logger.log(CLASS_TAG, "Stop scheduler");
            mgr.cancel(pendingIntent);
        }
    }

    public void updateScheduler(long interval) {
        Logger.log(CLASS_TAG, "updating scheduler");
        if (mgr != null && pendingIntent != null) {
            Logger.log(CLASS_TAG, "Update scheduler to " + interval);
            mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + 60000, interval,
                    pendingIntent);
        }
    }
}
