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

package org.addhen.smssync.database;

public interface IFilterSchema {

    public static final String ID = "_id";

    public static final String PHONE_NUMBER = "phone_number";

    public static final String STATUS = "status";

    public static final String TABLE = "whitelist_blacklist";

    public static final String[] COLUMNS = new String[]{ID, PHONE_NUMBER, STATUS};

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + STATUS + " INTEGER , " + PHONE_NUMBER + " TEXT "
            + ")";
}
