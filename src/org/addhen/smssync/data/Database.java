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

/**
 * Handles all database activities.
 * 
 * @author eyedol
 */
public class Database {
    private static final String TAG = "SmssyncDatabase";

    public static final String MESSAGES_ID = "_id";

    public static final String MESSAGES_FROM = "messages_from";

    public static final String MESSAGES_BODY = "messages_body";

    public static final String MESSAGES_DATE = "messages_date";

    public static final String[] MESSAGES_COLUMNS = new String[] {
            MESSAGES_ID, MESSAGES_FROM, MESSAGES_BODY, MESSAGES_DATE
    };

    public static final String SENT_MESSAGES_ID = "_id";

    public static final String SENT_MESSAGES_FROM = "messages_from";

    public static final String SENT_MESSAGES_BODY = "messages_body";

    public static final String SENT_MESSAGES_DATE = "messages_date";

    public static final String[] SENT_MESSAGES_COLUMNS = new String[] {
            SENT_MESSAGES_ID, SENT_MESSAGES_FROM, SENT_MESSAGES_BODY, SENT_MESSAGES_DATE
    };

    private DatabaseHelper mDbHelper;

    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "smssync_db";

    private static final String MESSAGES_TABLE = "messages";

    private static final String SENT_MESSAGES_TABLE = "sent_messages";

    private static final int DATABASE_VERSION = 2;

    // NOTE: the message ID is used as the row ID.
    // Furthermore, if a row already exists, an insert will replace
    // the old row upon conflict.

    private static final String MESSAGES_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + MESSAGES_TABLE + " (" + MESSAGES_ID + " INTEGER PRIMARY KEY ON CONFLICT REPLACE, "
            + MESSAGES_FROM + " TEXT NOT NULL, " + MESSAGES_BODY + " TEXT, " + MESSAGES_DATE
            + " DATE NOT NULL " + ")";

    private static final String SENT_MESSAGES_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + SENT_MESSAGES_TABLE + " (" + SENT_MESSAGES_ID
            + " INTEGER PRIMARY KEY ON CONFLICT REPLACE, " + SENT_MESSAGES_FROM
            + " TEXT NOT NULL, " + SENT_MESSAGES_BODY + " TEXT, " + SENT_MESSAGES_DATE
            + " DATE NOT NULL " + ")";

    private final Context mContext;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(MESSAGES_TABLE_CREATE);
            db.execSQL(SENT_MESSAGES_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                    + " which destroys all old data");
            List<String> messagesColumns;
            List<String> sentMessagesColumns;
            // upgrade messages table
            db.execSQL(MESSAGES_TABLE_CREATE);
            messagesColumns = Database.getColumns(db, MESSAGES_TABLE);
            db.execSQL("ALTER TABLE " + MESSAGES_TABLE + " RENAME TO temp_" + MESSAGES_TABLE);
            db.execSQL(MESSAGES_TABLE_CREATE);
            messagesColumns.retainAll(Database.getColumns(db, MESSAGES_TABLE));
            String cols = Database.join(messagesColumns, ",");
            db.execSQL(String.format("INSERT INTO %s (%s) SELECT %s FROM temp_%s", MESSAGES_TABLE,
                    cols, cols, MESSAGES_TABLE));
            db.execSQL("DROP TABLE IF EXISTS temp_" + MESSAGES_TABLE);

