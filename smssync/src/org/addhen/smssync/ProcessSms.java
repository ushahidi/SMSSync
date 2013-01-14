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
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
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
	 *            messagesId The unique ID of the messages.
	 * @param SmsMessages
	 *            sms The SMS object as
	 * 
	 * @return boolean The status of the message routing.
	 */
	public boolean routeMessages(String messagesFrom, String messagesBody,
			String messagesTimestamp, String messagesId) {

		// load prefrences.
		Prefs.loadPreferences(context);

		boolean posted = true;
		// is smssync service running
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

					// process keyword
					if (!TextUtils.isEmpty(syncUrl.getKeywords())) {
						String keywords[] = syncUrl.getKeywords().split(",");
						if (filterByKeywords(messagesBody, keywords)) {
							posted = messageSyncUtil.postToAWebService(
									messagesFrom, messagesBody,
									messagesTimestamp, messagesId,
									syncUrl.getSecret());
							if (!posted) {
								Util.showFailNotification(
										context,
										messagesBody,
										context.getString(R.string.sending_failed));

								// attempt to make a data connection to sync
								// the failed messages.
								Util.connectToDataNetwork(context);
							} else {

								postToSentBox(messagesFrom, messagesBody,
										messagesId, messagesTimestamp);
								Util.showFailNotification(
										context,
										messagesBody,
										context.getString(R.string.sending_succeeded));
							}

						}

					} else { // there is no keyword set up on a sync URL
						posted = messageSyncUtil.postToAWebService(
								messagesFrom, messagesBody, messagesTimestamp,
								messagesId, syncUrl.getSecret());
						if (!posted) {
							Util.showFailNotification(context, messagesBody,
									context.getString(R.string.sending_failed));

							// attempt to make a data connection so to sync
							// the failed messages.
							Util.connectToDataNetwork(context);

						} else {

							postToSentBox(messagesFrom, messagesBody,
									messagesId, messagesTimestamp);

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
	 * Processes the incoming SMS to figure out how to exactly route the
	 * message. If it fails to be synced online, cache it and queue it up for
	 * the scheduler to process it.
	 * 
	 * @param String
	 *            messagesFrom The number that sent the SMS
	 * @param String
	 *            messagesBody The message body. This is the message sent ot the
	 *            phone.
	 * @param String
	 *            messagesTimestamp The timestamp of the message
	 * @param String
	 *            messagesId The unique ID of the messages.
	 * @param SmsMessages
	 *            sms The SMS object as
	 */
	public void routeSms(String messagesFrom, String messagesBody,
			String messagesTimestamp, String messagesId, SmsMessage sms) {

		if (routeMessages(messagesFrom, messagesBody, messagesTimestamp,
				messagesId)) {

			// Delete messages from message app's inbox, only
			// when smssync has that feature turned on
			if (Prefs.autoDelete) {
				delSmsFromInbox(sms);
			}

		} else {
			postToPendingBox(messagesFrom, messagesBody, sms);
		}

	}

	/**
	 * Processes failed messages aka pending message to figure out how to
	 * exactly route the message. If it fails to be synced online, cache it and
	 * queue it up for the scheduler to process it.
	 * 
	 * @param String
	 *            messagesFrom The number that sent the SMS
	 * @param String
	 *            messagesBody The message body. This is the message sent ot the
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
	 * Filter CSV strings for particular
	 * 
	 * @param message
	 *            The CSV string to be filtered for the keywords
	 * @param keywords
	 *            An array that contains the keywords to be filtered
	 * 
	 * @return boolean
	 */
	public boolean filterByKeywords(String message, String[] keywords) {
		for (int i = 0; i < keywords.length; i++) {
			if (message.toLowerCase()
					.contains(keywords[i].toLowerCase().trim())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Import messages from the messages app's table and puts them in SMSSync's
	 * outbox table. This will allow messages the imported messages to be sync'd
	 * to the configured Sync URL.
	 * 
	 * @return int - 0 for success, 1 for failure.
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
					messages.setMessageId(Integer.valueOf(c.getString(c
							.getColumnIndex("_id"))));

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
	 * Tries to locate the message id or thread id given the address (phone
	 * number or email) of the message sender.
	 * 
	 * @param SmsMessage
	 *            msg - The SMS object to get the address of the message from.
	 * @param idType
	 *            The type it use to fetch the ID of the message. Either id type
	 *            or thread type
	 * 
	 * @return the message id
	 */
	public long getId(SmsMessage msg, String idType) {
		Logger.log(CLASS_TAG,
				"getId(): Locate message id or thread id: idType:" + idType);
		Uri uriSms = Uri.parse(SMS_CONTENT_INBOX);

		StringBuilder sb = new StringBuilder();
		sb.append("address='" + msg.getOriginatingAddress() + "' AND ");
		sb.append("body=" + DatabaseUtils.sqlEscapeString(msg.getMessageBody()));
		Cursor c = context.getContentResolver().query(uriSms, null,
				sb.toString(), null, null);

		if (c.getCount() > 0 && c != null) {
			c.moveToFirst();
			if (idType.equals("id")) {
				return c.getLong(c.getColumnIndex("_id"));

			} else if (idType.equals("thread")) {
				return c.getLong(c.getColumnIndex("thread_id"));
			}
			c.close();
		}
		return 0;
	}
	/*
	 * Determine whether to use SMSSync or to use SMSPortals
	 */
	private MessengerConnection getCurrentMessengerConnection(){
		ArrayList<MessengerConnection> availableConnections = MainApplication.availableConnections;
		int s = availableConnections.size();
		int c = MainApplication.currentConnectionIndex;
		MainApplication.currentConnectionIndex++;
		int nextIndex = (c + 1) % s;
		return availableConnections.get(nextIndex);
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
		MessengerConnection connectionToUse = getCurrentMessengerConnection();
		if(connectionToUse == null){
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
			if (PhoneNumberUtils.isGlobalPhoneNumber(sendTo))
				sms.sendMultipartTextMessage(sendTo, null, parts, sentIntents,
						deliveryIntents);
		} else {
	        Message message = Message.obtain(null, 1, 0, 0);
	        try {
				Bundle data = new Bundle();
				data.putString("sendTo", sendTo);
				data.putString("msg", msg);
				message.setData(data);
				connectionToUse.messenger.send(message);
	        } catch (RemoteException e) {
	            e.printStackTrace();
	        }
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
	public void delSmsFromInbox(SmsMessage msg) {
		Logger.log(CLASS_TAG, "delSmsFromInbox(): Delete SMS message app inbox");
		final long threadId = getId(msg, "thread");

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
			String messageId, String messageDate) {
		Logger.log(CLASS_TAG, "postToOutbox(): post failed messages to outbox");

		SentMessagesUtil.smsMap.put("messagesFrom", messagesFrom);
		SentMessagesUtil.smsMap.put("messagesBody", messagesBody);
		SentMessagesUtil.smsMap.put("messagesDate", messageDate);
		SentMessagesUtil.smsMap.put("messagesId", messageId);

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
		String messageId = String.valueOf(getId(sms, "id"));

		String messageDate = String.valueOf(sms.getTimestampMillis());
		Util.smsMap.put("messagesFrom", messagesFrom);
		Util.smsMap.put("messagesBody", messagesBody);
		Util.smsMap.put("messagesDate", messageDate);
		Util.smsMap.put("messagesId", messageId);
		new PendingMessages().showMessages();

		int status = MessageSyncUtil.processMessages();
		statusIntent = new Intent(ServicesConstants.FAILED_ACTION);
		statusIntent.putExtra("failed", status);
		context.sendBroadcast(statusIntent);

	}

}
