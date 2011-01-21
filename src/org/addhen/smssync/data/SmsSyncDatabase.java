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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SmsSyncDatabase {
	private static final String TAG = "SmssyncDatabase";

	public static final String MESSAGES_ID = "_id";
	public static final String MESSAGES_FROM = "messages_from";
	public static final String MESSAGES_BODY = "messages_body";
	public static final String MESSAGES_DATE = "messages_date";
 	
	public static final String[] MESSAGES_COLUMNS = new String[] {	MESSAGES_ID,
		MESSAGES_FROM, MESSAGES_BODY, MESSAGES_DATE
	};
	
	
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private static final String DATABASE_NAME = "smssync_db";
	private static final String MESSAGES_TABLE = "messages";
	private static final int DATABASE_VERSION = 1;

  // NOTE: the incident ID is used as the row ID.
  // Furthermore, if a row already exists, an insert will replace
  // the old row upon conflict.
	
	private static final String MESSAGES_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + MESSAGES_TABLE + " ("
		+ MESSAGES_ID + " INTEGER PRIMARY KEY ON CONFLICT REPLACE, "  
		+ MESSAGES_FROM + " TEXT NOT NULL, "
		+ MESSAGES_BODY + " TEXT, "
		+ MESSAGES_DATE + " DATE NOT NULL "
		+ ")";
	
	private final Context mContext;

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(MESSAGES_TABLE_CREATE);
		}

    	@Override
    	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
    				+ newVersion + " which destroys all old data");
    		List<String> messagesColumns;
  
    		// upgrade messages table
    		db.execSQL(MESSAGES_TABLE_CREATE);
    		messagesColumns = SmsSyncDatabase.getColumns(db, MESSAGES_TABLE);
    		db.execSQL("ALTER TABLE "  + MESSAGES_TABLE + " RENAME TO temp_" + MESSAGES_TABLE);
    		db.execSQL(MESSAGES_TABLE_CREATE);
    		messagesColumns.retainAll(SmsSyncDatabase.getColumns(db, MESSAGES_TABLE));
    		String cols = SmsSyncDatabase.join(messagesColumns, ",");
    		db.execSQL(String.format( "INSERT INTO %s (%s) SELECT %s FROM temp_%s", MESSAGES_TABLE, cols, cols, MESSAGES_TABLE));
    		db.execSQL("DROP TABLE IF EXISTS temp_" + MESSAGES_TABLE);
      		onCreate(db);
    	}
    	
    	
	}

	/**
	 * Credits http://goo.gl/7kOpU
	 * @param db
	 * @param tableName
	 * @return
	 */
	public static List<String> getColumns(SQLiteDatabase db, String tableName) {
		List<String> ar = null;
		Cursor c = null;
		
		try {
			c = db.rawQuery("SELECT * FROM "+tableName+" LIMIT 1", null);
			
			if ( c!= null) {
				ar = new ArrayList<String>(Arrays.asList(c.getColumnNames()));
			}
			
		}catch(Exception e){	
			Log.v(tableName, e.getMessage(), e);
			e.printStackTrace();
		} finally {
			if (c!= null)
				c.close();
		}
		return ar;
	}
	
	public static String join(List<String> list, String delim) {
		StringBuilder buf = new StringBuilder();
		int num = list.size();
		for ( int i = 0; i < num; i++){
			if (i != 0)
				buf.append(delim);
			buf.append((String) list.get(i));
		}
		return buf.toString();
	}
	
	public SmsSyncDatabase(Context context) {
		this.mContext = context;
	}

  	public SmsSyncDatabase open() throws SQLException {
  		mDbHelper = new DatabaseHelper(mContext);
	  	mDb = mDbHelper.getWritableDatabase();

	  	return this;
  	}

  	public void close() {
  		mDbHelper.close();
  	}

  	public long createIncidents(Messages messages) {
  		ContentValues initialValues = new ContentValues();
  		
    	initialValues.put(MESSAGES_ID, messages.getMessageId());
    	initialValues.put(MESSAGES_FROM, messages.getMessageFrom());
    	initialValues.put(MESSAGES_BODY, messages.getMessageBody());
    	initialValues.put(MESSAGES_DATE, messages.getMessageDate());
  
    	return mDb.insert(MESSAGES_TABLE, null, initialValues);
  	}
  	

  	public Cursor fetchAllMessages() {
  		return mDb.query(MESSAGES_TABLE, MESSAGES_COLUMNS, null, null, null, null, MESSAGES_DATE
  				+ " DESC");
  	}

  	public boolean clearData() {
  		deleteAllMessages();
  		return true;
  		
  	}

  	public boolean deleteAllMessages() {
  		return mDb.delete(MESSAGES_TABLE, null, null) > 0;
  	}
  	
  	public boolean deleteMessagesById( int messageId) {
  		String whereClause = MESSAGES_ID+"= ?";
  		String whereArgs[] = {new Integer(messageId).toString()};
  		return mDb.delete(MESSAGES_TABLE, whereClause, whereArgs) > 0;
  	}

  	public void addMessages(List<Messages> messages) {
  		try {
  			mDb.beginTransaction();

  			for (Messages message : messages) {
  				createIncidents(message);
  			}
  			
  			mDb.setTransactionSuccessful();
  		} finally {
  			mDb.endTransaction();
  		}
  	}

}
