/*
 * Copyright (c) 2010 - 2015 Ushahidi Inc
 * All rights reserved
 * Contact: team@ushahidi.com
 * Website: http://www.ushahidi.com
 * GNU Lesser General Public License Usage
 * This file may be used under the terms of the GNU Lesser
 * General Public License version 3 as published by the Free Software
 * Foundation and appearing in the file LICENSE.LGPL included in the
 * packaging of this file. Please review the following information to
 * ensure the GNU Lesser General Public License version 3 requirements
 * will be met: http://www.gnu.org/licenses/lgpl.html.
 *
 * If you have questions regarding the use of this file, please contact
 * Ushahidi developers at team@ushahidi.com.
 */

package org.addhen.smssync.database;

/**
 * Created by Tomasz Stalka(tstalka@soldevelo.com) on 4/29/14.
 */

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import org.addhen.smssync.models.Filter;
import org.addhen.smssync.models.Message;
import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.net.SyncScheme;
import org.addhen.smssync.util.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;


public final class DatabaseUpgrade {

    public static final String MESSAGES_TABLE = "messages";

    public static final String MESSAGE_TYPE = "message_type";

    public static final String MESSAGE_FROM = "messages_from";

    public static final String MESSAGE_BODY = "messages_body";

    public static final String MESSAGE_DATE = "messages_date";

    public static final String MESSAGE_UUID = "message_uuid";

    public static final String MESSAGE_SENT_RESULT_CODE = "sent_result_code";

    public static final String MESSAGE_SENT_RESULT_MESSAGE = "sent_result_message";

    public static final String MESSAGE_DELIVERY_RESULT_CODE = "delivery_result_code";

    public static final String MESSAGE_DELIVERY_RESULT_MESSAGE = "delivery_result_message";

    public static final String SENT_MESSAGES_UUID = "_id";

    public static final String SENT_MESSAGES_FROM = "messages_from";

    public static final String SENT_MESSAGES_BODY = "messages_body";

    public static final String SENT_MESSAGES_DATE = "messages_date";

    public static final String SENT_MESSAGE_TYPE = "message_type";

    public static final String SENT_RESULT_CODE = "sent_result_code";

    public static final String SENT_RESULT_MESSAGE = "sent_result_message";

    public static final String DELIVERY_RESULT_CODE = "delivery_result_code";

    public static final String DELIVERY_RESULT_MESSAGE = "delivery_result_message";

    public static final String SENT_MESSAGES_TABLE = "sent_messages";

    public static final String INT = " INT";

    public static final String TEXT = " TEXT";
    public static final String[] SENT_MESSAGES_COLUMNS = new String[]{
            SENT_MESSAGES_UUID, SENT_MESSAGES_FROM, SENT_MESSAGES_BODY,
            SENT_MESSAGES_DATE, SENT_MESSAGE_TYPE, SENT_RESULT_CODE,
            SENT_RESULT_MESSAGE, DELIVERY_RESULT_CODE, DELIVERY_RESULT_MESSAGE};
    public static final String[] MESSAGES_COLUMNS = new String[]{
            MESSAGE_UUID, MESSAGE_FROM, MESSAGE_BODY,
            MESSAGE_DATE, MESSAGE_TYPE, MESSAGE_SENT_RESULT_CODE,
            MESSAGE_SENT_RESULT_MESSAGE, MESSAGE_DELIVERY_RESULT_CODE, MESSAGE_DELIVERY_RESULT_MESSAGE};
    public static final int PENDING = 0;
    public static final int TASK = 1;
    public static final int UNCONFIRMED = 2;
    public static final int FAILED = 3;
    private static final String TAG = DatabaseUpgrade.class.getName();
    private static final String ALTER_TABLE = "ALTER TABLE ";
    private static final String ADD_COLUMN = " ADD COLUMN ";

    private DatabaseUpgrade() {

    }

    private static void addStringColumn(SQLiteDatabase sqLiteDatabase, String tableName,
                                        String column) {
        Logger.log(TAG, "adding String column " + column + " to " + tableName);
        sqLiteDatabase.execSQL(ALTER_TABLE + tableName + ADD_COLUMN
                + column + TEXT + ";");
    }

