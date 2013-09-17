/*******************************************************************************
 *  Copyright (c) 2010 - 2013 Ushahidi Inc
 *  All rights reserved
 *  Contact: team@ushahidi.com
 *  Website: http://www.ushahidi.com
 *  GNU Lesser General Public License Usage
 *  This file may be used under the terms of the GNU Lesser
 *  General Public License version 3 as published by the Free Software
 *  Foundation and appearing in the file LICENSE.LGPL included in the
 *  packaging of this file. Please review the following information to
 *  ensure the GNU Lesser General Public License version 3 requirements
 *  will be met: http://www.gnu.org/licenses/lgpl.html.
 *
 * If you have questions regarding the use of this file, please contact
 * Ushahidi developers at team@ushahidi.com.
 ******************************************************************************/
package org.addhen.smssync.net;

import org.addhen.smssync.R;
import org.addhen.smssync.models.Message;
import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.net.SyncScheme.SyncDataFormat;
import org.addhen.smssync.net.SyncScheme.SyncDataKey;
import org.addhen.smssync.net.SyncScheme.SyncMethod;
import org.addhen.smssync.util.DataFormatUtil;
import org.addhen.smssync.util.Util;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

        try {

            // Add your data
            HttpUriRequest request = getRequest(message, toNumber);

            if (request == null) {
                return false;
            }

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(request);
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

    /**
     * Get an appropriate HTTP request to send message with based on current sync scheme
     */
    public HttpUriRequest getRequest(Message message, String toNumber) {

        HttpUriRequest request;

        SyncScheme syncScheme = syncUrl.getSyncScheme();
        SyncMethod method = syncScheme.getMethod();
        SyncDataFormat format = syncScheme.getDataFormat();
        HttpEntity httpEntity = getHttpEntity(format, message, toNumber);

        if (httpEntity == null) {
            return null;
        }

        switch (method) {
            case POST:
                request = new HttpPost(url);
                ((HttpPost) request).setEntity(httpEntity);
                break;
            case PUT:
                request = new HttpPut(url);
                ((HttpPut) request).setEntity(httpEntity);
                break;
            default:
                log("Invalid server method");
                request = null;
        }

        if (request != null) {
            request.addHeader("User-Agent", userAgent.toString());
            request.setHeader("Content-Type", syncScheme.getContentType());
        }
        return request;
    }

    /**
     * Get HTTP Entity populated with data in a format specified by the current sync scheme
     */
    private HttpEntity getHttpEntity(SyncDataFormat format, Message message, String toNumber) {

        HttpEntity httpEntity;
        SyncScheme syncScheme = syncUrl.getSyncScheme();

        try {

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(syncScheme.getKey(SyncDataKey.SECRET),
                    syncUrl.getSecret()));
            nameValuePairs.add(new BasicNameValuePair(syncScheme.getKey(SyncDataKey.FROM),
                    message.getFrom()));
            nameValuePairs.add(new BasicNameValuePair(syncScheme.getKey(SyncDataKey.MESSAGE),
                    message.getBody()));
            nameValuePairs.add(new BasicNameValuePair(syncScheme.getKey(SyncDataKey.SENT_TIMESTAMP),
                    message.getTimestamp()));
            nameValuePairs
                    .add(new BasicNameValuePair(syncScheme.getKey(SyncDataKey.SENT_TO), toNumber));
            nameValuePairs.add(new BasicNameValuePair(syncScheme.getKey(SyncDataKey.MESSAGE_ID),
                    message.getUuid()));

            switch (format) {
                case JSON:
                    httpEntity = new StringEntity(DataFormatUtil.makeJSONString(nameValuePairs),
                            HTTP.UTF_8);
                    break;
                case XML:
                    //TODO: Make parent node URL specific as well
                    httpEntity = new StringEntity(
                            DataFormatUtil.makeXMLString(nameValuePairs, "payload", HTTP.UTF_8),
                            HTTP.UTF_8);
                    break;
                case YAML:
                    httpEntity = new StringEntity(DataFormatUtil.makeYAMLString(nameValuePairs),
                            HTTP.UTF_8);
                    break;
                case URLEncoded:
                    httpEntity = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
                    break;
                default:
                    throw new Exception("Invalid data format");
            }

        } catch (JSONException ex) {
            log("Failed to format json", ex);
            httpEntity = null;
        } catch (UnsupportedEncodingException ex) {
            log("Failed to encode data", ex);
            httpEntity = null;
        } catch (IOException ioex) {
            log("Failed to format xml", ioex);
            httpEntity = null;
        } catch (Exception ex) {
            log("Failed to format data", ex);
            httpEntity = null;
        }

        return httpEntity;
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
