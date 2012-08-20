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

import java.util.ArrayList;
import java.util.List;

import org.addhen.smssync.MainApplication;
import org.addhen.smssync.database.Database;
import org.addhen.smssync.util.Util;

import android.database.Cursor;

/**
 * Class to handle set and getters.
 * 
 * @author eyedol
 */
public class MessagesModel extends Model {

	private String message;

	private String messageFrom;

	private String messageDate;

	private int messageId;

	public List<MessagesModel> listMessages;

	/**
	 * Set the content of the message. More like the body of the SMS message.
	 * 
	 * @param String
	 *            messageBody - The content of the SMS message.
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
	 * @param String
	 *            messageFrom
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
	 * @param String
	 *            messageDate - The timestamp of the message. To be changed into
	 *            human readable.
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
	 * Set the message ID.
	 * 
	 * @param int messageId - The message ID.
	 * @return void
	 */
	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	/**
	 * Get the message ID.
	 * 
	 * @return int
	 */
	public int getMessageId() {
		return this.messageId;
	}

	@Override
	public boolean load() {
		listMessages = Database.mMessagesContentProvider.fetchAllMessages();
		if (listMessages != null) {
			return true;
		}
		return false;
	}

	public boolean loadById(int messageId) {
		listMessages = Database.mMessagesContentProvider
				.fetchMessagesById(messageId);
		if (listMessages != null) {
			return true;
		}
		return false;
	}

	public boolean loadByLimit(int limit) {
		listMessages = Database.mMessagesContentProvider
				.fetchMessagesByLimit(limit);
		if (listMessages != null) {
			return true;
		}
		return false;
	}

	@Override
	public boolean save() {
		if (listMessages != null && listMessages.size() > 0) {
			return Database.mMessagesContentProvider.addMessages(listMessages);
		}
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
	 * Delete messages by id
	 * 
	 * @param int messageId - Message to be deleted ID
	 * @return boolean
	 */
	public boolean deleteMessagesById(int messageId) {
		return Database.mMessagesContentProvider.deleteMessagesById(messageId);
	}

	/**
	 * Count total number of pending messages.
	 * 
	 * @return int
	 */
	public int totalMessages() {
		return Database.mMessagesContentProvider.messagesCount();
	}

}
