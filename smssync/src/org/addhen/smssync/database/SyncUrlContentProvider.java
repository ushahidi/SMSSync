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

import org.addhen.smssync.models.SyncUrlModel;
import org.addhen.smssync.util.Util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteStatement;

public class SyncUrlContentProvider extends DbContentProvider implements
        ISyncUrlContentProvider, ISyncUrlSchema {

    private Cursor cursor;

    private List<SyncUrlModel> mListSyncUrl;

    private ContentValues mInitialValues;

    public SyncUrlContentProvider(SQLiteDatabase db) {
        super(db);
    }

    @Override
    public List<SyncUrlModel> fetchSyncUrl() {
        mListSyncUrl = new ArrayList<SyncUrlModel>();

        try {
            if (cursor != null) {
                cursor = super.query(TABLE, COLUMNS, null, null, ID);
                while (cursor.moveToNext()) {
                    SyncUrlModel syncUrl = cursorToEntity(cursor);
                    mListSyncUrl.add(syncUrl);

                }
            }

        } finally {

            if (cursor != null)
                cursor.close();

        }
        return mListSyncUrl;
    }

    @Override
    public List<SyncUrlModel> fetchSyncUrlById(int id) {

        final String selection = ID + " = ?";

        final String selectionArgs[] = {
                String.valueOf(id)
        };

        mListSyncUrl = new ArrayList<SyncUrlModel>();

        try {

            cursor = super.query(TABLE, COLUMNS, selection, selectionArgs, ID);
            if (cursor != null) {

                while (cursor.moveToNext()) {
                    SyncUrlModel syncUrl = cursorToEntity(cursor);
                    mListSyncUrl.add(syncUrl);

                }
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return mListSyncUrl;
    }

    @Override
    public List<SyncUrlModel> fetchSyncUrlByStatus(int status) {

        final String selection = STATUS + " = ?";

        final String selectionArgs[] = {
                String.valueOf(status)
        };

        mListSyncUrl = new ArrayList<SyncUrlModel>();

        try {
            cursor = super.query(TABLE, COLUMNS, selection, selectionArgs, ID);
            if (cursor != null) {

                while (cursor.moveToNext()) {
                    mListSyncUrl.add(cursorToEntity(cursor));
                }
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return mListSyncUrl;
    }

    @Override
    public boolean addSyncUrl(SyncUrlModel syncUrl) {
        // set values
        setContentValue(syncUrl);
        return super.insert(TABLE, getContentValue()) > 0;
    }

    @Override
    public boolean addSyncUrl(List<SyncUrlModel> syncUrls) {

        try {
            mDb.beginTransaction();

            for (SyncUrlModel syncUrl : syncUrls) {

                addSyncUrl(syncUrl);
            }

            mDb.setTransactionSuccessful();
        } finally {
            mDb.endTransaction();
        }
        return true;
    }

    @Override
    public boolean deleteAllSyncUrl() {
        return super.delete(TABLE, "1", null) > 0;
    }

    /**
     * Delete a particular sync URL.
     * 
     * @param int id The unique id of the sync URL
     * @return boolean
     */
    public boolean deleteSyncUrlById(int id) {

        final String selectionArgs[] = {
                String.valueOf(id)
        };
        final String selection = ID + " = ?";

        return super.delete(TABLE, selection, selectionArgs) > 0;
    }

    /**
     * Update the status of a sync url.
     * 
     * @param int status The status
     * @param int id The unique id of the sync URL
     * @return boolean
     */
    public boolean updateStatus(SyncUrlModel syncUrl) {

        mInitialValues = new ContentValues();
        mInitialValues.put(TITLE, syncUrl.getTitle());
        mInitialValues.put(URL, syncUrl.getUrl());
        mInitialValues.put(KEYWORDS, syncUrl.getKeywords());
        mInitialValues.put(SECRET, syncUrl.getSecret());
        mInitialValues.put(STATUS, syncUrl.getStatus());

        final String selectionArgs[] = {
                String.valueOf(syncUrl.getId())
        };
        final String selection = ID + " = ? ";

        return super.update(TABLE, mInitialValues, selection, selectionArgs) > 0;
    }

    private void setContentValue(SyncUrlModel syncUrl) {

        mInitialValues = new ContentValues();
        mInitialValues.put(TITLE, syncUrl.getTitle());
        mInitialValues.put(URL, syncUrl.getUrl());
        mInitialValues.put(KEYWORDS, syncUrl.getKeywords());
        mInitialValues.put(SECRET, syncUrl.getSecret());
        mInitialValues.put(STATUS, syncUrl.getStatus());

    }

    @Override
    public boolean updateSyncUrl(SyncUrlModel syncUrl) {
        mInitialValues = new ContentValues();
        mInitialValues.put(TITLE, syncUrl.getTitle());
        mInitialValues.put(URL, syncUrl.getUrl());
        mInitialValues.put(KEYWORDS, syncUrl.getKeywords());
        mInitialValues.put(SECRET, syncUrl.getSecret());
        mInitialValues.put(STATUS, syncUrl.getStatus());
        final String selectionArgs[] = {
                String.valueOf(syncUrl.getId())
        };
        final String selection = ID + " = ?";
        return super.update(TABLE, mInitialValues, selection, selectionArgs) > 0;
    }

    @Override
    public int totalActiveSyncUrl() {
        final String selectionArgs[] = {
                "1"
        };

        final String selection = STATUS + "=?";
        if (Util.isHoneycomb()) {
            return (int) DatabaseUtils.queryNumEntries(mDb, TABLE, selection, selectionArgs);
        }
        
        // For API < 11
        try {
            String sql = "SELECT COUNT(*) FROM " + TABLE + " WHERE " + STATUS + " =1";
            SQLiteStatement statement = mDb.compileStatement(sql);
            return (int) statement.simpleQueryForLong();
        } catch (SQLiteDoneException ex) {
            return 0;
        }

    }

    private ContentValues getContentValue() {
        return mInitialValues;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SyncUrlModel cursorToEntity(Cursor cursor) {
        SyncUrlModel syncUrl = new SyncUrlModel();

        int idIndex;
        int statusIndex;
        int titleIndex;
        int keywordsIndex;
        int urlIndex;
        int secretIndex;

        if (cursor != null) {
            if (cursor.getColumnIndex(ID) != -1) {
                idIndex = cursor.getColumnIndexOrThrow(ID);
                syncUrl.setId(cursor.getInt(idIndex));
            }
            if (cursor.getColumnIndex(STATUS) != -1) {
                statusIndex = cursor.getColumnIndexOrThrow(STATUS);
                syncUrl.setStatus(cursor.getInt(statusIndex));
            }

            if (cursor.getColumnIndex(TITLE) != -1) {
                titleIndex = cursor.getColumnIndexOrThrow(TITLE);
                syncUrl.setTitle(cursor.getString(titleIndex));
            }

            if (cursor.getColumnIndex(KEYWORDS) != -1) {
                keywordsIndex = cursor.getColumnIndexOrThrow(KEYWORDS);
                syncUrl.setKeywords(cursor.getString(keywordsIndex));
            }

            if (cursor.getColumnIndex(URL) != -1) {
                urlIndex = cursor.getColumnIndexOrThrow(URL);
                syncUrl.setUrl(cursor.getString(urlIndex));
            }

            if (cursor.getColumnIndex(SECRET) != -1) {
                secretIndex = cursor.getColumnIndex(SECRET);
                syncUrl.setSecret(cursor.getString(secretIndex));
            }

        }
        return syncUrl;
    }

}
