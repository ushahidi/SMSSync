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

package org.addhen.smssync.data.twitter;

import org.scribe.exceptions.OAuthException;
import org.scribe.utils.OAuthEncoder;

import android.support.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class RequestUtils {

    private static final Pattern TOKEN_REGEX = Pattern.compile("oauth_token=([^&]+)");

    private static final Pattern SECRET_REGEX = Pattern.compile("oauth_token_secret=([^&]*)");

    private static final Pattern SCREEN_NAME_REGEX = Pattern.compile("screen_name=([^&]+)");

    private static final Pattern USER_ID_REGEX = Pattern.compile("user_id=([^&]+)");

    /**
     * {@inheritDoc}
     */
    public static OAuthResponse extract(@NonNull String response) {
        String token = extract(response, TOKEN_REGEX);
        String secret = extract(response, SECRET_REGEX);
        String screenName = extract(response, SCREEN_NAME_REGEX);
        String userId = extract(response, USER_ID_REGEX);
        OAuthResponse authResponse = new OAuthResponse();
        authResponse.oauthToken = token;
        authResponse.oauthTokenSecret = secret;
        authResponse.screenName = screenName;
        authResponse.userId = Long.valueOf(userId);
        return authResponse;
    }

    private static String extract(String response, Pattern p) {
        Matcher matcher = p.matcher(response);
        if (matcher.find() && matcher.groupCount() >= 1) {
            return OAuthEncoder.decode(matcher.group(1));
        } else {
            throw new OAuthException(
                    "Response body is incorrect. Can't extract token and secret from this: '"
                            + response + "'", null);
        }
    }
}
