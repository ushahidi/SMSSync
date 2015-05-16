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

package com.github.jberkel.pay.me.model;

import java.util.Locale;

public enum ItemType {
    /** normal in app purchase */
    INAPP,
    /** subscription */
    SUBS,
    /** unknown type */
    UNKNOWN;

    public String toString() {
        return name().toLowerCase(Locale.ENGLISH);
    }

    public static ItemType fromString(String type) {
        for (ItemType t : values()) {
            if (t.toString().equals(type)) return t;
        }
        return UNKNOWN;
    }
}
