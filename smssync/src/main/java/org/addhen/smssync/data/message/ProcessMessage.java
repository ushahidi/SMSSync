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
import org.addhen.smssync.data.entity.Message;
import org.addhen.smssync.data.repository.datasource.filter.FilterDataSource;
import org.addhen.smssync.data.repository.datasource.filter.FilterDataSourceFactory;
import org.addhen.smssync.data.repository.datasource.message.MessageDataSource;
import org.addhen.smssync.data.repository.datasource.message.MessageDataSourceFactory;
import org.addhen.smssync.data.repository.datasource.webservice.WebServiceDataSource;
import org.addhen.smssync.data.repository.datasource.webservice.WebServiceDataSourceFactory;
import org.addhen.smssync.data.util.Logger;
import org.addhen.smssync.presentation.model.MessageModel;
import org.addhen.smssync.smslib.sms.ProcessSms;

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Tweets a {@link Message} to logged in twitter account
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public abstract class ProcessMessage {

    protected static final String TAG = ProcessMessage.class
            .getSimpleName();

    protected PrefsFactory mPrefsFactory;

    protected MessageDataSource mMessageDataSource;

    protected WebServiceDataSource mWebServiceDataSource;

    protected FilterDataSource mFilterDataSource;

    protected ProcessSms mProcessSms;

    protected FileManager mFileManager;

    protected Context mContext;

    public ProcessMessage(Context context, PrefsFactory prefsFactory,
            MessageDataSourceFactory messageDataSourceFactory,
            WebServiceDataSourceFactory webServiceDataSourceFactory,
            FilterDataSourceFactory filterDataSourceFactory,
            ProcessSms processSms,
            FileManager fileManager) {
        mPrefsFactory = prefsFactory;
        mWebServiceDataSource = webServiceDataSourceFactory.createDatabaseDataSource();
        mMessageDataSource = messageDataSourceFactory.createMessageDatabaseSource();
        mFilterDataSource = filterDataSourceFactory.createFilterDataSource();
        mProcessSms = processSms;
        mFileManager = fileManager;
        mContext = context;

    }

    public ProcessSms getProcessSms() {
        return mProcessSms;
    }

    public MessageModel map(Message message) {
        MessageModel messageModel = new MessageModel();
        message._id = message._id;
        messageModel.messageBody = message.messageBody;
        messageModel.messageDate = message.messageDate;
        messageModel.messageFrom = message.messageFrom;
        messageModel.messageUuid = message.messageUuid;
        messageModel.sentResultMessage = message.sentResultMessage;
        messageModel.sentResultCode = message.sentResultCode;
        messageModel.messageType = MessageModel.Type.valueOf(message.messageType.name());
        if (message.status != null) {
            messageModel.status = MessageModel.Status.valueOf(message.status.name());
        } else {
            messageModel.status = MessageModel.Status.UNCONFIRMED;
        }
        messageModel.deliveryResultCode = messageModel.deliveryResultCode;
        messageModel.deliveryResultMessage = messageModel.deliveryResultMessage;
        return messageModel;
    }

    public Message map(MessageModel messageModel) {
        Message message = new Message();
        message._id = message._id;
        message.messageBody = messageModel.messageBody;
        message.messageDate = messageModel.messageDate;
        message.messageFrom = messageModel.messageFrom;
        message.messageUuid = messageModel.messageUuid;
        message.sentResultMessage = messageModel.sentResultMessage;
        message.sentResultCode = messageModel.sentResultCode;
        message.messageType = Message.Type.valueOf(messageModel.messageType.name());
        message.status = Message.Status.valueOf(messageModel.status.name());
        message.deliveryResultCode = messageModel.deliveryResultCode;
        message.deliveryResultMessage = messageModel.deliveryResultMessage;
        return message;
    }

    public void processRetries(Message message) {
        if (message.retries > mPrefsFactory.retries().get()) {
            // Delete from db
            deleteMessage(message);
        } else {
            // Increase message's number of tries for future comparison to know when to delete it.
            int retries = message.retries + 1;
            message.retries = retries;
            mMessageDataSource.putMessage(message);
        }
    }

    protected boolean filterByKeywords(String message, List<String> keywords) {
        for (String keyword : keywords) {
            if (message.toLowerCase().contains(keyword.toLowerCase().trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Filter message string for RegEx match
     *
     * @param message   The message to be tested against the RegEx
     * @param regxTexts A string representing the regular expression to test against.
     * @return boolean
     */
    protected boolean filterByRegex(String message, List<String> regxTexts) {
        Pattern pattern = null;
        for (String regText : regxTexts) {
            try {
                pattern = Pattern.compile(regText, Pattern.CASE_INSENSITIVE);
            } catch (PatternSyntaxException e) {
                // Invalid RegEx
                return false;
            }
            Matcher matcher = pattern.matcher(message);

            return (matcher.find());
        }
        return false;
    }

    /**
     * Saves successfully sent messages into the db
     *
     * @param message the message
     */
    protected boolean postToSentBox(Message message) {
        Logger.log(TAG,
                "postToSentBox(): postToWebService message to sent box " + message.toString());
        // Change the status to SENT
        message.status = Message.Status.SENT;
        mMessageDataSource.putMessage(message);
        return true;
    }

    protected void savePendingMessage(Message message) {
        //only save to pending when the number is not blacklisted
        if (!mPrefsFactory.enableBlacklist().get()) {
            message.status = Message.Status.FAILED;
            mMessageDataSource.putMessage(message);
        }
    }

    protected void deleteMessage(Message message) {
        Logger.log(TAG, " message ID " + message.messageUuid);
        mMessageDataSource.deleteByUuid(message.messageUuid);
    }

    protected void logActivities(@StringRes int id) {
        mFileManager.appendAndClose(mContext.getString(id));
    }

    protected void deleteFromSmsInbox(Message message) {
        if (mPrefsFactory.autoDelete().get()) {
            mProcessSms.delSmsFromInbox(map(message));
            mFileManager.appendAndClose(
                    mContext.getString(R.string.auto_message_deleted, message.messageBody));
        }
    }

    protected boolean sendTaskSms(Message message) {
        if (message.messageDate == null || !TextUtils.isEmpty(message.messageUuid)) {
            final Long timeMills = System.currentTimeMillis();
            message.messageDate = new Date(timeMills);
        }
        if (message.messageUuid == null || TextUtils.isEmpty(message.messageUuid)) {
            message.messageUuid = mProcessSms.getUuid();
        }
        message.messageType = Message.Type.TASK;
        if (mPrefsFactory.smsReportDelivery().get()) {
            mProcessSms.sendSms(map(message), true);
        }
        mProcessSms.sendSms(map(message), false);
        return true;
    }
}