            // upgrade sent messages table
            db.execSQL(SENT_MESSAGES_TABLE_CREATE);
            sentMessagesColumns = Database.getColumns(db, SENT_MESSAGES_TABLE);
            db.execSQL("ALTER TABLE " + SENT_MESSAGES_TABLE + " RENAME TO temp_"
                    + SENT_MESSAGES_TABLE);
            db.execSQL(SENT_MESSAGES_TABLE_CREATE);
            messagesColumns.retainAll(Database.getColumns(db, SENT_MESSAGES_TABLE));
            String sentMessagesCols = Database.join(sentMessagesColumns, ",");
            db.execSQL(String.format("INSERT INTO %s (%s) SELECT %s FROM temp_%s",
                    SENT_MESSAGES_TABLE, sentMessagesCols, sentMessagesCols, SENT_MESSAGES_TABLE));
            db.execSQL("DROP TABLE IF EXISTS temp_" + SENT_MESSAGES_TABLE);
            onCreate(db);
        }

    }

    /**
     * Get the table columns in the database. Credits http://goo.gl/7kOpU
     * 
     * @param SQLiteDatabase db - The SQLiteDatabase to get the table columns
     * @param String tableName - The table to get the columns
     * @return List<String>
     */
    public static List<String> getColumns(SQLiteDatabase db, String tableName) {
        List<String> ar = null;
        Cursor c = null;

        try {
            c = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 1", null);

            if (c != null) {
                ar = new ArrayList<String>(Arrays.asList(c.getColumnNames()));
            }

        } catch (Exception e) {
            Log.v(tableName, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
        }
        return ar;
    }

    public static String join(List<String> list, String delim) {
        StringBuilder buf = new StringBuilder();
        int num = list.size();
        for (int i = 0; i < num; i++) {
            if (i != 0)
                buf.append(delim);
            buf.append((String)list.get(i));
        }
        return buf.toString();
    }

    public Database(Context context) {
        this.mContext = context;
    }

    public Database open() throws SQLException {
        mDbHelper = new DatabaseHelper(mContext);
        mDb = mDbHelper.getWritableDatabase();

        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    /**
     * Insert new message into the messages table.
     * 
     * @param Messages messages - The messages items.
     * @return long
     */
    public long createPendingMessages(Messages messages) {
        ContentValues initialValues = new ContentValues();

        initialValues.put(MESSAGES_ID, messages.getMessageId());
        initialValues.put(MESSAGES_FROM, messages.getMessageFrom());
        initialValues.put(MESSAGES_BODY, messages.getMessageBody());
        initialValues.put(MESSAGES_DATE, messages.getMessageDate());

        return mDb.insert(MESSAGES_TABLE, null, initialValues);
    }

    /**
     * Insert new message into the messages table. TODO://Change the name of
     * this function to insertMessages -- copy and paste is *evil*
     * 
     * @param Messages messages - The messages items.
     * @return long
     */
    public long createSentMessages(Messages messages) {
        ContentValues initialValues = new ContentValues();

        initialValues.put(SENT_MESSAGES_ID, messages.getMessageId());
        initialValues.put(SENT_MESSAGES_FROM, messages.getMessageFrom());
        initialValues.put(SENT_MESSAGES_BODY, messages.getMessageBody());
        initialValues.put(SENT_MESSAGES_DATE, messages.getMessageDate());

        return mDb.insert(SENT_MESSAGES_TABLE, null, initialValues);
    }

    /**
     * Fetch all messages in the database.
     * 
     * @return Cursor
     */
    public Cursor fetchAllMessages() {
        return mDb.query(MESSAGES_TABLE, MESSAGES_COLUMNS, null, null, null, null, MESSAGES_DATE
                + " DESC");
    }

    /**
     * Fetch all sent messages in the database.
     * 
     * @return Cursor
     */
    public Cursor fetchAllSentMessages() {
        return mDb.query(SENT_MESSAGES_TABLE, SENT_MESSAGES_COLUMNS, null, null, null, null,
                SENT_MESSAGES_DATE + " DESC");
    }

    /**
     * Fetch all messages in the database.
     * 
     * @param int limit - The limit to stop fetching pending messages
     * @return Cursor
     */
    public Cursor fetchMessagesByLimit(int limit) {
        String strLimit = String.valueOf(limit);
        return mDb.query(MESSAGES_TABLE, MESSAGES_COLUMNS, null, null, null, null, MESSAGES_DATE
                + " DESC", strLimit);
    }

    /**
     * Fetch messages by message id in the database.
     * 
     * @param int messageId - Message id to fetch by.
     * @return Cursor
     */
    public Cursor fetchMessagesById(int messageId) {
        String selection = MESSAGES_ID + "= ?";
        String selectionArgs[] = {
            new Integer(messageId).toString()
        };

        return mDb.query(MESSAGES_TABLE, MESSAGES_COLUMNS, selection, selectionArgs, null, null,
                MESSAGES_DATE + " DESC");
    }

    /**
     * Delete all messages in the database.
     * 
     * @return boolean
     */
    public boolean deleteAllMessages() {
        return mDb.delete(MESSAGES_TABLE, "1", null) > 0;
    }

    /**
     * Delete messages in the database by ID.
     * 
     * @param int messageId
     * @return boolean
     */
    public boolean deleteMessagesById(int messageId) {
        String whereClause = MESSAGES_ID + "= ?";
        String whereArgs[] = {
            new Integer(messageId).toString()
        };
        return mDb.delete(MESSAGES_TABLE, whereClause, whereArgs) > 0;
    }

    /**
     * Delete all sent messages in the database.
     * 
     * @return boolean
     */
    public boolean deleteAllSentMessages() {
        return mDb.delete(SENT_MESSAGES_TABLE, "1", null) > 0;
    }

    /**
     * Delete sent messages in the database by ID.
     * 
     * @param int messageId
     * @return boolean
     */
    public boolean deleteSentMessagesById(int messageId) {
        String whereClause = SENT_MESSAGES_ID + "= ?";
        String whereArgs[] = {
            new Integer(messageId).toString()
        };
        return mDb.delete(SENT_MESSAGES_TABLE, whereClause, whereArgs) > 0;
    }

    /**
     * Add a new message to the database.
     * 
     * @param List<Messages> messages - The messages to be added to the
     *            database.
     */
    public void addMessages(List<Messages> messages) {
        try {
            mDb.beginTransaction();

            for (Messages message : messages) {
                createPendingMessages(message);
            }

            mDb.setTransactionSuccessful();
        } finally {
            mDb.endTransaction();
        }
    }

    /**
     * Add a new sent message to the database.
     * 
     * @param List<Messages> messages - The messages to be added to the
     *            database.
     */
    public void addSentMessages(List<Messages> messages) {
        try {
            mDb.beginTransaction();

            for (Messages message : messages) {
                createSentMessages(message);
            }
            limitRows(SENT_MESSAGES_TABLE, 20, SENT_MESSAGES_ID);
            mDb.setTransactionSuccessful();
        } finally {
            mDb.endTransaction();
        }
    }

    /**
     * Count the number of sent messages in the database.
     * 
     * @return int
     */
    public int fetchSentMessagesCount() {
        Cursor mCursor = mDb.rawQuery("SELECT COUNT(" + SENT_MESSAGES_ID + ") FROM "
                + SENT_MESSAGES_TABLE, null);

        int result = 0;

        if (mCursor == null) {
            return result;
        }

        mCursor.moveToFirst();
        result = mCursor.getInt(0);
        mCursor.close();

        return result;
    }

    /**
     * Count the number of messages in the database.
     * 
     * @return int
     */
    public int fetchMessagesCount() {
        Cursor mCursor = mDb.rawQuery("SELECT COUNT(" + MESSAGES_ID + ") FROM " + MESSAGES_TABLE,
                null);

        int result = 0;

        if (mCursor == null) {
            return result;
        }

        mCursor.moveToFirst();
        result = mCursor.getInt(0);
        mCursor.close();

        return result;
    }

    public int limitRows(String tablename, int limit, String KEY_ID) {
        Cursor cursor = mDb.rawQuery("SELECT " + KEY_ID + " FROM " + tablename + " ORDER BY "
                + KEY_ID + " DESC LIMIT 1 OFFSET ?", new String[] {
            limit - 1 + ""
        });

        int deleted = 0;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int limitId = cursor.getInt(0);
                deleted = mDb.delete(tablename, KEY_ID + "<" + limitId, null);
            }
            cursor.close();
        }

        return deleted;
    }

}
