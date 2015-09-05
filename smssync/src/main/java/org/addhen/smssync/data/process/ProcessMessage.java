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

package org.addhen.smssync.data.process;

import org.addhen.smssync.R;
import org.addhen.smssync.data.PrefsFactory;
import org.addhen.smssync.data.cache.FileManager;
import org.addhen.smssync.data.database.FilterDatabaseHelper;
import org.addhen.smssync.data.database.MessageDatabaseHelper;
import org.addhen.smssync.data.database.WebServiceDatabaseHelper;
import org.addhen.smssync.data.entity.Filter;
import org.addhen.smssync.data.entity.Message;
import org.addhen.smssync.data.entity.WebService;
import org.addhen.smssync.data.net.MessageHttpClient;
import org.addhen.smssync.data.util.Logger;
import org.addhen.smssync.data.util.Utility;
import org.addhen.smssync.smslib.model.SmsMessage;
import org.addhen.smssync.smslib.sms.ProcessSms;

import android.content.Context;
import android.text.TextUtils;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
@Singleton
public class ProcessMessage {

    private static final String TAG = ProcessMessage.class
            .getSimpleName();

    private MessageHttpClient mMessageHttpClient;

    private PrefsFactory mPrefsFactory;

    private MessageDatabaseHelper mMessageDatabaseHelper;

    private WebServiceDatabaseHelper mWebServiceDatabaseHelper;

    private FilterDatabaseHelper mFilterDatabaseHelper;

    private ProcessSms mProcessSms;

    private FileManager mFileManager;

    private Context mContext;

    @Inject
    public ProcessMessage(Context context, PrefsFactory prefsFactory,
            MessageHttpClient messageHttpClient,
            MessageDatabaseHelper messageDatabaseHelper,
            WebServiceDatabaseHelper webServiceDatabaseHelper,
            FilterDatabaseHelper filterDatabaseHelper,
            ProcessSms processSms,
            FileManager fileManager) {
        mPrefsFactory = prefsFactory;
        mMessageHttpClient = messageHttpClient;
        mMessageDatabaseHelper = messageDatabaseHelper;
        mWebServiceDatabaseHelper = webServiceDatabaseHelper;
        mFilterDatabaseHelper = filterDatabaseHelper;
        mProcessSms = processSms;
        mFileManager = fileManager;
        mContext = context;
    }

    public boolean postMessage(List<Message> messages, List<String> keywords) {
        List<WebService> webServiceList = mWebServiceDatabaseHelper.listWebServices();
        List<Filter> filters = mFilterDatabaseHelper.getFilters();
        for (WebService webService : webServiceList) {
            // Process if whitelisting is enabled
            if (mPrefsFactory.enableWhitelist().get()) {
                for (Filter filter : filters) {
                    for (Message message : messages) {
                        if (filter.phoneNumber.equals(message.messageFrom)) {
                            if (postMessage(message, webService, keywords)) {
                                postToSentBox(message);
                            }
                        }
                    }
                }
            }

            if (mPrefsFactory.enableBlacklist().get()) {
                for (Filter filter : filters) {
                    for (Message message : messages) {
                        if (filter.phoneNumber.equals(message.messageFrom)) {
                            Logger.log("message",
                                    " from:" + message.messageFrom + " filter:"
                                            + filter.phoneNumber);
                            return false;
                        } else {
                            if (postMessage(message, webService, keywords)) {
                                postToSentBox(message);
                            }
                        }
                    }
                }
            } else {
                for (Message message : messages) {
                    if (postMessage(message, webService, keywords)) {
                        postToSentBox(message);
                    }
                }
            }
        }
        return true;
    }

    private boolean postMessage(Message message, WebService webService, List<String> keywords) {
        // Process filter text (keyword or RegEx)
        if (!Utility.isEmpty(keywords)) {
            if (filterByKeywords(message.messageBody, keywords) || filterByRegex(
                    message.messageBody, keywords)) {
                return post(message, webService);
            }
        } else {
            return post(message, webService);
        }
        return false;
    }

    private boolean post(Message message, WebService webService) {
        if (message.messageType == Message.Type.PENDING) {
            Logger.log(TAG, "Process message with keyword filtering enabled " + message);
            return mMessageHttpClient.postSmsToWebService(webService, message, "",
                    mPrefsFactory.uniqueId().get());
        }
        // TODO: sendTasks
        return false;
    }

    private boolean filterByKeywords(String message, List<String> keywords) {
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
    private boolean filterByRegex(String message, List<String> regxTexts) {
        Pattern pattern = null;
        for (String regText : regxTexts) {
            try {
                pattern = Pattern.compile(regText, Pattern.CASE_INSENSITIVE);
            } catch (PatternSyntaxException e) {
                // Invalid RegEx
                return true;
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
    private boolean postToSentBox(Message message) {
        Logger.log(TAG, "postToSentBox(): post message to sentbox " + message.toString());
        // Change the status to SENT
        message.status = Message.Status.SENT;
        mMessageDatabaseHelper.putMessage(message);
        return true;
    }

    private boolean sendTaskSms(Message message) {

        if (message.messageDate == null || !TextUtils.isEmpty(message.messageUuid)) {
            final Long timeMills = System.currentTimeMillis();
            message.messageDate = new Date(timeMills);
        }
        if (message.messageUuid == null || TextUtils.isEmpty(message.messageUuid)) {
            message.messageUuid = mProcessSms.getUuid();
        }
        message.messageType = Message.Type.TASK;
        if (mPrefsFactory.smsReportDelivery().get()) {
            mProcessSms.sendSms(map(message), false);
        }
        mProcessSms.sendSms(map(message), true);
        return true;
    }

    private void deleteFromSmsInbox(Message message) {
        if (mPrefsFactory.autoDelete().get()) {

            mProcessSms.delSmsFromInbox(map(message));
            mFileManager.appendAndClose(
                    mContext.getString(R.string.auto_message_deleted, message.messageBody));
        }
    }

    private SmsMessage map(Message message) {
        SmsMessage smsMessage = new SmsMessage();
        smsMessage.id = message._id;
        smsMessage.uuid = message.messageUuid;
        smsMessage.body = message.messageBody;
        smsMessage.phone = message.messageFrom;
        smsMessage.timestamp = message.messageDate.getTime();
        return smsMessage;
    }

    private void deleteMessage(Message message) {
        Logger.log(TAG, " message ID " + message.messageUuid);
        mMessageDatabaseHelper.deleteByUuid(message.messageUuid);
    }
}
