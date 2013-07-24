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

package org.addhen.smssync.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.net.URLEncoder;

import org.addhen.smssync.Prefs;
import org.addhen.smssync.ProcessSms;
import org.addhen.smssync.R;
import org.addhen.smssync.models.MessagesModel;
import org.addhen.smssync.net.MainHttpClient;
import org.addhen.smssync.net.MessageSyncHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author eyedol
 */
public class MessageSyncUtil extends Util {

    private Context context;

    private String url;

    private static JSONObject jsonObject;

    private static JSONArray jsonArray;

    private static final String CLASS_TAG = MessageSyncUtil.class
            .getSimpleName();

    private MessageSyncHttpClient msgSyncHttpClient;

    private ProcessSms processSms;

    public MessageSyncUtil(Context context, String url) {
        this.context = context;
        this.url = url;
        this.msgSyncHttpClient = new MessageSyncHttpClient(context, url);
        processSms = new ProcessSms(context);
    }

    /**
     * Posts received SMS to a configured callback URL.
     * 
     * @param String apiKey
     * @param String fromAddress
     * @param String messageBody
     * @return boolean
     */
    public boolean postToAWebService(String messagesFrom, String messagesBody,
            String messagesTimestamp, String messagesUuid, String secret) {
        log("postToAWebService(): Post received SMS to configured URL:"
                + Prefs.website + " messagesTimestamp: " + messagesTimestamp
                + " messagesBody: " + messagesBody + " messagesFrom "
                + messagesFrom + " Secret " + secret);

        HashMap<String, String> params = new HashMap<String, String>();
        Prefs.loadPreferences(context);

        if (!TextUtils.isEmpty(url)) {
            params.put("secret", secret);
            params.put("from", messagesFrom);
            params.put("message", messagesBody);
            params.put("sent_timestamp", messagesTimestamp);
            params.put("sent_to", getPhoneNumber(context));
            params.put("message_id", messagesUuid);
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
     * @param int messageId - Sync by Id - 0 for no ID > 0 to for an id
     * @param String url The sync URL to push the message to.
     * @return int
     */
    public int syncToWeb(String messageUuid) {
        log("syncToWeb(): push pending messages to the Sync URL");
        MessagesModel model = new MessagesModel();
        List<MessagesModel> listMessages = new ArrayList<MessagesModel>();
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

            for (MessagesModel messages : listMessages) {
                log("processing");
                if (processSms.routePendingMessages(messages.getMessageFrom(),
                        messages.getMessage(), messages.getMessageDate(),
                        messages.getMessageUuid())) {

                    // / if it successfully pushes message, delete message
                    // from db
                    if (new MessagesModel().deleteMessagesByUuid(messages
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
        Logger.log(CLASS_TAG, "performResponseFromServer(): " + " response:"
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
                new Util().log(CLASS_TAG, "Error: " + e.getMessage());
                showToast(context, R.string.no_task);
            }
        }

    }

    /**
     * Process messages as received from the user; 0 - successful 1 - failed
     * fetching categories
     * 
     * @return int - status
     */
    public static int processMessages() {
        Logger.log(CLASS_TAG,
                "processMessages(): Process text messages as received from the user's phone");
        List<MessagesModel> listMessages = new ArrayList<MessagesModel>();
        String messageUuid = "";
        int status = 1;
        MessagesModel messages = new MessagesModel();
        listMessages.add(messages);

        // check if messageId is actually initialized
        if (smsMap.get("messagesUuid") != null) {
            messageUuid = smsMap.get("messagesUuid");
        }

        messages.setMessageUuid(messageUuid);
        messages.setMessageFrom(smsMap.get("messagesFrom"));
        messages.setMessage(smsMap.get("messagesBody"));
        messages.setMessageDate(smsMap.get("messagesDate"));

        if (listMessages != null) {
            MessagesModel model = new MessagesModel();
            model.listMessages = listMessages;
            model.save();

            status = 0;
        }
        return status;

    }

    /**
     * Performs a task based on what callback URL tells it.
     * 
     * @param Context context - the activity calling this method.
     * @return void
     */
    public void performTask(String urlSecret) {
        Logger.log(CLASS_TAG, "performTask(): perform a task");
        // load Prefs
        Prefs.loadPreferences(context);

        // validate configured url
        int status = validateCallbackUrl(url);
        if (status == 1) {
            showToast(context, R.string.no_configured_url);
        } else if (status == 2) {
            showToast(context, R.string.invalid_url);
        } else if (status == 3) {
            showToast(context, R.string.no_connection);
        } else {

            StringBuilder uriBuilder = new StringBuilder(url);

            uriBuilder.append("?task=send");

	if(!TextUtils.isEmpty(urlSecret)) {
		String urlSecretEncoded;
		try {
			urlSecretEncoded = URLEncoder.encode(urlSecret, "UTF-8");
		} catch (java.io.UnsupportedEncodingException e) {
			urlSecretEncoded = urlSecret;
		}
		uriBuilder.append("&secret=" + urlSecretEncoded);
	}

            String response = MainHttpClient.getFromWebService(uriBuilder
                    .toString());
            Log.d(CLASS_TAG, "TaskCheckResponse: " + response);
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
                            log(context.getString(R.string.no_task));
                        }

                    } else { // 'payload' data may not be present in JSON
                             // response
                        log(context.getString(R.string.no_task));
                    }

                } catch (JSONException e) {
                    log("Error: " + e.getMessage());
                }
            }
        }
    }
}
