/*****************************************************************************
 ** Copyright (c) 2010 - 2012 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 **
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.
 **
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 **
 *****************************************************************************/

package org.addhen.smssync.messages;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

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
import java.util.HashMap;
import java.util.List;

/**
 * Handles synchronizing messages to the
 * configured Sync URL.
 */
public class MessageSync {

    private static final String TAG = MessageSync.class
            .getSimpleName();
    private static JSONObject jsonObject;
    private static JSONArray jsonArray;
    private Context context;
    private MessageSyncHttpClient msgSyncHttpClient;
    private ProcessSms processSms;
    private Message messageBody;

    public MessageSync(Context context, Message messageBody) {
        this.context = context;
        this.messageBody = messageBody;
        this.msgSyncHttpClient = new MessageSyncHttpClient(context, messageBody.getSyncUrl());
        processSms = new ProcessSms(context);
    }

    /**
     * Process text messages as received from the user; 0 - successful 1 - failed
     * fetching categories
     *
     * @return int - status
     */
    public static int processMessages() {
        Logger.log(TAG,
                "processMessages(): Process text messages as received from the user's phone");

        String messageUuid = "";
        int status = 1;

        MessageModel messages = new MessageModel();
        // check if messageId is actually initialized
        if (Util.smsMap.get("messagesUuid") != null) {
            messageUuid = Util.smsMap.get("messagesUuid");
        }

        messages.setMessageUuid(messageUuid);
        messages.setMessageFrom(Util.smsMap.get("messagesFrom"));
        messages.setMessage(Util.smsMap.get("messagesBody"));
        messages.setMessageDate(Util.smsMap.get("messagesDate"));
        messages.listMessages.add(messages);
        if(messages.save())
            status = 0;
        return status;
    }

    /**
     * Posts received SMS to a configured callback URL.
     *
     * @param String apiKey
     * @param String fromAddress
     * @param String messageBody
     * @return boolean
     */
    public boolean postToAWebService() {
        Logger.log(TAG, "postToAWebService(): Post received SMS to configured URL:"+messageBody.toString());

        HashMap<String, String> params = new HashMap<String, String>();
        Prefs.loadPreferences(context);

        if (!TextUtils.isEmpty(messageBody.getSyncUrl())) {
            params.put("secret", messageBody.getSecret());
            params.put("from", messageBody.getFrom());
            params.put("message", messageBody.getBody());
            params.put("sent_timestamp", messageBody.getTimestamp());
            //TODO: move this to message body
            params.put("sent_to", Util.getPhoneNumber(context));
            params.put("message_id", messageBody.getUuid());
            return msgSyncHttpClient.postSmsToWebService(params);
        }

        return false;
    }

    /**
     * Pushes pending messages to the configured URL.
     *
     * @return
     */
    public int syncToWeb() {
        return syncToWeb("");
    }

    /**
     * Pushes pending messages to the configured URL.
     *
     * @param int    messageId - Sync by Id - 0 for no ID > 0 to for an id
     * @param String url The sync URL to push the message to.
     * @return int
     */
    public int syncToWeb(String messageUuid) {
        Logger.log(TAG, "syncToWeb(): push pending messages to the Sync URL");
        MessageModel model = new MessageModel();
        List<MessageModel> listMessages;
        // check if it should sync by id
        if (!TextUtils.isEmpty(messageUuid)) {
            model.loadByUuid(messageUuid);
            listMessages = model.listMessages;

        } else {
            model.load();
            listMessages = model.listMessages;

        }
        int deleted = 0;

        if (listMessages != null) {
            if (listMessages.size() == 0) {
                return 2;
            }

            for (MessageModel messages : listMessages) {
                Logger.log(TAG, "processing");
                if (processSms.routePendingMessages(messages.getMessageFrom(),
                        messages.getMessage(), messages.getMessageDate(),
                        messages.getMessageUuid())) {

                    // / if it successfully pushes message, delete message
                    // from db
                    if (new MessageModel().deleteMessagesByUuid(messages
                            .getMessageUuid()))
                        deleted++;
                }

            }
        }

        return deleted;

    }

    /**
     * Sends messages received from the server as SMS.
     *
     * @param String response - the response from the server.
     */
    public void sendResponseFromServer(String response) {
        Logger.log(TAG, "performResponseFromServer(): " + " response:"
                + response);

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
                Logger.log(TAG, "Error: " + e.getMessage());
                Util.showToast(context, R.string.no_task);
            }
        }

    }

    /**
     * Performs a task based on what callback URL tells it.
     *
     * @param Context context - the activity calling this method.
     * @return void
     */
    public void performTask(String urlSecret) {
        Logger.log(TAG, "performTask(): perform a task");
        // load Prefs
        Prefs.loadPreferences(context);

        // validate configured url
        int status = Util.validateCallbackUrl(messageBody.getSyncUrl());
        if (status == 1) {
            Util.showToast(context, R.string.no_configured_url);
        } else if (status == 2) {
            Util.showToast(context, R.string.invalid_url);
        } else if (status == 3) {
            Util.showToast(context, R.string.no_connection);
        } else {

            StringBuilder uriBuilder = new StringBuilder(messageBody.getSyncUrl());

            uriBuilder.append("?task=send");

            if (!TextUtils.isEmpty(urlSecret)) {
                String urlSecretEncoded;
                try {
                    urlSecretEncoded = URLEncoder.encode(urlSecret, "UTF-8");
                } catch (java.io.UnsupportedEncodingException e) {
                    urlSecretEncoded = urlSecret;
                }
                uriBuilder.append("&secret=");
                uriBuilder.append(urlSecretEncoded);
            }

            String response = MainHttpClient.getFromWebService(uriBuilder
                    .toString());
            Log.d(TAG, "TaskCheckResponse: " + response);
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
