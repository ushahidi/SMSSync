/**
 * ****************************************************************************
 * Copyright (c) 2010 - 2013 Ushahidi Inc
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
 * <p/>
 * If you have questions regarding the use of this file, please contact
 * Ushahidi developers at team@ushahidi.com.
 * ****************************************************************************
 */

package org.addhen.smssync.services;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import org.addhen.smssync.App;
import org.addhen.smssync.receivers.ConnectivityChangedReceiver;
import org.addhen.smssync.util.Logger;
import org.addhen.smssync.util.Util;


public abstract class SmsSyncServices extends WakefulIntentService {

    protected static String TAG = SmsSyncServices.class.getSimpleName();

    public SmsSyncServices(String name) {
        super(name);
    }

    /*
     * Subclasses must implement this method so it executes any tasks
     * implemented in it.
     */
    protected abstract void executeTask(Intent intent);

    @Override
    public void doWakefulWork(final Intent intent) {
        log("onHandleIntent(): running service");
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
            // execute the task
            executeTask(intent);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // load setting. Just in case someone changes a setting
        App.bus.register(this);
    }

    @Override
    public void onDestroy() {
        App.bus.unregister(this);
        super.onDestroy();
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
