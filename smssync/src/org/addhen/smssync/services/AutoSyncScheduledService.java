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

import static org.addhen.smssync.tasks.SyncType.MANUAL;

import org.addhen.smssync.R;
import org.addhen.smssync.models.MessagesModel;
import org.addhen.smssync.tasks.SyncType;
import org.addhen.smssync.tasks.state.MessageSyncState;
import org.addhen.smssync.util.MessageSyncUtil;
import org.addhen.smssync.util.ServicesConstants;

import android.content.Intent;

import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

/**
 * A this class handles background services for periodic synchronization of
 * pending messages.
 * 
 * @author eyedol
 */

public class AutoSyncScheduledService extends SmsSyncServices {

    private static String CLASS_TAG = AutoSyncScheduledService.class
            .getSimpleName();

    // holds the status of the sync and sends it to pending messages activity to
    // update the ui
    private Intent statusIntent;

    private MessagesModel messagesModel;

    private MessageSyncState mState = new MessageSyncState();

    private static AutoSyncScheduledService service;

    public AutoSyncScheduledService() {
        super(CLASS_TAG);
        statusIntent = new Intent(ServicesConstants.AUTO_SYNC_ACTION);
        messagesModel = new MessagesModel();
        service = this;
    }

    @Override
    protected void executeTask(Intent intent) {

        log(CLASS_TAG, "executeTask() executing this scheduled task");
       
        if (messagesModel.totalMessages() > 0) {

            log(CLASS_TAG, "Sending pending messages");

            int status = new MessageSyncUtil(AutoSyncScheduledService.this,
                    "").syncToWeb();
            statusIntent.putExtra("status", status);
            sendBroadcast(statusIntent);

        }
    }

    @Override
    public MessageSyncState getState() {
        return mState;
    }

    @Subscribe
    public void syncStateChanged(final MessageSyncState state) {
        mState = state;
        if (mState.isInitialState())
            return;

        if (state.isError()) {

            createNotification(R.string.status,
                    state.getNotification(getResources()), getPendingIntent());
        }

        if (state.isRunning()) {
            if (state.syncType == MANUAL) {
                updateSyncStatusNotification(state);
            }
        } else {
            log(state.isCanceled() ? getString(R.string.canceled) : getString(R.string.done));

            stopForeground(true);
            stopSelf();
        }
    }

    @Produce
    public MessageSyncState produceLastState() {
        return mState;
    }

    private void updateSyncStatusNotification(MessageSyncState state) {
        createNotification(R.string.status,
                state.getNotification(getResources()), getPendingIntent());

    }

    public boolean isWorking() {
        return getState().isRunning();
    }

    @Override
    protected boolean isBackgroundTask() {
        return mState.syncType.isBackground();
    }

    public static boolean isServiceWorking() {
        return service != null && service.isWorking();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        service = null;
    }

}
