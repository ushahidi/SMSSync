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

package org.addhen.smssync.util;


import org.addhen.smssync.MainApplication;
import org.addhen.smssync.database.Messages;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Utility class for sent messages feature
 *
 * @author eyedol
 */
public class SentMessagesUtil {

    private static final String CLASS_TAG = Util.class.getCanonicalName();

    public static HashMap<String, String> smsMap = new HashMap<String, String>();

    private static List<Messages> mMessages;

    /**
     * Process messages as received from the user; 0 - successful 1 - failed fetching categories
     *
     * @return int - status
     */
    public static boolean processSentMessages(Context context) {
        Logger.log(CLASS_TAG,
                "processMessages(): Process text messages as received from the user's phone");

        List<Messages> listMessages = new ArrayList<Messages>();
        String messageUuid = "";
        Messages messages = new Messages();
        listMessages.add(messages);

        // check if messageId is actually initialized
        if (smsMap.get("messagesUuid") != null) {
            messageUuid = smsMap.get("messagesUuid");
        }

        messages.setMessageUuid(messageUuid);
        messages.setMessageFrom(smsMap.get("messagesFrom"));
        messages.setMessageBody(smsMap.get("messagesBody"));
        messages.setMessageDate(smsMap.get("messagesDate"));
        messages.setMessageType(Integer.valueOf(smsMap.get("messagesType")));

        mMessages = listMessages;

        if (mMessages != null) {
            MainApplication.mDb.addSentMessages(mMessages);
            return true;
        }
        return false;

    }

}
