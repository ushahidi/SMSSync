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

package org.addhen.smssync.presentation.receiver;

import org.addhen.smssync.R;
import org.addhen.smssync.data.PrefsFactory;
import org.addhen.smssync.data.cache.FileManager;
import org.addhen.smssync.presentation.App;
import org.addhen.smssync.presentation.presenter.AlertPresenter;
import org.addhen.smssync.presentation.service.AutoSyncScheduledService;
import org.addhen.smssync.presentation.service.CheckTaskService;
import org.addhen.smssync.presentation.util.Utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

/**
 * The manifest Receiver is used to detect changes in battery state. When the system broadcasts a
 * "Battery Low" warning we turn off and stop all enabled services. When the system broadcasts
 * "Battery OK" to indicate the battery has returned to an okay state, we start all enabled
 * services
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class PowerStateChangedReceiver extends BroadcastReceiver {

    private Intent mSmsSyncAutoSyncServiceIntent;

    private Intent mSmsSyncTaskCheckServiceIntent;

    @Override
    public void onReceive(final Context context, Intent intent) {
        boolean mBatteryLow = intent.getAction().equals(Intent.ACTION_BATTERY_LOW);
        boolean batteryOkay = intent.getAction().equals(Intent.ACTION_BATTERY_OKAY);

        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        final int percentage = Utility.calculateBatteryLevel(level, scale);
        PrefsFactory prefsFactory = App.getAppComponent().prefsFactory();
        FileManager fileManager = App.getAppComponent().fileManager();
        AlertPresenter alertPresenter = App.getAppComponent().alertPresenter();
        if (mBatteryLow) {
            // is smssync service enabled
            fileManager.append(context.getString(R.string.battery_low));
            if (prefsFactory.serviceEnabled().get()) {
                Utility.clearAll(context);
                // Stop the service that pushes pending messages
                if (prefsFactory.enableAutoSync().get()) {
                    mSmsSyncAutoSyncServiceIntent = new Intent(context,
                            AutoSyncScheduledService.class);
                    context.stopService(mSmsSyncAutoSyncServiceIntent);
                }
                // Stop the service that checks for tasks
                if (prefsFactory.enableTaskCheck().get()) {
                    mSmsSyncTaskCheckServiceIntent = new Intent(context, CheckTaskService.class);
                    context.stopService(mSmsSyncTaskCheckServiceIntent);
                }
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    alertPresenter.lowBatteryLevelRequest(percentage);
                }
            }).start();
        }

        if (batteryOkay) {
            fileManager.append(context.getString(R.string.battery_okay));
            // is smssync enabled
            if (prefsFactory.serviceEnabled().get()) {

                // clear all notifications
                Utility.clearNotify(context);

                if (prefsFactory.enableAutoSync().get()) {
                    mSmsSyncAutoSyncServiceIntent = new Intent(context,
                            AutoSyncScheduledService.class);
                    AutoSyncScheduledService
                            .sendWakefulWork(context, mSmsSyncAutoSyncServiceIntent);
                }

                if (prefsFactory.enableTaskCheck().get()) {
                    mSmsSyncTaskCheckServiceIntent = new Intent(context, CheckTaskService.class);
                    CheckTaskService.sendWakefulWork(context, mSmsSyncTaskCheckServiceIntent);
                }
            }
        }
    }
}
