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

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.addhen.smssync.util.Logger;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Base64;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public abstract class BaseHttpClient {

    private static final int TIME_OUT_CONNECTION = 30;

    private static final String DEFAULT_ENCODING = "UTF-8";

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=" + DEFAULT_ENCODING);

    public static final MediaType XML = MediaType
            .parse("application/json; charset=" + DEFAULT_ENCODING);

    public static final MediaType YAML = MediaType
            .parse("application/xml; charset=" + DEFAULT_ENCODING);

    private static final String CLASS_TAG = BaseHttpClient.class.getSimpleName();

    protected OkHttpClient httpClient;

    protected Context context;

    protected String url;

    private Response response;

    private Request request;

    private ArrayList<NameValuePair> params;

    private Map<String, String> header;

    private Headers headers;

    private HttpMethod method = HttpMethod.GET;

    private RequestBody requestBody;

    public BaseHttpClient(String url, Context context) {

        this.url = url;
        this.context = context;
        this.params = new ArrayList<>();
        this.header = new HashMap<>();

        httpClient = new OkHttpClient();
        httpClient.setConnectTimeout(TIME_OUT_CONNECTION, TimeUnit.SECONDS);
        httpClient.setWriteTimeout(TIME_OUT_CONNECTION, TimeUnit.SECONDS);
        httpClient.setReadTimeout(TIME_OUT_CONNECTION, TimeUnit.SECONDS);
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
        this.header.put(name, value);
    }

    public void setHeaders(Headers headers) {
        this.headers = headers;
    }

    private void addHeader() {

        try {
            URI uri = new URI(url);
            String userInfo = uri.getUserInfo();
            if (userInfo != null) {
                setHeader("Authorization", "Basic " + base64Encode(userInfo));
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
            setHeader("User-Agent", userAgent.toString());
        } catch (NameNotFoundException e) {
            debug(e);
        }

        // set headers on request
        Headers.Builder headerBuilder = new Headers.Builder();
        for (String key : header.keySet()) {
            headerBuilder.set(key, header.get(key));
        }
        setHeaders(headerBuilder.build());
    }

    public void addParam(String name, String value) {
        this.params.add(new BasicNameValuePair(name, value));
    }

    public ArrayList getParams() {
        return params;
    }

    public void execute() throws Exception {
        prepareRequest();
        if (request != null) {
            final Response resp = httpClient.newCall(request).execute();
            setResponse(resp);
        }
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
        this.method = method;
    }

    public void setRequestBody(RequestBody requestBody) throws Exception {
        this.requestBody = requestBody;
    }

    public Request getRequest() {
        return request;
    }

    private void prepareRequest() throws Exception {
        addHeader();
        // setup parameters on request
        if (method.equals(HttpMethod.GET)) {
            request = new Request.Builder()
                    .url(url + getQueryString())
                    .headers(headers)
                    .build();
        } else if (method.equals(HttpMethod.POST)) {
            request = new Request.Builder()
                    .url(url)
                    .headers(headers)
                    .post(requestBody)
                    .build();

        } else if (method.equals(HttpMethod.PUT)) {
            request = new Request.Builder()
                    .url(url)
                    .headers(headers)
                    .put(requestBody)
                    .build();
        }
    }

    private String getQueryString() throws Exception {
        //add query parameters
        String combinedParams = "";
        if (!params.isEmpty()) {
            combinedParams += "?";
            for (NameValuePair p : params) {
                String paramString = p.getName() + "=" + URLEncoder
                        .encode(p.getValue(), DEFAULT_ENCODING);
                if (combinedParams.length() > 1) {
                    combinedParams += "&" + paramString;
                } else {
                    combinedParams += paramString;
                }
            }
        }
        return combinedParams;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
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

    public enum HttpMethod {
        POST("POST"),
        GET("GET"),
        PUT("PUT");

        private final String mMethod;

        HttpMethod(String method) {
            mMethod = method;
        }

        public String value() {
            return mMethod;
        }
    }
}