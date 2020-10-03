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

import com.addhen.android.raiburari.presentation.di.HasComponent;
import com.addhen.android.raiburari.presentation.di.component.ApplicationComponent;
import com.commonsware.cwac.wakeful.WakefulIntentService;

import org.addhen.smssync.data.util.Logger;
import org.addhen.smssync.presentation.App;
import org.addhen.smssync.presentation.di.component.AppComponent;
import org.addhen.smssync.presentation.di.component.AppServiceComponent;
import org.addhen.smssync.presentation.di.component.DaggerAppServiceComponent;
import org.addhen.smssync.presentation.di.module.ServiceModule;
import org.addhen.smssync.presentation.receiver.ConnectivityChangedReceiver;
import org.addhen.smssync.presentation.util.Utility;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public abstract class BaseWakefulIntentService extends WakefulIntentService implements
        HasComponent<AppServiceComponent> {

    protected AppServiceComponent mAppServiceComponent;

    /*
     * Subclasses must implement this method so it executes any tasks
     * implemented in it.
     */
    protected abstract void executeTask(Intent intent);

    @Override
    public void doWakefulWork(final Intent intent) {
        log("onHandleIntent(): running service");

        boolean isConnected = Utility.isConnected(this);

        // check if we have internet
        if (!isConnected) {
            // Enable the Connectivity Changed Receiver to listen for
            // connection to a network so we can execute pending messages.
            PackageManager pm = getPackageManager();
            ComponentName connectivityReceiver = new ComponentName(this,
                    ConnectivityChangedReceiver.class);
            pm.setComponentEnabledSetting(connectivityReceiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

        } else {
            // Execute the task
            executeTask(intent);
        }
    }

    private void injector() {
        mAppServiceComponent = DaggerAppServiceComponent.builder()
                .appComponent(getAppComponent())
                .serviceModule(new ServiceModule(this))
                .build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        injector();
    }

    @Override
    public void onDestroy() {
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


    public BaseWakefulIntentService(String name) {
        super(name);
    }

    protected ApplicationComponent getApplicationComponent() {
        return ((App) getApplication()).getApplicationComponent();
    }

    @Override
    public AppServiceComponent getComponent() {
        return mAppServiceComponent;
    }

    protected AppComponent getAppComponent() {
        return App.getAppComponent();
    }
}
