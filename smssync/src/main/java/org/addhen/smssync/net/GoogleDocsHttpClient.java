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

package org.addhen.smssync.net;

import org.addhen.smssync.R;
import org.addhen.smssync.util.Util;

import android.content.Context;
import android.content.res.Resources;

import java.util.Locale;

/**
 * Post email address to a google spreadsheet
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class GoogleDocsHttpClient extends MainHttpClient {

    public GoogleDocsHttpClient(String url, Context context) {
        super(url, context);
    }

    public boolean postToGoogleDocs(String email) {
        addParam("entry.1221859611", email);
        try {
            setMethod(HttpMethod.POST);
            execute();
        } catch (Exception e) {
            log("Request failed", e);
            setClientError("Request failed. " + e.getMessage());
        }

        final int statusCode = responseCode();

        if (statusCode != 200 && statusCode != 201) {
            setServerError("bad http return code", statusCode);
            return false;
        }

        return true;
    }

    public void setClientError(String error) {
        log("Client error " + error);
        Resources res = context.getResources();
        final String clientError = String.format(Locale.getDefault(), "%s",
                res.getString(R.string.sending_failed_custom_error, error));
        Util.logActivities(context, clientError);
    }

    public void setServerError(String error, int statusCode) {
        log("Server error " + error);
        Resources res = context.getResources();
        final String serverError = String
                .format("%s %s ", res.getString(R.string.sending_failed_custom_error, error),
                        res.getString(R.string.sending_failed_http_code, statusCode));
        Util.logActivities(context, serverError);
    }
}
