/* Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

