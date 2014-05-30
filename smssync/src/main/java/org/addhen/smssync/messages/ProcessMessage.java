package org.addhen.smssync.messages;

import org.addhen.smssync.MainApplication;
import org.addhen.smssync.Prefs;
import org.addhen.smssync.R;
import org.addhen.smssync.database.Database;
import org.addhen.smssync.models.Filter;
import org.addhen.smssync.models.Message;
import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.net.MainHttpClient;
import org.addhen.smssync.net.MessageSyncHttpClient;
import org.addhen.smssync.util.Logger;
import org.addhen.smssync.util.Util;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

import java.net.URLEncoder;
import java.util.List;

import static org.addhen.smssync.messages.ProcessSms.PENDING;
import static org.addhen.smssync.messages.ProcessSms.TASK;
import static org.addhen.smssync.messages.ProcessSms.UNCONFIRMED;
import static org.addhen.smssync.messages.ProcessSms.FAILED;

/**
 * Process messages
 */
public class ProcessMessage {

    private static final String TAG = ProcessMessage.class
            .getSimpleName();

    private static final int ACTIVE_SYNC_URL = 1;

    private static JSONObject jsonObject;

    private static JSONArray jsonArray;

    private Context context;

    private ProcessSms processSms;

    private String errorMessage;

    public ProcessMessage(Context context) {
        this.context = context;
        processSms = new ProcessSms(context);
    }

    /**
     * Save SMS that failed to be sent to the sync Url to the the db.
     */
    public boolean saveMessage(Message message) {
        Logger.log(TAG,
                "saveMessage(): save text messages as received from the user's phone");
        return message.save();
    }

    /**
     * Sync received SMS to a configured sync URL.
     *
     * @param message The sms to be sync
     * @param syncUrl The sync URL to post the message to
     * @return boolean
     */
    public boolean syncReceivedSms(Message message, SyncUrl syncUrl) {
        Logger.log(TAG, "syncReceivedSms(): Post received SMS to configured URL:" +
                message.toString() + " SyncUrlFragment: " + syncUrl.toString());

        MessageSyncHttpClient client = new MessageSyncHttpClient(
                context, syncUrl, message, Util.getPhoneNumber(context)
        );
        final boolean posted = client.postSmsToWebService();

        if (posted) {
            Util.logActivities(context,
                    context.getString(R.string.sms_sent_to_webserivce, message.getBody(),
                            syncUrl.getUrl()));
            smsServerResponse(client.getServerSuccessResp());
        } else {
            String clientError = client.getClientError();
            String serverError = client.getServerError();
            if (clientError != null) {
                setErrorMessage(clientError);
            } else if (serverError != null) {
                setErrorMessage(serverError);
            }
        }

        return posted;
    }

    /**
     * Sync pending messages to the configured sync URL.
     *
     * @param uuid The message uuid
     */
    public boolean syncPendingMessages(String uuid) {
        Logger.log(TAG, "syncPendingMessages: push pending messages to the Sync URL" + uuid);
        Message messageModel = new Message();
        List<Message> listMessages;
        // check if it should sync by id
        if (!TextUtils.isEmpty(uuid)) {
            messageModel.loadByUuid(uuid);

        } else {
            messageModel.load();
        }
        listMessages = messageModel.getMessageList();

        if (listMessages != null && listMessages.size() > 0) {

            for (Message message : listMessages) {
                if (routeMessage(message)) {
                    messageModel.deleteMessagesByUuid(message.getUuid());
                }

            }
            return true;
        }

        return false;

    }

