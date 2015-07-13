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

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Henry Addo
 */
public class TwitterAuthToken extends AuthToken implements Parcelable {

    // OAuth2
    public static final String AUTHORIZATION_BASIC = "Basic ";

    private static final String AUTHORIZATION_BEARER = "Bearer ";

    public static final String PARAM_GRANT_TYPE = "grant_type";

    public static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";

    public static final Creator<TwitterAuthToken> CREATOR
            = new Creator<TwitterAuthToken>() {
        public TwitterAuthToken createFromParcel(Parcel in) {
            return new TwitterAuthToken(in);
        }

        public TwitterAuthToken[] newArray(int size) {
            return new TwitterAuthToken[size];
        }
    };

    @SerializedName("token")
    public final String token;

    @SerializedName("secret")
    public final String secret;

    public TwitterAuthToken(String token, String secret) {
        super();
        this.token = token;
        this.secret = secret;
    }

    private TwitterAuthToken(Parcel in) {
        super();
        this.token = in.readString();
        this.secret = in.readString();
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    public Map<String, String> getAuthHeaders() {
        final Map<String, String> headers = new HashMap<>(1);
        String authorizationHeader = AUTHORIZATION_BEARER + token;
        headers.put(HEADER_AUTHORIZATION, authorizationHeader);
        return headers;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder()
                .append("token=").append(this.token)
                .append(",secret=").append(this.secret);
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(token);
        out.writeString(secret);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TwitterAuthToken)) {
            return false;
        }

        final TwitterAuthToken that = (TwitterAuthToken) o;

        if (secret != null ? !secret.equals(that.secret) : that.secret != null) {
            return false;
        }
        if (token != null ? !token.equals(that.token) : that.token != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = token != null ? token.hashCode() : 0;
        result = 31 * result + (secret != null ? secret.hashCode() : 0);
        return result;
    }
}
