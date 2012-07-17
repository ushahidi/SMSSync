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
package org.addhen.smssync.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.addhen.smssync.MainApplication;
import org.addhen.smssync.Prefs;
import org.addhen.smssync.ProcessSms;
import org.addhen.smssync.R;
import org.addhen.smssync.database.Database;
import org.addhen.smssync.database.Messages;
import org.addhen.smssync.net.MainHttpClient;
import org.addhen.smssync.net.MessageSyncHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author eyedol
 * 
 */
public class MessageSyncUtil extends Util {

	private Context context;

	private String url;

	private static JSONObject jsonObject;

	private static JSONArray jsonArray;

	private static final String CLASS_TAG = MessageSyncUtil.class
			.getSimpleName();

	private MessageSyncHttpClient msgSyncHttpClient;

	private ProcessSms processSms;

	public MessageSyncUtil(Context context, String url) {
		this.context = context;
		this.url = url;
		this.msgSyncHttpClient = new MessageSyncHttpClient(context, url);
		processSms = new ProcessSms(context);
	}

	/**
	 * Posts received SMS to a configured callback URL.
	 * 
	 * @param String
	 *            apiKey
	 * @param String
	 *            fromAddress
	 * @param String
	 *            messageBody
	 * @return boolean
	 */
	public boolean postToAWebService(String messagesFrom, String messagesBody,
			String messagesTimestamp, String messagesId, String secret) {
		log("postToAWebService(): Post received SMS to configured URL:"
				+ Prefs.website + " messagesFrom: " + messagesFrom
				+ " messagesBody: " + messagesBody);

		HashMap<String, String> params = new HashMap<String, String>();
		Prefs.loadPreferences(context);

		if (!TextUtils.isEmpty(url)) {
			params.put("secret", secret);
			params.put("from", messagesFrom);
			params.put("message", messagesBody);
			params.put("sent_timestamp", messagesTimestamp);
			params.put("sent_to", getPhoneNumber(context));
			params.put("message_id", messagesId);
			return msgSyncHttpClient.postSmsToWebService(params);
		}

		return false;
	}

	/**
	 * Pushes pending messages to the configured URL.
	 * 
	 * @param int messageId - Sync by Id - 0 for no ID > 0 to for an id
	 * @param String
	 *            url The sync URL to push the message to.
	 * @param String
	 *            secret The secret key as set on the server.
	 * 
	 * @return int
	 */
	public int snycToWeb(int messagesId, String secret) {
		log("syncToWeb(): push pending messages to the configured URL");
		Cursor cursor;
		// check if it should sync by id
		if (messagesId > 0) {
			cursor = MainApplication.mDb.fetchMessagesById(messagesId);
		} else {
			cursor = MainApplication.mDb.fetchAllMessages();
		}

		String messagesFrom;
		String messagesBody;
		String messagesTimestamp;
		int deleted = 0;

		List<Messages> listMessages = new ArrayList<Messages>();

		if (cursor != null) {
			if (cursor.getCount() == 0) {
				return 2;
			}

			if (cursor.moveToFirst()) {
				int messagesIdIndex = cursor
						.getColumnIndexOrThrow(Database.MESSAGES_ID);
				int messagesFromIndex = cursor
						.getColumnIndexOrThrow(Database.MESSAGES_FROM);

				int messagesBodyIndex = cursor
						.getColumnIndexOrThrow(Database.MESSAGES_BODY);
				int messagesTimestampIndex = cursor
						.getColumnIndexOrThrow(Database.MESSAGES_DATE);
				do {
					Messages messages = new Messages();
					listMessages.add(messages);

					int messageId = Util.toInt(cursor
							.getString(messagesIdIndex));
					messages.setMessageId(messageId);

					messagesFrom = Util.capitalizeString(cursor
							.getString(messagesFromIndex));
					messages.setMessageFrom(messagesFrom);

					messagesBody = cursor.getString(messagesBodyIndex);
					messages.setMessageBody(messagesBody);

					messagesTimestamp = cursor
							.getString(messagesTimestampIndex);
					messages.setMessageDate(messagesTimestamp);
					// post to web service
					if (postToAWebService(messagesFrom, messagesBody,
							messagesTimestamp, String.valueOf(messageId),
							secret)) {

						// log sent messages
						MainApplication.mDb.addSentMessages(listMessages);

						// if it successfully pushes message, delete message
						// from db
						MainApplication.mDb.deleteMessagesById(messageId);
						deleted = 0;
					} else {
						deleted = 1;
					}

				} while (cursor.moveToNext());
			}
			cursor.close();
		}
		return deleted;

	}

