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

package org.addhen.smssync.presentation.validator;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validates a given URL
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class UrlValidator implements Validator {

    @Override
    public boolean isValid(CharSequence text) {
        return valid(text.toString());
    }

    private boolean valid(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        final Pattern pattern = Patterns.WEB_URL;
        final Matcher matcher = pattern.matcher(url);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }
}
