package org.addhen.smssync.controllers;

import org.addhen.smssync.Prefs;
import org.addhen.smssync.R;
import org.addhen.smssync.messages.ProcessSms;
import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.net.MainHttpClient;
import org.addhen.smssync.util.ServicesConstants;
import org.addhen.smssync.util.Util;
import org.apache.http.HttpStatus;

import android.content.Context;

/**
 * Created by Kamil Kalfas(kkalfas@soldevelo.com) on 17.06.14.
 */
public class AlertCallbacks {

    public static final String METHOD_POST = "POST";

    public static final String TASK_PARAM = "Task";

    public static final String MESSAGE_PARAM = "message";

    public static Thread lostConnectionThread;

    public final static int MAX_DISCONNECT_TIME = 15000;

    /**
     * If battery level drops to low post alert to server send alert text to stored phone number
     */
    public static void lowBatteryLevelRequest(Context context) {
        int batteryLevel = Util.getBatteryLevel(context);
        SyncUrl model = new SyncUrl();
        for (SyncUrl syncUrl : model.loadByStatus(ServicesConstants.ACTIVE_SYNC_URL)) {
            if (syncUrl.getUrl() != null && !syncUrl.getUrl().equals("")) {
                MainHttpClient client = new MainHttpClient(syncUrl.getUrl(), context);
                try {
                    client.setMethod(METHOD_POST);
                    client.addParam(TASK_PARAM, "alert");
                    client.addParam(MESSAGE_PARAM, context.getResources()
                            .getString(R.string.battery_level_message, batteryLevel));
                    client.execute();
                } catch (Exception e) {
                    Util.logActivities(context, e.getMessage());
                } finally {
                    if (HttpStatus.SC_OK == client.getResponseCode()) {
                        Util.logActivities(context, context.getResources()
                                .getString(R.string.successful_alert_to_server));
                    }
                }
            }
        }

        if (!Prefs.alertPhoneNumber.matches("")) {
            new ProcessSms(context).sendSms(Prefs.alertPhoneNumber,
                    context.getResources().getString(R.string.battery_level_message, batteryLevel));
        }
    }

    /**
     * If an SMS fails to send (due to credit, cell coverage, or bad number) post alert to server
     */
    public static void smsSendFailedRequest(Context context, String resultMessage,
            String errorCode) {
        SyncUrl model = new SyncUrl();
        for (SyncUrl syncUrl : model.getSyncUrlList()) {
            if (syncUrl.getUrl() != null && !syncUrl.getUrl().equals("")) {
                MainHttpClient client = new MainHttpClient(syncUrl.getUrl(), context);
                try {
                    client.setMethod(METHOD_POST);
                    client.addParam(TASK_PARAM, "alert");
                    client.addParam(MESSAGE_PARAM, resultMessage);
                    if (!errorCode.matches("")) {
                        client.addParam("errorCode", errorCode);
                    }
                    client.execute();
                } catch (Exception e) {
                    Util.logActivities(context, e.getMessage());
                } finally {
                    if (HttpStatus.SC_OK == client.getResponseCode()) {
                        Util.logActivities(context, context.getResources()
                                .getString(R.string.successful_alert_to_server));
                    }
                }
            }
        }
    }

    /**
     * If data connection is lost for extended time (either WiFi, or GSM) send alert SMS to stored
     * phone number
     */
    public static void dataConnectionLost(Context context) {
        if (!Prefs.alertPhoneNumber.matches("")) {
            new ProcessSms(context).sendSms(Prefs.alertPhoneNumber,
                    context.getResources().getString(R.string.lost_connection_message));
        }
    }
}
