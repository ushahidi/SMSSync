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

import org.addhen.smssync.data.message.PostMessage;

import android.content.Intent;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class AutoSyncScheduledService extends BaseWakefulIntentService {

    private static String CLASS_TAG = AutoSyncScheduledService.class
            .getSimpleName();

    // holds the status of the sync and sends it to the pending messages
    // activity to
    // update the ui
    private Intent statusIntent;

    private PostMessage mProcessMessage;

    public AutoSyncScheduledService() {
        super(CLASS_TAG);
        statusIntent = new Intent(ServiceConstants.AUTO_SYNC_ACTION);
    }

    public void onCreate() {
        super.onCreate();
        getComponent().inject(this);
    }

    @Override
    protected void executeTask(Intent intent) {
        log(CLASS_TAG, "doWakefulWork() executing " + CLASS_TAG);
        mProcessMessage = getAppComponent().processMessage();
        // TODO: Process pending messages
        statusIntent.putExtra("status", "error message");
        sendBroadcast(statusIntent);
    }
}
