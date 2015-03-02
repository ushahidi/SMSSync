package org.addhen.smssync.receivers;

import org.addhen.smssync.App;
import org.addhen.smssync.R;
import org.addhen.smssync.database.BaseDatabseHelper;
import org.addhen.smssync.models.Message;
import org.addhen.smssync.util.Logger;
import org.addhen.smssync.util.ServicesConstants;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

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
