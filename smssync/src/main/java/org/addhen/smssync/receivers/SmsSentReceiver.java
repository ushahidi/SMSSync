package org.addhen.smssync.receivers;

import org.addhen.smssync.App;
import org.addhen.smssync.R;
import org.addhen.smssync.controllers.AlertCallbacks;
import org.addhen.smssync.database.BaseDatabseHelper;
import org.addhen.smssync.models.Message;
import org.addhen.smssync.prefs.Prefs;
import org.addhen.smssync.util.Logger;
import org.addhen.smssync.util.ServicesConstants;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;

/**
 * Created by Tomasz Stalka(tstalka@soldevelo.com) on 5/5/14.
 */
public class SmsSentReceiver extends BaseBroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        Message message = null;
        if(extras != null) {
            message = (Message) extras.getSerializable(ServicesConstants.SENT_SMS_BUNDLE);
        }
        final int result = getResultCode();
        boolean sentSuccess = false;
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
            Logger.log("Sent", "message sent "+message);
            if (sentSuccess) {
                message.setStatus(Message.Status.SENT);
                App.getDatabaseInstance().getMessageInstance().updateSentFields(message,
                        new BaseDatabseHelper.DatabaseCallback<Void>() {
                            @Override
                            public void onFinished(Void result) {
                                // Save details to sent inbox
                            }

                            @Override
                            public void onError(Exception exception) {

                            }
                        });

            } else {
                //TODO: Renable if alert is made configurable.
                /*
                final String errorCode;
                final AlertCallbacks alertCallbacks = new AlertCallbacks(new Prefs(context));
                if (intent.hasExtra("errorCode")) {
                    errorCode = intent.getStringExtra("errorCode");
                } else {
                    errorCode = "";
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        alertCallbacks.smsSendFailedRequest(resultMessage, errorCode);
                    }
                }).start();*/

                Prefs prefs = new Prefs(context);
                boolean deleted = false;

                Logger.log(SmsSentReceiver.class.getSimpleName(), "Statuses: "+prefs.enableRetry().get());
                if (prefs.enableRetry().get()) {
                    final int retry = prefs.retries().get();
                    if (message.getRetries() > retry) {
                        App.getDatabaseInstance().getMessageInstance().deleteByUuid(message.getUuid());
                        // Mark message as deleted so it's not updated
                        deleted = true;
                    } else {
                        int retries = message.getRetries() + 1;
                        message.setRetries(retries);
                    }
                }

                // Make sure the message is not deleted before attempting to update it retries status;
                if (!deleted) {
                    message.setStatus(Message.Status.FAILED);
                Logger.log(SmsSentReceiver.class.getSimpleName(), "messages "+message);
                    App.getDatabaseInstance().getMessageInstance().updateSentFields(message,
                            new BaseDatabseHelper.DatabaseCallback<Void>() {
                                @Override
                                public void onFinished(Void result) {

                                }

                                @Override
                                public void onError(Exception exception) {

                                }
                            });
                }
            }
        }

    }
}
