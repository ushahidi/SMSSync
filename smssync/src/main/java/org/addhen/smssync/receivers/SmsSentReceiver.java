package org.addhen.smssync.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;

import org.addhen.smssync.MainApplication;
import org.addhen.smssync.R;
import org.addhen.smssync.models.Message;
import org.addhen.smssync.util.ServicesConstants;

import static org.addhen.smssync.messages.ProcessSms.FAILED;
import static org.addhen.smssync.messages.ProcessSms.TASK;

/**
 * Created by Tomasz Stalka(tstalka@soldevelo.com) on 5/5/14.
 */
public class SmsSentReceiver extends BaseBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        Message message = (Message) extras.getSerializable(ServicesConstants.SENT_SMS_BUNDLE);
        int result = getResultCode();
        Boolean sentSuccess = false;
        log("smsSentReceiver onReceive result: " + result);
        String resultMessage = "";

        switch (result) {
            case Activity.RESULT_OK:
                resultMessage = context.getResources().getString(R.string.sms_status_success);
                toastLong(context.getResources().getString(R.string.sms_status_success), context);
                logActivities(context.getResources().getString(R.string.sms_status_success), context);
                sentSuccess = true;
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                resultMessage = context.getResources().getString(R.string.sms_delivery_status_failed);
                toastLong(context.getResources().getString(R.string.sms_delivery_status_failed), context);
                logActivities(context.getResources().getString(R.string.sms_delivery_status_failed), context);
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                resultMessage = context.getResources().getString(R.string.sms_delivery_status_no_service);
                toastLong(context.getResources().getString(R.string.sms_delivery_status_no_service), context);
                logActivities(context.getResources().getString(R.string.sms_delivery_status_no_service), context);
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                resultMessage = context.getResources().getString(R.string.sms_delivery_status_null_pdu);
                toastLong(context.getResources().getString(R.string.sms_delivery_status_null_pdu), context);
                logActivities(context.getResources().getString(R.string.sms_delivery_status_null_pdu), context);
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                resultMessage = context.getResources().getString(R.string.sms_delivery_status_radio_off);
                toastLong(context.getResources().getString(R.string.sms_delivery_status_radio_off), context);
                logActivities(context.getResources().getString(R.string.sms_delivery_status_radio_off), context);
                break;
        }


        if (message != null) {
            message.setSentResultMessage(resultMessage);
            message.setSentResultCode(result);
            if (sentSuccess) {
                message.setMessageType(TASK);
                MainApplication.mDb.updateSentResult(message); //update type, sent result msg and code
            } else {
                message.setMessageType(FAILED);
                message.save();// save message into pending tray
                MainApplication.mDb.deleteSentMessagesByUuid(message.getUuid());
            }
        }

    }
}
