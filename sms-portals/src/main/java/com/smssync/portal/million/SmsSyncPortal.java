package com.smssync.portal.million;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.widget.Toast;

@SuppressLint({ "NewApi", "HandlerLeak", "HandlerLeak" })
public class SmsSyncPortal extends IntentService {

	public SmsSyncPortal() {
		super("SmsSyncPortal");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		//doing nothing currently
	}

	private void sendSMS(String sendTo, String msg) {
		Context context = this.getBaseContext();

		Toast.makeText(getApplicationContext(), "Portal 1 sending to "+sendTo, Toast.LENGTH_LONG).show();
		ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
		ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();

		SmsManager sms = SmsManager.getDefault();
		ArrayList<String> parts = sms.divideMessage(msg);
		for (int i = 0; i < parts.size(); i++) {
			PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0,
					new Intent("SMS_SENT"), 0);
			PendingIntent deliveryIntent = PendingIntent.getBroadcast(context,
					0, new Intent("SMS_DELIVERED"), 0);
			sentIntents.add(sentIntent);
			deliveryIntents.add(deliveryIntent);
		}
		if (PhoneNumberUtils.isGlobalPhoneNumber(sendTo))
			sms.sendMultipartTextMessage(sendTo, null, parts, sentIntents,
					deliveryIntents);
	}

	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
			String sendTo = data.getString("sendTo");
			String messageToSend = data.getString("msg");
			sendSMS(sendTo, messageToSend);
		}
	}

	/**
	 * Target we publish for clients to send messages to IncomingHandler.
	 */
	final Messenger mMessenger = new Messenger(new IncomingHandler());

	/**
	 * When binding to the service, we return an interface to our messenger for
	 * sending messages to the service.
	 */
	@Override
	public IBinder onBind(Intent intent) {
		Toast.makeText(getApplicationContext(), "Binding to Portal 1", Toast.LENGTH_SHORT)
				.show();
		return mMessenger.getBinder();
	}

}
