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
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Base64;

import org.addhen.smssync.util.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


public class MainHttpClient {

    protected static DefaultHttpClient httpClient;

    protected static StringBuilder userAgent;

    protected Context context;

    protected String url;

    private HttpParams httpParameters;

    private int timeoutConnection = 60000;

    private int timeoutSocket = 60000;

    private static final String DEFAULT_ENCODING = "UTF-8";

    private static final String CLASS_TAG = MainHttpClient.class.getSimpleName();

    private ArrayList <NameValuePair> params;

    private ArrayList <NameValuePair> headers;

    private HttpEntity entity;

    private int responseCode;

    private String response;

    private HttpRequestBase request; 

    private HttpResponse httpResponse;

    private String errorMessage;

    public MainHttpClient(String url, Context context) {

        this.url = url;
        this.context = context;
        this.params = new ArrayList<NameValuePair>();
        this.headers = new ArrayList<NameValuePair>();

        httpParameters = new BasicHttpParams();
        httpParameters.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 1);
        httpParameters.setParameter(
                ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE,
                new ConnPerRouteBean(1));

        httpParameters.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE,
                false);
        HttpProtocolParams.setVersion(httpParameters, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(httpParameters, "utf8");
        // Set the timeout in milliseconds until a connection is established.
        HttpConnectionParams.setConnectionTimeout(httpParameters,
                timeoutConnection);

        // in milliseconds which is the timeout for waiting for data.
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        SchemeRegistry schemeRegistry = new SchemeRegistry();

        // http scheme
        schemeRegistry.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        // https scheme
        try {
            schemeRegistry.register(new Scheme("https",
                    new TrustedSocketFactory(url, false), 443));
        } catch (KeyManagementException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(
                httpParameters, schemeRegistry);

        httpClient = new DefaultHttpClient(manager, httpParameters);

        // support basic auth header
        try {
            URI uri = new URI(url);
            String userInfo = uri.getUserInfo();
            if (userInfo != null) {
                addHeader("Authorization", "Basic " + base64Encode(userInfo));
            }
        } catch (URISyntaxException e) {
            debug(e);
            //log("URISyntaxException", e);
        }

        // add user-agent header
        try {
            final String versionName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
            // Add version name to user agent
            userAgent = new StringBuilder("SMSSync-Android/");
            userAgent.append("v");
            userAgent.append(versionName);
            headers.add(new BasicNameValuePair("User-Agent", userAgent.toString()));
        } catch (NameNotFoundException e) {
            debug(e);
            //e.printStackTrace();
        }
    }

    public ArrayList getParams() {
        return params;
    }

    public String getResponse() {
        return response;
    }

    public HttpResponse getResponseObject() {
        return httpResponse;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setEntity(HttpEntity data) throws Exception {
        entity = data;
    }

    public void setEntity(String data) throws Exception {
        entity = new StringEntity(data, DEFAULT_ENCODING);
    }

    public String base64Encode(String str) {
        byte[] bytes = str.getBytes();
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    public static Throwable getRootCause(Throwable throwable) {
        if (throwable.getCause() != null) {
            return getRootCause(throwable.getCause());
        }
        return throwable;
    }

    public void addParam(String name, String value) {
        params.add(new BasicNameValuePair(name, value));
    }

    public void addHeader(String name, String value) {
        headers.add(new BasicNameValuePair(name, value));
    }

    public HttpRequestBase getRequest() {
        return request;
    }

    public void setMethod(String method) throws Exception {
        if (method == "POST") {
            setMethodPost();
        } else if (method == "PUT") {
            setMethodPut();
        } else if (method == "GET") {
            setMethodGet();
        } else {
            throw new Exception(
                "Invalid method. POST, PUT and GET currently supported."
            );
        }
    }

    /**
     * Does a HTTP GET request
     *
     * @return String - the HTTP response or null
     */
    public String getFromWebService() {
        try {
            setMethod("GET");
            execute();
            if (getResponseCode() == 200) {
                return getResponse();
            }
        } catch (Exception e) {
            debug(e);
        }
        return null;
    }

    public static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            debug(e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                debug(e);
            }
        }
        return sb.toString();
    }

    private void setMethodGet() throws Exception {
        //add query parameters
        String combinedParams = "";
        if (!params.isEmpty()) {
            combinedParams += "?";
            for(NameValuePair p : params) {
                String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(), DEFAULT_ENCODING);
                if(combinedParams.length() > 1) {
                    combinedParams  +=  "&" + paramString;
                } else {
                    combinedParams += paramString;
                }
            }
        }
        request = new HttpGet(url + combinedParams);
    }


    private void setMethodPost() throws Exception {
        HttpPost req = new HttpPost(url);
        if (entity.getContentLength() > 0) {
            req.setEntity(entity);
        } else if (!params.isEmpty()) {
            // construct entity if not already set
            req.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        }
        request = req;
    }

    private void setMethodPut() throws Exception {
        HttpPut req = new HttpPut(url);
        if (entity.getContentLength() > 0) {
            req.setEntity(entity);
        } else if (!params.isEmpty()) {
            // construct entity if not already set
            req.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        }
        request = req;
    }

    public void execute() throws Exception {

        // add headers
        for(NameValuePair h : headers) {
            request.addHeader(h.getName(), h.getValue());
        }

        try {
            httpResponse = httpClient.execute(request);
            responseCode = httpResponse.getStatusLine().getStatusCode();
            errorMessage = httpResponse.getStatusLine().getReasonPhrase();
            HttpEntity entity = httpResponse.getEntity();

            if (entity != null) {
                InputStream instream = entity.getContent();
                response = convertStreamToString(instream);
                // Closing the input stream will trigger connection release
                instream.close();
            }

        } catch (ClientProtocolException e)  {
            httpClient.getConnectionManager().shutdown();
            throw e;
        } catch (IOException e) {
            httpClient.getConnectionManager().shutdown();
            throw e;
        }
    }

    private static void debug(Exception e) {
        Logger.log(CLASS_TAG, "Exception: " 
            + e.getClass().getName()
            + " " + getRootCause(e).getMessage());
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
