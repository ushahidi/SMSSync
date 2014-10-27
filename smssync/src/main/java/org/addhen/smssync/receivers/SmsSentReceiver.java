package org.addhen.smssync.receivers;

import org.addhen.smssync.MainApplication;
import org.addhen.smssync.R;
import org.addhen.smssync.controllers.AlertCallbacks;
import org.addhen.smssync.models.Message;
import org.addhen.smssync.util.ServicesConstants;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;

import static org.addhen.smssync.messages.ProcessSms.FAILED;
import static org.addhen.smssync.messages.ProcessSms.TASK;

/**
 * Created by Tomasz Stalka(tstalka@soldevelo.com) on 5/5/14.
 */
public class SmsSentReceiver extends BaseBroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        final Message message = (Message) extras.getSerializable(ServicesConstants.SENT_SMS_BUNDLE);
        final int result = getResultCode();
        Boolean sentSuccess = false;
        log("smsSentReceiver onReceive result: " + result);
        final String resultMessage;

        switch (result) {
            case 133404:
                /**
                 * HTC devices issue
                 * http://stackoverflow.com/questions/7526179/smsmanager-keeps-retrying-to-send-sms-on-htc-desire/7685238#7685238
                 */
                logActivities(context.getResources().getString(R.string.sms_not_delivered_htc_device_retry), context);
                // This intentionally returns, while the rest below does break and more after.
                return;
            case Activity.RESULT_OK:
                resultMessage = context.getResources().getString(R.string.sms_status_success);
                sentSuccess = true;
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                resultMessage = context.getResources().getString(R.string.sms_delivery_status_failed);
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                resultMessage = context.getResources().getString(R.string.sms_delivery_status_no_service);
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                resultMessage = context.getResources().getString(R.string.sms_delivery_status_null_pdu);
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                resultMessage = context.getResources().getString(R.string.sms_delivery_status_radio_off);
                break;
            default:
                resultMessage = context.getResources().getString(R.string.sms_not_delivered_unknown_error);
                break;
        }
        toastLong(resultMessage, context);
        logActivities(resultMessage, context);

        if (message != null) {
            message.setSentResultMessage(resultMessage);
            message.setSentResultCode(result);
            if (sentSuccess) {
                message.setMessageType(TASK);
                MainApplication.mDb
                        .updateSentResult(message); //update type, sent result msg and code
            } else {
                final String errorCode;
                if (intent.hasExtra("errorCode")) {
                    errorCode = intent.getStringExtra("errorCode");
                } else {
                    errorCode = "";
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AlertCallbacks.smsSendFailedRequest(context, resultMessage, errorCode);
                    }
                }).start();

                message.setMessageType(FAILED);
                message.save();// save message into pending tray
                MainApplication.mDb.deleteSentMessagesByUuid(message.getUuid());
            }
        }

    }
}
