/** 
 ** Copyright (c) 2010 Ushahidi Inc
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
 **/

package org.addhen.smssync.data;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SmsSyncDatabase {
	
	// column names
	public static final String MESSAGE_ID = "_id";
	public static final String MESSAGE_BODY = "message_body";
	public static final String MESSAGE_FROM = "message_from";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String MMS_BODY = "mms_body";
	public static final String IS_MESSAGE_SENT = "is_sent";
	
	// colum titles
	public static final String[] OUTBOX_COLUMNS = new String[] {MESSAGE_ID,
		MESSAGE_BODY, MESSAGE_FROM, LATITUDE, LONGITUDE, MMS_BODY,
		IS_MESSAGE_SENT
	};
	
	private DatabaseHelper mDbHelper;
	
	private SQLiteDatabase mDb;

	private static final String DATABASE_NAME = "smssync_db";

	private static final String MESSAGE_OUTBOX_TABLE = "message_outbox";
	
	private static final int DATABASE_VERSION = 1;
	
	private static final String MESSAGE_OUTBOX_TABLE_CREATE = "CREATE TABLE " + MESSAGE_OUTBOX_TABLE + " ("
		+ MESSAGE_ID + " INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY , "  
		+ MESSAGE_BODY + " TEXT NOT NULL, "
		+ MESSAGE_FROM + " TEXT NOT NULL, "
		+ LATITUDE + "TEXT, "
		+ LONGITUDE + " TEXT, "
		+ MMS_BODY + " TEXT, "
		+ IS_MESSAGE_SENT + " BOOLEAN NOT NULL "
		+ ")";
	
	private final Context mContext;
	
	public SmsSyncDatabase( Context context) {
		mContext = context;
	}
	
	public SmsSyncDatabase open() throws SQLException {
  		mDbHelper = new DatabaseHelper(mContext);
	  	mDb = mDbHelper.getWritableDatabase();

	  	return this;
  	}

  	public void close() {
  		mDbHelper.close();
  	}
	
  	/**
  	 * Insert item to the message_outbox table
  	 * @author eyedol
  	 *
  	 */
  	public long insertMessage( Messages messages) {
  		ContentValues initialValues = new ContentValues();
  		initialValues.put(MESSAGE_BODY, messages.getMessageBody());
  		initialValues.put(MESSAGE_FROM, messages.getMessageFrom());
  		initialValues.put(LATITUDE, messages.getLatitude());
  		initialValues.put(LONGITUDE, messages.getLongitude());
  		initialValues.put(MMS_BODY, messages.getMmsBody());
  		initialValues.put(IS_MESSAGE_SENT, messages.getMessageSent());  		
  		return mDb.insert(MESSAGE_OUTBOX_TABLE, null, initialValues);
  	}
  	
  	/**
  	 * Add item to the message_outbox table.
  	 * @param List addIncidents
  	 */
  	public long addMessages(List<Messages> messages ) {
  		long rowId = 0;
  		try {
  			mDb.beginTransaction();
  			for( Messages message: messages ) {
  				rowId = insertMessage(message);
  			}
  			mDb.setTransactionSuccessful();
  			
  		} finally {
  			mDb.endTransaction();
  		}
  		
  		return rowId;
  	}
  	
  	/**
  	 * Fetch all unsent messages.
  	 * @param sent
  	 * @return
  	 */
  	public Cursor fetchUnsentMessages( String sent ) {
	  		String sql = "SELECT * FROM "+MESSAGE_OUTBOX_TABLE+" WHERE "+IS_MESSAGE_SENT+" = ? ORDER BY "
	  			+MESSAGE_BODY+" COLLATE NOCASE";
	  		return mDb.rawQuery(sql, new String[] { sent } );
  	}
  	
  	/**
  	 * Mark a message as sent / unsent
  	 * @param String status - 1 for sent, 0 for unset
  	 * 
  	 * @return void
  	 */
  	public void markMessageStatus( String status ) {
  		ContentValues values = new ContentValues();
  		values.put(IS_MESSAGE_SENT, 0);
  		
  		String whereClause = "WHERE "+IS_MESSAGE_SENT;
  		String whereArgs [] = {status};
  		
  		mDb.update(MESSAGE_OUTBOX_TABLE, values, whereClause, whereArgs);
  	}
  	
  	public boolean deleteSentMessage(int id) {
  		return mDb.delete(MESSAGE_OUTBOX_TABLE, MESSAGE_ID + "=" + id, null) > 0;
  	}
  	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			
			db.execSQL(MESSAGE_OUTBOX_TABLE_CREATE);
		}

    	@Override
    	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    		db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_OUTBOX_TABLE_CREATE);
      		onCreate(db);
    	}
	
	}
		
}
