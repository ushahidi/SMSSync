/*******************************************************************************
 *  Copyright (c) 2010 - 2013 Ushahidi Inc
 *  All rights reserved
 *  Contact: team@ushahidi.com
 *  Website: http://www.ushahidi.com
 *  GNU Lesser General Public License Usage
 *  This file may be used under the terms of the GNU Lesser
 *  General Public License version 3 as published by the Free Software
 *  Foundation and appearing in the file LICENSE.LGPL included in the
 *  packaging of this file. Please review the following information to
 *  ensure the GNU Lesser General Public License version 3 requirements
 *  will be met: http://www.gnu.org/licenses/lgpl.html.
 *
 * If you have questions regarding the use of this file, please contact
 * Ushahidi developers at team@ushahidi.com.
 ******************************************************************************/

package org.addhen.smssync.messages;

import org.addhen.smssync.App;
import org.addhen.smssync.R;
import org.addhen.smssync.database.BaseDatabseHelper;
import org.addhen.smssync.models.Message;
import org.addhen.smssync.prefs.Prefs;
import org.addhen.smssync.util.LogUtil;
import org.addhen.smssync.util.Logger;
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
import android.provider.Telephony;
import android.provider.Telephony.Sms.Conversations;
import android.provider.Telephony.Sms.Inbox;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.text.format.DateFormat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * This class has the main logic to dispatch the messages that comes to the device. It decides where
 * to post the messages to depending on the status of the device. If the message fails to send to
 * the configured web service, it saves them in the pending list and when it succeeds it saves them
 * in the sent list.
 *
 * @author eyedol
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

    public static final int PENDING = 0;

    public static final int TASK = 1;

    public static final int UNCONFIRMED = 2;

    public static final int FAILED = 3;

    private static final String CLASS_TAG = ProcessSms.class.getSimpleName();

    public static HashMap<String, String> smsMap;

    Prefs prefs;

    private Context context;

    public ProcessSms(Context context) {
        this.context = context;
        smsMap = new HashMap<>();
        prefs = new Prefs(context);
    }

    /**
     * Tries to locate the message id (from the system database), given the message thread id and
     * the timestamp of the message.
     *
     * @param threadId  - The message's thread ID.
     * @param timestamp - The timestamp of the message.
     * @return the message id
     */
    public long findMessageId(long threadId,
            long timestamp) {
        Logger.log(CLASS_TAG,
                "findMessageId(): get the message id using thread id and timestamp: threadId: "
                        + threadId + " timestamp: " + timestamp);
        if (Util.isKitKat()) {
            return findMessageIdKitKat(threadId, timestamp);
        }
        long id = 0;
        if (threadId > 0) {

            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(CONVERSATION_CONTENT_URI,
                            threadId),
                    new String[]{
                            "_id", "date", "thread_id"
                    },
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

    public long findMessageIdKitKat(long threadId, long timestamp) {
        Logger.log(CLASS_TAG,
                "findMessageId(): get the message id using thread id and timestamp: threadId: "
                        + threadId + " timestamp: " + timestamp);
        long id = 0;
        if (threadId > 0) {

            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SmsQuery.SMS_CONVERSATION_URI,
                            threadId),
                    SmsQuery.PROJECTION,
                    Conversations.DATE + "=" + timestamp, null, "date desc");

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
     * Filter message string for particular keywords
     *
     * @param message    The message to be tested against the keywords
     * @param filterText A CSV string listing keywords to match against message
     * @return boolean
     */
    public boolean filterByKeywords(String message, String filterText) {
        String[] keywords = filterText.split(",");

        for (String keyword : keywords) {
            if (message.toLowerCase()
                    .contains(keyword.toLowerCase().trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Filter message string for RegEx match
     *
     * @param message    The message to be tested against the RegEx
     * @param filterText A string representing the regular expression to test against.
     * @return boolean
     */
    public boolean filterByRegex(String message, String filterText) {
        Pattern pattern = null;
        try {
            pattern = Pattern.compile(filterText, Pattern.CASE_INSENSITIVE);
        } catch (PatternSyntaxException e) {
            // invalid RegEx
            return true;
        }
        Matcher matcher = pattern.matcher(message);

        return (matcher.find());
    }

    /**
     * Import messages from the messages app's table and puts them in SMSSync's outbox table.
     * This will allow the imported messages to be sync'd to the configured Sync URL.
     * 
     * @return true for success, false for failure.
     */
    public boolean importMessages() {
        Logger.log(CLASS_TAG,
                "importMessages(): import messages from messages app");
        if (Util.isKitKat()) {
            return importMessageKitKat();
        }
        Uri uriSms = Uri.parse(SMS_CONTENT_INBOX);
        uriSms = uriSms.buildUpon().appendQueryParameter("LIMIT", "10").build();
        String[] projection = {
                "_id", "address", "date", "body"
        };

        Cursor c = context.getContentResolver().query(uriSms, projection, null,
                null, "date DESC");

        if (c != null && c.getCount() > 0) {
            try {
                if (c.moveToFirst()) {
                    List<Message> messages = new ArrayList<>();
                    do {
                        Message message = new Message();

                        final long messageDate = c.getLong(c
                                .getColumnIndex("date"));
                        message.setDate(new Date(messageDate));

                        message.setPhoneNumber(c.getString(c
                                .getColumnIndex("address")));
                        message.setBody(c.getString(c.getColumnIndex("body")));
                        message.setUuid(getUuid());
                        messages.add(message);
                    } while (c.moveToNext());
                    saveMessage(messages);
                }
            } finally {
                if (c != null) {
                    c.close();
                }
            }
            return true;

        } else {
            return false;
        }

    }

    private void saveMessage(List<Message> messages) {
        App
                .getDatabaseInstance().getMessageInstance().put(messages,
                new BaseDatabseHelper.DatabaseCallback<Void>() {
                    @Override
                    public void onFinished(Void result) {

                    }

                    @Override
                    public void onError(Exception exception) {

                    }
                });
    }

    public int importMessageKitKat() {
        Logger.log(CLASS_TAG,
                "importMessages(): import messages from messages app");
        Uri uriSms = SmsQuery.INBOX_CONTENT_URI;
        uriSms = uriSms.buildUpon().appendQueryParameter("LIMIT", "10").build();

        Cursor c = context.getContentResolver().query(uriSms, SmsQuery.PROJECTION, null,
                null, Inbox.DATE + " DESC");

        if (c != null && c.getCount() > 0) {
            try {
                if (c.moveToFirst()) {
                    List<Message> messages = new ArrayList<>();
                    do {
                        Message message = new Message();

                        final long messageDate = c.getLong(c
                                .getColumnIndex(Inbox.DATE));

                        message.setDate(new Date(messageDate));

                        message.setPhoneNumber(c.getString(c
                                .getColumnIndex(Inbox.ADDRESS)));
                        message.setBody(c.getString(c.getColumnIndex(Inbox.BODY)));
                        message.setUuid(getUuid());
                        messages.add(message);

                    } while (c.moveToNext());
                }
            } finally {
                if (c != null) {
                    c.close();
                }
            }
            return 0;

        } else {
            return 1;
        }
    }

    /**
     * Tries to locate the thread id given the address (phone number or email) of the message
     * sender.
     *
     * @return the thread id
     */
    public long getThreadId(String body, String address) {
        Logger.log(CLASS_TAG, "getId(): thread id");

        if (Util.isKitKat()) {
            return getThreadIdKitKat(body, address);
        }
        Uri uriSms = Uri.parse(SMS_CONTENT_INBOX);

        StringBuilder sb = new StringBuilder();
        sb.append("address=" + DatabaseUtils.sqlEscapeString(address) + " AND ");
        sb.append("body=" + DatabaseUtils.sqlEscapeString(body));

        Cursor c = context.getContentResolver().query(uriSms, null, sb.toString(), null,
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

    public long getThreadIdKitKat(String body, String address) {
        Logger.log(CLASS_TAG, "getId(): thread id the kitkat way");
        StringBuilder sb = new StringBuilder();
        sb.append(Inbox.ADDRESS + "=" + DatabaseUtils.sqlEscapeString(address) + " AND ");
        sb.append(Inbox.BODY + "=" + DatabaseUtils.sqlEscapeString(body));

        Cursor c = context.getContentResolver()
                .query(SmsQuery.INBOX_CONTENT_URI, SmsQuery.PROJECTION, sb.toString(), null,
                        SmsQuery.SORT_ORDER);

        if (c != null) {
            if (c.getCount() > 0) {
                c.moveToFirst();
                long threadId = c.getLong(c.getColumnIndex(Inbox.THREAD_ID));
                c.close();
                return threadId;
            }
        }

        return 0;
    }

    public String getUuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * Sends SMS to a number.
     *
     * @param message - Number to send SMS to.
     */
    public boolean sendSms(Message message) {

        ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();
        Logger.log(CLASS_TAG, "sendSms(): Sends SMS to a number: sendTo: "
                + message.getPhoneNumber() + " message: " + message.getBody() +" message"+message);

        Util.logActivities(context, context.getString(R.string.sent_msg, message.getBody(), message.getPhoneNumber()));

        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> parts = sms.divideMessage(message.getBody());
        //String validUUID;
        for (int i = 0; i < parts.size(); i++) {

            Intent sentMessageIntent = new Intent(ServicesConstants.SENT);
            sentMessageIntent.putExtra(ServicesConstants.SENT_SMS_BUNDLE, message);

            PendingIntent sentIntent = PendingIntent.getBroadcast(context,
                    (int) System.currentTimeMillis(), sentMessageIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Intent delivered = new Intent(ServicesConstants.DELIVERED);
            delivered.putExtra(ServicesConstants.DELIVERED_SMS_BUNDLE, message);

            PendingIntent deliveryIntent = PendingIntent.getBroadcast(context,
                    (int) System.currentTimeMillis(), delivered, PendingIntent.FLAG_UPDATE_CURRENT);

            sentIntents.add(sentIntent);

            deliveryIntents.add(deliveryIntent);
        }

        if (PhoneNumberUtils.isGlobalPhoneNumber(message.getPhoneNumber())) {

            message.setStatus(Message.Status.UNCONFIRMED);

            if (prefs.smsReportDelivery().get()) {
                sms.sendMultipartTextMessage(message.getPhoneNumber(), null, parts, sentIntents,
                        deliveryIntents);
            } else {
                sms.sendMultipartTextMessage(message.getPhoneNumber(), null, parts, sentIntents,
                        null);
            }

        } else {
            final String errNotGlobalPhoneNumber = "sendSms(): !PhoneNumberUtils.isGlobalPhoneNumber: " + message.getPhoneNumber();
            Logger.log(CLASS_TAG, errNotGlobalPhoneNumber);
            // Following copy/pasted from BaseBroadcastReceiver.. should be in shared util instead
            if (prefs.enableLog().get()) {
                new LogUtil(DateFormat.getDateFormatOrder(context)).appendAndClose(errNotGlobalPhoneNumber);
            }
            Toast.makeText(context, errNotGlobalPhoneNumber, Toast.LENGTH_LONG).show();
        }
        return false;
    }

    /**
     * Delete SMS from the message app inbox
     *
     * @param body    The message body
     * @param address The address / from
     * @return boolean
     */
    public boolean delSmsFromInbox(String body, String address) {
        Logger.log(CLASS_TAG, "delSmsFromInbox(): Delete SMS message app inbox");
        final long threadId = getThreadId(body, address);
        Uri smsUri = Util.isKitKat() ? ContentUris.withAppendedId(SmsQuery.SMS_CONVERSATION_URI,
                threadId) : ContentUris.withAppendedId(Uri.parse(SMS_CONTENT_URI), threadId);
        if (threadId >= 0) {

            int rowsDeleted = context.getContentResolver().delete(
                    smsUri, null, null);
            if (rowsDeleted > 0) {
                return true;
            }
            return false;
        }
        return false;
    }

    public Context getContext() {
        return context;
    }

    /**
     * A basic SmsQuery on android.provider.Telephony.Sms.Inbox
     */
    private interface SmsQuery {

        int TOKEN = 1;

        static final Uri INBOX_CONTENT_URI = Inbox.CONTENT_URI;

        static final Uri SMS_CONVERSATION_URI = Conversations.CONTENT_URI;

        static final String[] PROJECTION = {
                Inbox._ID,
                Inbox.ADDRESS,
                Inbox.BODY,
                Inbox.DATE,
        };

        static final String SORT_ORDER = Telephony.Sms.Inbox.DEFAULT_SORT_ORDER;

        int ID = 0;
        int ADDRESS = 1;
        int BODY = 2;
    }

}
