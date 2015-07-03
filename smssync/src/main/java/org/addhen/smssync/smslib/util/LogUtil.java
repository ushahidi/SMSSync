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

package org.addhen.smssync.smslib.util;

import org.addhen.smssync.BuildConfig;

import android.util.Log;

/**
 * Utility for logging items
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class LogUtil {

    private LogUtil() {
        // No instantiation
    }

    public static final boolean LOGGING_MODE = BuildConfig.DEBUG;

    public static void logDebug(String tag, String message) {
        if (LOGGING_MODE) {
            Log.d(tag, message);
        }
    }

    public static void logInfo(String tag, String message) {
        if (LOGGING_MODE) {
            Log.i(tag, message);
        }
    }

    public static void logDebug(String tag, String message, Object... args) {
        if (LOGGING_MODE) {
            Log.d(tag, message);
        }
    }

    public static void logInfo(String tag, String format, Object... args) {
        if (LOGGING_MODE) {
            Log.i(tag, String.format(format, args));
        }
    }

    public static void logError(String tag, String message, Exception ex) {
        if (LOGGING_MODE) {
            Log.e(tag, message, ex);
        }
    }
}
