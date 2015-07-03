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
import java.util.List;

/**
 * Offers nifty utilities for processing SMS message.
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class ProcessSms {

    private static final String SMS_CONTENT_URI = "content://sms/conversations/";

    private static final String SMS_CONTENT_INBOX = "content://sms/inbox";

    private static String SENT = "SMS_SENT";

    private static final String SENT_SMS_BUNDLE = "sent";

    private static String DELIVERED = "SMS_DELIVERED";

    private static final String DELIVERED_SMS_BUNDLE = "delivered";

    private static final String CLASS_TAG = ProcessSms.class.getSimpleName();

    private Context mContext;

    public ProcessSms(Context context) {
        mContext = context;
    }

    /**
     * Send message as SMS to a phone number. When sendDeliveryReport is set to true
     * the message {@link SmsMessage} will be sent as part of the delivery report Intent
     *
     * @param message            The message of the SMS
     * @param sendDeliveryReport Whether to send delivery report or not
     */
    public void sendSms(SmsMessage message, boolean sendDeliveryReport) {
        LogUtil.logInfo(CLASS_TAG, "sendSms(): Sends SMS to a number: sendTo: %s message: %s",
                message.phone,
                message.body);
        ArrayList<PendingIntent> sentIntents = new ArrayList<>();
        ArrayList<PendingIntent> deliveryIntents = new ArrayList<>();
        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> parts = sms.divideMessage(message.body);

        for (int i = 0; i < parts.size(); i++) {

            Intent sentMessageIntent = new Intent(SENT);
            sentMessageIntent.putExtra(SENT_SMS_BUNDLE, message.body);

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
            sms.sendMultipartTextMessage(message.body, null, parts, sentIntents, deliveryIntents);
            return;
        }

        sms.sendMultipartTextMessage(message.phone, null, parts, sentIntents, null);
    }

    /**
     * Delete SMS from the message app inbox
     *
     * @param smsMessage The message to be deleted
     * @return true if deleted otherwise false
     */
    public boolean delSmsFromInbox(SmsMessage smsMessage) {
        LogUtil.logInfo(CLASS_TAG, "delSmsFromInbox(): Delete SMS message app inbox");
        final long threadId = getThreadId(smsMessage);
        Uri smsUri = Util.isKitKatOrHigher() ? ContentUris
                .withAppendedId(SmsQuery.SMS_CONVERSATION_URI,
                        threadId)
                : ContentUris.withAppendedId(Uri.parse(SMS_CONTENT_URI), threadId);
        if (threadId >= 0) {

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
     * Import messages from the messages app's table and puts them in SMSSync's outbox table.
     * This will allow messages the imported messages to be sync'd to the configured Sync URL.
     *
     * @return true for success, false for failure.
     */
    public List<SmsMessage> importMessages() {
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
        List<SmsMessage> messages = new ArrayList<>();
        if (c != null && c.getCount() > 0) {
            try {
                if (c.moveToFirst()) {
                    do {
                        SmsMessage message = new SmsMessage();

                        final long messageDate = c.getLong(c.getColumnIndex("date"));
                        message.timestamp = messageDate;
                        message.phone = c.getString(c.getColumnIndex("address"));
                        message.body = c.getString(c.getColumnIndex("body"));
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

    private List<SmsMessage> importMessageKitKat() {
        LogUtil.logInfo(CLASS_TAG, "importMessages(): import messages from messages app");
        Uri uriSms = SmsQuery.INBOX_CONTENT_URI;
        uriSms = uriSms.buildUpon().appendQueryParameter("LIMIT", "10").build();
        Cursor c = mContext.getContentResolver().query(uriSms, SmsQuery.PROJECTION, null,
                null, Telephony.Sms.Inbox.DATE + " DESC");
        List<SmsMessage> messages = new ArrayList<>();
        if (c != null && c.getCount() > 0) {
            try {
                if (c.moveToFirst()) {
                    do {
                        SmsMessage message = new SmsMessage();

                        final long messageDate = c.getLong(c
                                .getColumnIndex(Telephony.Sms.Inbox.DATE));
                        message.timestamp = messageDate;
                        message.phone = c.getString(c.getColumnIndex(Telephony.Sms.Inbox.ADDRESS));
                        message.body = c.getString(c.getColumnIndex(Telephony.Sms.Inbox.BODY));
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
    private long getThreadId(SmsMessage smsMessage) {
        LogUtil.logInfo(CLASS_TAG, "getId(): thread id");

        if (Util.isKitKatOrHigher()) {
            return getThreadIdKitKat(smsMessage);
        }
        Uri uriSms = Uri.parse(SMS_CONTENT_INBOX);
        StringBuilder sb = new StringBuilder();
        sb.append("address=" + DatabaseUtils.sqlEscapeString(smsMessage.phone) + " AND ");
        sb.append("body=" + DatabaseUtils.sqlEscapeString(smsMessage.body));
        Cursor c = mContext.getContentResolver().query(uriSms, null, sb.toString(), null,
                "date DESC ");
        if (c != null) {
            if (c.getCount() > 0) {
                c.moveToFirst();
                long threadId = c.getLong(c.getColumnIndex("thread_id"));
                c.close();
                return threadId;
            }
        }
        return 0;
    }

    private long getThreadIdKitKat(SmsMessage smsMessage) {
        LogUtil.logInfo(CLASS_TAG, "getId(): thread id the kitkat way");
        StringBuilder sb = new StringBuilder();
        sb.append(
                Telephony.Sms.Inbox.ADDRESS + "=" + DatabaseUtils.sqlEscapeString(smsMessage.phone)
                        + " AND ");
        sb.append(Telephony.Sms.Inbox.BODY + "=" + DatabaseUtils.sqlEscapeString(smsMessage.body));
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
        }
        return 0;
    }

    /**
     * A basic SmsQuery on android.provider.Telephony.Sms.Inbox
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
}
