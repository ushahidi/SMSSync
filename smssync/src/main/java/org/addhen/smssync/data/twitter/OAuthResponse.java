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

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author Henry Addo
 */
public class OAuthResponse implements Serializable {

    private static final long serialVersionUID = 715000866082812683L;

    @SerializedName("oauth_token")
    public String oauthToken;

    @SerializedName("oauth_token_secret")
    public String oauthTokenSecret;

    @SerializedName("screen_name")
    public String screenName;

    @SerializedName("user_id")
    public long userId;

    @Override
    public String toString() {
        return "OAuthResponse{" +
                "oauthToken='" + oauthToken + '\'' +
                ", oauthTokenSecret='" + oauthTokenSecret + '\'' +
                ", screenName='" + screenName + '\'' +
                ", userId=" + userId +
                '}';
    }
}
