package org.addhen.smssync.controllers;

import android.content.Context;
import android.telephony.SmsMessage;

import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.net.MainHttpClient;
import org.addhen.smssync.util.Util;
import org.apache.http.HttpStatus;

/**
 * Created by Kamil Kalfas(kkalfas@soldevelo.com) on 09.06.14.
 */
public class DebugAndAlertsController implements DebugCallbacks, AlertCallbacks {

    private static Util mUtil;
    private static SyncUrl mSyncUrl;

    public DebugAndAlertsController() {
        this.mSyncUrl = new SyncUrl();
        this.mUtil = new Util();
    }

    @Override
    public void isServerOKRequest() {

    }

    @Override
    public void getBatteryLevelRequest() {

    }

    @Override
    public void getStatusRequest() {
        isServerOKRequest();
        getBatteryLevelRequest();
    }

    @Override
    public void lowBatteryLevelRequest() {
    }

    @Override
    public void smsSendFailedRequest(Context context) {
        MainHttpClient client = new MainHttpClient(mSyncUrl.getUrl(), context);
        try {
            client.setMethod("POST");
            client.execute();
        } catch (Exception e) {
            mUtil.log("");
        } finally {
            if (HttpStatus.SC_OK == client.getResponseCode()) {
                mUtil.log("");
            }
        }
    }

    @Override
    public void dataConnectionLost() {

    }

    public boolean isStatusMessage(SmsMessage sms) {
        boolean isStatusMessage = true;

        switch (sms.getMessageBody()) {
            case StatusSMS.CELL_RECEPTION:
                break;
            case StatusSMS.SERVER_OK:

                break;
            case StatusSMS.BATTERY_LEVEL:
                break;
            case StatusSMS.GET_STATUS:
                break;
            default:
                isStatusMessage = false;
                break;
        }
        return isStatusMessage;
    }

    protected interface StatusSMS {
        static final String CELL_RECEPTION ="Is cell reception ok";
        static final String SERVER_OK ="Is server ok";
        static final String BATTERY_LEVEL ="battery level";
        static final String GET_STATUS ="get status";
    }

}
