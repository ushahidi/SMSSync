package org.addhen.smssync.database;

/**
 * Created by Tomasz Stalka(tstalka@soldevelo.com) on 4/29/14.
 */

import org.addhen.smssync.util.Logger;
import org.addhen.smssync.util.Util;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;


public final class DatabaseUpgrade {

    public static final String MESSAGES_TABLE = "messages";

    public static final String MESSAGE_TYPE = "message_type";

    public static final String SENT_RESULT_CODE = "sent_result_code";

    public static final String SENT_RESULT_MESSAGE = "sent_result_message";

    public static final String DELIVERY_RESULT_CODE = "delivery_result_code";

    public static final String DELIVERY_RESULT_MESSAGE = "delivery_result_message";

    public static final String INT = " INT";

    public static final String TEXT = " TEXT";

    private static final String TAG = DatabaseUpgrade.class.getName();

    private static final String ALTER_TABLE = "ALTER TABLE ";

    private static final String ADD_COLUMN = " ADD COLUMN ";


    private DatabaseUpgrade() {

    }

    private static void addStringColumn(SQLiteDatabase sqLiteDatabase, String tableName,
            String column) {
        sqLiteDatabase.execSQL(ALTER_TABLE + tableName + ADD_COLUMN
                + column + TEXT + ";");
    }

    private static void addIntColumn(SQLiteDatabase sqLiteDatabase, String tableName,
            String column) {
        sqLiteDatabase.execSQL(ALTER_TABLE + tableName + ADD_COLUMN
                + column + INT + ";");
    }

    private static void cloneTable(SQLiteDatabase sqLiteDatabase, String colums,
            String newTableName, String oldTableName) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS " + newTableName + " AS SELECT " + colums + " FROM "
                        + oldTableName);
    }

    private static void dropTable(SQLiteDatabase sqLiteDatabase, String tableName) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + tableName);
    }

    private static void createTable(SQLiteDatabase sqLiteDatabase, String tableName,
            String columnNames) {
        sqLiteDatabase.execSQL(
                String.format("CREATE TABLE IF NOT EXISTS %s (%s) ", tableName, columnNames));
    }

    private static void insertInto(SQLiteDatabase sqLiteDatabase, String columns, String newTable,
            String oldTable) {
        final String stmt = String.format("INSERT INTO %s (%s) SELECT * FROM %s ", newTable,
                columns, oldTable);
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

           /* addIntColumn(sqLiteDatabase, Database.SENT_MESSAGES_TABLE, SENT_RESULT_CODE);
            addStringColumn(sqLiteDatabase, Database.SENT_MESSAGES_TABLE, SENT_RESULT_MESSAGE);
            addIntColumn(sqLiteDatabase, Database.SENT_MESSAGES_TABLE, DELIVERY_RESULT_CODE);
            addStringColumn(sqLiteDatabase, Database.SENT_MESSAGES_TABLE, DELIVERY_RESULT_MESSAGE);*/

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
           /* final String CLONE_MESSAGE_TABLE = "clone_"+MESSAGES_TABLE;
            final String CLONE_SENT_TABEL = "clone_"+Database.SENT_MESSAGES_TABLE;

            cloneTable(sqLiteDatabase, "*", CLONE_MESSAGE_TABLE , MESSAGES_TABLE);
            cloneTable(sqLiteDatabase, "*", CLONE_SENT_TABEL, Database.SENT_MESSAGES_TABLE);

            dropTable(sqLiteDatabase, MESSAGES_TABLE);
            dropTable(sqLiteDatabase, Database.SENT_MESSAGES_TABLE);

            createTable(sqLiteDatabase, IMessagesSchema.TABLE,
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, message_uuid TEXT, messages_from TEXT NOT NULL, messages_body TEXT, messages_date DATE NOT NULL , message_type INT, sent_result_code INT, sent_result_message TEXT, delivery_result_code INT, delivery_result_message TEXT, retries INTEGER");
            createTable(sqLiteDatabase, Database.SENT_MESSAGES_TABLE,
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, message_uuid TEXT, messages_from TEXT NOT NULL, messages_body TEXT, messages_date DATE NOT NULL , message_type INT, sent_result_code INT, sent_result_message TEXT, delivery_result_code INT, delivery_result_message TEXT, retries INTEGER");

            insertInto(sqLiteDatabase,
                    "message_uuid,messages_from,messages_body,messages_date,message_type,sent_result_code,sent_result_message,delivery_result_code,delivery_result_message",
                    MESSAGES_TABLE, CLONE_MESSAGE_TABLE);
            insertInto(sqLiteDatabase,
                    "_id,messages_from,messages_body,messages_date,message_type,sent_result_code,sent_result_message,delivery_result_code,delivery_result_message",
                    Database.SENT_MESSAGES_TABLE, CLONE_SENT_TABEL);

            dropTable(sqLiteDatabase, CLONE_MESSAGE_TABLE);
            dropTable(sqLiteDatabase, CLONE_SENT_TABEL);*/
            success = true;
        } catch (SQLiteException ex) {
            Log.e(TAG, "Error executing SQL : ", ex);
        }

        return success;
    }

}