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

import org.addhen.smssync.App;
import org.addhen.smssync.database.BaseDatabseHelper;
import org.addhen.smssync.messages.ProcessMessage;
import org.addhen.smssync.messages.ProcessSms;
import org.addhen.smssync.models.SyncUrl;

import android.content.Intent;

import java.util.List;

/**
 * A this class handles background services for periodic checks of task that needs to be executed by
 * the app. Task for now is sending SMS. In the future, it will support other tasks. Maybe send
 * email.
 *
 * @author eyedol
 */
public class CheckTaskService extends SmsSyncServices {

    private final static String CLASS_TAG = CheckTaskService.class
            .getSimpleName();

    public CheckTaskService() {
        super(CLASS_TAG);
    }

    /**
     * Starts the background service
     *
     * @return void
     */
    protected void executeTask(Intent intent) {
        log("checkTaskService: check if a task has been enabled.");
        // Perform a task
        // get enabled Sync URL
        final List<SyncUrl> result = App.getDatabaseInstance().getSyncUrlInstance().fetchSyncUrlByStatus(SyncUrl.Status.ENABLED);
        for(SyncUrl syncUrl: result) {
            new ProcessMessage(CheckTaskService.this,new ProcessSms(CheckTaskService.this)).performTask(syncUrl);
        }
    }
}
