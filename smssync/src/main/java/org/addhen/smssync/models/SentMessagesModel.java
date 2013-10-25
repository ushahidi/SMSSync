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

package org.addhen.smssync.models;

import org.addhen.smssync.MainApplication;
import org.addhen.smssync.database.Database;
import org.addhen.smssync.util.Util;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO:: refactor
 */
public class SentMessagesModel extends Model {

    private String message;

    private String messageFrom;

    private String messageDate;

    private String messageUuid;

    private int messageType;

    public List<SentMessagesModel> listMessages;

    /**
     * @return the messageType
     */
    public int getMessageType() {
        return messageType;
    }

    /**
     * @param messageType the messageType to set
     */
    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    /**
     * Set the content of the message. More like the body of the SMS message.
     *
     * @param String messageBody - The content of the SMS message.
     * @return void
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Get the content of the message.
     *
     * @return String
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Set the address of the SMS message.
     *
     * @param String messageFrom
     * @return void
     */
    public void setMessageFrom(String messageFrom) {
        this.messageFrom = messageFrom;
    }

    /**
     * Get the address of the SMS Message
     *
     * @return String
     */
    public String getMessageFrom() {
        return this.messageFrom;
    }

    /**
     * Set the date of the message.
     *
     * @param String messageDate - The timestamp of the message. To be changed into human readable.
     * @return void
     */
    public void setMessageDate(String messageDate) {
        try {
            this.messageDate = Util.formatDateTime(Long.parseLong(messageDate),
                    "MMM dd, yyyy 'at' hh:mm a");

        } catch (NumberFormatException e) {
            this.messageDate = messageDate;
        }
    }

    /**
     * Get the message date
     *
     * @return String
     */
    public String getMessageDate() {
        return this.messageDate;
    }

    /**
     * Set the message UUID.
     *
     * @param int messageId - The message UuiD.
     * @return void
     */
    public void setMessageUuid(String messageUuid) {
        this.messageUuid = messageUuid;
    }

    /**
     * Get the message UUID.
     *
     * @return String
     */
    public String getMessageUuid() {
        return this.messageUuid;
    }

    /**
     * Delete all pending messages.
     *
     * @return boolean
     */
    public boolean deleteAllSentMessages() {

        return MainApplication.mDb.deleteAllSentMessages();
    }

    /**
     * Delete sent messages by uuid
     *
     * @param int messageId - Message to be deleted ID
     * @return boolean
     */
    public boolean deleteSentMessagesByUuid(String messageUuid) {
        return MainApplication.mDb.deleteSentMessagesByUuid(messageUuid);
    }

    @Override
    public boolean load() {

        listMessages = new ArrayList<SentMessagesModel>();
        Cursor cursor;
        cursor = MainApplication.mDb.fetchAllSentMessages();

        String messagesFrom;
        String messagesDate;
        String messagesBody;
        String messageUuid;
        int messageType;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int messagesIdIndex = cursor
                        .getColumnIndexOrThrow(Database.SENT_MESSAGES_UUID);
                int messagesFromIndex = cursor
                        .getColumnIndexOrThrow(Database.SENT_MESSAGES_FROM);
                int messagesDateIndex = cursor
                        .getColumnIndexOrThrow(Database.SENT_MESSAGES_DATE);

                int messagesBodyIndex = cursor
                        .getColumnIndexOrThrow(Database.SENT_MESSAGES_BODY);
                int messagesTypeIndex = cursor
                        .getColumnIndexOrThrow(Database.SENT_MESSAGE_TYPE);

                do {

                    SentMessagesModel messages = new SentMessagesModel();

                    messageUuid = cursor.getString(messagesIdIndex);
                    messages.setMessageUuid(messageUuid);

                    messagesFrom = Util.capitalizeFirstLetter(cursor
                            .getString(messagesFromIndex));
                    messages.setMessageFrom(messagesFrom);

                    messagesDate = cursor.getString(messagesDateIndex);
                    messages.setMessageDate(messagesDate);

                    messagesBody = cursor.getString(messagesBodyIndex);
                    messages.setMessage(messagesBody);
                    messageType = cursor.getInt(messagesTypeIndex);
                    messages.setMessageType(messageType);

                    listMessages.add(messages);

                } while (cursor.moveToNext());
            }

            cursor.close();
            return true;
        }
        return false;
    }

    @Override
    public boolean save() {
        // TODO Auto-generated method stub
        return false;
    }

}
