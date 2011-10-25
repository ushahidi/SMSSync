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

package org.addhen.smssync.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.addhen.smssync.MainApplication;
import org.addhen.smssync.MessagesTabActivity;
import org.addhen.smssync.Prefrences;
import org.addhen.smssync.R;
import org.addhen.smssync.data.Database;
import org.addhen.smssync.data.Messages;
import org.addhen.smssync.net.MainHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * This class habours common util methods that are available for other classes
 * to use.
 * 
 * @author eyedol
 */
public class Util {

    public static final Uri MMS_SMS_CONTENT_URI = Uri.parse("content://mms-sms/");

    public static final Uri THREAD_ID_CONTENT_URI = Uri.withAppendedPath(MMS_SMS_CONTENT_URI,
            "threadID");

    public static final Uri CONVERSATION_CONTENT_URI = Uri.withAppendedPath(MMS_SMS_CONTENT_URI,
            "conversations");

    public static final String SMS_CONTENT_URI = "content://sms/conversations/";

    public static final int NOTIFICATION_ALERT = 1337;

    public static final String SMS_ID = "_id";

    public static final String SMS_CONTENT_INBOX = "content://sms/inbox";

    public static final int READ_THREAD = 1;

    public static HashMap<String, String> smsMap = new HashMap<String, String>();

    private static NetworkInfo networkInfo;

    private static List<Messages> mMessages;

    private static JSONObject jsonObject;

    private static JSONArray jsonArray;

    private static Pattern pattern;

    private static Matcher matcher;

    private static final String TIME_FORMAT_12_HOUR = "h:mm a";

    private static final String TIME_FORMAT_24_HOUR = "H:mm";

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static final String URL_PATTERN = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

    private static final int NOTIFY_RUNNING = 100;

    private static final String CLASS_TAG = Util.class.getSimpleName();

    /**
     * Joins two strings together.
     * 
     * @param String first - The first String to be joined to a second string.
     * @param String second - The second String to join to the first string.
     * @return String
     */
    public static String joinString(String first, String second) {
        return first.concat(second);
    }

    /**
     * Converts a string into an int value.
     * 
     * @param String value - The string to be converted into int value.
     * @return int
     */
    public static int toInt(String value) {
        return Integer.parseInt(value);
    }

    /**
     * Capitalize any String given to it.
     * 
     * @param String text - The string to capitalized.
     * @return String
     */
    public static String capitalizeString(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    /**
     * Create CSV from a given string in a Vector object.
     * 
     * @param Vector<String> text - The Vector object containing the Strings
     * @return String
     */
    public static String implode(Vector<String> text) {

        String implodedStr = "";

        int i = 0;

        for (String value : text) {
            implodedStr += i == text.size() - 1 ? value : value + ",";
            i++;
        }

        return implodedStr;
    }

    /**
     * Checks if there is Internet connection or data connection on the device.
     * 
     * @param Context context - The activity calling this method.
     * @return boolean
     */
    public static boolean isConnected(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        networkInfo = connectivity.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;

    }

    /**
     * Limit a string to a defined length.
     * 
     * @param int limit - the total length.
     * @param string limited - the limited string.
     * @return String
     */
    public static String limitString(String value, int length) {
        StringBuilder buf = new StringBuilder(value);
        if (buf.length() > length) {
            buf.setLength(length);
            buf.append(" ...");
        }
        return buf.toString();
    }

    /**
     * Format date into more human readable format.
     * 
     * @param date - The date to be formatted.
     * @return String
     */
    public static String formatDate(String dateFormat, String date, String toFormat) {

        String formatted = "";

        DateFormat formatter = new SimpleDateFormat(dateFormat);
        try {
            Date dateStr = formatter.parse(date);
            formatted = formatter.format(dateStr);
            Date formatDate = formatter.parse(formatted);
            formatter = new SimpleDateFormat(toFormat);
            formatted = formatter.format(formatDate);

        } catch (ParseException e) {

            e.printStackTrace();
        }
        return formatted;
    }

    /**
     * Extract Ushahidi payload JSON data
     * 
     * @apram json_data - The json data to be formatted.
     * @return String
     */
    public static boolean extractPayloadJSON(String json_data) {
        Log.i(CLASS_TAG, "extracPayloadJSON(): Extracting payload JSON data" + json_data);
        try {

            jsonObject = new JSONObject(json_data);
            return jsonObject.getJSONObject("payload").getBoolean("success");

        } catch (JSONException e) {
            Log.e(CLASS_TAG, "JSONException " + e.getMessage());
            return false;
        }

    }

    /**
     * Process messages as received from the user; 0 - successful 1 - failed
     * fetching categories
     * 
     * @return int - status
     */
    public static int processMessages(Context context) {
        Log.i(CLASS_TAG,
                "processMessages(): Process text messages as received from the user's phone");
        List<Messages> listMessages = new ArrayList<Messages>();
        int messageId = 0;
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
        mMessages = listMessages;

        if (mMessages != null) {
            MainApplication.mDb.addMessages(mMessages);
            return 0;

        } else {
            return 1;
        }
    }

    /**
     * Show toast
     * 
     * @param Context - the application's context
     * @param Int - string resource id
     * @return void
     */
    public static void showToast(Context context, int i) {
        int duration = Toast.LENGTH_LONG;
        Toast.makeText(context, i, duration).show();
    }

    /**
     * Show notification
     */
    public static void showNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager)context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent baseIntent = new Intent(context, MessagesTabActivity.class);

        baseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Notification notification = new Notification(R.drawable.icon,
                context.getString(R.string.status), System.currentTimeMillis());

        notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, baseIntent, 0);

        notification.setLatestEventInfo(context, context.getString(R.string.app_name),
                context.getString(R.string.notification_summary), pendingIntent);

        notificationManager.notify(NOTIFY_RUNNING, notification);

    }

    /**
     * Validates an email address Credits:
     * http://www.mkyong.com/regular-expressions
     * /how-to-validate-email-address-with-regular-expression/
     * 
     * @param String - email address to be validated
     * @return boolean
     */
    public static boolean validateEmail(String emailAddress) {
        Log.i(CLASS_TAG, "validateEmail(): Validate Email address");
        if (!emailAddress.equals("")) {
            pattern = Pattern.compile(EMAIL_PATTERN);
            matcher = pattern.matcher(emailAddress);
            return matcher.matches();
        }
        return false;
    }

    /**
     * Tries to locate the message id (from the system database), given the
     * message thread id and the timestamp of the message.
     * 
     * @param Context context - The activity calling the method.
     * @param long threadId - The message's thread ID.
     * @param long _timestamp - The timestamp of the message.
     */
    public static long findMessageId(Context context, long threadId, long _timestamp) {
        Log.i(CLASS_TAG,
                "findMessageId(): get the message id using thread id and timestamp: threadId: "
                        + threadId + " timestamp: " + _timestamp);
        long id = 0;
        long timestamp = _timestamp;
        if (threadId > 0) {

            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(CONVERSATION_CONTENT_URI, threadId), new String[] {
                            "_id", "date", "thread_id"
                    }, "date=" + timestamp, null, "date desc");

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
     * @param Context context - The activity calling this method.
     * @param SmsMessage msg - The SMS object to get the address of the message
     *            from.
     * @return long.
     */
    public static long getId(Context context, SmsMessage msg, String idType) {
        Log.i(CLASS_TAG, "getId(): Locate message id or thread id: idType:" + idType);
        Uri uriSms = Uri.parse(SMS_CONTENT_INBOX);

        StringBuilder sb = new StringBuilder();
        sb.append("address='" + msg.getOriginatingAddress() + "' AND ");
        sb.append("body=" + DatabaseUtils.sqlEscapeString(msg.getMessageBody()));
        Cursor c = context.getContentResolver().query(uriSms, null, sb.toString(), null, null);

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

    /**
     * Clear the standard notification alert.
     * 
     * @param Context context - The context of the calling activity.
     * @return void
     */
    public static void clear(Context context) {
        clearAll(context);
    }

    /**
     * Clear all notifications shown to the user.
     * 
     * @param Context context - The context of the calling activity.
     * @return void.
     */
    public static void clearAll(Context context) {
        NotificationManager myNM = (NotificationManager)context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        myNM.cancelAll();
    }

    /**
     * Clear a running notification.
     * 
     * @param Context context - The context of the calling activity.
     * @return void
     */
    public static void clearNotify(Context context) {
        NotificationManager myNM = (NotificationManager)context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        myNM.cancel(NOTIFY_RUNNING);
    }

    /**
     * Format an Unix timestamp to a string suitable for display to the user
     * according to their system settings (12 or 24 hour time).
     * 
     * @param Context context - The context of the calling activity.
     * @param long timestamp - The human unfriendly timestamp.
     * @return String
     */
    public static String formatTimestamp(Context context, long timestamp) {
        String HOURS_24 = "24";
        String hours = "24";

        SimpleDateFormat mSDF = new SimpleDateFormat();
        if (HOURS_24.equals(hours)) {
            mSDF.applyLocalizedPattern(TIME_FORMAT_24_HOUR);
        } else {
            mSDF.applyLocalizedPattern(TIME_FORMAT_12_HOUR);
        }
        return mSDF.format(new Date(timestamp));
    }

    /**
     * Delete SMS from the message app inbox.
     * 
     * @param Context context - The calling activity
     * @param msg
     */
    public static void delSmsFromInbox(Context context, SmsMessage msg) {
        Log.i(CLASS_TAG, "delSmsFromInbox(): Delete SMS message app inbox");
        long threadId = getId(context, msg, "thread");

        if (threadId >= 0) {
            context.getContentResolver().delete(Uri.parse(SMS_CONTENT_URI + threadId), null, null);
        }
    }

    /**
     * Posts received SMS to a configured callback URL.
     * 
     * @param String apiKey
     * @param String fromAddress
     * @param String messageBody
     * @return boolean
     */
    public static boolean postToAWebService(String messagesFrom, String messagesBody,
            String messagesTimestamp, String messagesId, Context context) {
        Log.i(CLASS_TAG, "postToAWebService(): Post received SMS to configured URL:"
                + Prefrences.website + " messagesFrom: " + messagesFrom + " messagesBody: "
                + messagesBody);

        HashMap<String, String> params = new HashMap<String, String>();
        Prefrences.loadPreferences(context);

        if (!Prefrences.website.equals("")) {

            StringBuilder urlBuilder = new StringBuilder(Prefrences.website);
            params.put("secret", Prefrences.apiKey);
            params.put("from", messagesFrom);
            params.put("message", messagesBody);
            params.put("sent_timestamp", messagesTimestamp);
            params.put("sent_to", getPhoneNumber(context));
            params.put("message_id", messagesId);
            return MainHttpClient.postSmsToWebService(urlBuilder.toString(), params, context);
        }

        return false;
    }

    /**
     * Validate the callback URL
     * 
     * @param String callbackURL - The callback URL to be validated.
     * @return int - 0 = well formed URL, 1 = no configured url, 2 = Malformed
     *         URL - 3 = can't make connection to it.
     */
    public static int validateCallbackUrl(String callbackUrl) {

        if (TextUtils.isEmpty(callbackUrl)) {
            return 1;
        }

        pattern = Pattern.compile(URL_PATTERN);
        matcher = pattern.matcher(callbackUrl);
        if (matcher.matches()) {
            return 0;
        }
        return 1;

    }

    /**
     * Pushes pending messages to the configured URL.
     * 
     * @param Context context - The activity calling the method
     * @return int
     */
    public static int snycToWeb(Context context) {
        Log.i(CLASS_TAG, "syncToWeb(): push pending messages to the configured URL");
        Cursor cursor;
        cursor = MainApplication.mDb.fetchAllMessages();
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
                int messagesIdIndex = cursor.getColumnIndexOrThrow(Database.MESSAGES_ID);
                int messagesFromIndex = cursor.getColumnIndexOrThrow(Database.MESSAGES_FROM);

                int messagesBodyIndex = cursor.getColumnIndexOrThrow(Database.MESSAGES_BODY);
                int messagesTimestampIndex = cursor.getColumnIndexOrThrow(Database.MESSAGES_DATE);
                do {
                    Messages messages = new Messages();
                    listMessages.add(messages);

                    int messageId = Util.toInt(cursor.getString(messagesIdIndex));
                    messages.setMessageId(messageId);

                    messagesFrom = Util.capitalizeString(cursor.getString(messagesFromIndex));
                    messages.setMessageFrom(messagesFrom);

                    messagesBody = cursor.getString(messagesBodyIndex);
                    messages.setMessageBody(messagesBody);

                    messagesTimestamp = cursor.getString(messagesTimestampIndex);
                    messages.setMessageDate(messagesTimestamp);
                    // post to web service
                    if (Util.postToAWebService(messagesFrom, messagesBody, messagesTimestamp,
                            String.valueOf(messageId),context)) {

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
     * Sends SMS to a number.
     * 
     * @param String sendTo - Number to send SMS to.
     * @param String msg - The message to be sent.
     */
    public static void sendSms(Context context, String sendTo, String msg) {

        ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();
        Log.i(CLASS_TAG, "sendSms(): Sends SMS to a number: sendTo: " + sendTo + " message: " + msg);

        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> parts = sms.divideMessage(msg);
        for (int i = 0; i < parts.size(); i++) {
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(
                    ServicesConstants.SENT), 0);

            PendingIntent deliveryIntent = PendingIntent.getBroadcast(context, 0, new Intent(
                    ServicesConstants.DELIVERED), 0);
            sentIntents.add(sentIntent);

            deliveryIntents.add(deliveryIntent);
        }
        if (PhoneNumberUtils.isGlobalPhoneNumber(sendTo))
            sms.sendMultipartTextMessage(sendTo, null, parts, sentIntents, deliveryIntents);
    }

    /**
     * Performs a task based on what callback URL tells it.
     * 
     * @param Context context - the activity calling this method.
     * @return void
     */
    public static void performTask(Context context) {
        Log.i(CLASS_TAG, "performTask(): perform a task");
        // load preferences
        Prefrences.loadPreferences(context);

        // validate configured url
        int status = validateCallbackUrl(Prefrences.website);
        if (status == 1) {
            showToast(context, R.string.no_configured_url);
        } else if (status == 2) {
            showToast(context, R.string.invalid_url);
        } else if (status == 3) {
            showToast(context, R.string.no_connection);
        } else {

            StringBuilder uriBuilder = new StringBuilder(Prefrences.website);

            uriBuilder.append("?task=send");

            String response = MainHttpClient.getFromWebService(uriBuilder.toString());
            Log.d(CLASS_TAG, "TaskCheckResponse: " + response);
            String task = "";
            String secret = "";
            if (!TextUtils.isEmpty(response) && response != null) {

                try {

                    jsonObject = new JSONObject(response);
                    JSONObject payloadObject = jsonObject.getJSONObject("payload");

                    if (payloadObject != null) {
                        task = payloadObject.getString("task");
                        secret = payloadObject.getString("secret");
                        if ((task.equals("send")) && (secret.equals(Prefrences.apiKey))) {
                            jsonArray = payloadObject.getJSONArray("messages");

                            for (int index = 0; index < jsonArray.length(); ++index) {
                                jsonObject = jsonArray.getJSONObject(index);

                                sendSms(context, jsonObject.getString("to"),
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

    /**
     * Sends messages received from the server as SMS.
     * 
     * @param Context context - the activity calling this method.
     * @param String response - the response from the server.
     */
    public static void sendResponseFromServer(Context context, String response) {
        Log.i(CLASS_TAG, "performResponseFromServer(): " + " response:" + response);

        if (!TextUtils.isEmpty(response) && response != null) {

            try {

                jsonObject = new JSONObject(response);
                JSONObject payloadObject = jsonObject.getJSONObject("payload");

                if (payloadObject != null) {

                    jsonArray = payloadObject.getJSONArray("messages");

                    for (int index = 0; index < jsonArray.length(); ++index) {
                        jsonObject = jsonArray.getJSONObject(index);
                        Log.i(CLASS_TAG, "Send sms: To: " + jsonObject.getString("to")
                                + "Message: " + jsonObject.getString("message"));

                        sendSms(context, jsonObject.getString("to"),
                                jsonObject.getString("message"));
                    }

                }
            } catch (JSONException e) {
                Log.i(CLASS_TAG, "Error: " + e.getMessage());
                showToast(context, R.string.no_task);
            }
        }

    }

    /**
     * Find words in a string
     * 
     * @param String message - The string to search by.
     * @param String keywords - The keywords to
     * @return boolean
     */
    public static boolean processString(String message, String[] keywords) {
        Log.i(CLASS_TAG, "processString(): find words in a string: " + message);
        /*
         * Scanner scanner = new Scanner(message); while (scanner.hasNext()) {
         * for (String keyword : keywords) { if
         * (scanner.nextLine().contentEquals(keyword)) { return true; } } }
         * return false;
         */

        for (int i = 0; i < keywords.length; i++) {
            if (message.contains(keywords[i])) {
                return true;
            }
        }

        return false;
    }

    /**
     * Import messages from messages app table and puts them in SmsSync's outbox
     * table.
     * 
     * @param Context context - the activity calling this method.
     * @return int - 0 for success, 1 for failure.
     */
    public static int importMessages(Context context) {
        Log.i(CLASS_TAG, "importMessages(): import messages from messages app");
        Uri uriSms = Uri.parse(SMS_CONTENT_INBOX);
        uriSms = uriSms.buildUpon().appendQueryParameter("LIMIT", "10").build();
        String[] projection = {
                "_id", "address", "date", "body"
        };
        String messageDate = "";
        Cursor c = context.getContentResolver().query(uriSms, projection, null, null, "date DESC");

        if (c.getCount() > 0 && c != null) {
            if (c.moveToFirst()) {

                do {

                    messageDate = String.valueOf(c.getLong(c.getColumnIndex("date")));
                    Util.smsMap.put("messagesFrom", c.getString(c.getColumnIndex("address")));
                    Util.smsMap.put("messagesBody", c.getString(c.getColumnIndex("body")));
                    Util.smsMap.put("messagesDate", messageDate);
                    Util.smsMap.put("messagesId", c.getString(c.getColumnIndex("_id")));
                    Util.processMessages(context);

                } while (c.moveToNext());
            }
            c.close();
            return 0;

        } else {
            return 1;
        }

    }

    /**
     * For debugging purposes. Append content of a string to a file
     * 
     * @param text
     */
    public static void appendLog(String text) {
        File logFile = new File(Environment.getExternalStorageDirectory(), "smssync.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            // BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static String getMyPhoneNumber(Context context) {
        TelephonyManager mTelephonyMgr;
        mTelephonyMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getLine1Number();
    }

    public static String getPhoneNumber(Context context) {
        String s = getMyPhoneNumber(context);
        return s.substring(2);
    }

    public static String formatDateTime(long milliseconds, String dateTimeFormat) {
        final Date date = new Date(milliseconds);
        try {
            if (date != null) {
                SimpleDateFormat submitFormat = new SimpleDateFormat(dateTimeFormat);
                return submitFormat.format(date);
            }
        } catch (IllegalArgumentException e) {
            return null;
        }
        return null;
    }
}
