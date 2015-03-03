package org.addhen.smssync.controllers;

import org.addhen.smssync.App;
import org.addhen.smssync.R;
import org.addhen.smssync.messages.ProcessSms;
import org.addhen.smssync.models.Message;
import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.net.HttpMethod;
import org.addhen.smssync.net.MainHttpClient;
import org.addhen.smssync.prefs.Prefs;
import org.addhen.smssync.util.Util;
import org.apache.http.HttpStatus;

import java.util.Date;
import java.util.List;

/**
 * Created by Kamil Kalfas(kkalfas@soldevelo.com) on 17.06.14.
 */
public class AlertCallbacks {

    public static final String TASK_PARAM = "Task";

    public static final String MESSAGE_PARAM = "message";

    public final static int MAX_DISCONNECT_TIME = 15000;

    public Thread lostConnectionThread;

    private Prefs prefs;

    public AlertCallbacks(Prefs prefs) {
        this.prefs = prefs;
    }

    /**
     * If battery level drops to low post alert to server send alert text to stored phone number
     */
    public void lowBatteryLevelRequest(int batteryLevel) {

        List<SyncUrl> syncUrls = App.getDatabaseInstance().getSyncUrlInstance().fetchSyncUrlByStatus(
                SyncUrl.Status.ENABLED);
        for (SyncUrl syncUrl : syncUrls) {
            if (syncUrl.getUrl() != null && !syncUrl.getUrl().equals("")) {
                MainHttpClient client = new MainHttpClient(syncUrl.getUrl(), prefs.getContext());
                try {
                    client.setMethod(HttpMethod.POST);
                    client.addParam(TASK_PARAM, "alert");
                    client.addParam(MESSAGE_PARAM, prefs.getContext().getResources()
                            .getString(R.string.battery_level_message, batteryLevel));
                    client.execute();
                } catch (Exception e) {
                    Util.logActivities(prefs.getContext(), e.getMessage());
                } finally {
                    if(client !=null) {
                        if (HttpStatus.SC_OK == client.responseCode()) {
                            Util.logActivities(prefs.getContext(), prefs.getContext().getResources()
                                    .getString(R.string.successful_alert_to_server));
                        }
                    }
                }
            }
        }

        if (!prefs.alertPhoneNumber().get().matches("")) {
            ProcessSms process = new ProcessSms(prefs.getContext());
            final Long timeMills = System.currentTimeMillis();
            Message message = new Message();
            message.setBody( prefs.getContext().getResources()
                    .getString(R.string.battery_level_message, batteryLevel));
            message.setDate(new Date(timeMills));
            message.setPhoneNumber(prefs.alertPhoneNumber().get());
            message.setUuid(process.getUuid());
            message.setType(Message.Type.TASK);
            process.sendSms(message);
        }
    }

    /**
     * If an SMS fails to send (due to credit, cell coverage, or bad number) post alert to server
     */
    public void smsSendFailedRequest(String resultMessage,
            String errorCode) {
        List<SyncUrl> syncUrls = App.getDatabaseInstance().getSyncUrlInstance().fetchSyncUrlByStatus(
                SyncUrl.Status.ENABLED);
        for (SyncUrl syncUrl : syncUrls) {
            if (syncUrl.getUrl() != null && !syncUrl.getUrl().equals("")) {
                MainHttpClient client = new MainHttpClient(syncUrl.getUrl(), prefs.getContext());
                try {
                    client.setMethod(HttpMethod.POST);
                    client.addParam(TASK_PARAM, "alert");
                    client.addParam(MESSAGE_PARAM, resultMessage);
                    if (!errorCode.matches("")) {
                        client.addParam("errorCode", errorCode);
                    }
                    client.execute();
                } catch (Exception e) {
                    Util.logActivities(prefs.getContext(), e.getMessage());
                } finally {
                    if(client !=null) {
                        if (HttpStatus.SC_OK == client.responseCode()) {
                            Util.logActivities(prefs.getContext(), prefs.getContext().getResources()
                                    .getString(R.string.successful_alert_to_server));
                        }
                    }
                }
            }
        }
    }

    /**
     * If data connection is lost for extended time (either WiFi, or GSM) send alert SMS to stored
     * phone number
     */
    public void dataConnectionLost() {
        if (!prefs.alertPhoneNumber().get().matches("")) {
            ProcessSms process = new ProcessSms(prefs.getContext());
            final Long timeMills = System.currentTimeMillis();
            Message message = new Message();
            message.setBody( prefs.getContext().getResources().getString(R.string.lost_connection_message));
            message.setDate(new Date(timeMills));
            message.setPhoneNumber(prefs.alertPhoneNumber().get());
            message.setUuid(process.getUuid());
            message.setType(Message.Type.TASK);
            process.sendSms(message);
        }
    }
}