    /**
     * Send the response received from the server as SMS
     *
     * @param response The JSON string response from the server.
     */
    public void smsServerResponse(String response) {
        Logger.log(TAG, "performResponseFromServer(): " + " response:"
                + response);
        if (!Prefs.enableReplyFrmServer) {
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
            }
        }
    }

    public void performTask(SyncUrl syncUrl) {
        Logger.log(TAG, "performTask(): perform a task");
        Util.logActivities(context, context.getString(R.string.perform_task));
        // load Prefs
        Prefs.loadPreferences(context);

        // validate configured url
        int status = Util.validateCallbackUrl(syncUrl.getUrl());
        if (status == 1) {
            setErrorMessage(context.getString(R.string.no_configured_url));
            Util.logActivities(context, context.getString(R.string.no_configured_url));
        } else if (status == 2) {
            setErrorMessage(context.getString(R.string.invalid_url));
            Util.logActivities(context, context.getString(R.string.invalid_url) + syncUrl.getUrl());
        } else if (status == 3) {
            setErrorMessage(context.getString(R.string.no_connection));
            Util.logActivities(context, context.getString(R.string.no_connection));
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

            syncUrl.setUrl(uriBuilder.toString());

            MainHttpClient client = new MainHttpClient(syncUrl.getUrl(), context);
            String response = null;
            try {
                client.execute();
                response = client.getResponse();
            } catch (Exception e) {
                Logger.log(TAG, e.getMessage());
            }

            Logger.log(TAG, "TaskCheckResponse: " + response);
            if (response != null && !TextUtils.isEmpty(response)) {

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
                                        jsonObject.getString("message"), jsonObject.getString("uuid"));

                                Util.logActivities(context,
                                        context.getString(R.string.processed_task,
                                                jsonObject.getString("message")));
                            }

                        } else {
                            Logger.log(TAG, context.getString(R.string.no_task));
                            Util.logActivities(context, context.getString(R.string.no_task));
                            setErrorMessage(context.getString(R.string.no_task));
                        }

                    } else { // 'payload' data may not be present in JSON
                        // response
                        Logger.log(TAG, context.getString(R.string.no_task));
                        Util.logActivities(context, context.getString(R.string.no_task));
                        setErrorMessage(context.getString(R.string.no_task));
                    }

                } catch (JSONException e) {
                    Logger.log(TAG, "Error: " + e.getMessage());
                    setErrorMessage(e.getMessage());
                }
            }
        }
        Util.logActivities(context, context.getString(R.string.finish_task_check));
    }

    /**
     * Processes the incoming SMS to figure out how to exactly to route the message. If it fails to
     * be synced online, cache it and queue it up for the scheduler to process it.
     *
     * @param message The sms to be routed
     * @return boolean
     */
    public boolean routeSms(Message message) {
        Logger.log(TAG, "routeSms uuid: " + message.toString());

        // is SMSSync service running?
        if (Prefs.enabled) {
            // send auto response from phone not server.
            if (Prefs.enableReply) {
                // send auto response as SMS to user's phone
                Util.logActivities(context, context.getString(R.string.auto_response_sent));
                processSms.sendSms(message.getFrom(), Prefs.reply);
            }

            if (routeMessage(message)) {

                // Delete messages from message app's inbox, only
                // when SMSSync has that feature turned on
                if (Prefs.autoDelete) {

                    processSms.delSmsFromInbox(message.getBody(), message.getFrom());
                    Util.logActivities(context,
                            context.getString(R.string.auto_message_deleted, message.getBody()));
                }
                return true;
            } else {
                //only save to pending when the number is not blacklisted
                if (!Prefs.enableBlacklist) {
                    saveMessage(message);
                }
            }
        }
        return false;

    }

    public boolean routePendingMessage(Message message) {
        if (routeMessage(message)) {
            return message.deleteMessagesByUuid(message.getUuid());
        }
        return false;
    }

    /**
     * @param message
     * @param syncUrl
     * @return
     */
    private boolean processMessage(Message message, SyncUrl syncUrl) {
        boolean posted = false;

        // process filter text (keyword or RegEx)
        if (!TextUtils.isEmpty(syncUrl.getKeywords())) {
            String filterText = syncUrl.getKeywords();
            if (processSms.filterByKeywords(message.getBody(), filterText)
                    || processSms.filterByRegex(message.getBody(), filterText)) {
                Logger.log(TAG, syncUrl.getUrl());


                if (message.getMessageType() == PENDING) {
                    posted = syncReceivedSms(message, syncUrl);
                    if (!posted) {
                        // Note: HTTP Error code or custom error message
                        // will have been shown already

                        // attempt to make a data connection to sync
                        // the failed messages.
                        Util.connectToDataNetwork(context);

                    } else {
                        processSms.postToSentBox(message, PENDING);
                    }
                } else {
                    processSms.sendSms(message.getFrom(), message.getBody(), message.getUuid());
                    posted = true;
                }

            }

        } else { // there is no filter text set up on a sync URL

            if (message.getMessageType() == PENDING) {
                posted = syncReceivedSms(message, syncUrl);
                setErrorMessage(syncUrl.getUrl());
                if (!posted) {

                    // attempt to make a data connection to the sync
                    // url
                    Util.connectToDataNetwork(context);

                } else {
                    processSms.postToSentBox(message, PENDING);
                }
            } else {
                processSms.sendSms(message.getFrom(), message.getBody(), message.getUuid());
                posted = true;

            }
        }

        return posted;
    }

    /**
     * Routes both incoming SMS and pending messages.
     *
     * @param message The message to be rounted
     */
    private boolean routeMessage(Message message) {

        // load preferences
        Prefs.loadPreferences(context);
        boolean posted = false;
        // is SMSSync service running?
        if (!Prefs.enabled || !Util.isConnected(context)) {
            return posted;
        }
        SyncUrl model = new SyncUrl();
        Filter filters = new Filter();
        // get enabled Sync URLs
        for (SyncUrl syncUrl : model.loadByStatus(ACTIVE_SYNC_URL)) {
            // white listed is enabled
            if (Prefs.enableWhitelist) {
                filters.loadByStatus(Filter.Status.WHITELIST);
                for (Filter filter : filters.getFilterList()) {
                    if (filter.getPhoneNumber().equals(message.getFrom())) {
                        return processMessage(message, syncUrl);
                    }
                }
                return false;
            }

            if (Prefs.enableBlacklist) {

                filters.loadByStatus(Filter.Status.BLACKLIST);
                for (Filter filter : filters.getFilterList()) {

                    if (filter.getPhoneNumber().equals(message.getFrom())) {
                        Logger.log("message", " from:" + message.getFrom() + " filter:" + filter
                                .getPhoneNumber());
                        return false;
                    }
                }
            } else {

                return processMessage(message, syncUrl);
            }

        }

        return posted;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
