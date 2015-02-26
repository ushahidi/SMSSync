package org.addhen.smssync.database.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.net.SyncScheme;

import java.lang.reflect.Field;

import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.convert.FieldConverter;
import nl.qbusict.cupboard.convert.ReflectiveEntityConverter;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class SyncUrlConverter extends ReflectiveEntityConverter<SyncUrl> {

    public SyncUrlConverter(Cupboard cupboard) {
        super(cupboard, SyncUrl.class);
    }

    @Override
    protected FieldConverter<?> getFieldConverter(Field field) {
        if("syncscheme".equals(field.getName())) {
            return new SyncSchmeFieldConverter(new TypeToken<SyncScheme>() {

            }.getType(), new Gson());
        }
        return super.getFieldConverter(field);
    }

}
