/*
 * Copyright (c) 2010 - 2015 Ushahidi Inc
 * All rights reserved
 * Contact: team@ushahidi.com
 * Website: http://www.ushahidi.com
 * GNU Lesser General Public License Usage
 * This file may be used under the terms of the GNU Lesser
 * General Public License version 3 as published by the Free Software
 * Foundation and appearing in the file LICENSE.LGPL included in the
 * packaging of this file. Please review the following information to
 * ensure the GNU Lesser General Public License version 3 requirements
 * will be met: http://www.gnu.org/licenses/lgpl.html.
 *
 * If you have questions regarding the use of this file, please contact
 * Ushahidi developers at team@ushahidi.com.
 */

package org.addhen.smssync.smslib.sms;

import org.addhen.smssync.presentation.model.MessageModel;
import org.addhen.smssync.smslib.model.SmsMessage;
import org.addhen.smssync.smslib.util.LogUtil;
import org.addhen.smssync.smslib.util.Util;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.provider.Telephony;
import android.telephony.SmsManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Offers nifty utilities for processing SMS message.
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
@Singleton
public class ProcessSms {

    public static String DELIVERED = "SMS_DELIVERED";

    public static final String SENT_SMS_BUNDLE = "sent";

    public static final String DELIVERED_SMS_BUNDLE = "delivered";

    private static final String SMS_CONTENT_URI = "content://sms/conversations/";

    private static final String SMS_CONTENT_INBOX = "content://sms/inbox";

    private static String SENT = "SMS_SENT";

    private static final String CLASS_TAG = ProcessSms.class.getSimpleName();

    private Context mContext;

    @Inject
    public ProcessSms(Context context) {
        mContext = context;
    }

