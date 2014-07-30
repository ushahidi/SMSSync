package org.addhen.smssync.controllers;

import android.telephony.SmsMessage;

/**
 * Created by Kamil Kalfas(kkalfas@soldevelo.com) on 17.06.14.
 */
public class DebugCallbacks {


    public static void isServerOKRequest(){

    }


    public static void isCellReceptionOKRequest(){

    }


    public static void getBatteryLevelRequest(){

    }


    public static void getStatusRequest() {
        isServerOKRequest();
        getBatteryLevelRequest();
        isCellReceptionOKRequest();
    }


    public static boolean isStatusMessage(SmsMessage sms) {
        boolean isStatusMessage = true;

        switch (sms.getMessageBody()) {
            case StatusSMS.CELL_RECEPTION_CODE:
                break;
            case StatusSMS.SERVER_OK_CODE:
                break;
            case StatusSMS.BATTERY_LEVEL_CODE:
                break;
            case StatusSMS.GET_STATUS_CODE:
                break;
            default:
                isStatusMessage = false;
                break;
        }
        return isStatusMessage;
    }


    protected interface StatusSMS {
        static final String CELL_RECEPTION_CODE ="@10";
        static final String SERVER_OK_CODE ="@20";
        static final String BATTERY_LEVEL_CODE ="@30";
        static final String GET_STATUS_CODE ="@40";
    }
}
