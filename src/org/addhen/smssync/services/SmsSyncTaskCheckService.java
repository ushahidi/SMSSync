
package org.addhen.smssync.services;

import java.util.Timer;
import java.util.TimerTask;

import org.addhen.smssync.SmsSyncPref;
import org.addhen.smssync.Util;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

/**
 * A this class handles background services for periodic checks of task that
 * needs to be executed by the app. Task for now is sending SMS. In the future,
 * it will support other tasks. Maybe send email.
 * 
 * @author eyedol
 */
public class SmsSyncTaskCheckService extends Service {

    private TimerTask mDoTask;

    private final Handler handler = new Handler();

    private Timer mT = new Timer();
    
    private final String CLASS_TAG = SmsSyncTaskCheckService.class.getCanonicalName();

    public void onCreate() {
        super.onCreate();

        this.startService();
    }

    /**
     * Starts the background service
     * 
     * @return void
     */
    private void startService() {
        Log.i(CLASS_TAG, "importMessages(): import messages from messages app");
        SmsSyncPref.loadPreferences(SmsSyncTaskCheckService.this);
        long period = (SmsSyncPref.taskCheckTime * 60000);
        long delay = 500;

        mDoTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {

                    public void run() {

                        // Perform a task
                        Util.performTask(SmsSyncTaskCheckService.this);
                    }

                });
            }

        };

        // Schedule the task.
        mT.scheduleAtFixedRate(mDoTask, delay, period);
    }

    /**
     * Stop background service.
     * 
     * @return void
     */
    private void stopService() {
        if (mDoTask != null) {
            mDoTask.cancel();
            mT.cancel();
            mT.purge();
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.stopService();
    }

}
