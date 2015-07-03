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

package org.addhen.smssync.data.database.converter;

import com.google.gson.Gson;

import android.content.ContentValues;
import android.database.Cursor;

import java.lang.reflect.Type;

import nl.qbusict.cupboard.convert.EntityConverter;
import nl.qbusict.cupboard.convert.FieldConverter;

public class SyncSchmeFieldConverter<T> implements FieldConverter<T> {

    private final Gson mGson;

    private final Type mType;

    SyncSchmeFieldConverter(Type type, Gson gson) {
        mType = type;
        mGson = gson;
    }

    @Override
    public T fromCursorValue(Cursor cursor, int index) {
        return mGson.fromJson(cursor.getString(index), mType);
    }

    @Override
    public void toContentValue(T value, String key, ContentValues values) {
        values.put(key, mGson.toJson(value));
    }

    @Override
    public EntityConverter.ColumnType getColumnType() {
        return EntityConverter.ColumnType.TEXT;
    }
}