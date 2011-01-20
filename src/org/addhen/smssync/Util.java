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

package org.addhen.smssync;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.app.NotificationManager;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.addhen.smssync.data.Messages;
import org.json.JSONException;
import org.json.JSONObject;


public class Util{

	private static NetworkInfo networkInfo;
	private static List<Messages> mMessages;
	private static JSONObject jsonObject;
	private static Pattern pattern;
	private static Matcher matcher;
	public static final Uri MMS_SMS_CONTENT_URI = Uri.parse("content://mms-sms/");
	public static final Uri THREAD_ID_CONTENT_URI =
        Uri.withAppendedPath(MMS_SMS_CONTENT_URI, "threadID");
	public static final Uri CONVERSATION_CONTENT_URI =
        Uri.withAppendedPath(MMS_SMS_CONTENT_URI, "conversations");
	public static final String SMS_CONTENT_URI = "content://sms/conversations/";
	public static final int NOTIFICATION_ALERT = 1337;
	public static final String SMS_ID = "_id";
	public static final String SMS_CONTENT_INBOX = "content://sms/inbox";
	public static final int READ_THREAD = 1;
	public static HashMap<String,String> smsMap = new HashMap<String,String>();
	private static final String TIME_FORMAT_12_HOUR = "h:mm a";
    private static final String TIME_FORMAT_24_HOUR = "H:mm";
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@" +
			"[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	/**
	 * joins two strings together
	 * @param first
	 * @param second
	 * @return
	 */
	public static String joinString(String first, String second ) {
		return first.concat(second);
	}
	
	/**
	 * Converts a string integer 
	 * @param value
	 * @return
	 */
	public static int toInt( String value){
		return Integer.parseInt(value);
	}
	
	/**
	 * Capitalize any string given to it.
	 * @param text
	 * @return capitalized string
	 */
	public static String capitalizeString( String text ) {
		return text.substring(0,1).toUpperCase() + text.substring(1);
	}
	
	/**
	 * Create csv
	 * @param Vector<String> text
	 * 
	 * @return csv
	 */
	public static String implode( Vector<String> text ) {
		String implode = "";
		int i = 0;
		for( String value : text ) {
			implode += i == text.size() -1 ? value : value+",";
			i++;
		}
		
		return implode;
	}
	
	/**
	 * Is there internet connection
	 */
	public static boolean isConnected(Context context )  {
		  
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		networkInfo = connectivity.getActiveNetworkInfo();
		//NetworkInfo info
		
		if(networkInfo == null || !networkInfo.isConnected()){  
	        return false;  
	    } 
	    return true; 
	     
	}
	
	/**
	 * Limit a string to defined length
	 * 
	 * @param int limit - the total length 
	 * @param string limited - the limited string
	 */
	public static String limitString( String value, int length ) {
		StringBuilder buf = new StringBuilder(value);
		if( buf.length() > length ) {
			buf.setLength(length);
			buf.append(" ...");
		}
		return buf.toString();
	}
	
