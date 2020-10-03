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

import org.addhen.smssync.R;
import org.addhen.smssync.data.cache.FileManager;
import org.addhen.smssync.data.util.Logger;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

/**
 * Schedules background services to run at specified intervals. This is not a service in itself.
 *
 * @author Henry Addo
 */
public class Scheduler {

    private static final String CLASS_TAG = Scheduler.class
            .getSimpleName();

    private AlarmManager mAlarmManager;

    private PendingIntent mPendingIntent;

    private Context mContext;

    private FileManager mFileManager;

    public Scheduler(Context context, FileManager fileManager, Intent intent, int requestCode,
            int flags) {
        mContext = context.getApplicationContext();
        mFileManager = fileManager;
        Logger.log(CLASS_TAG, "ScheduleServices() executing scheduled services ");
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        mPendingIntent = PendingIntent.getBroadcast(mContext, requestCode, intent, flags);
    }


    /**
     * Stops the scheduled service
     */
    public void stopScheduler() {
        if (mAlarmManager != null && mPendingIntent != null) {
            Logger.log(CLASS_TAG, "Stop scheduler");
            mFileManager.append(mContext.getString(R.string.stopping_scheduler));
            mAlarmManager.cancel(mPendingIntent);
        }
    }

    /**
     * Updates the interval of the scheduled service
     *
     * @param interval The interval at which the service should run.
     */
    public void updateScheduler(long interval) {
        Logger.log(CLASS_TAG, "updating scheduler");
        if (mAlarmManager != null && mPendingIntent != null) {
            Logger.log(CLASS_TAG, "Update scheduler to " + interval);
            mFileManager.append(mContext.getString(R.string.scheduler_updated_to));
            mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + 60000, interval, mPendingIntent);
        }
    }
}
