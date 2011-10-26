/** 
 ** Copyright (c) 2010 Ushahidi Inc
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
 **/

package org.addhen.smssync.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.addhen.smssync.Prefrences;
import org.addhen.smssync.util.Util;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;

public class MainHttpClient {

    private static DefaultHttpClient httpclient;

    private HttpParams httpParameters;

    private int timeoutConnection = 60000;

    private int timeoutSocket = 60000;

    public MainHttpClient() {
        httpParameters = new BasicHttpParams();
        httpParameters.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 1);
        httpParameters.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE,
                new ConnPerRouteBean(1));

        httpParameters.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
        HttpProtocolParams.setVersion(httpParameters, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(httpParameters, "utf8");
        // Set the timeout in milliseconds until a connection is established.
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

        // in milliseconds which is the timeout for waiting for data.
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        SchemeRegistry schemeRegistry = new SchemeRegistry();

        // http scheme
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        // https scheme
        schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));
        ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(httpParameters,
                schemeRegistry);

        httpclient = new DefaultHttpClient(manager, httpParameters);
    }

    public static HttpResponse GetURL(String URL) throws IOException {

        try {
            // wrap try around because this constructor can throw Error
            final HttpGet httpget = new HttpGet(URL);
            httpget.addHeader("User-Agent", "SmsSync-Android/1.0)");

            // Post, check and show the result (not really spectacular, but
            // works):
            HttpResponse response = httpclient.execute(httpget);

            return response;

        } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Upload SMS to a web service via HTTP POST
     * 
     * @param address
     * @throws MalformedURLException
     * @throws IOException
     * @return
     */
    public static boolean postSmsToWebService(String url, HashMap<String, String> params,
            Context context) {
        // Create a new HttpClient and Post Header
        HttpPost httppost = new HttpPost(url);

        try {

            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("secret", params.get("secret")));
            nameValuePairs.add(new BasicNameValuePair("from", params.get("from")));
            nameValuePairs.add(new BasicNameValuePair("message", params.get("message")));
            nameValuePairs.add(new BasicNameValuePair("message_id",params.get("message_id")));
            nameValuePairs.add(new BasicNameValuePair("sent_timestamp", formatDate(params
                    .get("sent_timestamp"))));
            nameValuePairs.add(new BasicNameValuePair("sent_to", params.get("sent_to")));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

            if (response.getStatusLine().getStatusCode() == 200) {
                String resp = getText(response);
                boolean success = Util.extractPayloadJSON(resp);

                if (success) {
                    // auto response message is enabled to be received from the
                    // server.
                    if (Prefrences.enableReplyFrmServer) {
                        Util.sendResponseFromServer(context, resp);
                    }

                    return true;
                } else {
                    return false;
                }

            } else {
                return false;
            }

        } catch (ClientProtocolException e) {
            return false;
        } catch (IOException e) {
            return false;
        }

    }

    /**
     * Does a HTTP GET request
     * 
     * @param String url - The Callback URL to do the HTTP GET
     * @return String - the HTTP response
     */
    public static String getFromWebService(String url) {

        // Create a new HttpClient and Post Header
        final HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("User-Agent", "SMSSync-Android/1.0)");

        try {
            // Execute HTTP Get Request
            HttpResponse response = httpclient.execute(httpGet);

            if (response.getStatusLine().getStatusCode() == 200) {
                return getText(response);

            } else {
                return "";
            }

        } catch (ClientProtocolException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public static String getText(HttpResponse response) {
        String text = "";
        try {
            text = getText(response.getEntity().getContent());
        } catch (final Exception ex) {

        }
        return text;
    }

    public static String getText(InputStream in) {
        String text = "";
        final BufferedReader reader = new BufferedReader(new InputStreamReader(in), 1024);
        final StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            text = sb.toString();
        } catch (final Exception ex) {
        } finally {
            try {
                in.close();
            } catch (final Exception ex) {
            }
        }
        return text;
    }

    private static String formatDate(String date) {
        try {
           
            return Util.formatDateTime(Long.parseLong(date), "mm-dd-yy-hh:mm");
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
