
package org.addhen.smssync;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A this class handles background services for periodic synchronization of
 * pending messages.
 * 
 * @author eyedol
 */

public class SmsSyncAutoSyncService extends Service {

    private TimerTask mDoTask;

    private final Handler handler = new Handler();

    private Timer mT = new Timer();

    public void onCreate() {
        super.onCreate();
        this.startService();
    }

    /**
     * Start the background service.
     * 
     * @return void
     */
    private void startService() {

        SmsSyncPref.loadPreferences(SmsSyncAutoSyncService.this);

        // start background service based on selected period time.
        long period = (SmsSyncPref.autoTime * 60000);
        long delay = 500;

        mDoTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {

                    public void run() {
                        if (SmsSyncApplication.mDb.fetchMessagesCount() > 0)
                            Util.snycToWeb(SmsSyncAutoSyncService.this);
                    }

                });
            }

        };

        // Start schedule
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
