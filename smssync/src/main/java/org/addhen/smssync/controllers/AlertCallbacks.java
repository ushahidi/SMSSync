package org.addhen.smssync.controllers;

import android.content.Context;

/**
 * Created by Kamil Kalfas(kkalfas@soldevelo.com) on 17.06.14.
 */
public interface AlertCallbacks {
    /**
     * If battery level drops to low
     * post alert to server
     * send alert text to stored phone number
     */
    public void lowBatteryLevelRequest();

    /**
     * If an SMS fails to send (due to credit, cell coverage, or bad number)
     * post alert to server
     * @param context
     */
    public void smsSendFailedRequest(Context context);

    /**
     * If data connection is lost for extended time (either WiFi, or GSM)
     * send alert SMS to stored phone number
     */
    public void dataConnectionLost();
}
