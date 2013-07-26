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

import java.util.List;

import org.addhen.smssync.database.Database;
import org.addhen.smssync.messages.Message;

/**
 * Class to handle set and getters.
 *
 */
public class MessageModel extends Model {

	private List<Message> listMessages;

    private Message message;

    /**
     * Set list messages
     *
     * @param listMessages List of messages
     */
    public void setListMessages(List<Message> listMessages) {
        this.listMessages = listMessages;
    }
    /**
     * Initialize the message
     *
     * @param message The message object
     */
    public void setMessage(Message message) {
        this.message = message;
    }
	@Override
	public boolean load() {
		listMessages = Database.mMessagesContentProvider.fetchAllMessages();
        return listMessages != null;
    }

	public boolean loadByUuid(String messageUuid) {
		listMessages = Database.mMessagesContentProvider
				.fetchMessagesByUuid(messageUuid);
        return listMessages != null;
    }

	public boolean loadByLimit(int limit) {
		listMessages = Database.mMessagesContentProvider
				.fetchMessagesByLimit(limit);
        return listMessages != null;
    }

	@Override
	public boolean save() {
		if (listMessages != null && listMessages.size() > 0)
			return Database.mMessagesContentProvider.addMessages(listMessages);

		return false;
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
	 * @param  messageUuid - The message's UUID
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

    public Message getMessage(){
        return this.message;
    }

    public List<Message> getListMessages() {
        return this.listMessages;
    }
}
