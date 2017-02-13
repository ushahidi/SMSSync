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

import com.google.gson.Gson;

import org.addhen.smssync.R;
import org.addhen.smssync.data.PrefsFactory;
import org.addhen.smssync.data.cache.FileManager;
import org.addhen.smssync.data.entity.Filter;
import org.addhen.smssync.data.entity.Message;
import org.addhen.smssync.data.entity.MessagesUUIDSResponse;
import org.addhen.smssync.data.entity.QueuedMessages;
import org.addhen.smssync.data.entity.SmssyncResponse;
import org.addhen.smssync.data.entity.SyncUrl;
import org.addhen.smssync.data.net.MessageHttpClient;
import org.addhen.smssync.data.repository.datasource.filter.FilterDataSource;
import org.addhen.smssync.data.repository.datasource.message.MessageDataSource;
import org.addhen.smssync.data.repository.datasource.webservice.WebServiceDataSource;
import org.addhen.smssync.data.util.Logger;
import org.addhen.smssync.data.util.Utility;
import org.addhen.smssync.smslib.sms.ProcessSms;

import android.content.Context;
import android.text.TextUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Posts {@link Message} to a configured web service
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
@Singleton
public class PostMessage extends ProcessMessage {

    private MessageHttpClient mMessageHttpClient;

    private ProcessMessageResult mProcessMessageResult;

    private String mErrorMessage;

