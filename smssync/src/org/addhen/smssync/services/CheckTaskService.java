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

import static org.addhen.smssync.tasks.state.SyncState.ERROR;
import static org.addhen.smssync.tasks.state.SyncState.INITIAL;

import org.addhen.smssync.MainApplication;
import org.addhen.smssync.SyncDate;
import org.addhen.smssync.models.SyncUrlModel;
import org.addhen.smssync.tasks.SyncConfig;
import org.addhen.smssync.tasks.SyncPendingMessagesTask;
import org.addhen.smssync.tasks.SyncType;
import org.addhen.smssync.tasks.state.SyncPendingMessagesState;
import org.addhen.smssync.util.Logger;
import org.addhen.smssync.util.MessageSyncUtil;
import org.addhen.smssync.util.ServicesConstants;

import android.content.Intent;

/**
 * A this class handles background services for periodic checks of task that
 * needs to be executed by the app. Task for now is sending SMS. In the future,
 * it will support other tasks. Maybe send email.
 * 
 * @author eyedol
 */
public class CheckTaskService extends BaseService {

    private final static String CLASS_TAG = CheckTaskService.class
            .getSimpleName();
    private SyncUrlModel model;

    private SyncPendingMessagesState mState = getState();

    private CheckTaskService service;

    @Override
    public void onCreate() {
        super.onCreate();
        service = this;
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
        for (SyncUrlModel syncUrl : model
                .loadByStatus(ServicesConstants.ACTIVE_SYNC_URL)) {
            new MessageSyncUtil(CheckTaskService.this, syncUrl.getUrl())
                    .performTask(syncUrl.getSecret());
        }
    }

    @Override
    public SyncPendingMessagesState getState() {
        return mState;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.addhen.smssync.services.BaseService#handleIntent(android.content.
     * Intent)
     */
    @Override
    protected void handleIntent(Intent intent) {
        if (intent != null) {
            final SyncType syncType = SyncType.fromIntent(intent);
            
            Logger.log(CLASS_TAG, "SyncType: " + syncType);
            Logger.log(CLASS_TAG, "executeTask() executing this task ");
            if (!isWorking()) {
                if (!SyncPendingMessagesService.isServiceWorking()) {
                    log("Sync started");
                    mState = new SyncPendingMessagesState(INITIAL, 0, 0, syncType, SyncDate.PENDING,
                            null);
                    try {
                        SyncConfig config = new SyncConfig(3, false, "", syncType);
                        new SyncPendingMessagesTask(this, SyncDate.TASK).execute(config);
                    } catch (Exception e) {
                        log("Not syncing " + e.getMessage());
                        MainApplication.bus.post(mState.transition(ERROR, e));
                    }
                }
                else {
                    log("Sync is running now.");
                    MainApplication.bus.post(mState.transition(ERROR, null));
                }
            }
            else {
                log("Sync already running");
            }

        }
    }

}
