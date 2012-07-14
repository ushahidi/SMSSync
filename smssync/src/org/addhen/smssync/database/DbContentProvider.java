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

import org.addhen.smssync.models.Model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public abstract class DbContentProvider {

	public SQLiteDatabase mDb;

	public int delete(String tableName, String selection, String[] selectionArgs) {
		return mDb.delete(tableName, selection, selectionArgs);

	}

	public long insert(String tableName, ContentValues values) {
		return mDb.insert(tableName, null, values);

	}

	protected abstract <T extends Model> T cursorToEntity(Cursor cursor);

	public DbContentProvider(SQLiteDatabase db) {

		this.mDb = db;

	}

	public Cursor query(String tableName, String[] columns, String selection,
			String[] selectionArgs, String sortOrder) {
		try {
			final Cursor cursor = mDb.query(tableName, columns, selection,
					selectionArgs, null, null, sortOrder);

			return cursor;
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Cursor query(String tableName, String[] columns, String selection,
			String[] selectionArgs, String sortOrder, String limit) {
		try {
			return mDb.query(tableName, columns, selection, selectionArgs,
					null, null, sortOrder, limit);
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int update(String tableName, ContentValues values, String selection,
			String[] selectionArgs) {
		try {
			return mDb.update(tableName, values, selection, selectionArgs);
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public Cursor rawQuery(String sql, String[] selectionArgs) {
		try {
			return mDb.rawQuery(sql, selectionArgs);
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		return null;
	}

}
