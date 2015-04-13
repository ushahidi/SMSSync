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

package com.github.jberkel.pay.me;

import android.content.res.Resources;

/**
 * Represents the result of an in-app billing operation.
 * A result is composed of a response code (an integer) and possibly a
 * message (String). You can get those by calling
 * {@link #getResponse} and {@link #getMessage()}, respectively. You
 * can also inquire whether a result is a success or a failure by
 * calling {@link #isSuccess()} and {@link #isFailure()}.
 */
public class IabResult {
    private final Response mResponse;
    private final String mMessage;

    public IabResult(int code, String message) {
        this(Response.fromCode(code), message);
    }

    public IabResult(Response response) {
        this(response, null);
    }

    public IabResult(Response response, String message) {
        mResponse = response;
        if (message == null || message.trim().length() == 0) {
            mMessage = response.description;
        } else {
            mMessage = message + " (response: " + response.description + ")";
        }
    }

    public Response getResponse() {
        return mResponse;
    }

    /**
     * @param resources android resources
     * @return a localized description of the result
     */
    public String getLocalizedMessage(Resources resources) {
        return resources.getString(mResponse.stringId);
    }

    public String getMessage() {
        return mMessage;
    }

    public boolean isSuccess() {
        return mResponse == Response.OK;
    }

    public boolean isFailure() {
        return !isSuccess();
    }

    public String toString() {
        return "IabResult: " + getMessage();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IabResult iabResult = (IabResult) o;
        return mResponse == iabResult.mResponse;
    }

    @Override
    public int hashCode() {
        return mResponse.hashCode();
    }

}

