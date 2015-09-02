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

import org.addhen.smssync.data.PrefsFactory;
import org.addhen.smssync.data.database.FilterDatabaseHelper;
import org.addhen.smssync.data.database.MessageDatabaseHelper;
import org.addhen.smssync.data.database.WebServiceDatabaseHelper;
import org.addhen.smssync.data.entity.Filter;
import org.addhen.smssync.data.entity.Message;
import org.addhen.smssync.data.entity.WebService;
import org.addhen.smssync.data.net.MessageHttpClient;
import org.addhen.smssync.data.util.Logger;
import org.addhen.smssync.data.util.Utility;

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


    @Inject
    public ProcessMessage(PrefsFactory prefsFactory, MessageHttpClient messageHttpClient,
            MessageDatabaseHelper messageDatabaseHelper,
            WebServiceDatabaseHelper webServiceDatabaseHelper,
            FilterDatabaseHelper filterDatabaseHelper) {
        mPrefsFactory = prefsFactory;
        mMessageHttpClient = messageHttpClient;
        mMessageDatabaseHelper = messageDatabaseHelper;
        mWebServiceDatabaseHelper = webServiceDatabaseHelper;
        mFilterDatabaseHelper = filterDatabaseHelper;
    }

    public boolean postMessage(Message message, List<String> keywords) {
        List<WebService> webServiceList = mWebServiceDatabaseHelper.listWebServices();
        List<Filter> filters = mFilterDatabaseHelper.getFilters();
        for (WebService webService : webServiceList) {
            // Process if whitelisting is enabled
            if (mPrefsFactory.enableWhitelist().get()) {
                for (Filter filter : filters) {
                    if (filter.phoneNumber.equals(message.messageFrom)) {
                        if (postMessage(message, webService, keywords)) {
                            postToSentBox(message);
                        }
                    }
                }
            }

            if (mPrefsFactory.enableBlacklist().get()) {
                for (Filter filter : filters) {
                    if (filter.phoneNumber.equals(message.messageFrom)) {
                        Logger.log("message",
                                " from:" + message.messageFrom + " filter:" + filter.phoneNumber);
                        return false;
                    } else {
                        if (postMessage(message, webService, keywords)) {
                            postToSentBox(message);
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean postMessage(Message message, WebService webService, List<String> keywords) {
        boolean posted = false;
        if (!Utility.isEmpty(keywords)) {
            if (filterByKeywords(message.messageBody, keywords) || filterByRegex(
                    message.messageBody, keywords)) {
                if (message.messageType == Message.Type.PENDING) {
                    Logger.log(TAG, "Process message with keyword filtering enabled " + message);
                    return mMessageHttpClient.postSmsToWebService(webService, message, "",
                            mPrefsFactory.uniqueId().get());
                }
            }
        }
        return posted;
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
        for (String regxText : regxTexts) {
            try {
                pattern = Pattern.compile(regxText, Pattern.CASE_INSENSITIVE);
            } catch (PatternSyntaxException e) {
                // invalid RegEx
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
        // Change status to sent
        message.status = Message.Status.SENT;
        mMessageDatabaseHelper.putMessage(message);
        return true;
    }
}
