/*****************************************************************************
 ** Copyright (c) 2010 - 2013 Ushahidi Inc
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

import org.addhen.smssync.net.sources.HttpClientWrapper;
import org.addhen.smssync.net.sources.HttpClientWrapperFactory;
import org.addhen.smssync.util.Logger;
import org.apache.http.NameValuePair;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Base64;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;


public abstract class BaseHttpClient {

    private static final int TIME_OUT_CONNECTION = 30;

    private static final String CLASS_TAG = BaseHttpClient.class.getSimpleName();

    protected HttpClientWrapper httpClient;

    protected Context context;

    protected String url;

    public BaseHttpClient(String url, Context context) {

        this.url = url;
        this.context = context;

        httpClient = new HttpClientWrapperFactory().create(url, TIME_OUT_CONNECTION);
        addHeader();
    }

    private static void debug(Exception e) {
        Logger.log(CLASS_TAG, "Exception: "
                + e.getClass().getName()
                + " " + getRootCause(e).getMessage());
    }

    public static String base64Encode(String str) {
        byte[] bytes = str.getBytes();
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    public static Throwable getRootCause(Throwable throwable) {
        if (throwable.getCause() != null) {
            return getRootCause(throwable.getCause());
        }
        return throwable;
    }

    public void setHeader(String name, String value) {
        this.httpClient.setHeader(name, value);
    }

    private void addHeader() {

        try {
            URI uri = new URI(url);
            String userInfo = uri.getUserInfo();
            if (userInfo != null) {
                httpClient.setHeader("Authorization", "Basic " + base64Encode(userInfo));
            }
        } catch (URISyntaxException e) {
            debug(e);
        }

        // add user-agent header
        try {
            final String versionName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
            // Add version name to user agent
            StringBuilder userAgent = new StringBuilder("SMSSync-Android/");
            userAgent.append("v");
            userAgent.append(versionName);
            httpClient.setHeader("User-Agent", userAgent.toString());
        } catch (NameNotFoundException e) {
            debug(e);
        }

    }

    public void addParam(String name, String value) {
        httpClient.setRequestParameter(name, value);
    }

    public ArrayList getParams() {
        return httpClient.getParams();
    }

    public void execute() throws Exception {
        httpClient.execute();
    }

    public boolean isMethodSupported(HttpMethod method) {
        return (method.equals(HttpMethod.GET) || method.equals(HttpMethod.POST) || method
                .equals(HttpMethod.PUT));
    }

    public void setMethod(HttpMethod method) throws Exception {

        if (!isMethodSupported(method)) {
            throw new Exception(
                    "Invalid method '" + method + "'."
                            + " POST, PUT and GET currently supported."
            );
        }
        httpClient.setMethod(method);
    }

    public void setRequestBody(ArrayList<NameValuePair> body)
            throws Exception {
        this.httpClient.setRequestBody(body);
    }

    public void setRequestBody(HttpMediaType mediaType, String body) throws Exception {
        httpClient.setRequestBody(mediaType, body);
    }

    public HttpClientWrapper getHttpClientWrapper() {
        return httpClient;
    }


    public String getResponse() {
        return httpClient.getResponseBody();
    }

    public String getErrorMessage() {
        return httpClient.getErrorMessage();
    }

    public int responseCode() {
        return httpClient.responseCode();
    }

    protected void log(String message) {
        Logger.log(getClass().getName(), message);
    }

    protected void log(String format, Object... args) {
        Logger.log(getClass().getName(), format, args);
    }

    protected void log(String message, Exception ex) {
        Logger.log(getClass().getName(), message, ex);
    }
}