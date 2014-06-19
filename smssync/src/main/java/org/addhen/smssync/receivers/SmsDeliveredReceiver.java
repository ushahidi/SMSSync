package org.addhen.smssync.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.format.DateFormat;

import org.addhen.smssync.MainApplication;
import org.addhen.smssync.Prefs;
import org.addhen.smssync.R;
import org.addhen.smssync.models.Message;
import org.addhen.smssync.util.LogUtil;
import org.addhen.smssync.util.ServicesConstants;

import static org.addhen.smssync.messages.ProcessSms.FAILED;
import static org.addhen.smssync.messages.ProcessSms.TASK;

/**
 * Created by Tomasz Stalka(tstalka@soldevelo.com) on 5/5/14.
 */
public class SmsDeliveredReceiver extends BaseBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int result = getResultCode();
        Bundle extras = intent.getExtras();
        Message message = (Message) extras.getSerializable(ServicesConstants.DELIVERED_SMS_BUNDLE);
        log("smsDeliveredReceiver onReceive result: " + result);
        String resultMessage = "";
        switch (result) {
            case Activity.RESULT_OK:
                resultMessage = context.getResources().getString(R.string.sms_delivered);
                toastLong(context.getResources().getString(R.string.sms_delivered), context);
                logActivities(context.getResources().getString(R.string.sms_delivered), context);
                break;
            case Activity.RESULT_CANCELED:
                resultMessage = context.getResources().getString(R.string.sms_not_delivered);
                toastLong(context.getResources().getString(R.string.sms_not_delivered), context);
                logActivities(context.getResources().getString(R.string.sms_not_delivered), context);
                break;
        }

        if (message != null) {
            message.setDeliveryResultMessage(resultMessage);
            message.setDeliveryResultCode(result);
            message.setMessageType(TASK);
            MainApplication.mDb.updateDeliveryResult(message);//update type, delivery result msg and code
        }
    }

}
