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

import org.addhen.smssync.models.MessagesModel;
import org.addhen.smssync.models.SyncUrlModel;
import org.addhen.smssync.util.Logger;
import org.addhen.smssync.util.MessageSyncUtil;
import org.addhen.smssync.util.ServicesConstants;

import android.content.Intent;

/**
 * This will sync pending messages as it's commanded by the user.
 * 
 * @author eyedol
 */
public class SyncPendingMessagesService extends SmsSyncServices {

	private static String CLASS_TAG = SyncPendingMessagesService.class
			.getSimpleName();

	private Intent statusIntent; // holds the status of the sync and sends it to

	private String messageUuid = "";

	private SyncUrlModel model;

	private MessagesModel messagesModel;

	public SyncPendingMessagesService() {
		super(CLASS_TAG);
		statusIntent = new Intent(ServicesConstants.AUTO_SYNC_ACTION);
		model = new SyncUrlModel();
		messagesModel = new MessagesModel();
	}

	@Override
	protected void executeTask(Intent intent) {
		// SmsSyncPref.loadPreferences(SmsSyncAutoSyncService.this);
		Logger.log(CLASS_TAG, "executeTask() executing this task");
		int status = 3;
		if (intent != null) {
			// get Id
			messageUuid = intent.getStringExtra(ServicesConstants.MESSAGE_UUID);
			if (messagesModel.totalMessages() > 0) {
				for (SyncUrlModel syncUrl : model
						.loadByStatus(ServicesConstants.ACTIVE_SYNC_URL)) {

					status = new MessageSyncUtil(
							SyncPendingMessagesService.this, syncUrl.getUrl())
							.syncToWeb(messageUuid);
				}
				
				statusIntent.putExtra("syncstatus", status);
				sendBroadcast(statusIntent);
			}
		}

	}

}