	/**
	 * Format date into more readable format.
	 * 
	 * @param  date - the date to be formatted.
	 * @return String
	 */
	public static String formatDate( String dateFormat, String date, String toFormat ) {
	
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
	 * @apram json_data - the json data to be formatted.
	 * @return String 
	 */
	public static boolean extractPayloadJSON( String json_data ) {
	
		try {
			jsonObject = new JSONObject(json_data);
			return jsonObject.getJSONObject("payload").getBoolean("success");
		
		} catch (JSONException e) {
			return false;
			//e.printStackTrace();
		}
		
	}
	
	/**
	 * process reports
	 * 0 - successful
	 * 1 - failed fetching categories
	 * 2 - failed fetching reports
	 * 3 - non ushahidi instance
	 * 4 - No internet connection
	 * 
	 * @return int - status
	 */
	public static int processMessages( Context context ) {
		
		if( Util.isConnected(context)) {
			List<Messages> listMessages = new ArrayList<Messages>();
			
			Messages messages = new Messages();
			listMessages.add(messages);
			int messageId = Integer.parseInt(smsMap.get("messageId"));
			messages.setMessageId(messageId);
			messages.setMessageFrom(smsMap.get("messageFrom"));
			messages.setMessageBody(smsMap.get("messageBody"));
			messages.setMessageDate(smsMap.get("messageDate"));
			mMessages = listMessages;
			
			if(mMessages != null) {
				 SmsSyncApplication.mDb.addMessages(mMessages);
				 return 0;
			 
			 } else {
				 return 1;
			 }
			  

		} else {
			return 4;
		}
	}
	
	/**
	 * Show toast
	 * 
	 * @param Context - the application's context
	 * @param Int - string resource id
	 * 
	 * @return void
	 */
	public static void showToast(Context context, int i ) {
		int duration = Toast.LENGTH_SHORT;
		Toast.makeText(context, i, duration).show();
	}
	
	/**
	 * Validates an email address
	 * Credits: http://www.mkyong.com/regular-expressions/how-to-validate-email-address-with-regular-expression/
	 * 
	 * @param String - email address to be validated
	 * 
	 * @return boolean
	 */
	public static boolean validateEmail( String emailAddress) {
		if( !emailAddress.equals("") ) {
			pattern = Pattern.compile(EMAIL_PATTERN);
			matcher = pattern.matcher(emailAddress);
			return matcher.matches();
		}
		return true;
	}
	
	/**
	 * Validate an Ushahidi instance
	 * 
	 * @param String - URL to be validated.
	 * 
	 * @return boolean
	 */
	public static boolean validateUshahidiInstance( String ushahidiUrl ) {
		//make an http get request to a dummy api call
		//TODO improve on how to do this
		boolean status = false;
		try {
		    URL url = new URL(ushahidiUrl);
		    URLConnection conn = url.openConnection();
		    conn.connect();
		    status = true;
		} catch (MalformedURLException e) {
		    status = false;
		} catch (IOException e) {
		    status = true;
		}

		return status;
	}
	
	/**
     * Tries to locate the message id (from the system database), given the message
     * thread id, the timestamp of the message.
     */
    public static long findMessageId(Context context, long threadId, long _timestamp) {
    	long id = 0;
    	long timestamp = _timestamp;
    	if (threadId > 0) {
                    
    		Cursor cursor = context.getContentResolver().query(
    				ContentUris.withAppendedId(CONVERSATION_CONTENT_URI, threadId),
    				new String[] { "_id", "date", "thread_id" },
    				//"thread_id=" + threadId + " and " + "date=" + timestamp,
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
     * Tries to locate the message thread id given the address (phone or email) of the
     * message sender
     */
    public static long getMessageId(Context context, SmsMessage msg) {
    	long messageId = 0;
    
		Uri uriSms = Uri.parse(SMS_CONTENT_INBOX);
				
		StringBuilder sb = new StringBuilder();
		sb.append("address='" + msg.getOriginatingAddress() + "' AND ");
		sb.append("body='" + msg.getMessageBody() + "'");
		Cursor c = context.getContentResolver().query(uriSms, null, sb.toString(), null, null);
		
		if(c.getCount() > 0 && c != null ) {
			c.moveToFirst();
			messageId = c.getLong(c.getColumnIndex("_id"));
			c.close();
		}
		
    	return messageId;
    }
    
    /**
     * Tries to locate the message thread id given the address (phone or email) of the
     * message sender
     */
    public static long getThreadId(Context context, SmsMessage msg) {
    	long threadId = 0;
    
		Uri uriSms = Uri.parse(SMS_CONTENT_INBOX);
				
		StringBuilder sb = new StringBuilder();
		sb.append("address='" + msg.getOriginatingAddress() + "' AND ");
		sb.append("body='" + msg.getMessageBody() + "'");
		Cursor c = context.getContentResolver().query(uriSms, null, sb.toString(), null, null);
		
		if(c.getCount() > 0 && c != null ) {
			c.moveToFirst();
			threadId = c.getLong(c.getColumnIndex("thread_id"));
			c.close();
		}
		
    	return threadId;
    }
    
    // Clear the standard notification alert
    public static void clear(Context context) {
    	clearAll(context);
    }

    // Clear a single notification
    public static void clearAll(Context context) {
    	NotificationManager myNM =
    		(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    	myNM.cancelAll();
    }


	
    /*
     * Format a unix timestamp to a string suitable for display to the user according
     * to their system settings (12 or 24 hour time)
     */
    public static String formatTimestamp(Context context, long timestamp) {
            String HOURS_24 = "24";
            String hours;
            hours = "24";
            
            SimpleDateFormat mSDF = new SimpleDateFormat();
            if (HOURS_24.equals(hours)) {
                    mSDF.applyLocalizedPattern(TIME_FORMAT_24_HOUR);
            } else {
                    mSDF.applyLocalizedPattern(TIME_FORMAT_12_HOUR);
            }
            return mSDF.format(new Date(timestamp));
    }
    
    public static void delSmsFromInbox(Context context, SmsMessage msg) {   	
    	long threadId = getThreadId(context, msg);
    	if( threadId >= 0 ) {
    		context.getContentResolver().delete(Uri.parse("content://sms/conversations/" + threadId), null, null);
    	}
    }
    
}
