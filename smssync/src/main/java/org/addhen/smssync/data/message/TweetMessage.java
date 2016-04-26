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

package org.addhen.smssync.data.message;

import org.addhen.smssync.R;
import org.addhen.smssync.data.PrefsFactory;
import org.addhen.smssync.data.cache.FileManager;
import org.addhen.smssync.data.entity.Filter;
import org.addhen.smssync.data.entity.Message;
import org.addhen.smssync.data.repository.datasource.filter.FilterDataSource;
import org.addhen.smssync.data.repository.datasource.message.MessageDataSource;
import org.addhen.smssync.data.repository.datasource.webservice.WebServiceDataSource;
import org.addhen.smssync.data.twitter.TwitterClient;
import org.addhen.smssync.data.util.Logger;
import org.addhen.smssync.data.util.Utility;
import org.addhen.smssync.smslib.sms.ProcessSms;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import twitter4j.Status;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
@Singleton
public class TweetMessage extends ProcessMessage {

    private static final String TAG = TweetMessage.class
            .getSimpleName();

    private TwitterClient mTwitterClient;

    @Inject
    public TweetMessage(Context context, PrefsFactory prefsFactory,
            TwitterClient twitterClient,
            MessageDataSource messageDataSource,
            WebServiceDataSource webServiceDataSource,
            FilterDataSource filterDataSource,
            ProcessSms processSms,
            FileManager fileManager
    ) {
        super(context, prefsFactory, messageDataSource, webServiceDataSource,
                filterDataSource, processSms, fileManager);
        mTwitterClient = twitterClient;
    }

    /**
     * Processes the incoming SMS to figure out how to exactly route the message. If it fails to be
     * synced online, cache it and queue it up for the scheduler to process it.
     *
     * @param message The sms to be routed
     * @return boolean
     */
    public boolean routeSms(Message message) {
        Logger.log(TAG, "routeSms uuid: " + message.toString());
        // Double check if SMSsync service is running
        if (!mPrefsFactory.serviceEnabled().get()) {
            return false;
        }

        // Send auto response from phone not server
        if (mPrefsFactory.enableReply().get()) {
            // send auto response as SMS to user's phone
            logActivities(R.string.auto_response_sent);
            Message msg = new Message();
            msg.setMessageBody(mPrefsFactory.reply().get());
            msg.setMessageFrom(message.getMessageFrom());
            msg.setMessageType(message.getMessageType());
            mProcessSms.sendSms(map(msg), false);
        }
        if (Utility.isConnected(mContext)) {
            List<Filter> filters = mFilterDataSource.getFilters();
            // Process if white-listing is enabled
            if (mPrefsFactory.enableWhitelist().get()) {
                for (Filter filter : filters) {
                    if (filter.getPhoneNumber().equals(message.getMessageFrom())) {
                        if (tweetMessage(message)) {
                            deleteFromSmsInbox(message);
                        } else {
                            savePendingMessage(message);
                        }
                    }
                }
            }

            // Process blacklist
            if (mPrefsFactory.enableBlacklist().get()) {
                for (Filter filter : filters) {

                    if (!filter.getPhoneNumber().equals(message.getMessageFrom())) {
                        if (tweetMessage(message)) {
                            deleteFromSmsInbox(message);
                        } else {
                            savePendingMessage(message);
                        }
                    }

                }
            } else {
                if (tweetMessage(message)) {
                    deleteFromSmsInbox(message);
                } else {
                    savePendingMessage(message);
                }
            }

            return true;
        }

        // There is no internet save message
        savePendingMessage(message);
        return false;
    }

    /**
     * Sync pending messages to the configured sync URL.
     */
    public boolean syncPendingMessages() {
        Logger.log(TAG, "syncPendingMessages: push pending messages to the Sync URL");

        final List<Message> messages = mMessageDataSource.syncFetchPending();
        if (messages != null && messages.size() > 0) {
            return tweetMessages(messages);
        }

        return false;
    }

    @NonNull
    private List<String> getKeywords() {
        return Arrays.asList(mPrefsFactory.twitterKeywords().get().split(","));
    }

    public boolean tweetMessages(List<Message> messages) {
        Logger.log(TAG, "tweetMessages");
        List<Filter> filters = mFilterDataSource.getFilters();
        if (mTwitterClient.getSessionManager().getActiveSession() != null) {
            // Process if white-listing is enabled
            if (mPrefsFactory.enableWhitelist().get()) {
                for (Filter filter : filters) {
                    for (Message message : messages) {
                        if (filter.getPhoneNumber().equals(message.getMessageFrom())) {
                            if (tweetMessage(message)) {
                                postToSentBox(message);
                            }
                        }
                    }
                }
            }

            if (mPrefsFactory.enableBlacklist().get()) {
                for (Filter filter : filters) {
                    for (Message msg : messages) {
                        if (!filter.getPhoneNumber().equals(msg.getMessageFrom())) {
                            if (tweetMessage(msg)) {
                                postToSentBox(msg);
                            }
                        }
                    }
                }
            } else {
                for (Message messg : messages) {
                    if (tweetMessage(messg)) {
                        postToSentBox(messg);
                    }
                }
            }
        }
        return true;
    }

    public boolean tweetPendingMessage(Message message) {
        Logger.log(TAG, "tweetPendingMessage");
        List<Filter> filters = mFilterDataSource.getFilters();
        if (mTwitterClient.getSessionManager().getActiveSession() != null) {
            // Process if white-listing is enabled
            if (mPrefsFactory.enableWhitelist().get()) {
                for (Filter filter : filters) {

                    if (filter.getPhoneNumber().equals(message.getMessageFrom())) {
                        if (tweetMessage(message)) {
                            postToSentBox(message);
                        }
                    }

                }
            }

            if (mPrefsFactory.enableBlacklist().get()) {
                for (Filter filter : filters) {

                    if (!filter.getPhoneNumber().equals(message.getMessageFrom())) {
                        if (tweetMessage(message)) {
                            postToSentBox(message);
                        }
                    }

                }
            } else {
                if (tweetMessage(message)) {
                    postToSentBox(message);
                }

            }
        }
        return true;
    }

    private boolean tweetMessage(Message message) {
        // Process filter text (keyword or RegEx)
        if (!TextUtils.isEmpty(mPrefsFactory.twitterKeywords().get()) && mPrefsFactory
                .enableTwitterKeywords().get()) {
            List<String> keywords = new ArrayList<>(
                    Arrays.asList(mPrefsFactory.twitterKeywords().get().split(",")));
            if (filterByKeywords(message.getMessageBody(), keywords) || filterByRegex(
                    message.getMessageBody(), keywords)) {
                return tweet(message);
            }
        } else {
            return tweet(message);
        }
        return false;
    }

    private boolean tweet(Message message) {
        boolean posted = false;
        if (message.getMessageType().equals(Message.Type.PENDING)) {
            Logger.log(TAG, "Process message with keyword filtering enabled " + message);
            // Post to Twitter as well.
            Status status = mTwitterClient.tweet(message.getMessageBody());
            posted = status != null;

        } else {
            posted = sendTaskSms(message);
        }
        if (!posted) {
            processRetries(message);
        }
        return false;
    }
}
