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

/**
 * @author Henry Addo
 */
public class Session<T extends AuthToken> {

    @SerializedName("auth_token")
    private final T authToken;

    @SerializedName("id")
    private final long id;

    public Session(T authToken, long id) {
        this.authToken = authToken;
        this.id = id;
    }

    public T getAuthToken() {
        return authToken;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Session session = (Session) o;

        if (id != session.id) {
            return false;
        }
        if (authToken != null ? !authToken.equals(session.authToken) : session.authToken != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = authToken != null ? authToken.hashCode() : 0;
        result = 31 * result + (int) (id ^ (id >>> 32));
        return result;
    }
}
