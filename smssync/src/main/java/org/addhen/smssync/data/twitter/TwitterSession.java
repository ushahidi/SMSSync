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

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import android.text.TextUtils;

/**
 * Twitter Session
 *
 * @author Henry Addo
 */
public class TwitterSession extends Session<TwitterAuthToken> {

    @SerializedName("user_name")
    public final String mUserName;

    /**
     * @param authToken Auth token
     * @param id        User ID
     * @param userName  User Name
     * @throws {@link IllegalArgumentException} if token argument is null
     */
    public TwitterSession(TwitterAuthToken authToken, long id, String userName) {
        super(authToken, id);
        if (authToken == null) {
            throw new IllegalArgumentException("AuthToken must not be null.");
        }
        mUserName = userName;
    }

    public long getUserId() {
        return getId();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        final TwitterSession that = (TwitterSession) o;

        if (mUserName != null ? !mUserName.equals(that.mUserName) : that.mUserName != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (mUserName != null ? mUserName.hashCode() : 0);
        return result;
    }

    public static class Serializer implements SerializationStrategy<TwitterSession> {

        private final Gson gson;

        public Serializer() {
            this.gson = new Gson();
        }

        @Override
        public String serialize(TwitterSession session) {
            if (session != null && session.getAuthToken() != null) {
                try {
                    return gson.toJson(session);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Do nothing
                }
            }
            return "";
        }

        @Override
        public TwitterSession deserialize(String serializedSession) {
            if (!TextUtils.isEmpty(serializedSession)) {
                try {
                    return gson.fromJson(serializedSession, TwitterSession.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Do nothing
                }
            }
            return null;
        }
    }
}
