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
import org.addhen.smssync.R;
import org.addhen.smssync.activities.MainActivity;
import org.addhen.smssync.exceptions.ConnectivityException;
import org.addhen.smssync.prefs.Prefs;
import org.addhen.smssync.tasks.state.State;
import org.addhen.smssync.util.LogUtil;
import org.addhen.smssync.util.Logger;
import org.addhen.smssync.util.Util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.text.format.DateFormat;

/**
 * @author eyedol
 */
public abstract class BaseService extends Service {

    protected final static String CLASS_TAG = BaseService.class.getSimpleName();

    protected Prefs prefs;

    /**
     * A wakelock held while this service is working.
     */
    private PowerManager.WakeLock sWakeLock;

    /**
     * A wifilock held while this service is working.
     */
    private WifiManager.WifiLock sWifiLock;

    private LogUtil mLogUtil;

    private String TAG = LogUtil.class.getSimpleName();

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = new Prefs(this);
        if (prefs.enableLog().get()) {
            mLogUtil = new LogUtil(DateFormat.getDateFormatOrder(this));
        }
        MainApplication.bus.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        MainApplication.bus.unregister(this);

    }

    /**
     * Acquire locks
     *
     * @throws ConnectivityException when unable to connect
     */
    public void acquireLocks() throws ConnectivityException {
        if (sWakeLock == null) {
            PowerManager pMgr = (PowerManager) getSystemService(POWER_SERVICE);
            sWakeLock = pMgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, CLASS_TAG);
        }
        sWakeLock.acquire();

        WifiManager wMgr = (WifiManager) getSystemService(WIFI_SERVICE);
        if (wMgr.isWifiEnabled()
                &&
                getConnectivityManager().getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null
                &&
                getConnectivityManager().getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                        .isConnected()) {

            // we have Wifi, lock it
            if (sWifiLock == null) {
                sWifiLock = wMgr.createWifiLock(CLASS_TAG);
            }
            sWifiLock.acquire();
        }
        NetworkInfo active = getConnectivityManager().getActiveNetworkInfo();

        if (active == null || !active.isConnectedOrConnecting()) {
            throw new ConnectivityException(getString(R.string.no_connection));
        }
    }

    public void releaseLocks() {
        if (sWakeLock != null && sWakeLock.isHeld()) {
            sWakeLock.release();
        }
        if (sWifiLock != null && sWifiLock.isHeld()) {
            sWifiLock.release();
        }
    }


    protected abstract void handleIntent(final Intent intent);

    // Android api level < 5
    @Override
    public void onStart(final Intent intent, int startId) {
        handleIntent(intent);
    }

    // Android api level >= 5
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        handleIntent(intent);
        return START_NOT_STICKY;
    }

    protected NotificationManager getNotifier() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    protected ConnectivityManager getConnectivityManager() {
        return (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public abstract State getState();

    public boolean isWorking() {
        return getState().isRunning();
    }

    protected void createNotification(int resId, String title, PendingIntent intent) {
        Util.buildNotification(this, R.drawable.ic_stat_notfiy, title, getString(resId),
                intent, true);

    }

    protected PendingIntent getPendingIntent() {
        return PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
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

    protected void logActivities(int id, Object... args) {
        final String msg = getString(id, args);
        if (mLogUtil != null) {
            mLogUtil.append(msg);
        }
        Logger.log(TAG, "Activity " + msg);
    }
}
