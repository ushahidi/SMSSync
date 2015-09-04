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

package org.addhen.smssync.presentation.util;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class Utility {

    private static final String URL_PATTERN
            = "\\b(https?|ftp|file)://[-a-zA-Z0-9+\\$&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

    private static Pattern pattern;

    private static Matcher matcher;

    public static String formatDate(Date messageDate) {
        DateFormat formatter = new SimpleDateFormat("hh:mm a");
        return formatter.format(messageDate);
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * This method removes all whitespaces from passed string
     *
     * @param s String to be trimmed
     * @return String without whitespaces
     */
    public static String removeWhitespaces(String s) {
        if (TextUtils.isEmpty(s)) {
            return "";
        }
        String withoutWhiteChars = s.replaceAll("\\s+", "");
        return withoutWhiteChars;
    }

    /**
     * Validate the callback URL
     *
     * @param url - The callback URL to be validated.
     * @return boolean True when URL is valid False otherwise
     */
    public static boolean validateUrl(String url) {

        if (TextUtils.isEmpty(url)) {
            return false;
        }

        pattern = Pattern.compile(URL_PATTERN);
        matcher = pattern.matcher(url);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }
}
