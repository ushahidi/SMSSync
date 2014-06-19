package org.addhen.smssync.database;

/**
 * Created by Tomasz Stalka(tstalka@soldevelo.com) on 4/29/14.
 */

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import org.addhen.smssync.MainApplication;

import static org.addhen.smssync.database.Database.messagesContentProvider;


public final class DatabaseUpgrade {

    private static final String TAG = DatabaseUpgrade.class.getName();

    private static final String ALTER_TABLE = "ALTER TABLE ";

    private static final String ADD_COLUMN = " ADD COLUMN ";

    private static final String SENT_MESSAGES_TABLE = "sent_messages";

    public static final String MESSAGES_TABLE = "messages";

    public static final String MESSAGE_TYPE = "message_type";

    public static final String SENT_RESULT_CODE = "sent_result_code";

    public static final String SENT_RESULT_MESSAGE = "sent_result_message";

    public static final String DELIVERY_RESULT_CODE = "delivery_result_code";

    public static final String DELIVERY_RESULT_MESSAGE = "delivery_result_message";

    public static final String INT = " INT";

    public static final String TEXT = " TEXT";

    private DatabaseUpgrade() {

    }

    private static void addStringColumn(SQLiteDatabase sqLiteDatabase, String tableName, String column) {
        sqLiteDatabase.execSQL(ALTER_TABLE + tableName + ADD_COLUMN
                + column + TEXT + ";");
    }

    private static void addIntColumn(SQLiteDatabase sqLiteDatabase, String tableName, String column) {
        sqLiteDatabase.execSQL(ALTER_TABLE + tableName + ADD_COLUMN
                + column + INT + ";");
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

}