    /**
     * Send message as SMS to a phone number. When sendDeliveryReport is set to true
     * the message {@link SmsMessage} will be sent as part of the delivery report Intent. So you
     * can know which message actually got delivered.
     *
     * @param message            The message of the SMS
     * @param sendDeliveryReport Whether to send delivery report or not
     */
    public void sendSms(MessageModel message, boolean sendDeliveryReport) {
        LogUtil.logInfo(CLASS_TAG, "sendSms(): Sends SMS to a number: sendTo: %s message: %s",
                message.getMessageFrom(),
                message.getMessageBody());
        ArrayList<PendingIntent> sentIntents = new ArrayList<>();
        ArrayList<PendingIntent> deliveryIntents = new ArrayList<>();
        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> parts = sms.divideMessage(message.getMessageBody());

        for (int i = 0; i < parts.size(); i++) {

            Intent sentMessageIntent = new Intent(SENT);
            sentMessageIntent.putExtra(SENT_SMS_BUNDLE, message);

            PendingIntent sentIntent = PendingIntent
                    .getBroadcast(mContext, (int) System.currentTimeMillis(), sentMessageIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

            Intent delivered = new Intent(DELIVERED);
            delivered.putExtra(DELIVERED_SMS_BUNDLE, message);

            PendingIntent deliveryIntent = PendingIntent
                    .getBroadcast(mContext, (int) System.currentTimeMillis(), delivered,
                            PendingIntent.FLAG_UPDATE_CURRENT);

            sentIntents.add(sentIntent);

            deliveryIntents.add(deliveryIntent);
        }
        if (sendDeliveryReport) {
            sms.sendMultipartTextMessage(message.getMessageFrom(), null, parts, sentIntents,
                    deliveryIntents);
            return;
        }

        sms.sendMultipartTextMessage(message.getMessageFrom(), null, parts, sentIntents, null);
    }

    /**
     * Delete SMS from the message app inbox
     *
     * @param messageModel The message to be deleted
     * @return true if deleted otherwise false
     */
    public boolean delSmsFromInbox(MessageModel messageModel) {
        LogUtil.logInfo(CLASS_TAG, "delSmsFromInbox(): Delete SMS message app inbox");
        final long threadId = getThreadId(messageModel);
        if (threadId >= 0) {
            Uri smsUri = Util.isKitKatOrHigher() ? ContentUris
                    .withAppendedId(SmsQuery.SMS_CONVERSATION_URI,
                            threadId)
                    : ContentUris.withAppendedId(Uri.parse(SMS_CONTENT_URI), threadId);

            int rowsDeleted = mContext.getContentResolver().delete(
                    smsUri, null, null);
            if (rowsDeleted > 0) {
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * Import messages from the messages app's table with a limitation of 10 messages.
     *
     * @return An empty list of {@link SmsMessage} when know message is imported or the total number
     * of messages imported.
     */
    public List<MessageModel> importMessages() {
        LogUtil.logInfo(CLASS_TAG, "importMessages(): import messages from messages app");
        if (Util.isKitKatOrHigher()) {
            return importMessageKitKat();
        }
        Uri uriSms = Uri.parse(SMS_CONTENT_INBOX);
        uriSms = uriSms.buildUpon().appendQueryParameter("LIMIT", "10").build();
        String[] projection = {
                "_id", "address", "date", "body"
        };

        Cursor c = mContext.getContentResolver().query(uriSms, projection, null,
                null, "date DESC");
        List<MessageModel> messages = new ArrayList<>();
        if (c != null && c.getCount() > 0) {
            try {
                if (c.moveToFirst()) {
                    do {
                        MessageModel message = new MessageModel();

                        final long messageDate = c.getLong(c.getColumnIndex("date"));
                        message.setMessageDate(new Date(messageDate));
                        message.setMessageFrom(c.getString(c.getColumnIndex("address")));
                        message.setMessageBody(c.getString(c.getColumnIndex("body")));
                        message.setMessageUuid(getUuid());
                        message.setMessageType(MessageModel.Type.PENDING);
                        // Treat imported messages as failed
                        message.setStatus(MessageModel.Status.FAILED);
                        messages.add(message);
                    } while (c.moveToNext());
                }
            } finally {
                if (c != null) {
                    c.close();
                }
            }
        }
        return messages;
    }

    private List<MessageModel> importMessageKitKat() {
        LogUtil.logInfo(CLASS_TAG, "importMessages(): import messages from messages app");
        Uri uriSms = SmsQuery.INBOX_CONTENT_URI;
        uriSms = uriSms.buildUpon().appendQueryParameter("LIMIT", "10").build();
        Cursor c = mContext.getContentResolver().query(uriSms, SmsQuery.PROJECTION, null,
                null, Telephony.Sms.Inbox.DATE + " DESC");
        List<MessageModel> messages = new ArrayList<>();
        if (c != null && c.getCount() > 0) {
            try {
                if (c.moveToFirst()) {
                    do {
                        MessageModel message = new MessageModel();

                        final long messageDate = c.getLong(c
                                .getColumnIndex(Telephony.Sms.Inbox.DATE));
                        message.setMessageDate(new Date(messageDate));
                        message.setMessageFrom(c
                                .getString(c.getColumnIndex(Telephony.Sms.Inbox.ADDRESS)));
                        message.setMessageBody(c
                                .getString(c.getColumnIndex(Telephony.Sms.Inbox.BODY)));
                        message.setMessageUuid(getUuid());
                        message.setMessageType(MessageModel.Type.PENDING);
                        // Treat imported messages as failed
                        message.setStatus(MessageModel.Status.FAILED);
                        messages.add(message);

                    } while (c.moveToNext());
                }
            } finally {
                if (c != null) {
                    c.close();
                }
            }
        }
        return messages;
    }

    /**
     * Tries to locate the thread id given the address (phone number or email) of the message
     * sender.
     *
     * @return the thread id
     */
    private long getThreadId(MessageModel messageModel) {
        LogUtil.logInfo(CLASS_TAG, "getId(): thread id");

        if (Util.isKitKatOrHigher()) {
            return getThreadIdKitKat(messageModel);
        }
        Uri uriSms = Uri.parse(SMS_CONTENT_INBOX);
        StringBuilder sb = new StringBuilder();
        sb.append("address=" + DatabaseUtils.sqlEscapeString(messageModel.getMessageFrom())
                + " AND ");
        sb.append("body=" + DatabaseUtils.sqlEscapeString(messageModel.getMessageBody()));
        Cursor c = mContext.getContentResolver().query(uriSms, null, sb.toString(), null,
                "date DESC ");
        if (c != null) {
            if (c.getCount() > 0) {
                c.moveToFirst();
                long threadId = c.getLong(c.getColumnIndex("thread_id"));
                c.close();
                return threadId;
            }
            c.close();
        }
        return 0;
    }

    private long getThreadIdKitKat(MessageModel messageModel) {
        LogUtil.logInfo(CLASS_TAG, "getId(): thread id the kitkat way");
        StringBuilder sb = new StringBuilder();
        sb.append(
                Telephony.Sms.Inbox.ADDRESS + "=" + DatabaseUtils
                        .sqlEscapeString(messageModel.getMessageFrom())
                        + " AND ");
        sb.append(Telephony.Sms.Inbox.BODY + "=" + DatabaseUtils
                .sqlEscapeString(messageModel.getMessageBody()));
        Cursor c = mContext.getContentResolver()
                .query(SmsQuery.INBOX_CONTENT_URI, SmsQuery.PROJECTION, sb.toString(), null,
                        SmsQuery.SORT_ORDER);
        if (c != null) {
            if (c.getCount() > 0) {
                c.moveToFirst();
                long threadId = c.getLong(c.getColumnIndex(Telephony.Sms.Inbox.THREAD_ID));
                c.close();
                return threadId;
            }
            c.close();
        }
        return 0;
    }

    /**
     * A basic SmsQuery based on android.provider.Telephony.Sms.Inbox
     */
    @SuppressLint("NewApi")
    private interface SmsQuery {

        Uri INBOX_CONTENT_URI = Telephony.Sms.Inbox.CONTENT_URI;
        Uri SMS_CONVERSATION_URI = Telephony.Sms.Conversations.CONTENT_URI;
        String[] PROJECTION = {
                Telephony.Sms.Inbox._ID,
                Telephony.Sms.Inbox.ADDRESS,
                Telephony.Sms.Inbox.BODY,
                Telephony.Sms.Inbox.DATE,
        };
        String SORT_ORDER = Telephony.Sms.Inbox.DEFAULT_SORT_ORDER;
    }

    public String getUuid() {
        return UUID.randomUUID().toString();
    }
}
