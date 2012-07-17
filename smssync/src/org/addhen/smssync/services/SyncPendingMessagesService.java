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
import org.addhen.smssync.util.MessageSyncUtil;
import org.addhen.smssync.util.ServicesConstants;

import android.content.Intent;
import android.util.Log;

/**
 * This will sync pending messages as it's commanded by the user.
 * 
 * @author eyedol
 */
public class SyncPendingMessagesService extends SmsSyncServices {

	private static String CLASS_TAG = SyncPendingMessagesService.class
			.getSimpleName();

	private Intent statusIntent; // holds the status of the sync and sends it to

	private int messageId = 0;

	public SyncPendingMessagesService() {
		super(CLASS_TAG);
		statusIntent = new Intent(ServicesConstants.AUTO_SYNC_ACTION);
	}

	@Override
	protected void executeTask(Intent intent) {
		// SmsSyncPref.loadPreferences(SmsSyncAutoSyncService.this);
		Log.i(CLASS_TAG, "executeTask() executing this task");

		if (intent != null) {
			// get Id
			messageId = intent.getIntExtra(ServicesConstants.MESSEAGE_ID,
					messageId);
			if (MainApplication.mDb.fetchMessagesCount() > 0) {
				int status = new MessageSyncUtil(
						SyncPendingMessagesService.this).snycToWeb(messageId);
				statusIntent.putExtra("status", status);
				sendBroadcast(statusIntent);
			}
		}

	}

}
