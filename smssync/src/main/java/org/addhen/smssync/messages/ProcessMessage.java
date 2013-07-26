package org.addhen.smssync.messages;

import android.content.Context;
import android.text.TextUtils;

import org.addhen.smssync.Prefs;
import org.addhen.smssync.R;
import org.addhen.smssync.models.MessageModel;
import org.addhen.smssync.net.MainHttpClient;
import org.addhen.smssync.net.MessageSyncHttpClient;
import org.addhen.smssync.util.Logger;
import org.addhen.smssync.util.Util;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * Created by eyedol on 7/26/13.
 */
public class ProcessMessage {

    private static final String TAG = MessageSync.class
            .getSimpleName();
    private static JSONObject jsonObject;
    private static JSONArray jsonArray;
    private Context context;
    private ProcessSms processSms;
    private Message message;

    public ProcessMessage(Context context) {
        this.context = context;
        processSms = new ProcessSms(context);
    }

    /**
     * Save SMS that failed to be sent to the sync Url to the
     * the db.
     */
    public boolean saveMessage(Message message) {
        Logger.log(TAG,
                "saveMessage(): save text messages as received from the user's phone");
        MessageModel messagesModel = new MessageModel();
        messagesModel.setMessage(message);
        return messagesModel.save();
    }

    /**
     * Posts received SMS to a configured sync URL.
     * @param message The sms to be sync
     * @param syncUrl The sync URL to post the message to
     *
     * @return boolean
     */
    public boolean postMessageToWeb(Message message, SyncUrl syncUrl) {
        Logger.log(TAG, "postToAWebService(): Post received SMS to configured URL:" +
                message.toString() + " SyncUrl: " + syncUrl.toString());
        MessageSyncHttpClient msgSyncHttpClient = new MessageSyncHttpClient(context, syncUrl);
        final boolean posted = msgSyncHttpClient.postSmsToWebService(message, Util.getPhoneNumber(context));
        if(posted)
            smsServerResponse(msgSyncHttpClient.getServerSuccessResp());
        else
            Util.showFailNotification(context,msgSyncHttpClient.getServerError(), context.getString(R.string.sending_failed));

        return posted;
    }

    /**
     * Post the response received from the server as SMS
     *
     * @param response The JSON string response from the server.
     */
    public void smsServerResponse(String response) {
        Logger.log(TAG, "performResponseFromServer(): " + " response:"
                + response);
        if (Prefs.enableReplyFrmServer) {
            return;
        }

        if (!TextUtils.isEmpty(response)) {

            try {

                jsonObject = new JSONObject(response);
                JSONObject payloadObject = jsonObject.getJSONObject("payload");

                if (payloadObject != null) {

                    jsonArray = payloadObject.getJSONArray("messages");

                    for (int index = 0; index < jsonArray.length(); ++index) {
                        jsonObject = jsonArray.getJSONObject(index);
                        new Util().log("Send sms: To: "
                                + jsonObject.getString("to") + "Message: "
                                + jsonObject.getString("message"));

                        processSms.sendSms(jsonObject.getString("to"),
                                jsonObject.getString("message"));
                    }

                }
            } catch (JSONException e) {
                new Util().log(TAG, "Error: " + e.getMessage());
                Util.showToast(context, R.string.no_task);
            }
        }
    }

    public void performTask(SyncUrl syncUrl) {
        Logger.log(TAG, "performTask(): perform a task");
        // load Prefs
        Prefs.loadPreferences(context);

        // validate configured url
        int status = Util.validateCallbackUrl(syncUrl.getUrl());
        if (status == 1) {
            Util.showToast(context, R.string.no_configured_url);
        } else if (status == 2) {
            Util.showToast(context, R.string.invalid_url);
        } else if (status == 3) {
            Util.showToast(context, R.string.no_connection);
        } else {

            StringBuilder uriBuilder = new StringBuilder(syncUrl.getUrl());
            final String urlSecret = syncUrl.getSecret();
            uriBuilder.append("?task=send");

            if (!TextUtils.isEmpty(urlSecret)) {
                String urlSecretEncoded = urlSecret;
                uriBuilder.append("&secret=");
                try {
                    urlSecretEncoded = URLEncoder.encode(urlSecret, "UTF-8");
                } catch (java.io.UnsupportedEncodingException e) {
                    Logger.log(TAG, e.getMessage());
                }
                uriBuilder.append(urlSecretEncoded);
            }

            String response = MainHttpClient.getFromWebService(uriBuilder
                    .toString());
            Logger.log(TAG, "TaskCheckResponse: " + response);
            if (!TextUtils.isEmpty(response)) {

                try {

                    jsonObject = new JSONObject(response);
                    JSONObject payloadObject = jsonObject
                            .getJSONObject("payload");

                    if (payloadObject != null) {
                        String task = payloadObject.getString("task");
                        boolean secretOk = TextUtils.isEmpty(urlSecret) ||
                                urlSecret.equals(payloadObject.getString("secret"));
                        if (secretOk && task.equals("send")) {
                            jsonArray = payloadObject.getJSONArray("messages");

                            for (int index = 0; index < jsonArray.length(); ++index) {
                                jsonObject = jsonArray.getJSONObject(index);

                                processSms.sendSms(jsonObject.getString("to"),
                                        jsonObject.getString("message"));
                            }

                        } else {
                            Logger.log(TAG, context.getString(R.string.no_task));
                        }

                    } else { // 'payload' data may not be present in JSON
                        // response
                        Logger.log(TAG, context.getString(R.string.no_task));
                    }

                } catch (JSONException e) {
                    Logger.log(TAG, "Error: " + e.getMessage());
                }
            }
        }
    }
}
