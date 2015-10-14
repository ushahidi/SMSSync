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

import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import org.addhen.smssync.R;
import org.addhen.smssync.data.cache.FileManager;
import org.addhen.smssync.data.entity.mapper.MessageDataMapper;
import org.addhen.smssync.data.message.PostMessage;
import org.addhen.smssync.data.message.TweetMessage;
import org.addhen.smssync.data.util.Logger;
import org.addhen.smssync.domain.repository.MessageRepository;
import org.addhen.smssync.presentation.App;
import org.addhen.smssync.presentation.task.SyncConfig;
import org.addhen.smssync.presentation.task.SyncPendingMessagesTask;
import org.addhen.smssync.presentation.task.SyncType;
import org.addhen.smssync.presentation.task.state.SyncPendingMessagesState;
import org.addhen.smssync.presentation.util.Utility;
import org.addhen.smssync.presentation.view.ui.activity.MainActivity;

import android.app.PendingIntent;
import android.content.Intent;

import java.util.ArrayList;

import javax.inject.Inject;

import static org.addhen.smssync.presentation.task.SyncType.MANUAL;
import static org.addhen.smssync.presentation.task.state.SyncState.ERROR;
import static org.addhen.smssync.presentation.task.state.SyncState.INITIAL;

/**
 * @author Henry Addo
 */
public class SyncPendingMessagesService extends BaseWakefulIntentService {

    private static String CLASS_TAG = SyncPendingMessagesService.class
            .getSimpleName();

    private static SyncPendingMessagesService mService;

    private ArrayList<String> messageUuids = null;

    private SyncPendingMessagesState mState = new SyncPendingMessagesState();

    @Inject
    FileManager mFileManager;

    @Inject
    PostMessage mPostMessage;

    @Inject
    TweetMessage mTweetMessage;

    @Inject
    MessageRepository mMessageRepository;

    @Inject
    MessageDataMapper mMessageDataMapper;

    public SyncPendingMessagesService() {
        super(CLASS_TAG);
    }

    public static boolean isServiceWorking() {
        return mService != null && mService.isWorking();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mService = this;
        getComponent().inject(this);
    }

    @Override
    protected void executeTask(final Intent intent) {
        if (intent != null) {
            final SyncType syncType = SyncType.fromIntent(intent);
            // Get Id
            if (intent.getFlags() == 100) {
                log("Get syncMessages messagesUuid: ");
                messageUuids = intent.getStringArrayListExtra(ServiceConstants.MESSAGE_UUID);
            }
            Logger.log(CLASS_TAG, "SyncType: " + syncType);
            Logger.log(CLASS_TAG,
                    "doWakefulWork() executing this task with Flag " + intent.getFlags());
            if (!isWorking()) {
                if (!SyncPendingMessagesService.isServiceWorking()) {
                    log("Sync started");
                    mFileManager.appendAndClose(getString(R.string.sync_started));
                    // Log activity
                    mFileManager.appendAndClose(getString(R.string.smssync_service_running));
                    mState = new SyncPendingMessagesState(INITIAL, 0, 0, 0, 0, syncType, null);
                    try {
                        SyncConfig config = new SyncConfig(3, false, messageUuids, syncType);
                        new SyncPendingMessagesTask(this, mPostMessage, mTweetMessage,
                                mMessageRepository, mMessageDataMapper).execute(config);
                    } catch (Exception e) {
                        log("Not syncing " + e.getMessage());
                        mFileManager
                                .appendAndClose(getString(R.string.not_syncing, e.getMessage()));
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
            mFileManager.appendAndClose(
                    (state.isCanceled() ? getString(R.string.canceled) : getString(R.string.done)));
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
        Utility.buildNotification(this, R.drawable.ic_stat_notfiy, title, getString(resId),
                intent, true);

    }

    protected PendingIntent getPendingIntent() {
        return PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mService = null;
    }
}
