package org.addhen.smssync.models;

import java.util.ArrayList;
import java.util.List;

import org.addhen.smssync.MainApplication;
import org.addhen.smssync.database.Database;
import org.addhen.smssync.util.Util;

import android.database.Cursor;

public class SyncUrlModel extends Model {

	private String title;

	private String keywords;

	private int status;

	private int id;

	public List<SyncUrlModel> listMessages;

	/**
	 * Set the title of the sync URL.
	 * 
	 * @param String title - The title of the of the URL.
	 * @return void
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Get the title of the sync URL.
	 * 
	 * @return String
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Set the address of the SMS message.
	 * 
	 * @param String keywords
	 *           
	 * @return void
	 */
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	/**
	 * Get the keywords for the sync URL
	 * 
	 * @return String
	 */
	public String getKeywords() {
		return this.keywords;
	}

	/**
	 * Set the status of a sync url. Whether active or inactive
	 * 
	 * @param int status 
	 *            
	 * @return void
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * Get the message date
	 * 
	 * @return String
	 */
	public int getStatus() {
		return this.status;
	}

	/**
	 * Set the unique ID for the sync URL.
	 * 
	 * @param int id - The sync URL ID.
	 * @return void
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Get the  unique ID for a particular sync URL.
	 * 
	 * @return int
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Delete all pending messages.
	 * 
	 * @return boolean
	 */
	public boolean deleteAllSyncUrl() {

		return MainApplication.mDb.deleteAllSentMessages();
	}

	/**
	 * Delete sent messages by id
	 * 
	 * @param int messageId - Message to be deleted ID
	 * @return boolean
	 */
	public boolean deleteSyncUrlById(int messageId) {
		return MainApplication.mDb.deleteSentMessagesById(messageId);
	}

	@Override
	public boolean load() {

		listMessages = new ArrayList<SyncUrlModel>();
		Cursor cursor;
		cursor = MainApplication.mDb.fetchAllSentMessages();

		String messagesFrom;
		String messagesDate;
		String messagesBody;
		int messageId;
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				int messagesIdIndex = cursor
						.getColumnIndexOrThrow(Database.SENT_MESSAGES_ID);
				int messagesFromIndex = cursor
						.getColumnIndexOrThrow(Database.SENT_MESSAGES_FROM);
				int messagesDateIndex = cursor
						.getColumnIndexOrThrow(Database.SENT_MESSAGES_DATE);

				int messagesBodyIndex = cursor
						.getColumnIndexOrThrow(Database.SENT_MESSAGES_BODY);

				do {

					SyncUrlModel messages = new SyncUrlModel();

					messageId = Util.toInt(cursor.getString(messagesIdIndex));
					messages.setMessageId(messageId);

					messagesFrom = Util.capitalizeString(cursor
							.getString(messagesFromIndex));
					messages.setMessageFrom(messagesFrom);

					messagesDate = cursor.getString(messagesDateIndex);
					messages.setMessageDate(messagesDate);

					messagesBody = cursor.getString(messagesBodyIndex);
					messages.setMessage(messagesBody);

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
