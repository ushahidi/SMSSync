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
package org.addhen.smssync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.addhen.smssync.fragments.PendingMessages;
import org.addhen.smssync.models.MessagesModel;
import org.addhen.smssync.models.SyncUrlModel;
import org.addhen.smssync.util.Logger;
import org.addhen.smssync.util.MessageSyncUtil;
import org.addhen.smssync.util.SentMessagesUtil;
import org.addhen.smssync.util.ServicesConstants;
import org.addhen.smssync.util.Util;

import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;

/**
 * This class has the main logic to dispatch the messages that comes to the
 * device. It decides where to post the messages to, depending on the status of
 * the device. If the message fails to send to the configured web service, it
 * saves them in the pending list and when it succeeds it saves them in the sent
 * list.
 * 
 * @author eyedol
 * 
 */
public class ProcessSms {

	public static final Uri MMS_SMS_CONTENT_URI = Uri
			.parse("content://mms-sms/");

	public static final Uri THREAD_ID_CONTENT_URI = Uri.withAppendedPath(
			MMS_SMS_CONTENT_URI, "threadID");

	public static final Uri CONVERSATION_CONTENT_URI = Uri.withAppendedPath(
			MMS_SMS_CONTENT_URI, "conversations");

	public static final String SMS_CONTENT_URI = "content://sms/conversations/";

	public static final String SMS_ID = "_id";

	public static final String SMS_CONTENT_INBOX = "content://sms/inbox";

	private static final String CLASS_TAG = ProcessSms.class.getSimpleName();

	private static final int ACTIVE_SYNC_URL = 1;

	private SyncUrlModel model;

	private Context context;

	public static HashMap<String, String> smsMap;

	private MessageSyncUtil messageSyncUtil;

	private Intent statusIntent;

	private int PENDING = 0;

	private int TASK = 1;

	public ProcessSms(Context context) {
		this.context = context;
		smsMap = new HashMap<String, String>();
		model = new SyncUrlModel();
		statusIntent = new Intent(ServicesConstants.AUTO_SYNC_ACTION);
	}

	/**
	 * Routes both incoming SMS and pending messages.
	 * 
	 * @param String
	 *            messagesFrom The number that sent the SMS
	 * @param String
	 *            messagesBody The message body. This is the message sent ot the
	 *            phone.
	 * @param String
	 *            messagesTimestamp The timestamp of the message
	 * @param String
	 *            messagesId The universal unique ID of the messages.
	 * 
	 * @return boolean The status of the message routing.
	 */
	public boolean routeMessages(String messagesFrom, String messagesBody,
			String messagesTimestamp, String messagesUuid) {

		// load preferences
		Prefs.loadPreferences(context);

		boolean posted = true;
		// is SMSSync service running?
		if (Prefs.enabled) {

			if (Util.isConnected(context)) {

				// send auto response from phone not server.
				if (Prefs.enableReply) {

					// send auto response as SMS to user's phone
					sendSms(messagesFrom, Prefs.reply);
				}

				// get enabled Sync URLs
				for (SyncUrlModel syncUrl : model.loadByStatus(ACTIVE_SYNC_URL)) {

					messageSyncUtil = new MessageSyncUtil(context,
							syncUrl.getUrl());

					// process filter text (keyword or RegEx)
					if (!TextUtils.isEmpty(syncUrl.getKeywords())) {
						String filterText = syncUrl.getKeywords();
						if (filterByKeywords(messagesBody, filterText)
								|| filterByRegex(messagesBody, filterText)) {
							posted = messageSyncUtil.postToAWebService(
									messagesFrom, messagesBody,
									messagesTimestamp, messagesUuid,
									syncUrl.getSecret());
							if (!posted) {
								// Note: HTTP Error code or custom error message
								// will have been shown already
								Util.showFailNotification(
										context,
										messagesBody,
										context.getString(R.string.sending_failed));

								// attempt to make a data connection to sync
								// the failed messages.
								Util.connectToDataNetwork(context);
							} else {

								postToSentBox(messagesFrom, messagesBody,
										messagesUuid, messagesTimestamp,
										PENDING);
								Util.showFailNotification(
										context,
										messagesBody,
										context.getString(R.string.sending_succeeded));
							}

						}

					} else { // there is no filter text set up on a sync URL
						posted = messageSyncUtil.postToAWebService(
								messagesFrom, messagesBody, messagesTimestamp,
								messagesUuid, syncUrl.getSecret());
						if (!posted) {
							Util.showFailNotification(context, messagesBody,
									context.getString(R.string.sending_failed));

							// attempt to make a data connection so to sync
							// the failed messages.
							Util.connectToDataNetwork(context);

						} else {

							postToSentBox(messagesFrom, messagesBody,
									messagesUuid, messagesTimestamp, PENDING);

							Util.showFailNotification(
									context,
									messagesBody,
									context.getString(R.string.sending_succeeded));
						}
					}
				}

			} else { // no internet on the device.
				Util.showFailNotification(context, messagesBody,
						context.getString(R.string.sending_failed));
				posted = false;

			}
		}
		return posted;
	}