    private static void addIntColumn(SQLiteDatabase sqLiteDatabase, String tableName,
                                     String column) {
        Logger.log(TAG, "adding a new int column " + column + " to " + tableName);
        sqLiteDatabase.execSQL(ALTER_TABLE + tableName + ADD_COLUMN
                + column + INT + ";");
    }

    private static void cloneTable(SQLiteDatabase sqLiteDatabase, String colums,
                                   String newTableName, String oldTableName) {
        Logger.log(TAG, "cloning table " + oldTableName + " into " + newTableName);
        sqLiteDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS " + newTableName + " AS SELECT " + colums + " FROM "
                        + oldTableName);
    }

    private static void dropTable(SQLiteDatabase sqLiteDatabase, String tableName) {
        Logger.log(TAG, "drop table " + tableName);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + tableName);
    }

    private static void createTable(SQLiteDatabase sqLiteDatabase, String tableName,
                                    String columnNames) {
        Logger.log(TAG, "create table " + tableName + " with column names " + columnNames);
        sqLiteDatabase.execSQL(
                String.format("CREATE TABLE IF NOT EXISTS %s (%s) ", tableName, columnNames));
    }

    private static void insertInto(SQLiteDatabase sqLiteDatabase, String columns, String newTable,
                                   String oldTable) {
        Logger.log(TAG, "insert into " + newTable + " from " + oldTable + " with columns " + columns);
        insertInto(sqLiteDatabase, columns, "*", newTable, oldTable);

    }

    private static void insertInto(SQLiteDatabase sqLiteDatabase, String newColumns, String oldColumns, String newTable,
                                   String oldTable) {
        final String stmt = String.format("INSERT INTO %s (%s) SELECT %s FROM %s ", newTable, newColumns,
                oldColumns, oldTable);
        Logger.log(TAG, stmt);
        sqLiteDatabase.execSQL(stmt);
    }

    public static boolean upgradeToVersion7(SQLiteDatabase sqLiteDatabase) {
        Log.w(TAG, "Upgrading database to version 7.");
        boolean success = false;

        try {

            addIntColumn(sqLiteDatabase, MESSAGES_TABLE, MESSAGE_TYPE);
            addIntColumn(sqLiteDatabase, MESSAGES_TABLE, SENT_RESULT_CODE);
            addStringColumn(sqLiteDatabase, MESSAGES_TABLE, SENT_RESULT_MESSAGE);
            addIntColumn(sqLiteDatabase, MESSAGES_TABLE, DELIVERY_RESULT_CODE);
            addStringColumn(sqLiteDatabase, MESSAGES_TABLE, DELIVERY_RESULT_MESSAGE);

            addIntColumn(sqLiteDatabase, SENT_MESSAGES_TABLE, SENT_RESULT_CODE);
            addStringColumn(sqLiteDatabase, SENT_MESSAGES_TABLE, SENT_RESULT_MESSAGE);
            addIntColumn(sqLiteDatabase, SENT_MESSAGES_TABLE, DELIVERY_RESULT_CODE);
            addStringColumn(sqLiteDatabase, SENT_MESSAGES_TABLE, DELIVERY_RESULT_MESSAGE);

            success = true;

        } catch (SQLiteException ex) {
            Log.e(TAG, "Error executing SQL : ", ex);
        }

        return success;
    }

    public static boolean upgradeToVersion8(SQLiteDatabase sqLiteDatabase) {
        Log.w(TAG, "Upgrading database to version 8.");

        boolean success = false;
        try {
            Logger.log(TAG, "Cloning tables ");
            final String CLONE_MESSAGE_TABLE = "clone_" + MESSAGES_TABLE;
            final String CLONE_SENT_TABLE = "clone_" + SENT_MESSAGES_TABLE;

            cloneTable(sqLiteDatabase, "*", CLONE_MESSAGE_TABLE, MESSAGES_TABLE);
            cloneTable(sqLiteDatabase, "*", CLONE_SENT_TABLE, SENT_MESSAGES_TABLE);

            dropTable(sqLiteDatabase, MESSAGES_TABLE);

            dropTable(sqLiteDatabase, SENT_MESSAGES_TABLE);

            createTable(sqLiteDatabase, IMessagesSchema.TABLE,
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, message_uuid TEXT, messages_from TEXT NOT NULL, messages_body TEXT, messages_date DATE NOT NULL , message_type INT, sent_result_code INT, sent_result_message TEXT, delivery_result_code INT, delivery_result_message TEXT, retries INTEGER");
            createTable(sqLiteDatabase, SENT_MESSAGES_TABLE,
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, message_uuid TEXT, messages_from TEXT NOT NULL, messages_body TEXT, messages_date DATE NOT NULL , message_type INT, sent_result_code INT, sent_result_message TEXT, delivery_result_code INT, delivery_result_message TEXT, retries INTEGER");

            insertInto(sqLiteDatabase,
                    "message_uuid,messages_from,messages_body,messages_date,message_type,sent_result_code,sent_result_message,delivery_result_code,delivery_result_message",
                    MESSAGES_TABLE, CLONE_MESSAGE_TABLE);

            /*insertInto(sqLiteDatabase,
                    "_id,messages_from,messages_body,messages_date,message_type,sent_result_code,sent_result_message,delivery_result_code,delivery_result_message",
                    SENT_MESSAGES_TABLE, CLONE_SENT_TABLE);*/

            updatePendingMessages(sqLiteDatabase);
            fetchSentMessages(sqLiteDatabase, CLONE_SENT_TABLE);
            updateSentMessages(sqLiteDatabase, CLONE_SENT_TABLE);

            upgradeSyncUrl(sqLiteDatabase);

            updateFilterTable(sqLiteDatabase);

            dropTable(sqLiteDatabase, CLONE_MESSAGE_TABLE);
            dropTable(sqLiteDatabase, CLONE_SENT_TABLE);
            dropTable(sqLiteDatabase, MESSAGES_TABLE);
            dropTable(sqLiteDatabase, SENT_MESSAGES_TABLE);
            dropTable(sqLiteDatabase, "whitelist_blacklist");
            success = true;
        } catch (SQLiteException ex) {
            Log.e(TAG, "Error executing SQL : ", ex);
        }

        return success;
    }

    private static void updatePendingMessages(SQLiteDatabase sqLiteDatabase) {
        // Insert Items from old messages table into new message table
        Cursor cursor = sqLiteDatabase.query(MESSAGES_TABLE, MESSAGES_COLUMNS, null, null, null, null, MESSAGE_DATE + " DESC");

        List<Message> messages = new ArrayList<>();

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    int uuidIndex = cursor.getColumnIndexOrThrow(MESSAGE_UUID);
                    int fromIndex = cursor.getColumnIndexOrThrow(MESSAGE_FROM);
                    int dateIndex = cursor.getColumnIndexOrThrow(MESSAGE_DATE);
                    int bodyIndex = cursor.getColumnIndexOrThrow(MESSAGE_BODY);
                    int typeIndex = cursor.getColumnIndexOrThrow(MESSAGE_TYPE);
                    int sentResultCodeIndex = cursor.getColumnIndexOrThrow(MESSAGE_SENT_RESULT_CODE);
                    int sentResultMessageIndex = cursor.getColumnIndexOrThrow(MESSAGE_SENT_RESULT_MESSAGE);
                    int deliveryResultCodeIndex = cursor.getColumnIndexOrThrow(MESSAGE_DELIVERY_RESULT_CODE);
                    int deliveryResultMessageIndex = cursor.getColumnIndexOrThrow(MESSAGE_DELIVERY_RESULT_MESSAGE);

                    String uuid = cursor.getString(uuidIndex);
                    String from = cursor.getString(fromIndex);
                    String date = cursor.getString(dateIndex);
                    String body = cursor.getString(bodyIndex);
                    int type = cursor.getInt(typeIndex);
                    int sentResultCode = cursor.getInt(sentResultCodeIndex);
                    String sentResultMessage = cursor.getString(sentResultMessageIndex);
                    int deliveryResultCode = cursor.getInt(deliveryResultCodeIndex);
                    String deliveryResultMessage = cursor.getString(deliveryResultMessageIndex);

                    Message message = new Message();
                    message.setUuid(uuid);
                    message.setPhoneNumber(from);
                    message.setDate(new Date(Long.valueOf(date)));
                    message.setBody(body);
                    if (type == TASK) {
                        message.setType(Message.Type.TASK);
                    } else if (type == PENDING) {
                        message.setType(Message.Type.PENDING);
                        message.setStatus(Message.Status.FAILED);
                    } else if (type == UNCONFIRMED) {
                        message.setType(Message.Type.TASK);
                        message.setStatus(Message.Status.UNCONFIRMED);
                    } else if (type == FAILED) {
                        message.setType(Message.Type.TASK);
                        message.setStatus(Message.Status.FAILED);
                    }

                    message.setSentResultCode(sentResultCode);
                    message.setSentResultMessage(sentResultMessage);
                    message.setDeliveryResultCode(deliveryResultCode);
                    message.setDeliveryResultMessage(deliveryResultMessage);

                    messages.add(message);

                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            cupboard().withDatabase(sqLiteDatabase).put(messages);
        }
    }

    private static void fetchSentMessages(SQLiteDatabase sqLiteDatabase, String clonedTable) {
        Cursor cursor = sqLiteDatabase.query(SENT_MESSAGES_TABLE, SENT_MESSAGES_COLUMNS, null, null, null, null, SENT_MESSAGES_DATE + " DESC");

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    int uuidIndex = cursor.getColumnIndexOrThrow(SENT_MESSAGES_UUID);
                    int fromIndex = cursor.getColumnIndexOrThrow(SENT_MESSAGES_FROM);
                    int dateIndex = cursor.getColumnIndexOrThrow(SENT_MESSAGES_DATE);
                    int bodyIndex = cursor.getColumnIndexOrThrow(SENT_MESSAGES_BODY);
                    int typeIndex = cursor.getColumnIndexOrThrow(SENT_MESSAGE_TYPE);
                    int sentResultCodeIndex = cursor.getColumnIndexOrThrow(SENT_RESULT_CODE);
                    int sentResultMessageIndex = cursor.getColumnIndexOrThrow(SENT_RESULT_MESSAGE);
                    int deliveryResultCodeIndex = cursor.getColumnIndexOrThrow(DELIVERY_RESULT_CODE);
                    int deliveryResultMessageIndex = cursor.getColumnIndexOrThrow(DELIVERY_RESULT_MESSAGE);

                    String uuid = cursor.getString(uuidIndex);
                    String from = cursor.getString(fromIndex);
                    String date = cursor.getString(dateIndex);
                    String body = cursor.getString(bodyIndex);
                    int type = cursor.getInt(typeIndex);
                    int sentResultCode = cursor.getInt(sentResultCodeIndex);
                    String sentResultMessage = cursor.getString(sentResultMessageIndex);
                    int deliveryResultCode = cursor.getInt(deliveryResultCodeIndex);
                    String deliveryResultMessage = cursor.getString(deliveryResultMessageIndex);
                    ContentValues initialValues = new ContentValues();
                    initialValues.put(SENT_MESSAGES_UUID, uuid);
                    initialValues.put(SENT_MESSAGES_FROM, from);
                    initialValues.put(SENT_MESSAGES_BODY, body);
                    initialValues.put(SENT_MESSAGES_DATE, date);
                    initialValues.put(SENT_MESSAGE_TYPE, type);
                    initialValues.put(SENT_RESULT_CODE, sentResultCode);
                    initialValues.put(SENT_RESULT_MESSAGE, sentResultMessage);
                    initialValues.put(DELIVERY_RESULT_CODE, deliveryResultCode);
                    initialValues.put(DELIVERY_RESULT_MESSAGE, deliveryResultMessage);
                    sqLiteDatabase.insert(clonedTable, null, initialValues);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

    private static void updateSentMessages(SQLiteDatabase sqLiteDatabase, String clonedTable) {
        // Insert Items from old messages table into new message table
        Cursor cursor = sqLiteDatabase.query(clonedTable, SENT_MESSAGES_COLUMNS, null, null, null, null, SENT_MESSAGES_DATE + " DESC");

        List<Message> messages = new ArrayList<>();

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    int uuidIndex = cursor.getColumnIndexOrThrow("_id");
                    int fromIndex = cursor.getColumnIndexOrThrow(SENT_MESSAGES_FROM);
                    int dateIndex = cursor.getColumnIndexOrThrow(SENT_MESSAGES_DATE);
                    int bodyIndex = cursor.getColumnIndexOrThrow(SENT_MESSAGES_BODY);
                    int typeIndex = cursor.getColumnIndexOrThrow(SENT_MESSAGE_TYPE);
                    int sentResultCodeIndex = cursor.getColumnIndexOrThrow(SENT_RESULT_CODE);
                    int sentResultMessageIndex = cursor.getColumnIndexOrThrow(SENT_RESULT_MESSAGE);
                    int deliveryResultCodeIndex = cursor.getColumnIndexOrThrow(DELIVERY_RESULT_CODE);
                    int deliveryResultMessageIndex = cursor.getColumnIndexOrThrow(DELIVERY_RESULT_MESSAGE);

                    String uuid = cursor.getString(uuidIndex);
                    String from = cursor.getString(fromIndex);
                    String date = cursor.getString(dateIndex);
                    String body = cursor.getString(bodyIndex);
                    int type = cursor.getInt(typeIndex);
                    int sentResultCode = cursor.getInt(sentResultCodeIndex);
                    String sentResultMessage = cursor.getString(sentResultMessageIndex);
                    int deliveryResultCode = cursor.getInt(deliveryResultCodeIndex);
                    String deliveryResultMessage = cursor.getString(deliveryResultMessageIndex);

                    Message message = new Message();
                    message.setUuid(uuid);
                    message.setPhoneNumber(from);
                    message.setDate(new Date(Long.valueOf(date)));
                    message.setBody(body);

                    if (type == TASK) {

                        message.setType(Message.Type.TASK);
                    } else {

                        message.setType(Message.Type.PENDING);
                    }

                    message.setStatus(Message.Status.SENT);
                    message.setSentResultCode(sentResultCode);
                    message.setSentResultMessage(sentResultMessage);
                    message.setDeliveryResultCode(deliveryResultCode);
                    message.setDeliveryResultMessage(deliveryResultMessage);

                    messages.add(message);

                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            cupboard().withDatabase(sqLiteDatabase).put(messages);

        }
    }

    private static void upgradeSyncUrl(SQLiteDatabase sqLiteDatabase) {
        Logger.log(TAG, "Upgrading SyncUrl Table");
        final String CREATE_TABLE = ISyncUrlSchema.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ISyncUrlSchema.STATUS + " TEXT , " + ISyncUrlSchema.KEYWORDS + " TEXT, " + ISyncUrlSchema.TITLE
                + " TEXT NOT NULL, " + ISyncUrlSchema.SECRET + " TEXT, " + ISyncUrlSchema.URL + " TEXT "
                + ", " + ISyncUrlSchema.SYNCSCHEME + " TEXT";

        final String CLONE_SYNC_URL = "clone_sync_url";
        cloneTable(sqLiteDatabase, "*", CLONE_SYNC_URL, ISyncUrlSchema.TABLE);
        dropTable(sqLiteDatabase, ISyncUrlSchema.TABLE);
        createTable(sqLiteDatabase, ISyncUrlSchema.TABLE, CREATE_TABLE);
        // Insert Items from old messages table into new message table
        Cursor cursor = sqLiteDatabase.query(CLONE_SYNC_URL, ISyncUrlSchema.COLUMNS, null, null, null, null, null);

        List<SyncUrl> syncUrls = new ArrayList<>();

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {

                    int uuidIndex = cursor.getColumnIndexOrThrow("_id");
                    int statusIndex = cursor.getColumnIndexOrThrow(ISyncUrlSchema.STATUS);
                    int keywordsIndex = cursor.getColumnIndexOrThrow(ISyncUrlSchema.KEYWORDS);
                    int titleIndex = cursor.getColumnIndexOrThrow(ISyncUrlSchema.TITLE);
                    int schemeIndex = cursor.getColumnIndexOrThrow(ISyncUrlSchema.SYNCSCHEME);
                    int urlIndex = cursor.getColumnIndexOrThrow(ISyncUrlSchema.URL);
                    int secretKey = cursor.getColumnIndexOrThrow(ISyncUrlSchema.SECRET);

                    int uuid = cursor.getInt(uuidIndex);
                    int status = cursor.getInt(statusIndex);
                    String keywords = cursor.getString(keywordsIndex);
                    String title = cursor.getString(titleIndex);
                    String url = cursor.getString(urlIndex);
                    String scheme = cursor.getString(schemeIndex);
                    String secret = cursor.getString(secretKey);
                    SyncUrl syncUrl = new SyncUrl();
                    syncUrl.setId(Long.valueOf(uuid));
                    syncUrl.setKeywords(keywords);
                    syncUrl.setTitle(title);
                    syncUrl.setSyncScheme(new SyncScheme(scheme));
                    syncUrl.setSecret(secret);

                    if (status == 0) {
                        syncUrl.setStatus(SyncUrl.Status.DISABLED);
                    } else {
                        syncUrl.setStatus(SyncUrl.Status.ENABLED);
                    }
                    syncUrl.setUrl(url);
                    syncUrls.add(syncUrl);

                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            cupboard().withDatabase(sqLiteDatabase).put(syncUrls);
        }
        dropTable(sqLiteDatabase, CLONE_SYNC_URL);
    }

    private static void updateFilterTable(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_TABLE = "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + IFilterSchema.STATUS + " TEXT , " + IFilterSchema.PHONE_NUMBER + " TEXT ";

        final String[] COLUMNS = new String[]{IFilterSchema.ID, IFilterSchema.PHONE_NUMBER, IFilterSchema.STATUS};

        final String CLONE_WHITELIST_BLACKLIST = "clone_whitelist_blacklist";

        cloneTable(sqLiteDatabase, "*", CLONE_WHITELIST_BLACKLIST, IFilterSchema.TABLE);

        dropTable(sqLiteDatabase, IFilterSchema.TABLE);

        createTable(sqLiteDatabase, IFilterSchema.TABLE, CREATE_TABLE);

        // Insert Items from old messages table into new message table
        Cursor cursor = sqLiteDatabase.query(CLONE_WHITELIST_BLACKLIST, COLUMNS, null, null, null, null, null);

        List<Filter> filters = new ArrayList<>();

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    Filter filter = new Filter();
                    int uuidIndex = cursor.getColumnIndexOrThrow("_id");
                    int statusIndex = cursor.getColumnIndexOrThrow(IFilterSchema.STATUS);
                    int phoneIndex = cursor.getColumnIndexOrThrow(IFilterSchema.PHONE_NUMBER);

                    int uuid = cursor.getInt(uuidIndex);
                    int status = cursor.getInt(statusIndex);
                    String phoneNumber = cursor.getString(phoneIndex);
                    filter.setId(Long.valueOf(uuid));
                    if (status == 0) {
                        filter.setStatus(Filter.Status.BLACKLIST);
                    } else {
                        filter.setStatus(Filter.Status.WHITELIST);
                    }
                    filter.setPhoneNumber(phoneNumber);
                    filters.add(filter);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            cupboard().withDatabase(sqLiteDatabase).put(filters);
        }

        dropTable(sqLiteDatabase, CLONE_WHITELIST_BLACKLIST);
    }

}