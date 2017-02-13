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

package org.addhen.smssync.presentation.receiver;

import org.addhen.smssync.R;
import org.addhen.smssync.data.PrefsFactory;
import org.addhen.smssync.data.cache.FileManager;
import org.addhen.smssync.data.util.Logger;
import org.addhen.smssync.presentation.App;
import org.addhen.smssync.presentation.model.MessageModel;
import org.addhen.smssync.presentation.service.DeleteMessageService;
import org.addhen.smssync.presentation.service.ServiceConstants;
import org.addhen.smssync.presentation.service.UpdateMessageService;
import org.addhen.smssync.smslib.sms.ProcessSms;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class SmsSentReceiver extends BroadcastReceiver {

    MessageModel messageModel = null;

    @Override
    public void onReceive(final Context context, Intent intent) {
        messageModel = (MessageModel) intent.getParcelableExtra(ProcessSms.SENT_SMS_BUNDLE);
        final int result = getResultCode();
        boolean sentSuccess = false;
        log("smsSentReceiver onReceive result: " + result);
        final String resultMessage;
        switch (result) {
            case 133404:
                /**
                 * HTC devices issue
                 * http://stackoverflow.com/questions/7526179/sms-manager-keeps-retrying-to-send-sms-on-htc-desire/7685238#7685238
                 */
                logActivities(context.getResources().getString(
                        R.string.sms_not_delivered_htc_device_retry));
                // This intentionally returns, while the rest below does break and more after.
                return;
            case Activity.RESULT_OK:
                resultMessage = context.getResources().getString(R.string.sms_status_success);
                sentSuccess = true;
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                resultMessage = context.getResources()
                        .getString(R.string.sms_delivery_status_failed);
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                resultMessage = context.getResources()
                        .getString(R.string.sms_delivery_status_no_service);
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                resultMessage = context.getResources()
                        .getString(R.string.sms_delivery_status_null_pdu);
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                resultMessage = context.getResources()
                        .getString(R.string.sms_delivery_status_radio_off);
                break;
            default:
                resultMessage = context.getResources()
                        .getString(R.string.sms_not_delivered_unknown_error);
                break;
        }
        toastLong(resultMessage, context);
        logActivities(resultMessage);

        if (messageModel != null) {
            messageModel.setSentResultMessage(resultMessage);
            messageModel.setSentResultCode(result);
            Logger.log("Sent", "message sent " + messageModel);
            if (sentSuccess) {
                messageModel.setStatus(MessageModel.Status.SENT);
                // Update this in a service to guarantee it will run
                Intent updateService = new Intent(context, UpdateMessageService.class);
                updateService.putExtra(ServiceConstants.UPDATE_MESSAGE, messageModel);
                context.startService(updateService);
            } else {

                PrefsFactory prefsFactory = App.getAppComponent().prefsFactory();
                if (prefsFactory.enableRetry().get()) {
                    if (messageModel.getRetries() >= prefsFactory.retries().get()) {
                        Logger.log(SmsSentReceiver.class.getSimpleName(),
                                "Delete failed messages " + messageModel);
                        Intent deleteService = new Intent(context, DeleteMessageService.class);
                        deleteService.putExtra(ServiceConstants.DELETE_MESSAGE,
                                messageModel.getMessageUuid());
                        context.startService(deleteService);
                    } else {
                        int retries = messageModel.getRetries() + 1;
                        messageModel.setRetries(retries);
                        messageModel.setStatus(MessageModel.Status.FAILED);
                        Logger.log(SmsSentReceiver.class.getSimpleName(),
                                "update message retries " + messageModel);
                        // Update this in a service to guarantee it will run
                        Intent updateService = new Intent(context, UpdateMessageService.class);
                        updateService.putExtra(ServiceConstants.UPDATE_MESSAGE, messageModel);
                        context.startService(updateService);
                    }
                }
            }
        }
    }

    private void toastLong(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    private void log(String message) {
        Logger.log(getClass().getName(), message);
    }

    private void logActivities(String message) {
        FileManager fileManager = App.getAppComponent().fileManager();
        fileManager.append(message);
    }
}
