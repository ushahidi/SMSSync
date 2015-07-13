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

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Request for posting twitter update
 *
 * @author Henry Addo
 */
public class TweetRequest extends StringRequest {

    TwitterSession mSession;

    public TweetRequest(TwitterSession session, int method,
            String url,
            Response.Listener<String> listener,
            Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        mSession = session;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (mSession != null && mSession.getAuthToken() != null) {
            return mSession.getAuthToken().getAuthHeaders();
        }
        return new HashMap<>();
    }
}
