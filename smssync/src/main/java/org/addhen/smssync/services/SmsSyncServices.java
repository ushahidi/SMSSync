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

import org.addhen.smssync.MainApplication;
import org.addhen.smssync.receivers.ConnectivityChangedReceiver;
import org.addhen.smssync.util.Logger;
import org.addhen.smssync.util.Util;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.PowerManager;

public abstract class SmsSyncServices extends IntentService {

    protected static final Object mStartingServiceSync = new Object();

    protected static String TAG = SmsSyncServices.class.getSimpleName();

    protected static PowerManager.WakeLock mStartingService = null;

    protected static WifiManager.WifiLock wifilock = null;

    protected NotificationManager notificationManager;

    public SmsSyncServices(String name) {
        super(name);
    }

    synchronized private static PowerManager.WakeLock getPhoneWakeLock(
            Context context) {
        if (mStartingService == null) {
            PowerManager mgr = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            mStartingService = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    TAG);
        }
        return mStartingService;
    }

    synchronized private static WifiManager.WifiLock getPhoneWifiLock(
            Context context) {
        if (wifilock == null) {

            WifiManager manager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            wifilock = manager.createWifiLock(WifiManager.WIFI_MODE_FULL,
                    TAG);
        }
        return wifilock;
    }

    protected static void sendWakefulTask(Context context, Intent i) {

        if (!getPhoneWakeLock(context.getApplicationContext()).isHeld()) {
            getPhoneWakeLock(context.getApplicationContext()).acquire();
        }

        if (!getPhoneWifiLock(context.getApplicationContext()).isHeld()) {
            getPhoneWifiLock(context.getApplicationContext()).acquire();
        }

        context.startService(i);
    }

    public static void sendWakefulTask(Context context, Class<?> classService) {
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

            if (getPhoneWakeLock(this.getApplicationContext()).isHeld()
                    && getPhoneWakeLock(this.getApplicationContext()) != null) {
                getPhoneWakeLock(this.getApplicationContext()).release();
            }

            if (getPhoneWifiLock(this.getApplicationContext()).isHeld()
                    && getPhoneWifiLock(this.getApplicationContext()) != null) {
                getPhoneWifiLock(this.getApplicationContext()).release();
            }
        }
    }

    @Override
    public void onDestroy() {
        // release resources
        if (getPhoneWifiLock(this.getApplicationContext()).isHeld()
                && getPhoneWifiLock(this.getApplicationContext()) != null) {
            getPhoneWifiLock(this.getApplicationContext()).release();
        }

        if (getPhoneWakeLock(this.getApplicationContext()).isHeld()
                && getPhoneWakeLock(this.getApplicationContext()) != null) {
            getPhoneWakeLock(this.getApplicationContext()).release();
        }
        MainApplication.bus.unregister(this);
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
