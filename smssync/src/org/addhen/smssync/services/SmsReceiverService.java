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

package org.addhen.smssync.services;

import org.addhen.smssync.Prefs;
import org.addhen.smssync.ProcessSms;
import org.addhen.smssync.fragments.PendingMessages;
import org.addhen.smssync.util.Logger;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.telephony.SmsMessage;

public class SmsReceiverService extends Service {
	private static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

	private ServiceHandler mServiceHandler;

	private Looper mServiceLooper;

	private Context mContext;

	private String messagesFrom = "";

	private String messagesBody = "";

	private String messagesTimestamp = "";

	private String messagesId = "";

	private static final Object mStartingServiceSync = new Object();

	private static PowerManager.WakeLock mStartingService;

	private static WifiManager.WifiLock wifilock;

	private SmsMessage sms;

	private static final String CLASS_TAG = SmsReceiverService.class
			.getSimpleName();

	private ProcessSms processSms;

	synchronized protected static WifiManager.WifiLock getWifiLock(
			Context context) {
		// keep wifi alive
		if (wifilock == null) {
			WifiManager manager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			wifilock = manager.createWifiLock(CLASS_TAG);
			wifilock.setReferenceCounted(true);
		}
		return wifilock;
	}

	@Override
	public void onCreate() {

		HandlerThread thread = new HandlerThread(CLASS_TAG,
				Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();
		mContext = getApplicationContext();
		processSms = new ProcessSms(mContext);

		Prefs.loadPreferences(mContext);

		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);

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
			if (intent != null) {
				String action = intent.getAction();

				if (ACTION_SMS_RECEIVED.equals(action)) {
					handleSmsReceived(intent);
				}
			}
			finishStartingService(SmsReceiverService.this, serviceId);
		}
	}

	/**
	 * Handle receiving SMS message
	 */
	private void handleSmsReceived(Intent intent) {

		String body;
		Bundle bundle = intent.getExtras();
		Prefs.loadPreferences(SmsReceiverService.this);

		if (bundle != null) {
			SmsMessage[] messages = getMessagesFromIntent(intent);
			sms = messages[0];
			if (messages != null) {
				// extract message details. phone number and the message body
				messagesFrom = sms.getOriginatingAddress();
				messagesTimestamp = String.valueOf(sms.getTimestampMillis());

				if (messages.length == 1 || sms.isReplace()) {
					body = sms.getDisplayMessageBody();

				} else {
					StringBuilder bodyText = new StringBuilder();
					for (int i = 0; i < messages.length; i++) {
						bodyText.append(messages[i].getMessageBody());
					}
					body = bodyText.toString();
				}
				messagesBody = body;
				messagesId = String.valueOf(processSms.getId(messagesBody,
						messagesFrom, "id"));
			}
		}

		// route the sms
		processSms.routeSms(messagesFrom, messagesBody, messagesTimestamp,
				messagesId, sms);

	}

	/**
	 * Get the SMS message.
	 * 
	 * @param Intent
	 *            intent - The SMS message intent.
	 * @return SmsMessage
	 */
	public static final SmsMessage[] getMessagesFromIntent(Intent intent) {

		new SmsReceiverService()
				.log("getMessagesFromIntent(): getting SMS message");

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
	 * Start the service to process the current event notifications, acquiring
	 * the wake lock before returning to ensure that the service will run.
	 * 
	 * @param Context
	 *            context - The context of the calling activity.
	 * @param Intent
	 *            intent - The calling intent.
	 * @return void
	 */
	public static void beginStartingService(Context context, Intent intent) {
		synchronized (mStartingServiceSync) {

			if (mStartingService == null) {
				PowerManager pm = (PowerManager) context
						.getSystemService(Context.POWER_SERVICE);
				mStartingService = pm.newWakeLock(
						PowerManager.PARTIAL_WAKE_LOCK, CLASS_TAG);
				mStartingService.setReferenceCounted(false);
			}

			mStartingService.acquire();
			if (!getWifiLock(context).isHeld())
				getWifiLock(context).acquire();
			context.startService(intent);
		}
	}

	/**
	 * Called back by the service when it has finished processing notifications,
	 * releasing the wake lock and wifi lock if the service is now stopping.
	 * 
	 * @param Service
	 *            service - The calling service.
	 * @param int startId - The service start id.
	 * @return void
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

	// Display pending messages.
	final Runnable mDisplayMessages = new Runnable() {

		public void run() {
			new PendingMessages().showMessages();
		}

	};

	// Display pending messages.
	final Runnable mDisplaySentMessages = new Runnable() {

		public void run() {
			// SentMessagesActivity.showMessages();
		}

	};

	protected void log(String message) {
		Logger.log(getClass().getName(), message);
	}

	protected void log(String format, Object... args) {
		Logger.log(getClass().getName(), format, args);
	}

	protected void log(String message, Exception ex) {
		Logger.log(getClass().getName(), message, ex);
	}

}
