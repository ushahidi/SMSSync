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

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class TwitterAuthConfig implements Parcelable {

    /**
     * The default request code to use for Single Sign On. This code will
     * be returned in {@link android.app.Activity#onActivityResult(int, int,
     * android.content.Intent)}
     */
    public static final int DEFAULT_AUTH_REQUEST_CODE = 140;

    public static final Creator<TwitterAuthConfig> CREATOR
            = new Creator<TwitterAuthConfig>() {
        public TwitterAuthConfig createFromParcel(Parcel in) {
            return new TwitterAuthConfig(in);
        }

        public TwitterAuthConfig[] newArray(int size) {
            return new TwitterAuthConfig[size];
        }
    };

    public final String consumerKey;

    public final String consumerSecret;

    public String accessToken;

    public String accessTokenSecret;

    /**
     * @param consumerKey    The consumer key.
     * @param consumerSecret The consumer secret.
     * @throws {@link IllegalArgumentException} if consumer key or consumer secret is
     *                null.
     */
    public TwitterAuthConfig(String consumerKey, String consumerSecret) {
        if (consumerKey == null || consumerSecret == null) {
            throw new IllegalArgumentException(
                    "TwitterAuthConfig must not be created with null consumer key or secret.");
        }
        this.consumerKey = sanitizeAttribute(consumerKey);
        this.consumerSecret = sanitizeAttribute(consumerSecret);
    }

    private TwitterAuthConfig(Parcel in) {
        consumerKey = in.readString();
        consumerSecret = in.readString();
        accessToken = in.readString();
        accessTokenSecret = in.readString();
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setAccessTokenSecret(String accessTokenSecret) {
        this.accessTokenSecret = accessTokenSecret;
    }

    /**
     * @return The request code to use for Single Sign On. This code will
     * be returned in {@link android.app.Activity#onActivityResult(int, int,
     * android.content.Intent)}
     * when the activity exits.
     */
    public int getRequestCode() {
        return DEFAULT_AUTH_REQUEST_CODE;
    }

    static String sanitizeAttribute(String input) {
        if (input != null) {
            return input.trim();
        } else {
            return null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(consumerKey);
        out.writeString(consumerSecret);
        out.writeString(accessToken);
        out.writeString(accessTokenSecret);
    }
}
