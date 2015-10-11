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
import com.google.gson.reflect.TypeToken;

import org.addhen.smssync.data.entity.SyncScheme;
import org.addhen.smssync.data.entity.SyncUrl;

import java.lang.reflect.Field;

import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.convert.FieldConverter;
import nl.qbusict.cupboard.convert.ReflectiveEntityConverter;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class WebServiceConverter extends ReflectiveEntityConverter<SyncUrl> {

    public WebServiceConverter(Cupboard cupboard) {
        super(cupboard, SyncUrl.class);
    }

    @Override
    protected FieldConverter<?> getFieldConverter(Field field) {
        if ("syncscheme".equals(field.getName())) {
            return new SyncSchmeFieldConverter(new TypeToken<SyncScheme>() {

            }.getType(), new Gson());
        }
        return super.getFieldConverter(field);
    }

}
