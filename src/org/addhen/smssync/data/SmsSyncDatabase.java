package org.addhen.smssync.data;

import org.addhen.smssync.SmsSync.SmssyncMsgs;
import org.addhen.smssync.SmsSync;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

/**
 * Provides access to a database of notes. Each note has a title, the note
 * itself, a creation date and a modified data.
 */
public class SmsSyncDatabase extends ContentProvider {

    private static final String TAG = "SmsSyncDatabase";

    private static final String DATABASE_NAME = "smsync.db";
    private static final int DATABASE_VERSION = 2;
    private static final String SMSSYNC_MSG_TABLE_NAME = "smssync_msgs";

    private static HashMap<String, String> smsSyncMsgProjectionMap;

    private static final int SMSSYNC_MSG = 1;
    private static final int SMSSYNC_MSG_ID = 2;

    private static final UriMatcher sUriMatcher;

    /**
     * This class helps open, create, and upgrade the database file.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + SMSSYNC_MSG_TABLE_NAME + " ("
                    + SmssyncMsgs._ID + " INTEGER PRIMARY KEY,"
                    + SmssyncMsgs.MESSAGE_FROM + " TEXT,"
                    + SmssyncMsgs.MESSAGE_BODY + " TEXT,"
                    + SmssyncMsgs.CREATED_DATE + " INTEGER,"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }

    private DatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
        case SMSSYNC_MSG:
            qb.setTables(SMSSYNC_MSG_TABLE_NAME);
            qb.setProjectionMap(smsSyncMsgProjectionMap);
            break;

        case SMSSYNC_MSG_ID:
            qb.setTables(SMSSYNC_MSG_TABLE_NAME);
            qb.setProjectionMap(smsSyncMsgProjectionMap);
            qb.appendWhere(SmssyncMsgs._ID + "=" + uri.getPathSegments().get(1));
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = SmsSync.SmssyncMsgs.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
        case SMSSYNC_MSG:
            return SmssyncMsgs.CONTENT_TYPE;

        case SMSSYNC_MSG_ID:
            return SmssyncMsgs.CONTENT_ITEM_TYPE;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        // Validate the requested uri
        if (sUriMatcher.match(uri) != SMSSYNC_MSG) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        Long now = Long.valueOf(System.currentTimeMillis());

        // Make sure that the fields are all set
        if (values.containsKey(SmsSync.SmssyncMsgs.CREATED_DATE) == false) {
            values.put(SmsSync.SmssyncMsgs.CREATED_DATE, now);
        }

        if (values.containsKey(SmsSync.SmssyncMsgs.MESSAGE_BODY) == false) {
            Resources r = Resources.getSystem();
            values.put(SmsSync.SmssyncMsgs.MESSAGE_BODY, r.getString(android.R.string.untitled));
        }

        if (values.containsKey(SmsSync.SmssyncMsgs.SMSSYNC_MSG) == false) {
            values.put(SmsSync.SmssyncMsgs.SMSSYNC_MSG, "");
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(SMSSYNC_MSG_TABLE_NAME, SmssyncMsgs.SMSSYNC_MSG, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(SmsSync.SmssyncMsgs.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case SMSSYNC_MSG:
            count = db.delete(SMSSYNC_MSG_TABLE_NAME, where, whereArgs);
            break;

        case SMSSYNC_MSG_ID:
            String noteId = uri.getPathSegments().get(1);
            count = db.delete(SMSSYNC_MSG_TABLE_NAME, SmssyncMsgs._ID + "=" + noteId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case SMSSYNC_MSG:
            count = db.update(SMSSYNC_MSG_TABLE_NAME, values, where, whereArgs);
            break;

        case SMSSYNC_MSG_ID:
            String noteId = uri.getPathSegments().get(1);
            count = db.update(SMSSYNC_MSG_TABLE_NAME, values, SmssyncMsgs._ID + "=" + noteId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(SmsSync.AUTHORITY, "notes", SMSSYNC_MSG);
        sUriMatcher.addURI(SmsSync.AUTHORITY, "notes/#", SMSSYNC_MSG_ID);

        smsSyncMsgProjectionMap = new HashMap<String, String>();
        smsSyncMsgProjectionMap.put(SmssyncMsgs._ID, SmssyncMsgs._ID);
        smsSyncMsgProjectionMap.put(SmssyncMsgs.MESSAGE_FROM, SmssyncMsgs.MESSAGE_FROM);
        smsSyncMsgProjectionMap.put(SmssyncMsgs.SMSSYNC_MSG, SmssyncMsgs.SMSSYNC_MSG);
        smsSyncMsgProjectionMap.put(SmssyncMsgs.CREATED_DATE, SmssyncMsgs.CREATED_DATE);
    }
}
