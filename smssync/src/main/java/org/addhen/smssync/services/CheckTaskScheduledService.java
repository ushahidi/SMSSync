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

package org.addhen.smssync.services;

import org.addhen.smssync.App;
import org.addhen.smssync.R;
import org.addhen.smssync.database.BaseDatabseHelper;
import org.addhen.smssync.messages.ProcessMessage;
import org.addhen.smssync.messages.ProcessSms;
import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.util.Util;

import android.content.Intent;

import java.util.List;

public class CheckTaskScheduledService extends SmsSyncServices {

    private static final String CLASS_TAG = CheckTaskScheduledService.class
            .getSimpleName();

    public CheckTaskScheduledService() {
        super(CLASS_TAG);
    }

    @Override
    public void executeTask(Intent intent) {
        log("checking scheduled task services");
        Util.logActivities(this, getString(R.string.task_scheduler_running));
        // Perform a task
        App.getDatabaseInstance().getSyncUrlInstance().fetchSyncUrlByStatus(
                SyncUrl.Status.ENABLED, new BaseDatabseHelper.DatabaseCallback<List<SyncUrl>>() {
                    @Override
                    public void onFinished(List<SyncUrl> result) {
                        for (SyncUrl syncUrl : result) {
                            new ProcessMessage(CheckTaskScheduledService.this,
                                    new ProcessSms(CheckTaskScheduledService.this))
                                    .performTask(syncUrl);
                        }
                    }

                    @Override
                    public void onError(Exception exception) {

                    }
                });

    }
}
