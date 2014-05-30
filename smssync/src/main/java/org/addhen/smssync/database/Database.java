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

package org.addhen.smssync.database;

import org.addhen.smssync.Prefs;
import org.addhen.smssync.R;
import org.addhen.smssync.models.Message;
import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.net.SyncScheme;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Handles all database activities.
 *
 * @author eyedol
 */
public class Database {

    private static final String TAG = "SmssyncDatabase";

    public static final String SENT_MESSAGES_UUID = "_id";

    public static final String SENT_MESSAGES_FROM = "messages_from";

    public static final String SENT_MESSAGES_BODY = "messages_body";

    public static final String SENT_MESSAGES_DATE = "messages_date";

    public static final String SENT_MESSAGE_TYPE = "message_type";

    public static final String SENT_RESULT_CODE = "sent_result_code";

    public static final String SENT_RESULT_MESSAGE = "sent_result_message";

    public static final String DELIVERY_RESULT_CODE = "delivery_result_code";

    public static final String DELIVERY_RESULT_MESSAGE = "delivery_result_message";

    public static final String[] SENT_MESSAGES_COLUMNS = new String[]{
            SENT_MESSAGES_UUID, SENT_MESSAGES_FROM, SENT_MESSAGES_BODY,
            SENT_MESSAGES_DATE, SENT_MESSAGE_TYPE, SENT_RESULT_CODE,
            SENT_RESULT_MESSAGE, DELIVERY_RESULT_CODE, DELIVERY_RESULT_MESSAGE};

    private DatabaseHelper mDbHelper;

    protected SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "smssync_db";

    private static final String SENT_MESSAGES_TABLE = "sent_messages";

    private static final int DATABASE_VERSION = 7;

    private static final String SENT_MESSAGES_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + SENT_MESSAGES_TABLE
            + " ("
            + SENT_MESSAGES_UUID
            + " TEXT, "
            + SENT_MESSAGES_FROM
            + " TEXT NOT NULL, "
            + SENT_MESSAGES_BODY
            + " INT, "
            + SENT_MESSAGE_TYPE
            + " TEXT, "
            + SENT_MESSAGES_DATE
            + " DATE NOT NULL " + ")";

    private final Context mContext;

    public static SyncUrlContentProvider syncUrlContentProvider; // CP

    public static MessagesContentProvider messagesContentProvider;

