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
