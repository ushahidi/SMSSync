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
import org.addhen.smssync.data.repository.datasource.message.MessageDataSource;
import org.addhen.smssync.data.repository.datasource.webservice.WebServiceDataSource;
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
            MessageDataSource messageDataSource,
            WebServiceDataSource webServiceDataSource,
            FilterDataSource filterDataSource,
            ProcessSms processSms,
            FileManager fileManager) {
        mPrefsFactory = prefsFactory;
        mWebServiceDataSource = webServiceDataSource;
        mMessageDataSource = messageDataSource;
        mFilterDataSource = filterDataSource;
        mProcessSms = processSms;
        mFileManager = fileManager;
        mContext = context;

    }

    public ProcessSms getProcessSms() {
        return mProcessSms;
    }

    public MessageModel map(Message message) {
        MessageModel messageModel = new MessageModel();
        messageModel.setId(message.getId());
        messageModel.setMessageBody(message.getMessageBody());
        messageModel.setMessageDate(message.getMessageDate());
        messageModel.setMessageFrom(message.getMessageFrom());
        messageModel.setMessageUuid(message.getMessageUuid());
        messageModel.setSentResultMessage(message.getSentResultMessage());
        messageModel.setSentResultCode(message.getSentResultCode());
        messageModel.setMessageType(map(message.getMessageType()));
        if (message.getStatus() != null) {
            messageModel.setStatus(map(message.getStatus()));
        } else {
            messageModel.setStatus(MessageModel.Status.UNCONFIRMED);
        }
        messageModel.setDeliveryResultCode(messageModel.getDeliveryResultCode());
        messageModel.setDeliveryResultMessage(messageModel.getDeliveryResultMessage());
        return messageModel;
    }

    public Message map(MessageModel messageModel) {
        Message message = new Message();
        message.setId(messageModel.getId());
        message.setMessageBody(messageModel.getMessageBody());
        message.setMessageDate(messageModel.getMessageDate());
        message.setMessageFrom(messageModel.getMessageFrom());
        message.setMessageUuid(messageModel.getMessageUuid());
        message.setSentResultMessage(messageModel.getSentResultMessage());
        message.setSentResultCode(messageModel.getSentResultCode());
        message.setMessageType(map(messageModel.getMessageType()));
        message.setStatus(map(messageModel.getStatus()));
        message.setDeliveryResultCode(messageModel.getDeliveryResultCode());
        message.setDeliveryResultMessage(messageModel.getDeliveryResultMessage());
        return message;
    }

    public void processRetries(Message message) {
        if (message.getRetries() > mPrefsFactory.retries().get()) {
            // Delete from db
            deleteMessage(message);
        } else {
            // Increase message's number of tries for future comparison to know when to delete it.
            int retries = message.getRetries() + 1;
            message.setRetries(retries);
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
        message.setStatus(Message.Status.SENT);
        mMessageDataSource.putMessage(message);
        return true;
    }

    protected void savePendingMessage(Message message) {
        //only save to pending when the number is not blacklisted
        if (!mPrefsFactory.enableBlacklist().get()) {
            message.setStatus(Message.Status.FAILED);
            mMessageDataSource.putMessage(message);
        }
    }

    protected void deleteMessage(Message message) {
        Logger.log(TAG, " message ID " + message.getMessageUuid());
        mMessageDataSource.deleteByUuid(message.getMessageUuid());
    }

    protected void logActivities(@StringRes int id) {
        mFileManager.append(mContext.getString(id));
    }

    protected void deleteFromSmsInbox(Message message) {
        if (mPrefsFactory.autoDelete().get()) {
            mProcessSms.delSmsFromInbox(map(message));
            mFileManager.append(
                    mContext.getString(R.string.auto_message_deleted, message.getMessageBody()));
        }
    }

    protected boolean sendTaskSms(Message message) {
        if (message.getMessageDate() == null || !TextUtils.isEmpty(message.getMessageUuid())) {
            final Long timeMills = System.currentTimeMillis();
            message.setMessageDate(new Date(timeMills));
        }
        if (message.getMessageUuid() == null || TextUtils.isEmpty(message.getMessageUuid())) {
            message.setMessageUuid(mProcessSms.getUuid());
        }
        message.setMessageType(Message.Type.TASK);
        if (mPrefsFactory.smsReportDelivery().get()) {
            mProcessSms.sendSms(map(message), true);
        }
        mProcessSms.sendSms(map(message), false);
        return true;
    }

    private Message.Status map(MessageModel.Status status) {
        return status != null ? Message.Status.valueOf(status.name())
                : Message.Status.FAILED;
    }

    private MessageModel.Status map(Message.Status status) {
        return status != null ? MessageModel.Status.valueOf(status.name())
                : MessageModel.Status.UNCONFIRMED;
    }

    private Message.Type map(MessageModel.Type type) {
        return type != null ? Message.Type.valueOf(type.name()) : Message.Type.PENDING;
    }

    private MessageModel.Type map(Message.Type type) {
        return type != null ? MessageModel.Type.valueOf(type.name()) : MessageModel.Type.PENDING;
    }
}
