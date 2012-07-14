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

import org.addhen.smssync.Prefs;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

/**
 * This class schedules the various tasks that needs to be executed periodically
 * 
 * @author eyedol
 */
public class ScheduleServices {

    private AlarmManager mgr;

    private PendingIntent pendingIntent;

    private Intent i;

    private Context context;

    private static final String CLASS_TAG = ScheduleServices.class.getSimpleName();

    public ScheduleServices(Context contxt, Intent intent, Class<?> cls, long interval,
            int requestCode, int flags) {
        Log.i(CLASS_TAG, "ScheduleServices() executing scheduled services: interval:" + interval
                + " requestCode: " + requestCode);
        Prefs.loadPreferences(contxt);
        context = contxt;
        mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        i = new Intent(context, cls);
        pendingIntent = PendingIntent.getBroadcast(context, requestCode, i, flags);

        mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 60000, interval, pendingIntent);
    }

}
