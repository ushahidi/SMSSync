package org.addhen.smssync.controllers;

import org.addhen.smssync.R;
import org.addhen.smssync.messages.ProcessSms;
import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.net.MainHttpClient;
import org.addhen.smssync.util.ServicesConstants;
import org.addhen.smssync.util.Util;

import android.content.Context;
import android.telephony.SmsMessage;

/**
 * Created by Kamil Kalfas(kkalfas@soldevelo.com) on 17.06.14.
 */
public class DebugCallbacks {

    public static String isServerOKRequest(Context context, String requestRecipient) {
        SyncUrl model = new SyncUrl();
        int responseCode = 0;
        String message = "";
        for (SyncUrl syncUrl : model.loadByStatus(ServicesConstants.ACTIVE_SYNC_URL)) {
            MainHttpClient client = new MainHttpClient(syncUrl.getUrl(), context);
            try {
                client.execute();
                responseCode = client.getResponse().code();
            } catch (Exception e) {
                Util.logActivities(context, e.getMessage());
            }
            if (responseCode != 0) {
                message = message + context.getResources()
                        .getString(R.string.server_respond_message, syncUrl.getTitle(),
                                responseCode);
            } else {
                message = message + context.getResources()
                        .getString(R.string.unsuccessful_server_connection_message,
                                syncUrl.getTitle());
            }

        }
        return message;
    }


    public static String isCellReceptionOKRequest(Context context, String requestRecipient) {
        return context.getResources().getString(R.string.reception_ok_message);
    }


    public static String getBatteryLevelRequest(Context context, String requestRecipient) {
        return context.getResources()
                .getString(R.string.battery_level_message, Util.getBatteryLevel(context));
    }


    public static String getStatusRequest(Context context, String requestRecipient) {
        return isServerOKRequest(context, requestRecipient) + "\n" +
                getBatteryLevelRequest(context, requestRecipient) + "\n" +
                isCellReceptionOKRequest(context, requestRecipient);
    }


    public static boolean handleStatusMessage(final SmsMessage sms, final Context context) {
        boolean isStatusMessage = true;
        Runnable runnable = null;
        switch (sms.getMessageBody()) {
            case StatusSMS.CELL_RECEPTION_CODE:
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        new ProcessSms(context).sendSms(sms.getOriginatingAddress(),
                                isCellReceptionOKRequest(context, sms.getOriginatingAddress()));
                    }
                };
                break;
            case StatusSMS.SERVER_OK_CODE:
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        new ProcessSms(context).sendSms(sms.getOriginatingAddress(),
                                isServerOKRequest(context, sms.getOriginatingAddress()));
                    }
                };
                break;
            case StatusSMS.BATTERY_LEVEL_CODE:
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        new ProcessSms(context).sendSms(sms.getOriginatingAddress(),
                                getBatteryLevelRequest(context, sms.getOriginatingAddress()));
                    }
                };
                break;
            case StatusSMS.GET_STATUS_CODE:
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        new ProcessSms(context).sendSms(sms.getOriginatingAddress(),
                                getStatusRequest(context, sms.getOriginatingAddress()));
                    }
                };
                break;
            default:
                isStatusMessage = false;
                break;
        }
        if (runnable != null) {
            new Thread(runnable).start();
        }

        return isStatusMessage;
    }


    protected interface StatusSMS {

        static final String CELL_RECEPTION_CODE = "@10";
        static final String SERVER_OK_CODE = "@20";
        static final String BATTERY_LEVEL_CODE = "@30";
        static final String GET_STATUS_CODE = "@40";
    }
}
