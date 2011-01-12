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

import java.util.HashMap;
import java.util.List;

import org.addhen.smssync.net.SmsSyncHttpClient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.provider.Settings;
import android.telephony.SmsMessage;

public class SmsReceiverService extends Service {
	private static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	
	private ServiceHandler mServiceHandler;
	private Looper mServiceLooper;
	private String fromAddress = "";
    private String messageBody = "";
	private static final Object mStartingServiceSync = new Object();
	private static PowerManager.WakeLock mStartingService;
	private HashMap<String,String> params = new HashMap<String, String>();
	private LocationManager locationManager;
	public double latitude;
	public double longitude;

	@Override
	public void onCreate() {
	    SmsSyncPref.loadPreferences(this);
	    HandlerThread thread = new HandlerThread("Message sending starts", Process.THREAD_PRIORITY_BACKGROUND);
	    thread.start();
	    getApplicationContext();
	    mServiceLooper = thread.getLooper();
	    mServiceHandler = new ServiceHandler(mServiceLooper);
	    locationManager = (LocationManager) 
	    getSystemService(Context.LOCATION_SERVICE);
	}

	@Override
	public void onStart(Intent intent, int startId) {
	    
	    Message msg = mServiceHandler.obtainMessage();
	    msg.arg1 = startId;
	    msg.obj = intent;
	    mServiceHandler.sendMessage(msg);
	}

	@Override
	public void onDestroy() {
	    
		mServiceLooper.quit();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
	    }

