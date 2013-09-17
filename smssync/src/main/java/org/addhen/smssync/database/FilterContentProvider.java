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

import org.addhen.smssync.models.Filter;
import org.addhen.smssync.util.Util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

public class FilterContentProvider extends DbContentProvider implements
        IFilterContentProvider, IFilterSchema {

    private Cursor cursor;

    private List<Filter> mListFilter;

    private ContentValues mInitialValues;

    public FilterContentProvider(SQLiteDatabase db) {
        super(db);
    }

    @Override
    public List<Filter> fetchAll() {

        try {
            cursor = super.query(TABLE, COLUMNS, null, null, ID + " DESC");
            if (cursor != null) {

                mListFilter = new ArrayList<Filter>();
                while (cursor.moveToNext()) {
                    Filter filter = cursorToEntity(cursor);
                    mListFilter.add(filter);

                }
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }

        }
        return mListFilter;
    }

    @Override
    public List<Filter> fetchById(int id) {

        final String selection = ID + " = ?";

        final String selectionArgs[] = {
                String.valueOf(id)
        };

        try {
            cursor = super.query(TABLE, COLUMNS, selection, selectionArgs, ID + " DESC");

            if (cursor != null) {
                mListFilter = new ArrayList<Filter>();

                while (cursor.moveToNext()) {
                    Filter filter = cursorToEntity(cursor);
                    mListFilter.add(filter);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return mListFilter;
    }

    @Override
    public List<Filter> fetchByStatus(int status) {

        final String selection = STATUS + " = ?";

        final String selectionArgs[] = {
                String.valueOf(status)
        };

        try {
            cursor = super.query(TABLE, COLUMNS, selection, selectionArgs, ID + " DESC");
            if (cursor != null) {
                mListFilter = new ArrayList<Filter>();
                while (cursor.moveToNext()) {
                    mListFilter.add(cursorToEntity(cursor));
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return mListFilter;
    }

    @Override
    public boolean add(Filter filter) {
        // set values
        setContentValue(filter);
        return super.insert(TABLE, getContentValue()) > 0;
    }

    @Override
    public boolean add(List<Filter> filters) {

        try {
            mDb.beginTransaction();

            for (Filter filter : filters) {

                add(filter);
            }

            mDb.setTransactionSuccessful();
        } finally {
            mDb.endTransaction();
        }
        return true;
    }

    @Override
    public boolean deleteAll() {
        return super.delete(TABLE, "1", null) > 0;
    }

    /**
     * Delete a particular filter.
     *
     * @param id The sync url it's id
     * @return boolean
     */
    @Override
    public boolean deleteById(int id) {

        final String selectionArgs[] = {
                String.valueOf(id)
        };
        final String selection = ID + " = ?";

        return super.delete(TABLE, selection, selectionArgs) > 0;
    }

    @Override
    public boolean update(Filter filter) {

        setContentValue(filter);
        final String selectionArgs[] = {
                String.valueOf(filter.getId())
        };
        final String selection = ID + " = ?";
        return super.update(TABLE, getContentValue(), selection, selectionArgs) > 0;
    }

    @Override
    public int total() {
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

    private void setContentValue(Filter filter) {
        mInitialValues = new ContentValues();
        mInitialValues.put(PHONE_NUMBER, filter.getPhoneNumber());
        mInitialValues.put(STATUS, filter.getStatus().code);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Filter cursorToEntity(Cursor cursor) {
        Filter filter = new Filter();

        int idIndex;
        int statusIndex;
        int phoneNumberIndex;

        if (cursor != null) {
            if (cursor.getColumnIndex(ID) != -1) {
                idIndex = cursor.getColumnIndexOrThrow(ID);
                filter.setId(cursor.getInt(idIndex));
            }
            if (cursor.getColumnIndex(STATUS) != -1) {
                statusIndex = cursor.getColumnIndexOrThrow(STATUS);
                int status = cursor.getInt(statusIndex);
                if (status == Filter.Status.BLACKLIST.code) {
                    filter.setStatus(Filter.Status.BLACKLIST);
                }
            }

            if (cursor.getColumnIndex(PHONE_NUMBER) != -1) {
                phoneNumberIndex = cursor.getColumnIndexOrThrow(PHONE_NUMBER);
                filter.setPhoneNumber(cursor.getString(phoneNumberIndex));
            }

        }
        return filter;
    }

}