	/**
	 * Processes the incoming SMS to figure out how to exactly to route the
	 * message. If it fails to be synced online, cache it and queue it up for
	 * the scheduler to process it.
	 * 
	 * @param String
	 *            messagesFrom The number that sent the SMS
	 * @param String
	 *            messagesBody The message body. This is the message sent to the
	 *            phone.
	 * @param SmsMessages
	 *            sms The SMS object as
	 */
	public void routeSms(String messagesFrom, String messagesBody,
			String messagesTimestamp, String messagesId, SmsMessage sms) {
		Logger.log(CLASS_TAG, "messageID: " + messagesId);
		if (routeMessages(messagesFrom, messagesBody, messagesTimestamp,
				messagesId)) {

			// Delete messages from message app's inbox, only
			// when SMSSync has that feature turned on
			if (Prefs.autoDelete) {
				delSmsFromInbox(messagesBody, messagesFrom);
			}

		} else {
			postToPendingBox(messagesFrom, messagesBody, sms);
		}

	}

	/**
	 * Processes failed messages AKA pending message to figure out how to
	 * exactly route the message. If it fails to be synced online, cache it and
	 * queue it up for the scheduler to process it.
	 * 
	 * @param String
	 *            messagesFrom The number that sent the SMS
	 * @param String
	 *            messagesBody The message body. This is the message sent to the
	 *            phone.
	 * @param String
	 *            messagesTimestamp The timestamp of the message
	 * @param String
	 *            messagesId The unique ID of the messages.
	 * 
	 * @param boolean
	 */
	public boolean routePendingMessages(String messagesFrom,
			String messagesBody, String messagesTimestamp, String messagesId) {

		return routeMessages(messagesFrom, messagesBody, messagesTimestamp,
				messagesId);
	}

