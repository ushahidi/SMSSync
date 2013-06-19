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

package org.addhen.smssync.receivers;

import org.addhen.smssync.Prefs;
import org.addhen.smssync.exceptions.ConnectivityException;
import org.addhen.smssync.services.AutoSyncService;
import org.addhen.smssync.services.CheckTaskService;
import org.addhen.smssync.services.ScheduleServices;
import org.addhen.smssync.services.SmsSyncServices;
import org.addhen.smssync.util.ServicesConstants;
import org.addhen.smssync.util.Util;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This Receiver class listens for system boot. If smssync has been enabled run
 * the app.
 */
public class BootReceiver extends BroadcastReceiver {

	private boolean isConnected;

	@Override
	public void onReceive(Context context, Intent intent) {

		// load current settings
		Prefs.loadPreferences(context);

		// is smssync enabled
		if (Prefs.enabled) {

			// show notification
			Util.showNotification(context);

			// start pushing pending messages
			isConnected = Util.isConnected(context);

			// do we have data network?
			if (isConnected) {
				// Push any pending messages now that we have connectivity
				if (Prefs.enableAutoSync) {

					try {
                        SmsSyncServices.sendWakefulTask(context,
                        		AutoSyncService.class);
                    } catch (ConnectivityException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
					// start the scheduler for auto sync service
					long interval = (Prefs.autoTime * 60000);
					new ScheduleServices(
							context,
							intent,
							AutoSyncScheduledReceiver.class,
							ServicesConstants.AUTO_SYNC_SCHEDULED_SERVICE_REQUEST_CODE,
							PendingIntent.FLAG_UPDATE_CURRENT)
							.updateScheduler(interval);
				}

				// Check for tasks now that we have connectivity
				if (Prefs.enableTaskCheck) {
					try {
                        SmsSyncServices.sendWakefulTask(context,
                        		CheckTaskService.class);
                    } catch (ConnectivityException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

					// start the scheduler for 'task check' service
					long interval = (Prefs.taskCheckTime * 60000);
					new ScheduleServices(
							context,
							intent,
							CheckTaskScheduledReceiver.class,
							ServicesConstants.CHECK_TASK_SCHEDULED_SERVICE_REQUEST_CODE,
							PendingIntent.FLAG_UPDATE_CURRENT)
							.updateScheduler(interval);
				}
			}
		}
	}
}
