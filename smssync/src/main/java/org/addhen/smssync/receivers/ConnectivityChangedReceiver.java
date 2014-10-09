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

package org.addhen.smssync.receivers;

import org.addhen.smssync.Prefs;
import org.addhen.smssync.R;
import org.addhen.smssync.controllers.AlertCallbacks;
import org.addhen.smssync.services.CheckTaskService;
import org.addhen.smssync.services.SmsSyncServices;
import org.addhen.smssync.services.SyncPendingMessagesService;
import org.addhen.smssync.tasks.SyncType;
import org.addhen.smssync.util.ServicesConstants;
import org.addhen.smssync.util.Util;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * This Receiver class is designed to listen for changes in connectivity. When we lose connectivity
 * the relevant Service classes will automatically push pending messages and check for tasks. This
 * class will restart the service that pushes pending messages to the configured URL and check for
 * tasks from the configured URL.
 */

public class ConnectivityChangedReceiver extends BroadcastReceiver {

    private boolean isConnected;

    private PackageManager pm;

    private ComponentName connectivityReceiver;

    @Override
    public void onReceive(final Context context, Intent intent) {

        // load current settings
        Prefs.loadPreferences(context);

        // is smssync enabled
        if (Prefs.enabled) {

            // check to see if we're connected to an active data network
            isConnected = Util.isConnected(context);

            if (isConnected) {
                Util.logActivities(context, context.getString(R.string.active_data_connection));
                pm = context.getPackageManager();
                connectivityReceiver = new ComponentName(context,
                        ConnectivityChangedReceiver.class);
                // The default state for this Receiver is disabled. it is only
                // enabled when a Service disables updates pending connectivity.
                pm.setComponentEnabledSetting(connectivityReceiver,
                        PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
                        PackageManager.DONT_KILL_APP);

                // Push any pending messages now that we have connectivity
                if (Prefs.enableAutoSync) {
                    Intent syncPendingMessagesServiceIntent = new Intent(context,
                            SyncPendingMessagesService.class);

                    syncPendingMessagesServiceIntent.putExtra(
                            ServicesConstants.MESSAGE_UUID, "");
                    syncPendingMessagesServiceIntent.putExtra(SyncType.EXTRA,
                            SyncType.MANUAL.name());
                    context.startService(syncPendingMessagesServiceIntent);
                }
                if (AlertCallbacks.lostConnectionThread != null
                        && AlertCallbacks.lostConnectionThread.isAlive()) {
                    AlertCallbacks.lostConnectionThread.interrupt();
                }

                // Check for tasks now that we have connectivity
                if (Prefs.enableTaskCheck) {
                    SmsSyncServices.sendWakefulTask(context, CheckTaskService.class);
                }
            } else {

                if (AlertCallbacks.lostConnectionThread == null
                        || !AlertCallbacks.lostConnectionThread.isAlive()) {
                    AlertCallbacks.lostConnectionThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(AlertCallbacks.MAX_DISCONNECT_TIME);
                            } catch (InterruptedException e) {
                                return;
                            }
                            AlertCallbacks.dataConnectionLost(context);
                        }
                    });
                    AlertCallbacks.lostConnectionThread.start();
                }

                Util.logActivities(context, context.getString(R.string.no_data_connection));
            }
        }
    }
}
