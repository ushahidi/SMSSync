package org.addhen.smssync.database.converter;

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
