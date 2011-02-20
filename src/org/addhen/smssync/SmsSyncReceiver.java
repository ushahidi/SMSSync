package org.addhen.smssync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SmsSyncReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		intent.setClass(context, SmsSyncAutoSyncService.class);
		intent.putExtra("result", getResultCode());

		SmsReceiverService.beginStartingService(context, intent);
	}
}
