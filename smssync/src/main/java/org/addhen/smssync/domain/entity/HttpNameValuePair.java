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

package org.addhen.smssync.domain.entity;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class HttpNameValuePair {

    private String mName;

    private String mValue;

    public HttpNameValuePair(String name, String value) {
        mName = name;
        mValue = value;
    }

    public String getValue() {
        return mValue;
    }

    public String getName() {
        return mName;
    }
}
