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

package org.addhen.smssync.data.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Created by Kamil Kalfas(kkalfas@soldevelo.com) on 24.04.14.
 *
 * This class uses Google gson to parsing objects and strings
 */
public final class JsonUtils {

    private JsonUtils() {
        // Prevent instantiation
    }

    public static <T> T getObj(String json, java.lang.Class<T> afterClass) {
        return new Gson().fromJson(json, afterClass);
    }

    public static <T> T getObj(String json, Type type) {
        return new Gson().fromJson(json, type);
    }

    public static String objToJson(Object object) {
        return new Gson().toJson(object);
    }
}
