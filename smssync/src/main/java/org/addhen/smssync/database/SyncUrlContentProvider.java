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

import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.net.SyncScheme;
import org.addhen.smssync.util.Util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

public class SyncUrlContentProvider extends DbContentProvider implements
        ISyncUrlContentProvider, ISyncUrlSchema {

    private Cursor cursor;

    private List<SyncUrl> mListSyncUrl;

    private ContentValues mInitialValues;

    public SyncUrlContentProvider(SQLiteDatabase db) {
        super(db);
    }

    @Override
    public List<SyncUrl> fetchSyncUrl() {

        try {
            cursor = super.query(TABLE, COLUMNS, null, null, ID);
            if (cursor != null) {

                mListSyncUrl = new ArrayList<SyncUrl>();
                while (cursor.moveToNext()) {
                    SyncUrl syncUrl = cursorToEntity(cursor);
                    mListSyncUrl.add(syncUrl);

                }
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }

        }
        return mListSyncUrl;
    }

    @Override
    public List<SyncUrl> fetchSyncUrlById(int id) {

        final String selection = ID + " = ?";

        final String selectionArgs[] = {
                String.valueOf(id)
        };

        mListSyncUrl = new ArrayList<SyncUrl>();

        try {

            cursor = super.query(TABLE, COLUMNS, selection, selectionArgs, ID);
            if (cursor != null) {

                while (cursor.moveToNext()) {
                    SyncUrl syncUrl = cursorToEntity(cursor);
                    mListSyncUrl.add(syncUrl);

                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return mListSyncUrl;
    }

    @Override
    public List<SyncUrl> fetchSyncUrlByStatus(int status) {

        final String selection = STATUS + " = ?";

        final String selectionArgs[] = {
                String.valueOf(status)
        };

        mListSyncUrl = new ArrayList<SyncUrl>();

        try {
            cursor = super.query(TABLE, COLUMNS, selection, selectionArgs, ID);
            if (cursor != null) {

                while (cursor.moveToNext()) {
                    mListSyncUrl.add(cursorToEntity(cursor));
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return mListSyncUrl;
    }

    @Override
    public boolean addSyncUrl(SyncUrl syncUrl) {
        // set values
        setContentValue(syncUrl);
        return super.insert(TABLE, getContentValue()) > 0;
    }

    @Override
    public boolean addSyncUrl(List<SyncUrl> syncUrls) {

        try {
            mDb.beginTransaction();

            for (SyncUrl syncUrl : syncUrls) {

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
     * @param id The sync url it's id
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
     * Update the status of a sync url
     *
     * @param syncUrl The sync url to be updated
     * @return boolean
     */
    public boolean updateStatus(SyncUrl syncUrl) {

        mInitialValues = new ContentValues();
        mInitialValues.put(TITLE, syncUrl.getTitle());
        mInitialValues.put(URL, syncUrl.getUrl());
        mInitialValues.put(KEYWORDS, syncUrl.getKeywords());
        mInitialValues.put(SECRET, syncUrl.getSecret());
        mInitialValues.put(STATUS, syncUrl.getStatus());
        mInitialValues.put(SYNCSCHEME, syncUrl.getSyncScheme().toJSONString());

        final String selectionArgs[] = {
                String.valueOf(syncUrl.getId())
        };
        final String selection = ID + " = ? ";

        return super.update(TABLE, mInitialValues, selection, selectionArgs) > 0;
    }

    private void setContentValue(SyncUrl syncUrl) {

        mInitialValues = new ContentValues();
        mInitialValues.put(TITLE, syncUrl.getTitle());
        mInitialValues.put(URL, syncUrl.getUrl());
        mInitialValues.put(KEYWORDS, syncUrl.getKeywords());
        mInitialValues.put(SECRET, syncUrl.getSecret());
        mInitialValues.put(STATUS, syncUrl.getStatus());
        mInitialValues.put(SYNCSCHEME, syncUrl.getSyncScheme().toJSONString());

    }

    @Override
    public boolean updateSyncUrl(SyncUrl syncUrl) {
        mInitialValues = new ContentValues();
        mInitialValues.put(TITLE, syncUrl.getTitle());
        mInitialValues.put(URL, syncUrl.getUrl());
        mInitialValues.put(KEYWORDS, syncUrl.getKeywords());
        mInitialValues.put(SECRET, syncUrl.getSecret());
        mInitialValues.put(STATUS, syncUrl.getStatus());
        mInitialValues.put(SYNCSCHEME, syncUrl.getSyncScheme().toJSONString());
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
    public SyncUrl cursorToEntity(Cursor cursor) {
        SyncUrl syncUrl = new SyncUrl();

        int idIndex;
        int statusIndex;
        int titleIndex;
        int keywordsIndex;
        int urlIndex;
        int secretIndex;
        int syncSchemeIndex;

        if (cursor == null) {
            return syncUrl;
        }
        try {
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

            if (cursor.getColumnIndex(SYNCSCHEME) != -1) {
                syncSchemeIndex = cursor.getColumnIndex(SYNCSCHEME);
                syncUrl.setSyncScheme(new SyncScheme(cursor.getString(syncSchemeIndex)));
            }

        }catch (Exception e) {
            syncUrl = new SyncUrl();
        }
        return syncUrl;
    }

}