    @Inject
    public PostMessage(Context context, PrefsFactory prefsFactory,
            MessageHttpClient messageHttpClient,
            MessageDataSource messageDataSource,
            WebServiceDataSource webServiceDataSource,
            FilterDataSource filterDataSource,
            ProcessSms processSms,
            FileManager fileManager,
            ProcessMessageResult processMessageResult) {
        super(context, prefsFactory, messageDataSource, webServiceDataSource,
                filterDataSource, processSms, fileManager);

        mMessageHttpClient = messageHttpClient;
        mProcessMessageResult = processMessageResult;
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
        if (isConnected()) {
            List<SyncUrl> syncUrlList = mWebServiceDataSource
                    .get(SyncUrl.Status.ENABLED);
            List<Filter> filters = mFilterDataSource.getFilters();
            for (SyncUrl syncUrl : syncUrlList) {
                // Process if white-listing is enabled
                if (mPrefsFactory.enableWhitelist().get()) {
                    // TODO: Check for potential NPE for filters
                    for (Filter filter : filters) {
                        // Make sure phone number matches and it's indeed whitelisted
                        if ((filter.getPhoneNumber().equals(message.getMessageFrom())) && (filter
                                .getStatus().equals(Filter.Status.WHITELIST))) {
                            if (postMessage(message, syncUrl)) {
                                postToSentBox(message);
                                deleteFromSmsInbox(message);
                            } else {
                                savePendingMessage(message);
                            }
                        }
                    }
                }

                if (mPrefsFactory.enableBlacklist().get()) {
                    // Process blacklist
                    // TODO: Check for potential NPE for filters
                    for (Filter filter : filters) {
                        // Make sure phone number doesn't match and not blacklisted
                        if ((filter.getPhoneNumber().equals(message.getMessageFrom())) && (!filter
                                .getStatus().equals(Filter.Status.BLACKLIST))) {
                            if (postMessage(message, syncUrl)) {
                                postToSentBox(message);
                                deleteFromSmsInbox(message);
                            } else {
                                savePendingMessage(message);
                            }
                        }
                    }
                }

                if ((!mPrefsFactory.enableBlacklist().get()) && (!mPrefsFactory.enableWhitelist()
                        .get())) {
                    if (postMessage(message, syncUrl)) {
                        postToSentBox(message);
                        deleteFromSmsInbox(message);
                    } else {
                        savePendingMessage(message);
                    }
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
            return postMessage(messages);
        }

        return false;
    }

    public boolean postMessage(List<Message> messages) {
        Logger.log(TAG, "postMessages");
        List<SyncUrl> syncUrlList = mWebServiceDataSource.listWebServices();
        List<Filter> filters = mFilterDataSource.getFilters();
        for (SyncUrl syncUrl : syncUrlList) {
            // Process if white-listing is enabled
            if (mPrefsFactory.enableWhitelist().get()) {
                for (Filter filter : filters) {
                    for (Message message : messages) {
                        if (filter.getPhoneNumber().equals(message.getMessageFrom())) {
                            if (postMessage(message, syncUrl)) {
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
                            Logger.log("message",
                                    " from:" + msg.getMessageFrom() + " filter:"
                                            + filter.getPhoneNumber());
                            if (postMessage(msg, syncUrl)) {
                                postToSentBox(msg);
                            }
                        }
                    }
                }
            } else {
                for (Message messg : messages) {
                    if (postMessage(messg, syncUrl)) {
                        postToSentBox(messg);
                    }
                }
            }
        }
        return true;
    }


    public boolean routePendingMessage(Message message) {
        Logger.log(TAG, "postMessages");
        List<SyncUrl> syncUrlList = mWebServiceDataSource.listWebServices();
        List<Filter> filters = mFilterDataSource.getFilters();
        for (SyncUrl syncUrl : syncUrlList) {
            // Process if white-listing is enabled
            if (mPrefsFactory.enableWhitelist().get()) {
                for (Filter filter : filters) {

                    if (filter.getPhoneNumber().equals(message.getMessageFrom())) {
                        if (postMessage(message, syncUrl)) {
                            postToSentBox(message);
                        }
                    }

                }
            }

            if (mPrefsFactory.enableBlacklist().get()) {
                for (Filter filter : filters) {

                    if (!filter.getPhoneNumber().equals(message.getMessageFrom())) {
                        Logger.log("message",
                                " from:" + message.getMessageFrom() + " filter:"
                                        + filter.getPhoneNumber());
                        if (postMessage(message, syncUrl)) {
                            postToSentBox(message);
                        }
                    }

                }
            } else {
                if (postMessage(message, syncUrl)) {
                    postToSentBox(message);
                }

            }
        }
        return true;
    }

    private void sendSMSWithMessageResultsAPIEnabled(SyncUrl syncUrl, List<Message> msgs) {
        QueuedMessages messagesUUIDs = new QueuedMessages();
        for (Message msg : msgs) {
            msg.setMessageType(Message.Type.TASK);
            messagesUUIDs.getQueuedMessages().add(msg.getMessageUuid());
        }

        MessagesUUIDSResponse response =
                mProcessMessageResult.sendQueuedMessagesPOSTRequest(syncUrl, messagesUUIDs);
        if (null != response && response.isSuccess() && response.hasUUIDs()) {
            for (Message msg : msgs) {
                msg.setMessageType(Message.Type.TASK);
                if (response.getUuids().contains(msg.getMessageUuid())) {
                    sendTaskSms(msg);
                    mFileManager.append(mContext.getString(R.string.processed_task,
                            msg.getMessageBody()));
                }
            }
        }
    }

    private void sendSMSWithMessageResultsAPIDisabled(List<Message> msgs) {
        for (Message msg : msgs) {
            msg.setMessageType(Message.Type.TASK);
            sendTaskSms(msg);
        }
    }

    /**
     * Send the response received from the server as SMS
     *
     * @param response The JSON string response from the server.
     */
    private void smsServerResponse(SmssyncResponse response) {
        Logger.log(TAG, "performResponseFromServer(): " + " response:"
                + response);
        if (!mPrefsFactory.enableReplyFrmServer().get()) {
            return;
        }

        if ((response != null) && (response.getPayload() != null)
                && (response.getPayload().getMessages() != null) && (
                response.getPayload().getMessages().size() > 0)) {
            for (Message msg : response.getPayload().getMessages()) {
                sendTaskSms(msg);
            }
        }
    }

    private boolean postMessage(Message message, SyncUrl syncUrl) {
        // Process filter text (keyword or RegEx)
        if (!TextUtils.isEmpty(syncUrl.getKeywords())
                && syncUrl.getKeywordStatus() == SyncUrl.KeywordStatus.ENABLED) {
            List<String> keywords = new ArrayList<>(
                    Arrays.asList(syncUrl.getKeywords().split(",")));
            if (filterByKeywords(message.getMessageBody(), keywords) || filterByRegex(
                    message.getMessageBody(), keywords)) {
                return postToWebService(message, syncUrl);
            }
        }
        return postToWebService(message, syncUrl);
    }

    private boolean postToWebService(Message message, SyncUrl syncUrl) {
        boolean posted;
        if (message.getMessageType().equals(Message.Type.PENDING)) {
            Logger.log(TAG, "Process message with keyword filtering enabled " + message);
            posted = mMessageHttpClient.postSmsToWebService(syncUrl, message, getPhoneNumber(),
                    mPrefsFactory.uniqueId().get());
            // Process server side response so they are sent as SMS
            smsServerResponse(mMessageHttpClient.getServerSuccessResp());
        } else {
            posted = sendTaskSms(message);
        }
        if (!posted) {
            processRetries(message);
        }
        return posted;
    }

    public void performTask() {
        if ((!mPrefsFactory.serviceEnabled().get()) || (!mPrefsFactory.enableTaskCheck().get())) {
            // Don't continue
            return;
        }
        MessageHttpClient messageHttpClient = new MessageHttpClient(mContext, mFileManager);
        Logger.log(TAG, "performTask(): perform a task");
        logActivities(R.string.perform_task);
        List<SyncUrl> syncUrls = mWebServiceDataSource.get(SyncUrl.Status.ENABLED);
        for (SyncUrl syncUrl : syncUrls) {
            StringBuilder uriBuilder = new StringBuilder(syncUrl.getUrl());
            final String urlSecret = syncUrl.getSecret();
            uriBuilder.append("?task=send");

            if (!TextUtils.isEmpty(urlSecret)) {
                String urlSecretEncoded = urlSecret;
                uriBuilder.append("&secret=");
                try {
                    urlSecretEncoded = URLEncoder.encode(urlSecret, "UTF-8");
                } catch (java.io.UnsupportedEncodingException e) {
                    Logger.log(TAG, e.getMessage());
                }
                uriBuilder.append(urlSecretEncoded);
            }

            messageHttpClient.setUrl(uriBuilder.toString());
            SmssyncResponse smssyncResponses = null;
            Gson gson = null;
            try {
                messageHttpClient.execute();
                gson = new Gson();
                final String response = messageHttpClient.getResponse().body().string();
                mFileManager.append("HTTP Client Response: " + response);
                smssyncResponses = gson.fromJson(response, SmssyncResponse.class);
            } catch (Exception e) {
                Logger.log(TAG, "Task checking crashed " + e.getMessage() + " response: "
                        + messageHttpClient.getResponse());
                try {
                    mFileManager.append(
                            "Task crashed: " + e.getMessage() + " response: " + messageHttpClient
                                    .getResponse().body().string());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            if (smssyncResponses != null) {
                Logger.log(TAG, "TaskCheckResponse: " + smssyncResponses.toString());
                mFileManager.append("TaskCheckResponse: " + smssyncResponses.toString());

                if (smssyncResponses.getPayload() != null) {
                    String task = smssyncResponses.getPayload().getTask();
                    Logger.log(TAG, "Task " + task);
                    boolean secretOk = TextUtils.isEmpty(urlSecret) ||
                            urlSecret.equals(smssyncResponses.getPayload().getSecret());
                    if ((secretOk) && (task != null) && (task.equals("send"))) {
                        if (mPrefsFactory.messageResultsAPIEnable().get()) {
                            sendSMSWithMessageResultsAPIEnabled(syncUrl,
                                    smssyncResponses.getPayload().getMessages());
                        } else {
                            //backwards compatibility
                            sendSMSWithMessageResultsAPIDisabled(
                                    smssyncResponses.getPayload().getMessages());
                        }

                    } else {
                        Logger.log(TAG, mContext.getString(R.string.no_task));
                        logActivities(R.string.no_task);
                        mErrorMessage = mContext.getString(R.string.no_task);
                    }

                } else { // 'payload' data may not be present in JSON
                    Logger.log(TAG, mContext.getString(R.string.no_task));
                    logActivities(R.string.no_task);
                    mErrorMessage = mContext.getString(R.string.no_task);
                }
            }

            mFileManager.append(
                    mContext.getString(R.string.finish_task_check) + " " + mErrorMessage + " for "
                            + syncUrl.getUrl());
        }
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public boolean isConnected() {
        return Utility.isConnected(mContext);
    }

    public String getPhoneNumber() {
        return Utility.getPhoneNumber(mContext, mPrefsFactory);
    }
}