    public static FilterContentProvider filterContentProvider;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        private Context mContext;

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(IMessagesSchema.CREATE_TABLE);
            db.execSQL(SENT_MESSAGES_TABLE_CREATE);
            db.execSQL(ISyncUrlSchema.CREATE_TABLE);
            db.execSQL(IFilterSchema.CREATE_TABLE);
            DatabaseUpgrade.upgradeToVersion7(db); //updating database to version 7
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + " which destroys all old data");

            // upgrade messages table
            // drop the column to deleted
            dropColumn(db, IMessagesSchema.CREATE_TABLE, IMessagesSchema.TABLE,
                    new String[]{"_id"});

            // upgrade sent messages table
            dropColumn(db, SENT_MESSAGES_TABLE_CREATE, SENT_MESSAGES_TABLE,
                    new String[]{"_id"});

            //Upgrade database
            switch (oldVersion) {
                case 6:
                    DatabaseUpgrade.upgradeToVersion7(db);
                case 7:
                    break;
                default:
                    Log.w(TAG, "Unknown database version");
                    break;
            }


            db.execSQL(ISyncUrlSchema.CREATE_TABLE);
            db.execSQL(IFilterSchema.CREATE_TABLE);
            // add old sync url configuration to the database,
            syncLegacySyncUrl(mContext, db);
            onCreate(db);
        }

    }

    private static void dropColumn(SQLiteDatabase db, String createTableCmd,
                                   String tableName, String[] colsToRemove) {

        List<String> updatedTableColumns = getColumns(db, tableName);
        // Remove the columns we don't want anymore from the table's list of
        // columns
        updatedTableColumns.removeAll(Arrays.asList(colsToRemove));

        String columnsSeperated = TextUtils.join(",", updatedTableColumns);

        db.execSQL("ALTER TABLE " + tableName + " RENAME TO " + tableName
                + "_old;");

        // Creating the table with its new format (no redundant columns)
        db.execSQL(createTableCmd);

        // Populating the table with the data
        db.execSQL("INSERT INTO " + tableName + "(" + columnsSeperated
                + ") SELECT " + columnsSeperated + " FROM " + tableName
                + "_old;");
        db.execSQL("DROP TABLE " + tableName + "_old;");
    }

    /**
     * Get the table columns in the database. Credits http://goo.gl/7kOpU
     *
     * @param db        - The SQLiteDatabase to get the table columns
     * @param tableName - The table to get the columns
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
            if (c != null) {
                c.close();
            }
        }
        return ar;
    }

    public static String join(List<String> list, String delim) {
        StringBuilder buf = new StringBuilder();
        int num = list.size();
        for (int i = 0; i < num; i++) {
            if (i != 0) {
                buf.append(delim);
            }
            buf.append((String) list.get(i));
        }
        return buf.toString();
    }

    public Database(Context context) {
        this.mContext = context;
    }

    public Database open() throws SQLException {
        mDbHelper = new DatabaseHelper(mContext);
        mDb = mDbHelper.getWritableDatabase();
        syncUrlContentProvider = new SyncUrlContentProvider(mDb);
        messagesContentProvider = new MessagesContentProvider(mDb);
        filterContentProvider = new FilterContentProvider(mDb);
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Insert new message into the messages table.
     *
     * @param messages - The messages items.
     * @return boolean
     */
    public boolean insertMessage(Messages messages) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(SENT_MESSAGES_FROM, messages.getMessageFrom());
        initialValues.put(SENT_MESSAGES_BODY, messages.getMessageBody());
        initialValues.put(SENT_MESSAGES_DATE, messages.getMessageDate());
        initialValues.put(SENT_MESSAGE_TYPE, messages.getMessageType());
        initialValues.put(SENT_RESULT_CODE, messages.getMessageFrom());
        initialValues.put(SENT_RESULT_MESSAGE, messages.getMessageBody());
        initialValues.put(DELIVERY_RESULT_CODE, messages.getMessageDate());
        initialValues.put(DELIVERY_RESULT_MESSAGE, messages.getMessageType());

        String selectionClause = SENT_MESSAGES_UUID + "=" + '"' + messages.getMessageUuid() + '"';

        if (mDb.update(SENT_MESSAGES_TABLE, initialValues, selectionClause, null) > 0) {
            return true;
        } else {
            initialValues.put(SENT_MESSAGES_UUID, messages.getMessageUuid());
            return mDb.insert(SENT_MESSAGES_TABLE, null, initialValues) > 0;
        }
    }

    public boolean updateSentResult(Message msg) {
        ContentValues value = new ContentValues();
        value.put(SENT_RESULT_CODE, msg.getSentResultCode());
        value.put(SENT_RESULT_MESSAGE, msg.getSentResultMessage());
        value.put(SENT_MESSAGE_TYPE, msg.getMessageType());
        String selectionClause = SENT_MESSAGES_UUID + "=" + '"' + msg.getUuid() + '"';

        return mDb.update(SENT_MESSAGES_TABLE, value, selectionClause, null) > 0;
    }

    public boolean updateDeliveryResult(Message msg) {
        ContentValues value = new ContentValues();
        value.put(DELIVERY_RESULT_CODE, msg.getDeliveryResultCode());
        value.put(DELIVERY_RESULT_MESSAGE, msg.getDeliveryResultMessage());
        value.put(SENT_MESSAGE_TYPE, msg.getMessageType());
        String selectionClause = SENT_MESSAGES_UUID + "=" + '"' + msg.getUuid() + '"';

        return mDb.update(SENT_MESSAGES_TABLE, value, selectionClause, null) > 0;
    }

    /**
     * Fetch all sent messages in the database.
     *
     * @return Cursor
     */
    public Cursor fetchAllSentMessages() {
        return mDb.query(SENT_MESSAGES_TABLE, SENT_MESSAGES_COLUMNS, null,
                null, null, null, SENT_MESSAGES_DATE + " DESC");
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
     * @return boolean
     */
    public boolean deleteSentMessagesByUuid(String messageId) {
        String whereClause = SENT_MESSAGES_UUID + "= ?";
        String whereArgs[] = {messageId};
        return mDb.delete(SENT_MESSAGES_TABLE, whereClause, whereArgs) > 0;
    }

    /**
     * Add a new sent message to the database.
     *
     * @param messages - The messages to be added to the database.
     */
    public void addSentMessages(List<Messages> messages) {
        try {
            mDb.beginTransaction();

            for (Messages message : messages) {
                insertMessage(message);
            }
            limitRows(SENT_MESSAGES_TABLE, 20, SENT_MESSAGES_UUID);
            mDb.setTransactionSuccessful();
        } finally {
            mDb.endTransaction();
        }
    }

    public static boolean addSyncUrl(SyncUrl syncUrl, SQLiteDatabase db) {
        // set values
        ContentValues initialValues = new ContentValues();
        initialValues.put(ISyncUrlSchema.TITLE, syncUrl.getTitle());
        initialValues.put(ISyncUrlSchema.URL, syncUrl.getUrl());
        initialValues.put(ISyncUrlSchema.KEYWORDS, syncUrl.getKeywords());
        initialValues.put(ISyncUrlSchema.SECRET, syncUrl.getSecret());
        initialValues.put(ISyncUrlSchema.STATUS, syncUrl.getStatus());
        initialValues.put(ISyncUrlSchema.SYNCSCHEME, syncUrl.getSyncScheme().toJSONString());
        return db.insert(ISyncUrlSchema.TABLE, null, initialValues) > 0;
    }

    public static boolean addSyncUrl(List<SyncUrl> syncUrls,
                                     SQLiteDatabase db) {

        try {
            db.beginTransaction();

            for (SyncUrl syncUrl : syncUrls) {

                addSyncUrl(syncUrl, db);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return true;
    }

    /**
     * Count the number of sent messages in the database.
     *
     * @return int
     */
    public int fetchSentMessagesCount() {
        Cursor mCursor = mDb.rawQuery("SELECT COUNT(" + SENT_MESSAGES_UUID
                + ") FROM " + SENT_MESSAGES_TABLE, null);

        int result = 0;
        try {
            if (mCursor == null) {
                return result;
            }

            mCursor.moveToFirst();
            result = mCursor.getInt(0);
        } finally {
            mCursor.close();
        }

        return result;
    }

    public int limitRows(String tablename, int limit, String KEY_ID) {
        Cursor cursor = mDb.rawQuery("SELECT " + KEY_ID + " FROM " + tablename
                + " ORDER BY " + KEY_ID + " DESC LIMIT 1 OFFSET ?",
                new String[]{limit - 1 + ""});

        int deleted = 0;
        try {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int limitId = cursor.getInt(0);
                    deleted = mDb.delete(tablename, KEY_ID + "<" + limitId,
                            null);
                }

            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return deleted;
    }

    public static void syncLegacySyncUrl(Context context, SQLiteDatabase db) {
        // saved preferences
        final SharedPreferences settings = context.getSharedPreferences(
                Prefs.PREF_NAME, 0);

        final String website = settings.getString("WebsitePref", "");
        final String apiKey = settings.getString("ApiKey", "");
        final String keyword = settings.getString("Keyword", "");
        SyncUrl syncUrl = new SyncUrl();
        List<SyncUrl> listSyncUrl = new ArrayList<SyncUrl>();
        if (!TextUtils.isEmpty(website)) {
            syncUrl.setKeywords(keyword);
            syncUrl.setSecret(apiKey);
            syncUrl.setTitle(context.getString(R.string.sync_url));
            syncUrl.setUrl(website);
            syncUrl.setStatus(1);
            syncUrl.setSyncScheme(new SyncScheme());
            listSyncUrl.add(syncUrl);
            addSyncUrl(listSyncUrl, db);
        }

    }
}
