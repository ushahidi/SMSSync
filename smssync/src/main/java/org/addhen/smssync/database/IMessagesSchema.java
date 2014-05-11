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

/**
 * @author eyedol
 */
public interface IMessagesSchema {

    public static final String TYPE = "message_type";

    public static final String FROM = "messages_from";

    public static final String BODY = "messages_body";

    public static final String DATE = "messages_date";

    public static final String MESSAGE_UUID = "message_uuid";

    public static final String SENT_RESULT_CODE = "sent_result_code";

    public static final String SENT_RESULT_MESSAGE = "sent_result_message";

    public static final String DELIVERY_RESULT_CODE = "delivery_result_code";

    public static final String DELIVERY_RESULT_MESSAGE = "delivery_result_message";

    public static final String TABLE = "messages";

    public static final String[] COLUMNS = new String[]{MESSAGE_UUID,
            FROM, BODY, DATE, TYPE, SENT_RESULT_CODE, SENT_RESULT_MESSAGE,
            DELIVERY_RESULT_CODE, DELIVERY_RESULT_MESSAGE};

    // NOTE: the message ID is used as the row ID.
    // Furthermore, if a row already exists, an insert will replace
    // the old row upon conflict.

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE + " (" + MESSAGE_UUID + " TEXT, " + FROM
            + " TEXT NOT NULL, " + BODY + " TEXT, " + DATE + " DATE NOT NULL "
            + ")";


}