	/**
	 * Filter message string for particular keywords
	 * 
	 * @param message
	 *            The message to be tested against the keywords
	 * @param filterText
	 *            A CSV string listing keywords to match against message
	 * 
	 * @return boolean
	 */
	public boolean filterByKeywords(String message, String filterText) {
		String[] keywords = filterText.split(",");
		for (int i = 0; i < keywords.length; i++) {
			if (message.toLowerCase()
					.contains(keywords[i].toLowerCase().trim())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Filter message string for RegEx match
	 * 
	 * @param message
	 *            The message to be tested against the RegEx
	 * @param filterText
	 *            A string representing the regular expression to test against.
	 * 
	 * @return boolean
	 */
	public boolean filterByRegex(String message, String filterText) {
		Pattern pattern = null;
		try {
			pattern = Pattern.compile(filterText, Pattern.CASE_INSENSITIVE);
		} catch (PatternSyntaxException e) {
			// invalid RegEx
			return false;
		}
		Matcher matcher = pattern.matcher(message);
		return (matcher.find());
	}

	/**
	 * Import messages from the messages app's table and puts them in SMSSync's
	 * outbox table. This will allow messages the imported messages to be sync'd
	 * to the configured Sync URL.
	 * 
	 * @return int 0 for success, 1 for failure.
	 */
	public int importMessages() {
		Logger.log(CLASS_TAG,
				"importMessages(): import messages from messages app");
		Prefs.loadPreferences(context);
		Uri uriSms = Uri.parse(SMS_CONTENT_INBOX);
		uriSms = uriSms.buildUpon().appendQueryParameter("LIMIT", "10").build();
		String[] projection = { "_id", "address", "date", "body" };
		String messageDate = "";

		Cursor c = context.getContentResolver().query(uriSms, projection, null,
				null, "date DESC");

		List<MessagesModel> listMessages = new ArrayList<MessagesModel>();

		if (c.getCount() > 0 && c != null) {
			if (c.moveToFirst()) {

				do {
					MessagesModel messages = new MessagesModel();
					listMessages.add(messages);

					messageDate = String.valueOf(c.getLong(c
							.getColumnIndex("date")));
					messages.setMessageDate(messageDate);

					messages.setMessageFrom(c.getString(c
							.getColumnIndex("address")));
					messages.setMessage(c.getString(c.getColumnIndex("body")));
					messages.setMessageUuid(getUuid());

					messages.listMessages = listMessages;
					messages.save();

				} while (c.moveToNext());
			}
			c.close();
			return 0;

		} else {
			return 1;
		}

	}

	/**
	 * Tries to locate the message id (from the system database), given the
	 * message thread id and the timestamp of the message.
	 * 
	 * @param Context
	 *            context - The activity calling the method.
	 * @param long threadId - The message's thread ID.
	 * @param long _timestamp - The timestamp of the message.
	 * 
	 * @return the message id
	 */
	public static long findMessageId(Context context, long threadId,
			long _timestamp) {
		Logger.log(CLASS_TAG,
				"findMessageId(): get the message id using thread id and timestamp: threadId: "
						+ threadId + " timestamp: " + _timestamp);
		long id = 0;
		long timestamp = _timestamp;
		if (threadId > 0) {

			Cursor cursor = context.getContentResolver().query(
					ContentUris.withAppendedId(CONVERSATION_CONTENT_URI,
							threadId),
					new String[] { "_id", "date", "thread_id" },
					"date=" + timestamp, null, "date desc");

			if (cursor != null) {
				try {
					if (cursor.moveToFirst()) {
						id = cursor.getLong(0);

					}
				} finally {
					cursor.close();
				}
			}
		}
		return id;
	}

	/**
	 * Tries to locate the thread id given the address (phone number or email)
	 * of the message sender.
	 * 
	 * @param String
	 *            body
	 * @param String
	 *            address
	 * 
	 * @return the thread id
	 */
	public long getThreadId(String body, String address) {
		Logger.log(CLASS_TAG, "getId(): thread id");
		Uri uriSms = Uri.parse(SMS_CONTENT_INBOX);

		StringBuilder sb = new StringBuilder();
		sb.append("address=" + DatabaseUtils.sqlEscapeString(address) + " AND ");
		sb.append("body=" + DatabaseUtils.sqlEscapeString(body));

		Cursor c = context.getContentResolver().query(uriSms, null, null, null,
				"date DESC ");

		if (c.getCount() > 0 && c != null) {

			c.moveToFirst();
			long threadId = c.getLong(c.getColumnIndex("thread_id"));
			c.close();

			return threadId;
		}

		return 0;
	}

	public String getUuid() {
		return UUID.randomUUID().toString();
	}

	/**
	 * Sends SMS to a number.
	 * 
	 * @param String
	 *            sendTo - Number to send SMS to.
	 * @param String
	 *            msg - The message to be sent.
	 */
	public void sendSms(String sendTo, String msg) {

		ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
		ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();
		Logger.log(CLASS_TAG, "sendSms(): Sends SMS to a number: sendTo: "
				+ sendTo + " message: " + msg);

		SmsManager sms = SmsManager.getDefault();
		ArrayList<String> parts = sms.divideMessage(msg);

		for (int i = 0; i < parts.size(); i++) {
			PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0,
					new Intent(ServicesConstants.SENT), 0);

			PendingIntent deliveryIntent = PendingIntent.getBroadcast(context,
					0, new Intent(ServicesConstants.DELIVERED), 0);
			sentIntents.add(sentIntent);

			deliveryIntents.add(deliveryIntent);
		}

		if (PhoneNumberUtils.isGlobalPhoneNumber(sendTo)) {
			sms.sendMultipartTextMessage(sendTo, null, parts, sentIntents,
					deliveryIntents);

			// Get current Time Millis
			final Long timeMills = System.currentTimeMillis() / 1000;
			// Log to sent table
			postToSentBox(sendTo, msg, getUuid(), timeMills.toString(), TASK);
		}
	}

	/**
	 * Delete SMS from the message app inbox.
	 * 
	 * @param Context
	 *            context - The calling activity
	 * @param msg
	 *            The {@link android.telephony.SmsMessage }
	 */
	public void delSmsFromInbox(String body, String address) {
		Logger.log(CLASS_TAG, "delSmsFromInbox(): Delete SMS message app inbox");
		final long threadId = getThreadId(body, address);

		if (threadId >= 0) {
			context.getContentResolver().delete(
					Uri.parse(SMS_CONTENT_URI + threadId), null, null);
		}
	}

	/**
	 * Put successfully sent messages to a local database.
	 * 
	 * @return void
	 */
	public void postToSentBox(String messagesFrom, String messagesBody,
			String messageUuid, String messageDate, int messageType) {
		Logger.log(CLASS_TAG, "postToOutbox(): post failed messages to outbox");

		SentMessagesUtil.smsMap.put("messagesFrom", messagesFrom);
		SentMessagesUtil.smsMap.put("messagesBody", messagesBody);
		SentMessagesUtil.smsMap.put("messagesDate", messageDate);
		SentMessagesUtil.smsMap.put("messagesUuid", messageUuid);
		SentMessagesUtil.smsMap
				.put("messagesType", String.valueOf(messageType));

		int status = SentMessagesUtil.processSentMessages(context);
		statusIntent.putExtra("sentstatus", status);
		context.sendBroadcast(statusIntent);

	}

	/**
	 * Put failed messages to be sent to the Sync URL to a local database.
	 * 
	 * @return void
	 */
	private void postToPendingBox(final String messagesFrom,
			final String messagesBody, final SmsMessage sms) {
		Logger.log(CLASS_TAG, "postToOutbox(): post failed messages to outbox");

		// Get message id.
		String messageUuid = getUuid();

		String messageDate = String.valueOf(sms.getTimestampMillis());
		Util.smsMap.put("messagesFrom", messagesFrom);
		Util.smsMap.put("messagesBody", messagesBody);
		Util.smsMap.put("messagesDate", messageDate);
		Util.smsMap.put("messagesUuid", messageUuid);
		new PendingMessages().showMessages();

		int status = MessageSyncUtil.processMessages();
		statusIntent = new Intent(ServicesConstants.FAILED_ACTION);
		statusIntent.putExtra("failed", status);
		context.sendBroadcast(statusIntent);

	}

}
