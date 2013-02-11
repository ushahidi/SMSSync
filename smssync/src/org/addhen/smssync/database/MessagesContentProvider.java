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
package org.addhen.smssync.database;

import java.util.ArrayList;
import java.util.List;

import org.addhen.smssync.models.MessagesModel;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author eyedol
 * 
 */
public class MessagesContentProvider extends DbContentProvider implements
		IMessagesContentProvider, IMessagesSchema {

	private Cursor cursor;

	private List<MessagesModel> listMessages;

	private ContentValues initialValues;

	/**
	 * @param db
	 */
	public MessagesContentProvider(SQLiteDatabase db) {
		super(db);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.addhen.smssync.database.IMessagesContentProvider#messagesCount()
	 */
	@Override
	public int messagesCount() {
		Cursor mCursor = mDb.rawQuery("SELECT COUNT(" + MESSAGE_UUID + ") FROM " + TABLE,
				null);

		int result = 0;
		try {
			if (mCursor == null) {
				return result;
			}

			mCursor.moveToFirst();
			result = mCursor.getInt(0);
		} finally {
			if (mCursor != null)
				mCursor.close();
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.addhen.smssync.database.IMessagesContentProvider#addMessages(java
	 * .util.List)
	 */
	@Override
	public boolean addMessages(List<MessagesModel> messages) {
		try {
			mDb.beginTransaction();

			for (MessagesModel message : messages) {
				addMessages(message);
			}
			mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.addhen.smssync.database.IMessagesContentProvider#addMessages(org.
	 * addhen.smssync.models.MessagesModel)
	 */
	@Override
	public boolean addMessages(MessagesModel messages) {
		// set values
		setContentValue(messages);
		return super.insert(TABLE, getContentValue()) > 0;
	}

	/**
	 * Delete message by UUID
	 * 
	 * @see
	 * org.addhen.smssync.database.IMessagesContentProvider#deleteMessagesById
	 * (int)
	 */
	@Override
	public boolean deleteMessagesByUuid(String messageUuid) {
		String whereClause =MESSAGE_UUID + "= ?";
		String whereArgs[] = { messageUuid };
		return super.delete(TABLE, whereClause, whereArgs) > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.addhen.smssync.database.IMessagesContentProvider#deleteAllMessages()
	 */
	@Override
	public boolean deleteAllMessages() {
		return super.delete(TABLE, "1", null) > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.addhen.smssync.database.IMessagesContentProvider#fetchMessagesById
	 * (int)
	 */
	@Override
	public List<MessagesModel> fetchMessagesByUuid(String messageUuid) {
		listMessages = new ArrayList<MessagesModel>();
		String selection = MESSAGE_UUID + "= ?";
		String selectionArgs[] = { messageUuid };
		cursor = super.query(TABLE, COLUMNS, selection, selectionArgs, DATE
				+ " DESC");

		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				MessagesModel messages = cursorToEntity(cursor);
				listMessages.add(messages);
				cursor.moveToNext();
			}
			cursor.close();
		}

		return listMessages;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.addhen.smssync.database.IMessagesContentProvider#fetchAllMessages()
	 */
	@Override
	public List<MessagesModel> fetchAllMessages() {
		listMessages = new ArrayList<MessagesModel>();
		cursor = super.query(TABLE, COLUMNS, null, null, DATE + " DESC");

		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				MessagesModel messages = cursorToEntity(cursor);
				listMessages.add(messages);
				cursor.moveToNext();
			}
			cursor.close();
		}

		return listMessages;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.addhen.smssync.database.IMessagesContentProvider#fetchMessagesByLimit
	 * (int)
	 */
	@Override
	public List<MessagesModel> fetchMessagesByLimit(int limit) {
		listMessages = new ArrayList<MessagesModel>();
		cursor = super.query(TABLE, COLUMNS, null, null, MESSAGE_UUID + " DESC",
				String.valueOf(limit));
		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				MessagesModel messages = cursorToEntity(cursor);
				listMessages.add(messages);
				cursor.moveToNext();
			}
			cursor.close();
		}
		return listMessages;
	}

	/**
	 * Initializes content values for the messages table.
	 * 
	 * @param messages
	 * 
	 * @return void
	 */
	private void setContentValue(MessagesModel messages) {
		initialValues = new ContentValues();
		initialValues.put(MESSAGE_UUID, messages.getMessageUuid());
		initialValues.put(FROM, messages.getMessageFrom());
		initialValues.put(BODY, messages.getMessage());
		initialValues.put(DATE, messages.getMessageDate());
	}

	private ContentValues getContentValue() {
		return initialValues;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.addhen.smssync.database.DbContentProvider#cursorToEntity(android.
	 * database.Cursor)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected MessagesModel cursorToEntity(Cursor cursor) {
		MessagesModel messages = new MessagesModel();
		int messageUuidIndex;
		int fromIndex;
		int messageIndex;
		int dateIndex;

		if (cursor != null) {
			if (cursor.getColumnIndex(MESSAGE_UUID) != -1) {
				messageUuidIndex = cursor.getColumnIndexOrThrow(MESSAGE_UUID);
				messages.setMessageUuid(cursor.getString(messageUuidIndex));
			}

			if (cursor.getColumnIndex(FROM) != -1) {
				fromIndex = cursor.getColumnIndexOrThrow(FROM);
				messages.setMessageFrom(cursor.getString(fromIndex));
			}

			if (cursor.getColumnIndex(BODY) != -1) {
				messageIndex = cursor.getColumnIndexOrThrow(BODY);
				messages.setMessage(cursor.getString(messageIndex));
			}

			if (cursor.getColumnIndex(DATE) != -1) {
				dateIndex = cursor.getColumnIndexOrThrow(DATE);
				messages.setMessageDate(cursor.getString(dateIndex));
			}
		}
		return messages;
	}

}
