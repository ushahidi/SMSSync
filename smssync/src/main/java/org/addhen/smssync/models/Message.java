/*****************************************************************************
 ** Copyright (c) 2010 - 2012 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 **
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.
 **
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 **
 *****************************************************************************/

package org.addhen.smssync.models;

import org.addhen.smssync.database.Database;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the messages to be sent via HTTP request
 */
public class Message extends Model {

    private String body;

    private String from;

    private String timestamp;

    private String uuid;

    private List<Message> mMessageList;

    public Message() {
        mMessageList = new ArrayList<Message>();
    }

    @Override
    public boolean load() {
        mMessageList = Database.mMessagesContentProvider.fetchAllMessages();
        return mMessageList != null;
    }

    public boolean loadByUuid(String messageUuid) {
        mMessageList = Database.mMessagesContentProvider
                .fetchMessagesByUuid(messageUuid);
        return mMessageList != null;
    }

    public boolean loadByLimit(int limit) {
        mMessageList = Database.mMessagesContentProvider
                .fetchMessagesByLimit(limit);
        return mMessageList != null;
    }

    public boolean saveMessages(List<Message> messages) {
        return messages != null && messages.size() > 0 && Database.mMessagesContentProvider
                .addMessages(messages);

    }

    @Override
    public boolean save() {
        return Database.mMessagesContentProvider.addMessages(this);
    }

    /**
     * Delete all pending messages.
     *
     * @return boolean
     */
    public boolean deleteAllMessages() {
        return Database.mMessagesContentProvider.deleteAllMessages();
    }

    /**
     * Delete messages by UUID
     *
     * @param messageUuid - The message's UUID
     * @return boolean
     */
    public boolean deleteMessagesByUuid(String messageUuid) {
        return Database.mMessagesContentProvider
                .deleteMessagesByUuid(messageUuid);
    }

    /**
     * Count total number of pending messages.
     *
     * @return int
     */
    public int totalMessages() {
        return Database.mMessagesContentProvider.messagesCount();
    }

    public List<Message> getMessageList() {
        return this.mMessageList;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}