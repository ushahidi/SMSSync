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
import com.squareup.otto.Subscribe;

import org.addhen.smssync.App;
import org.addhen.smssync.R;
import org.addhen.smssync.activities.MainActivity;
import org.addhen.smssync.prefs.Prefs;
import org.addhen.smssync.tasks.SyncConfig;
import org.addhen.smssync.tasks.SyncPendingMessagesTask;
import org.addhen.smssync.tasks.SyncType;
import org.addhen.smssync.tasks.state.SyncPendingMessagesState;
import org.addhen.smssync.util.LogUtil;
import org.addhen.smssync.util.Logger;
import org.addhen.smssync.util.ServicesConstants;
import org.addhen.smssync.util.Util;

import android.app.PendingIntent;
import android.content.Intent;
import android.text.format.DateFormat;

import java.util.ArrayList;

import static org.addhen.smssync.tasks.SyncType.MANUAL;
import static org.addhen.smssync.tasks.state.SyncState.ERROR;
import static org.addhen.smssync.tasks.state.SyncState.INITIAL;

/**
 * This will sync pending messages as it's commanded by the user.
 *
 * @author eyedol
 */
public class SyncPendingMessagesService extends SmsSyncServices {

    private static String CLASS_TAG = SyncPendingMessagesService.class
            .getSimpleName();

    private static SyncPendingMessagesService service;

    private ArrayList<String> messageUuids = null;

    private SyncPendingMessagesState mState = new SyncPendingMessagesState();

    protected Prefs prefs;

    private LogUtil mLogUtil;

    public SyncPendingMessagesService() {
        super(CLASS_TAG);
    }

    public static boolean isServiceWorking() {
        return service != null && service.isWorking();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = new Prefs(this);
        if (prefs.enableLog().get()) {
            mLogUtil = new LogUtil(DateFormat.getDateFormatOrder(this));
        }
        service = this;
    }

    @Override
    protected void executeTask(final Intent intent) {

        if (intent != null) {
            final SyncType syncType = SyncType.fromIntent(intent);
            // Get Id
            messageUuids = intent.getStringArrayListExtra(ServicesConstants.MESSAGE_UUID);
            Logger.log(CLASS_TAG, "SyncType: " + syncType);
            Logger.log(CLASS_TAG, "doWakefulWork() executing this task ");
            if (!isWorking()) {
                if (!SyncPendingMessagesService.isServiceWorking()) {
                    log("Sync started");
                    logActivities(R.string.sync_started);
                    // log activity
                    logActivities(R.string.smssync_service_running);
                    mState = new SyncPendingMessagesState(INITIAL, 0, 0, 0, 0, syncType, null);
                    try {
                        SyncConfig config = new SyncConfig(3, false, messageUuids, syncType);
                        new SyncPendingMessagesTask(this).execute(config);
                    } catch (Exception e) {
                        log("Not syncing " + e.getMessage());
                        logActivities(R.string.not_syncing, e.getMessage());
                        App.bus.post(mState.transition(ERROR, e));
                    }
                } else {
                    log("Sync is running now.");
                    App.bus.post(mState.transition(ERROR, null));
                }
            } else {
                log("Sync already running");
            }

        }

    }

    @Subscribe
    public void syncStateChanged(final SyncPendingMessagesState state) {
        mState = state;
        if (mState.isInitialState()) {
            return;
        }

        if (state.isError()) {

            createNotification(R.string.sync_in_completed,
                    state.getNotification(getResources()), getPendingIntent());
        }

        if (state.isRunning()) {
            if (state.syncType == MANUAL) {
                updateSyncStatusNotification(state);
            }
        } else {
            log(state.isCanceled() ? getString(R.string.canceled) : getString(R.string.done));
            logActivities(state.isCanceled() ? R.string.canceled : R.string.done);
            stopForeground(true);
            stopSelf();
        }
    }

    public SyncPendingMessagesState getState() {
        return mState;

    }

    @Produce
    public SyncPendingMessagesState produceLastState() {
        return mState;
    }

    private void updateSyncStatusNotification(SyncPendingMessagesState state) {
        createNotification(R.string.sync_in_progress,
                state.getNotification(getResources()), getPendingIntent());

    }

    public boolean isWorking() {
        return getState().isRunning();
    }

    protected void createNotification(int resId, String title, PendingIntent intent) {
        Util.buildNotification(this, R.drawable.ic_stat_notfiy, title, getString(resId),
                intent, true);

    }

    protected PendingIntent getPendingIntent() {
        return PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    protected void logActivities(int id, Object... args) {
        final String msg = getString(id, args);
        if (mLogUtil != null) {
            mLogUtil.append(msg);
        }
        Logger.log(TAG, "Activity " + msg);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        service = null;
    }
}
