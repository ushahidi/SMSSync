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

import org.addhen.smssync.MainApplication;
import org.addhen.smssync.Prefs;
import org.addhen.smssync.R;
import org.addhen.smssync.activities.MessagesTabActivity;
import org.addhen.smssync.exceptions.ConnectivityException;
import org.addhen.smssync.receivers.ConnectivityChangedReceiver;
import org.addhen.smssync.tasks.state.SyncPendingMessagesState;
import org.addhen.smssync.util.Logger;
import org.addhen.smssync.util.Util;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.PowerManager;

public abstract class SmsSyncServices extends IntentService {

    protected static String CLASS_TAG = SmsSyncServices.class.getSimpleName();

    protected static PowerManager.WakeLock mStartingService = null;

    protected static WifiManager.WifiLock wifilock = null;

    protected NotificationManager notificationManager;

    protected static final Object mStartingServiceSync = new Object();

    public SmsSyncServices(String name) {
        super(name);
    }

    synchronized private static PowerManager.WakeLock getPhoneWakeLock(Context context) {
        if (mStartingService == null) {
            PowerManager mgr = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            mStartingService = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    CLASS_TAG);
        }

        return mStartingService;
    }

    synchronized private static WifiManager.WifiLock getPhoneWifiLock(Context context) {
        if (wifilock == null) {

            WifiManager manager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            wifilock = manager.createWifiLock(WifiManager.WIFI_MODE_FULL,
                    CLASS_TAG);
        }
        return wifilock;
    }

    protected static void sendWakefulTask(Context context, Intent i) throws ConnectivityException {
        acquireLocks(context);
        context.startService(i);
    }

    public static void sendWakefulTask(Context context, Class<?> classService)
            throws ConnectivityException {
        sendWakefulTask(context, new Intent(context, classService));
    }

    /*
     * Subclasses must implement this method so it executes any tasks
     * implemented in it.
     */
    protected abstract void executeTask(Intent intent);

    @Override
    public void onCreate() {
        super.onCreate();
        // load setting. Just in case someone changes a setting
        Prefs.loadPreferences(this);
        MainApplication.bus.register(this);
    }

    /**
     * {@inheritDoc} Perform a task as implemented by the executeTask()
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        log("onHandleIntent(): running service");
        try {

            boolean isConnected = Util.isConnected(this);

            // check if we have internet
            if (!isConnected) {
                // Enable the Connectivity Changed Receiver to listen for
                // connection
                // to a network
                // so we can execute pending messages.
                PackageManager pm = getPackageManager();
                ComponentName connectivityReceiver = new ComponentName(this,
                        ConnectivityChangedReceiver.class);
                pm.setComponentEnabledSetting(connectivityReceiver,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);

            } else {

                // execute the scheduled task
                executeTask(intent);
            }
        } finally {
            releaseLocks();
        }
    }

    @Override
    public void onDestroy() {
        // release resources
        releaseLocks();
        MainApplication.bus.unregister(this);
    }

    public static void acquireLocks(Context context) throws ConnectivityException {
        boolean isConnected = Util.isConnected(context);
        if (!isConnected)
            // throw connectivity exception
            throw new ConnectivityException(context.getString(R.string.no_connection));

        if (!getPhoneWakeLock(context).isHeld())
            getPhoneWakeLock(context).acquire();

        if (!getPhoneWifiLock(context).isHeld())
            getPhoneWifiLock(context).acquire();
    }

    public static void releaseLocks(Context context) {
        if (getPhoneWifiLock(context).isHeld()
                && getPhoneWifiLock(context) != null) {
            getPhoneWifiLock(context).release();
        }

        if (getPhoneWakeLock(context).isHeld()
                && getPhoneWakeLock(context) != null) {
            getPhoneWakeLock(context).release();
        }
    }

    private void releaseLocks() {
        if (getPhoneWakeLock(this.getApplicationContext()).isHeld()
                && getPhoneWakeLock(this.getApplicationContext()) != null) {
            getPhoneWakeLock(this.getApplicationContext()).release();
        }

        if (getPhoneWifiLock(this.getApplicationContext()).isHeld()
                && getPhoneWifiLock(this.getApplicationContext()) != null) {
            getPhoneWifiLock(this.getApplicationContext()).release();
        }
    }

    protected void createNotification(int resId, String title, PendingIntent intent) {
        Util.buildNotification(this, R.drawable.icon, getString(resId), title,
                intent, true);

    }

    protected PendingIntent getPendingIntent() {
        return PendingIntent.getActivity(this, 0,
                new Intent(this, MessagesTabActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    protected boolean isBackgroundTask() {
        return false;
    }

    public abstract SyncPendingMessagesState getState();

    public boolean isWorking() {
        return getState().isRunning();
    }

    protected void log(String message) {
        Logger.log(getClass().getName(), message);
    }

    protected void log(String format, Object... args) {
        Logger.log(getClass().getName(), format, args);
    }

    protected void log(String message, Exception ex) {
        Logger.log(getClass().getName(), message, ex);
    }

}
