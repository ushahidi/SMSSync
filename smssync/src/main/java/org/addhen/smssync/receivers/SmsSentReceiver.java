/*
 * Copyright (c) 2010 - 2015 Ushahidi Inc
 * All rights reserved
 * Contact: team@ushahidi.com
 * Website: http://www.ushahidi.com
 * GNU Lesser General Public License Usage
 * This file may be used under the terms of the GNU Lesser
 * General Public License version 3 as published by the Free Software
 * Foundation and appearing in the file LICENSE.LGPL included in the
 * packaging of this file. Please review the following information to
 * ensure the GNU Lesser General Public License version 3 requirements
 * will be met: http://www.gnu.org/licenses/lgpl.html.
 *
 * If you have questions regarding the use of this file, please contact
 * Ushahidi developers at team@ushahidi.com.
 */

package org.addhen.smssync.receivers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;

import org.addhen.smssync.App;
import org.addhen.smssync.R;
import org.addhen.smssync.UiThread;
import org.addhen.smssync.database.BaseDatabseHelper;
import org.addhen.smssync.models.Message;
import org.addhen.smssync.prefs.Prefs;
import org.addhen.smssync.state.ReloadMessagesEvent;
import org.addhen.smssync.util.Logger;
import org.addhen.smssync.util.ServicesConstants;

/**
 * Created by Tomasz Stalka(tstalka@soldevelo.com) on 5/5/14.
 */
public class SmsSentReceiver extends BaseBroadcastReceiver {
    Message message = null;

    @Override
    public void onReceive(final Context context, Intent intent) {
        Bundle extras = intent.getExtras();

        if (extras != null) {
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
            Logger.log("Sent", "message sent " + message);
            if (sentSuccess) {
                message.setStatus(Message.Status.SENT);
                App.getDatabaseInstance().getMessageInstance().updateSentFields(message,
                        new BaseDatabseHelper.DatabaseCallback<Void>() {
                            @Override
                            public void onFinished(Void result) {
                                UiThread.getInstance().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        App.bus.post(new ReloadMessagesEvent());
                                    }
                                });
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
                if (prefs.enableRetry().get()) {
                    if (message.getRetries() >= prefs.retries().get()) {
                        Logger.log(SmsSentReceiver.class.getSimpleName(), "Delete failed messages " + message);
                        App.getDatabaseInstance().getMessageInstance().deleteByUuid(message.getUuid(), new BaseDatabseHelper.DatabaseCallback<Void>() {
                            @Override
                            public void onFinished(Void result) {
                                UiThread.getInstance().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Logger.log(SmsSentReceiver.class.getSimpleName(), "Failed message deleted ");
                                        App.bus.post(new ReloadMessagesEvent());
                                    }
                                });
                            }

                            @Override
                            public void onError(Exception exception) {

                            }
                        });
                    } else {
                        int retries = message.getRetries() + 1;
                        message.setRetries(retries);
                        message.setStatus(Message.Status.FAILED);
                        Logger.log(SmsSentReceiver.class.getSimpleName(), "update messages retries " + message);
                        App.getDatabaseInstance().getMessageInstance().updateSentFields(message,
                                new BaseDatabseHelper.DatabaseCallback<Void>() {
                                    @Override
                                    public void onFinished(Void result) {
                                        UiThread.getInstance().post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Logger.log(SmsSentReceiver.class.getSimpleName(), "update messages retries updated ");
                                                App.bus.post(new ReloadMessagesEvent());
                                            }
                                        });
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
}
