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
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.addhen.smssync.Util;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


public class SmsSyncHttpClient {

	public static final DefaultHttpClient httpclient = new DefaultHttpClient();
	
	public static HttpResponse GetURL(String URL) throws IOException {
    	
		try {
			//wrap try around because this constructor can throw Error
			final HttpGet httpget = new HttpGet(URL);
			httpget.addHeader("User-Agent", "SmsSync-Android/1.0)");

			// Post, check and show the result (not really spectacular, but works):
			HttpResponse response =  httpclient.execute(httpget);

			return response;

		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }

	/**
     * Upload SMS to a web service via HTTP POST
     * @param address
     * @throws MalformedURLException
     * @throws IOException
     * 
     * @return
     */
    public static boolean postSmsToWebService(String url, HashMap<String, String> params) {
    	// Create a new HttpClient and Post Header  
        HttpClient httpclient = new DefaultHttpClient();  
        HttpPost httppost = new HttpPost(url);  
      
        try {
        	
            // Add your data  
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);  
            nameValuePairs.add(new BasicNameValuePair("secret", params.get("secret")));  
            nameValuePairs.add(new BasicNameValuePair("from", params.get("from")));
            nameValuePairs.add(new BasicNameValuePair("message", params.get("message")));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
            
            // Execute HTTP Post Request  
            HttpResponse response = httpclient.execute(httppost);  
            
            if( response.getStatusLine().getStatusCode() == 200 ) {
            	boolean success = Util.extractPayloadJSON(getText(response));
            	
            	if( success ){
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
     * @param String url - The Callback URL to do the HTTP GET
     * @return String - the HTTP response
     */
    public static String getFromWebService(String url) {
    	
    	// Create a new HttpClient and Post Header  
        HttpClient httpclient = new DefaultHttpClient();  
        final HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("User-Agent", "SMSSync-Android/1.0)");  
        
		try {        
            // Execute HTTP Get Request  
            HttpResponse response = httpclient.execute(httpGet);  
            
            if( response.getStatusLine().getStatusCode() == 200 ) {
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
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				in), 1024);
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
}