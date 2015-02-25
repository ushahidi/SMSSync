package org.addhen.smssync.database.converter;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Locale;

import nl.qbusict.cupboard.convert.EntityConverter;
import nl.qbusict.cupboard.convert.FieldConverter;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class EnumEntityFieldConverter<E extends Enum> implements FieldConverter<E> {

    private final Class<E> mEnumClass;

    public EnumEntityFieldConverter(Class<E> enumClass) {
        this.mEnumClass = enumClass;
    }

    @Override
    public E fromCursorValue(Cursor cursor, int columnIndex) {
        return (E) Enum.valueOf(mEnumClass, cursor.getString(columnIndex).toUpperCase(
                Locale.getDefault()));
    }

    @Override
    public void toContentValue(E value, String key, ContentValues values) {
        values.put(key, value.toString());
    }

    @Override
    public EntityConverter.ColumnType getColumnType() {
        return EntityConverter.ColumnType.TEXT;
    }
}
