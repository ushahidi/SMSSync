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
import org.addhen.smssync.data.cache.FileManager;
import org.addhen.smssync.presentation.App;
import org.addhen.smssync.presentation.model.MessageModel;
import org.addhen.smssync.presentation.service.ServiceConstants;
import org.addhen.smssync.presentation.service.UpdateMessageService;
import org.addhen.smssync.smslib.sms.ProcessSms;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * @author Henry Addo
 */
public class SmsDeliveredReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int result = getResultCode();
        MessageModel message = (MessageModel) intent.getParcelableExtra(
                ProcessSms.DELIVERED_SMS_BUNDLE);
        FileManager fileManager = App.getAppComponent().fileManager();
        String resultMessage = "";
        switch (result) {
            case Activity.RESULT_OK:
                resultMessage = context.getResources().getString(R.string.sms_delivered);
                Toast.makeText(context, context.getResources().getString(R.string.sms_delivered),
                        Toast.LENGTH_LONG);
                fileManager.append(context.getResources().getString(
                        R.string.sms_delivered));
                break;
            case Activity.RESULT_CANCELED:
                resultMessage = context.getResources().getString(R.string.sms_not_delivered);
                Toast.makeText(context,
                        context.getResources().getString(R.string.sms_not_delivered),
                        Toast.LENGTH_LONG);
                fileManager.append(
                        context.getResources().getString(R.string.sms_not_delivered));
                break;
        }

        if (message != null) {
            message.setDeliveryResultMessage(resultMessage);
            message.setDeliveryResultCode(result);
            message.setMessageType(MessageModel.Type.TASK);
            message.setStatus(MessageModel.Status.SENT);

            // Update this in a service to guarantee it will run
            Intent updateService = new Intent(context, UpdateMessageService.class);
            updateService.putExtra(ServiceConstants.UPDATE_MESSAGE, message);
            context.startService(updateService);
        }
    }
}
