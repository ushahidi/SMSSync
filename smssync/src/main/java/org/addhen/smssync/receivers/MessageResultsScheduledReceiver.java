package org.addhen.smssync.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.addhen.smssync.services.MessageResultsScheduledService;
import org.addhen.smssync.services.SmsSyncServices;

/**
 * Created by Kamil Kalfas(kkalfas@soldevelo.com) on 05.05.14.
 */
public class MessageResultsScheduledReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SmsSyncServices.sendWakefulTask(context, MessageResultsScheduledService.class);
    }
}
