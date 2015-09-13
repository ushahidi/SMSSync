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

package org.addhen.smssync.presentation.exception;

import org.addhen.smssync.BuildConfig;
import org.addhen.smssync.R;
import org.addhen.smssync.data.exception.FilterNotFoundException;
import org.addhen.smssync.data.exception.MessageNotFoundException;

import android.content.Context;

/**
 * Creates the various app exceptions
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class ErrorMessageFactory {

    private ErrorMessageFactory() {
        // Do nothing
    }

    /**
     * Creates a String representing an error message.
     *
     * @param context   Context needed to retrieve string resources.
     * @param exception An exception used as a condition to retrieve the correct error message.
     * @return {@link String} an error message.
     */
    public static String create(Context context, Exception exception) {
        String message = context.getString(R.string.exception_message_generic);
        if (exception instanceof FilterNotFoundException) {
            message = context.getString(R.string.exception_message_filter_not_found);
        } else if (exception instanceof MessageNotFoundException) {
            message = context.getString(R.string.exception_message_filter_not_found);
        }

        // Only print stacktrace when running a debug build for debugging purposes
        if (BuildConfig.DEBUG) {
            exception.printStackTrace();
        }
        return message;
    }
}
