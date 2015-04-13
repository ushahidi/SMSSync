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

package org.addhen.smssync.tests;

import android.test.AndroidTestCase;

import org.addhen.smssync.database.Database;

import java.util.Collection;

/**
 * Base test class for handling all unit test that doesn't call any of Android's
 * specific API.
 *
 * @author eyedol
 */
public abstract class BaseTest extends AndroidTestCase {

    public static Database mDb;

    /**
     * Assert not null or empty.
     *
     * @param message the message
     * @param value   the value
     */
    protected static void assertNotNullOrEmpty(String message, String value) {
        assertNotNull(message, value);
        assertFalse(message, "".equals(value));
    }

    /**
     * Assert not null or empty.
     *
     * @param message the message
     * @param value   the value
     */
    protected static void assertNotNullOrEmpty(String message,
                                               Collection<?> value) {
        assertNotNull(message, value);
        assertFalse(message, value.isEmpty());
    }

    protected static void assertNotNullOrZero(String message, int value) {
        assertNotNull(message, value);
        assertEquals(message, 0, value);
    }

    protected static void assertNotNullOrEqual(String message, String expected, String actual) {
        assertNotNullOrEmpty(message, actual);
        assertEquals(message, expected, actual);
    }

}
