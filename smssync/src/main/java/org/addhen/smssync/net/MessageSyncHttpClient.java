/*****************************************************************************
 ** Copyright (c) 2010 - 2012 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 **
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.
 **
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 **
 *****************************************************************************/
package org.addhen.smssync.net;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import org.addhen.smssync.R;
import org.addhen.smssync.models.Message;
import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.util.Util;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author eyedol
 */
public class MessageSyncHttpClient extends MainHttpClient {

    private SyncUrl syncUrl;

    private String serverError;

    private String serverSuccessResp;

    public MessageSyncHttpClient(Context context, SyncUrl syncUrl) {
        super(syncUrl.getUrl(), context);
        this.syncUrl = syncUrl;
    }

    /**
     * Post sms to the configured sync URL
     *
     * @param message  The sms sent
     * @param toNumber The phone number the sms was sent to
     * @return boolean
     */
    public boolean postSmsToWebService(Message message, String toNumber) {
        // Create a new HttpClient and Post Header
        HttpPost httppost = new HttpPost(url);
        httppost.addHeader("User-Agent", userAgent.toString());
        try {

            // Add your data

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);

            nameValuePairs.add(new BasicNameValuePair("secret", syncUrl.getSecret()));
            nameValuePairs.add(new BasicNameValuePair("from", message.getFrom()));
            nameValuePairs.add(new BasicNameValuePair("message", message.getBody()));
            nameValuePairs.add(new BasicNameValuePair("sent_timestamp", message.getTimestamp()));
            nameValuePairs.add(new BasicNameValuePair("sent_to", toNumber));
            nameValuePairs.add(new BasicNameValuePair("message_id", message.getUuid()));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
                    HTTP.UTF_8));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            int statusCode = response.getStatusLine().getStatusCode();
            log("statusCode: " + statusCode);
            if (statusCode == 200 || statusCode == 201) {
                String resp = getText(response);
                // Check JSON "success" status
                if (Util.getJsonSuccessStatus(resp)) {
                    // auto response message is enabled to be received from the
                    // server.
                    setServerSuccessResp(resp);
                    return true;
                }

                // Display error from server, if any
                // see https://github.com/ushahidi/SMSSync/issues/68
                String payloadError = Util.getJsonError(resp);
                if (!TextUtils.isEmpty(payloadError)) {

                    Resources res = context.getResources();

                    setServerError(String.format(Locale.getDefault(), "%s, %s ", String.format(
                            res.getString(R.string.sending_failed_custom_error),
                            payloadError, String.format(
                            res.getString(R.string.sending_failed_http_code),
                            statusCode))));
                }
            }

        } catch (ClientProtocolException e) {
            return false;
        } catch (IOException e) {
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
        return false;
    }

    public String getServerError() {
        return this.serverError;
    }

    public void setServerError(String serverError) {
        this.serverError = serverError;
    }

    public String getServerSuccessResp() {
        return this.serverSuccessResp;
    }

    public void setServerSuccessResp(String serverSuccessResp) {
        this.serverSuccessResp = serverSuccessResp;
    }
}