	/**
	 * Sends messages received from the server as SMS.
	 * 
	 * @param String
	 *            response - the response from the server.
	 */
	public void sendResponseFromServer(String response) {
		Logger.log(CLASS_TAG, "performResponseFromServer(): " + " response:"
				+ response);

		if (!TextUtils.isEmpty(response) && response != null) {

			try {

				jsonObject = new JSONObject(response);
				JSONObject payloadObject = jsonObject.getJSONObject("payload");

				if (payloadObject != null) {

					jsonArray = payloadObject.getJSONArray("messages");

					for (int index = 0; index < jsonArray.length(); ++index) {
						jsonObject = jsonArray.getJSONObject(index);
						new Util().log("Send sms: To: "
								+ jsonObject.getString("to") + "Message: "
								+ jsonObject.getString("message"));

						processSms.sendSms(jsonObject.getString("to"),
								jsonObject.getString("message"));
					}

				}
			} catch (JSONException e) {
				new Util().log(CLASS_TAG, "Error: " + e.getMessage());
				showToast(context, R.string.no_task);
			}
		}

	}

	/**
	 * Process messages as received from the user; 0 - successful 1 - failed
	 * fetching categories
	 * 
	 * @return int - status
	 */
	public static int processMessages() {
		Logger.log(CLASS_TAG,
				"processMessages(): Process text messages as received from the user's phone");
		List<Messages> listMessages = new ArrayList<Messages>();
		int messageId = 0;
		int status = 1;
		Messages messages = new Messages();
		listMessages.add(messages);

		// check if messageId is actually initialized
		if (smsMap.get("messagesId") != null) {
			messageId = Integer.parseInt(smsMap.get("messagesId"));
		}

		messages.setMessageId(messageId);
		messages.setMessageFrom(smsMap.get("messagesFrom"));
		messages.setMessageBody(smsMap.get("messagesBody"));
		messages.setMessageDate(smsMap.get("messagesDate"));

		if (listMessages != null) {
			MainApplication.mDb.addMessages(listMessages);
			status = 0;
		}
		return status;

	}

	/**
	 * Performs a task based on what callback URL tells it.
	 * 
	 * @param Context
	 *            context - the activity calling this method.
	 * @return void
	 */
	public void performTask(String urlSecret) {
		Logger.log(CLASS_TAG, "performTask(): perform a task");
		// load Prefs
		Prefs.loadPreferences(context);

		// validate configured url
		int status = validateCallbackUrl(url);
		if (status == 1) {
			showToast(context, R.string.no_configured_url);
		} else if (status == 2) {
			showToast(context, R.string.invalid_url);
		} else if (status == 3) {
			showToast(context, R.string.no_connection);
		} else {

			StringBuilder uriBuilder = new StringBuilder(url);

			uriBuilder.append("?task=send");

			String response = MainHttpClient.getFromWebService(uriBuilder
					.toString());
			Log.d(CLASS_TAG, "TaskCheckResponse: " + response);
			String task = "";
			String secret = "";
			if (!TextUtils.isEmpty(response) && response != null) {

				try {

					jsonObject = new JSONObject(response);
					JSONObject payloadObject = jsonObject
							.getJSONObject("payload");

					if (payloadObject != null) {
						task = payloadObject.getString("task");
						secret = payloadObject.getString("secret");
						if ((task.equals("send"))
								&& (secret.equals(urlSecret))) {
							jsonArray = payloadObject.getJSONArray("messages");

							for (int index = 0; index < jsonArray.length(); ++index) {
								jsonObject = jsonArray.getJSONObject(index);

								new ProcessSms(context).sendSms(
										jsonObject.getString("to"),
										jsonObject.getString("message"));
							}

						} else {
							// no task enabled on the callback url.
							showToast(context, R.string.no_task);
						}

					} else {

						showToast(context, R.string.no_task);
					}

				} catch (JSONException e) {
					Log.e(CLASS_TAG, "Error: " + e.getMessage());
					showToast(context, R.string.no_task);
				}
			}
		}
	}
}
