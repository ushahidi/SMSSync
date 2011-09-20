
package org.addhen.smssync.services;

import org.addhen.smssync.Prefrences;

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
        Prefrences.loadPreferences(contxt);
        context = contxt;
        mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        i = new Intent(context, cls);
        pendingIntent = PendingIntent.getBroadcast(context, requestCode, i, flags);

        mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 60000, interval, pendingIntent);
    }

}
