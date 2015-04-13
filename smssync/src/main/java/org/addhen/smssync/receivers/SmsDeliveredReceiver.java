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

import org.addhen.smssync.App;
import org.addhen.smssync.R;
import org.addhen.smssync.database.BaseDatabseHelper;
import org.addhen.smssync.models.Message;
import org.addhen.smssync.util.ServicesConstants;

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
                logActivities(context.getResources().getString(R.string.sms_not_delivered),
                        context);
                break;
        }


        if (message != null) {
            message.setDeliveryResultMessage(resultMessage);
            message.setDeliveryResultCode(result);
            message.setType(Message.Type.TASK);
            message.setStatus(Message.Status.SENT);
            App.getDatabaseInstance().getMessageInstance().updateDeliveryFields(message,
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