		@Override
	    public void handleMessage(Message msg) {
			
			int serviceId = msg.arg1;
			Intent intent = (Intent) msg.obj;
			String action = intent.getAction();
			
			if (ACTION_SMS_RECEIVED.equals(action)) {
				handleSmsReceived(intent);
			} 

			finishStartingService(SmsReceiverService.this, serviceId);
	    }
	}

	/**
	 * Handle receiving a SMS message
	 */
	private void handleSmsReceived(Intent intent) {
		
	    Bundle bundle = intent.getExtras();
	    if (bundle != null) {
	    	SmsMessage[] messages = getMessagesFromIntent(intent);
	    	SmsMessage sms = messages[0];
	    	if (messages != null) {
	    		//extract message details phone number and the message body
	    		fromAddress = sms.getDisplayOriginatingAddress();
	    		
	    		String body;
	    		if (messages.length == 1 || sms.isReplace()) {
	    			body = sms.getDisplayMessageBody();
	    		} else {
	    			StringBuilder bodyText = new StringBuilder();
	    			for (int i = 0; i < messages.length; i++) {
	    				bodyText.append(messages[i].getMessageBody());
	    			}
	    			body = bodyText.toString();
	    		}
	    		messageBody = body;
	    	}
	    }
	    
	    if( SmsSyncPref.enabled ) {
	    	
	    	if( SmsSyncUtil.isConnected(SmsReceiverService.this) ){
	    		// if keywoard is enabled
	    		if(!SmsSyncPref.keyword.equals("")){
	    			String [] keywords = SmsSyncPref.keyword.split(",");
	    			if( SmsSyncUtil.processString(messageBody, keywords)){
	    				if( !this.postToAWebService() ) {
		    				this.showNotification(messageBody, "SMSSync Message Sending failed.");
		    			}else {
		    				this.showNotification(messageBody, "SMSSync Message Sent.");
		    			}
	    			}
	    		// keyword is not enabled
	    		} else {
	    			if( !this.postToAWebService() ) {
	    				this.showNotification(messageBody, "SMSSync Message Sending failed.");
	    			}else {
	    				this.showNotification(messageBody, "SMSSync Message Sent.");
	    			}
	    		}
	    	}
	    }
	}
	
	/**
	 * Show a notification
	 * 
	 * @param String message to display
	 * @param String notification title
	 */
	private void showNotification(String message, String notification_title ) {
		
		Intent baseIntent = new Intent(this, Settings.class);
        baseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		NotificationManager notificationManager =
		    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.favicon, "Status", System.currentTimeMillis());
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, baseIntent, 0);
		notification.setLatestEventInfo(this, notification_title, message, pendingIntent);
		notificationManager.notify(1, notification);
	}

	/**
	 * Posts received sms to a configured web service.
	 * 
	 * @return
	 */
	private boolean postToAWebService() {
			
		StringBuilder urlBuilder = new StringBuilder(SmsSyncPref.website);
    	params.put("secret",SmsSyncPref.apiKey);
		params.put("from", fromAddress); 
		params.put("message",messageBody);
		return SmsSyncHttpClient.postSmsToWebService(urlBuilder.toString(), params);
		
	}

	public static final SmsMessage[] getMessagesFromIntent(Intent intent) {
		Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
	    if (messages == null) {
	    	return null;
	    }
	    if (messages.length == 0) {
	    	return null;
	    }

	    byte[][] pduObjs = new byte[messages.length][];

	    for (int i = 0; i < messages.length; i++) {
	      pduObjs[i] = (byte[]) messages[i];
	    }
	    byte[][] pdus = new byte[pduObjs.length][];
	    int pduCount = pdus.length;
	    SmsMessage[] msgs = new SmsMessage[pduCount];
	    for (int i = 0; i < pduCount; i++) {
	    	pdus[i] = pduObjs[i];
	    	msgs[i] = SmsMessage.createFromPdu(pdus[i]);
	    }
	    return msgs;
	}

	  
	/**
	 * Start the service to process the current event notifications, acquiring the
	 * wake lock before returning to ensure that the service will run.
	 */
	public static void beginStartingService(Context context, Intent intent) {
		synchronized (mStartingServiceSync) {
	      
			if (mStartingService == null) {
				PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
				mStartingService = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
	            	"Sms messages .SmsReceiverService");
				mStartingService.setReferenceCounted(false);
			}
	      
			mStartingService.acquire();
			context.startService(intent);
		}
	}
	
	private void delSmsFromInbox(SmsMessage msg) {

		try {
			Uri uriSms = Uri.parse("content://sms/inbox");
			int threadId;	
			StringBuilder sb = new StringBuilder();
			sb.append("address='" + msg.getOriginatingAddress() + "' AND ");
			sb.append("body='" + msg.getMessageBody() + "'");
			Cursor c = getContentResolver().query(uriSms, null, sb.toString(), null, null);
			c.moveToFirst();
			threadId= c.getInt(1);
			getContentResolver().delete(Uri.parse("content://sms/conversations/" + threadId), null, null);
			c.close();
		} catch (Exception ex) {
			
		}
	}

	/**
	 * Called back by the service when it has finished processing notifications,
	 * releasing the wake lock if the service is now stopping.
	 */
	public static void finishStartingService(Service service, int startId) {
		synchronized (mStartingServiceSync) {
	      
			if (mStartingService != null) {
	      		if (service.stopSelfResult(startId)) {
	      			mStartingService.release();
	      		}
	      	}
		}
	}
	
	//update the device current location
	private void updateLocation() {
		MyLocationListener listener = new MyLocationListener(); 
        LocationManager manager = (LocationManager) 
        	getSystemService(Context.LOCATION_SERVICE); 
        long updateTimeMsec = 1000L; 
        
        //DIPO Fix
        List<String> providers = manager.getProviders(true);
        boolean gps_provider = false, network_provider = false;
        
        for (String name : providers) {
        	if (name.equals(LocationManager.GPS_PROVIDER)) gps_provider = true;
        	if (name.equals(LocationManager.NETWORK_PROVIDER)) network_provider = true;        	
        }
        
        //Register for GPS location if enabled or if neither is enabled
        if( gps_provider || (!gps_provider && !network_provider) ) {
			manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
   updateTimeMsec, 500.0f, 
		    listener);
		} else if (network_provider) {
			manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
   updateTimeMsec, 500.0f, 
		    listener); 
		}
        
	}
	
	// get the current location of the user
	public class MyLocationListener implements LocationListener {
		
	    public void onLocationChanged(Location location) { 
	    	
	    	if (location != null) { 
	    		locationManager.removeUpdates(this);      
	    		latitude = location.getLatitude(); 
	  	        longitude = location.getLongitude(); 
	  	        
	  	    }	     
	    }
	    
	    public void onProviderDisabled(String provider) { 
	    	
	    }
	    
	    public void onProviderEnabled(String provider) { 
	   
	    }
	    
	    public void onStatusChanged(String provider, int status, Bundle extras) { 
	      
	    } 
	}
	
}
