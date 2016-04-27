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

import org.addhen.smssync.R;
import org.addhen.smssync.data.cache.FileManager;
import org.addhen.smssync.data.entity.mapper.MessageDataMapper;
import org.addhen.smssync.data.message.PostMessage;
import org.addhen.smssync.data.message.TweetMessage;
import org.addhen.smssync.data.util.Logger;
import org.addhen.smssync.domain.entity.MessageEntity;
import org.addhen.smssync.domain.repository.MessageRepository;
import org.addhen.smssync.presentation.task.SyncConfig;
import org.addhen.smssync.presentation.task.SyncType;
import org.addhen.smssync.presentation.view.ui.activity.MainActivity;

import android.app.PendingIntent;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

/**
 * @author Henry Addo
 */
public class SyncPendingMessagesService extends BaseWakefulIntentService {

    private static String CLASS_TAG = SyncPendingMessagesService.class
            .getSimpleName();

    private static SyncPendingMessagesService mService;

    private ArrayList<String> messageUuids = null;


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

            log("Sync started");
            mFileManager.append(getString(R.string.sync_started));
            // Log activity
            mFileManager.append(getString(R.string.smssync_service_running));
            try {
                SyncConfig config = new SyncConfig(3, false, messageUuids, syncType);
                syncPending(config);
            } catch (Exception e) {
                log("Not syncing " + e.getMessage());
                mFileManager
                        .append(getString(R.string.not_syncing, e.getMessage()));

            }
        }
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

    /**
     * Sync pending messages to the enabled sync URL.
     *
     * @param config The sync configuration.
     * @return int The number of syncd items
     */
    private void syncPending(SyncConfig config) {
        List<MessageEntity> listMessages = new ArrayList<>();

        // determine if syncing by message UUID
        if (config.messageUuids != null && config.messageUuids.size() > 0) {
            for (String messageUuid : config.messageUuids) {
                MessageEntity msg = mMessageRepository.syncFetchByUuid(messageUuid);
                listMessages.add(msg);
            }
        } else {
            // load all messages
            listMessages = mMessageRepository.syncFetchPending();
        }
        if (listMessages.size() > 0) {
            Logger.log(CLASS_TAG, String.format(Locale.ENGLISH, "Starting to sync (%d messages)",
                    listMessages.size()));
            for (MessageEntity m : listMessages) {
                // route the message to twitter
                mTweetMessage.tweetPendingMessage(mMessageDataMapper.map(m));
                // route the message to the appropriate enabled sync URL
                mPostMessage.routePendingMessage(mMessageDataMapper.map(m));
            }
        }
    }
}
