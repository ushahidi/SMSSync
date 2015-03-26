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

import com.squareup.otto.Produce;

import org.addhen.smssync.App;
import org.addhen.smssync.messages.ProcessMessage;
import org.addhen.smssync.messages.ProcessSms;
import org.addhen.smssync.state.LogEvent;
import org.addhen.smssync.util.ServicesConstants;

import android.content.Intent;

/**
 * This class handles background services for periodic synchronization of pending messages.
 *
 * @author eyedol
 */

public class AutoSyncScheduledService extends SmsSyncServices {

    private static String CLASS_TAG = AutoSyncScheduledService.class
            .getSimpleName();

    // holds the status of the sync and sends it to the pending messages
    // activity to
    // update the ui
    private Intent statusIntent;

    public AutoSyncScheduledService() {
        super(CLASS_TAG);
        statusIntent = new Intent(ServicesConstants.AUTO_SYNC_ACTION);
    }

    @Override
    protected void executeTask(Intent intent) {

        log(CLASS_TAG, "doWakefulWork() executing "+CLASS_TAG);
        final int total = App.getDatabaseInstance().getMessageInstance().pendingTotal();
        log(CLASS_TAG, "Total: "+total);
        if (total > 0) {
            log(CLASS_TAG, "Sending pending messages");
            ProcessMessage processMessage = new ProcessMessage(AutoSyncScheduledService.this,
                    new ProcessSms(AutoSyncScheduledService.this));
            processMessage.syncPendingMessages("");
            statusIntent.putExtra("status", processMessage.getErrorMessage());
            sendBroadcast(statusIntent);
        }
    }

    @Produce
    public LogEvent readLogs() {
        return new LogEvent();
    }
}
