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

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.addhen.smssync.Prefs;
import org.addhen.smssync.R;
import org.addhen.smssync.util.MessageSyncUtil;
import org.addhen.smssync.util.Util;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.content.res.Resources;

/**
 * @author eyedol
 * 
 */
public class MessageSyncHttpClient extends MainHttpClient {

	private Context context;

	public MessageSyncHttpClient(Context context, String url) {
		super(url, context);
		this.context = context;
	}

	/**
	 * Upload SMS to a web service via HTTP POST
	 * 
	 * @param address
	 * @throws MalformedURLException
	 * @throws IOException
	 * @return
	 */
	public boolean postSmsToWebService(HashMap<String, String> params) {
		// Create a new HttpClient and Post Header
		HttpPost httppost = new HttpPost(url);
		httppost.addHeader("User-Agent", userAgent.toString());
		try {

			// Add your data

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);

			if (params != null) {
				// get params values
				
				for (Entry<String, String> en : params.entrySet()) {
					String key = en.getKey();
					if (key == null || "".equals(key))
						continue;
					String val = en.getValue();

					nameValuePairs.add(new BasicNameValuePair(key, val));
				}
			}

			
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
					if (Prefs.enableReplyFrmServer) {
						new MessageSyncUtil(context, url)
								.sendResponseFromServer(resp);
					}

					return true;
				}

				// Display error from server, if any
				// see https://github.com/ushahidi/SMSSync/issues/68
				String payloadError = Util.getJsonError(resp);
				if (payloadError != "") {
					Resources res = context.getResources();
					Util.showToast(
							context,
							String.format(
									res.getString(R.string.sending_failed_custom_error),
									payloadError));
				}

				return false;
			}

			// HTTP Status code error
			// see https://github.com/ushahidi/SMSSync/issues/69
			Resources res = context.getResources();
			Util.showToast(context, String.format(
					res.getString(R.string.sending_failed_http_code),
					statusCode));

			return false;

		} catch (ClientProtocolException e) {
			return false;
		} catch (IOException e) {
			return false;
		} catch (IllegalArgumentException e) {
			return false;
		}

	}
}
