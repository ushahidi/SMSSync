package org.addhen.smssync.messages;

import com.google.gson.Gson;

import org.addhen.smssync.R;
import org.addhen.smssync.controllers.MessageResultsController;
import org.addhen.smssync.models.Filter;
import org.addhen.smssync.models.Message;
import org.addhen.smssync.models.MessagesUUIDSResponse;
import org.addhen.smssync.models.QueuedMessages;
import org.addhen.smssync.models.SmssyncResponse;
import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.net.MainHttpClient;
import org.addhen.smssync.net.MessageSyncHttpClient;
import org.addhen.smssync.prefs.Prefs;
import org.addhen.smssync.util.Logger;
import org.addhen.smssync.util.Util;

import android.content.Context;
import android.text.TextUtils;

import java.net.URLEncoder;
import java.util.List;

import static org.addhen.smssync.messages.ProcessSms.PENDING;
import static org.addhen.smssync.messages.ProcessSms.TASK;

/**
 * Process messages
 */
public class ProcessMessage {

    private static final String TAG = ProcessMessage.class
            .getSimpleName();

    private static final int ACTIVE_SYNC_URL = 1;

    private Context context;

    private ProcessSms processSms;

    private String errorMessage;

    private MessageResultsController mMessageResultsController;

    private Prefs prefs;

    public ProcessMessage(Context context, ProcessSms processSms) {
        this.context = context;
        this.processSms = processSms;
        mMessageResultsController = new MessageResultsController(context);
        prefs = new Prefs(context);
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
     * @param client The http client
     * @return boolean
     */
    public boolean syncReceivedSms(Message message, MessageSyncHttpClient client) {
        Logger.log(TAG, "syncReceivedSms(): Post received SMS to configured URL:" +
                message.toString() + " SyncUrlFragment: " );

        final boolean posted = client.postSmsToWebService();

        if (posted) {
            logActivites(context.getString(R.string.sms_sent_to_webserivce, message.getMessage(),
                    client.getSyncUrl().getUrl()));
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

    public String getPhoneNumber() {
        return Util.getPhoneNumber(context);
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
    public void smsServerResponse(SmssyncResponse response) {
        Logger.log(TAG, "performResponseFromServer(): " + " response:"
                + response);
        if (!prefs.enableReplyFrmServer().get()) {
            return;
        }

        if (response != null && response.getPayload().getMessages().size() > 0) {
            for (Message msg : response.getPayload().getMessages()) {
                sendSms(msg);
            }
        }
    }

    public void performTask(SyncUrl syncUrl) {
        Logger.log(TAG, "performTask(): perform a task");
        Util.logActivities(context, context.getString(R.string.perform_task));
        // load Prefs

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

            MainHttpClient client = new MainHttpClient(uriBuilder.toString(), context);
            SmssyncResponse smssyncResponses = null;

            try {
                client.execute();
                final Gson gson = new Gson();
                smssyncResponses = gson.fromJson(client.getResponse().body().charStream(),
                        SmssyncResponse.class);
            } catch (Exception e) {
                e.printStackTrace();
                Util.logActivities(context, "Task crashed: "+e.getMessage());
            }

            if (smssyncResponses != null) {
                Logger.log(TAG, "TaskCheckResponse: " + smssyncResponses.toString());
                Util.logActivities(context, "TaskCheckResponse: " + smssyncResponses.toString());

                if (smssyncResponses.getPayload() != null) {
                    String task = smssyncResponses.getPayload().getTask();
                    Logger.log(TAG, "Task " + task);
                    boolean secretOk = TextUtils.isEmpty(urlSecret) ||
                            urlSecret.equals(smssyncResponses.getPayload().getSecret());
                    if (secretOk && task.equals("send")) {
                        if (prefs.messageResultsAPIEnable().get()) {
                            sendSMSWithMessageResultsAPIEnabled(syncUrl,
                                    smssyncResponses.getPayload().getMessages());
                        } else {
                            //backwards compatibility
                            sendSMSWithMessageResultsAPIDisabled(
                                    smssyncResponses.getPayload().getMessages());
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

            }
        }

        Util.logActivities(context, context.getString(R.string.finish_task_check)+" "+getErrorMessage());
    }

    private void sendSMSWithMessageResultsAPIEnabled(SyncUrl syncUrl,
            List<Message> msgs) {
        QueuedMessages messagesUUIDs = new QueuedMessages();
        for (Message msg : msgs) {
            msg.setMessageType(TASK);
            messagesUUIDs.getQueuedMessages().add(msg.getUuid());
        }

        MessagesUUIDSResponse response = mMessageResultsController
                .sendQueuedMessagesPOSTRequest(syncUrl, messagesUUIDs);
        if (null != response && response.isSuccess() && response.hasUUIDs()) {
            for (Message msg : msgs) {
                msg.setMessageType(TASK);
                if (response.getUuids().contains(msg.getUuid())) {
                    sendSms(msg);
                    Util.logActivities(context,
                            context.getString(R.string.processed_task,
                                    msg.getMessage()));
                }
            }
        }
    }

    private void sendSMSWithMessageResultsAPIDisabled(List<Message> msgs) {
        for (Message msg : msgs) {
            msg.setMessageType(TASK);
            sendSms(msg);
        }
    }

    /**
     * Processes the incoming SMS to figure out how to exactly route the message. If it fails to be
     * synced online, cache it and queue it up for the scheduler to process it.
     *
     * @param message The sms to be routed
     * @return boolean
     */
    public boolean routeSms(Message message) {
        Logger.log(TAG, "routeSms uuid: " + message.toString());

        // is SMSSync service running?
        if (prefs.serviceEnabled().get()) {

            // send auto response from phone not server.
            if (prefs.enableReply().get()) {

                // send auto response as SMS to user's phone
                Util.logActivities(context, context.getString(R.string.auto_response_sent));
                processSms.sendSms(message.getPhoneNumber(), prefs.reply().get());
            }

            if (routeMessage(message)) {

                // Delete messages from message app's inbox, only
                // when SMSSync has that feature turned on
                if (prefs.autoDelete().get()) {

                    processSms.delSmsFromInbox(message.getMessage(), message.getPhoneNumber());
                    Util.logActivities(context,
                            context.getString(R.string.auto_message_deleted, message.getMessage()));
                }
                return true;
            } else {
                //only save to pending when the number is not blacklisted
                if (!prefs.enableBlacklist().get()) {
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
        MessageSyncHttpClient client = new MessageSyncHttpClient(
                context, syncUrl, message, getPhoneNumber(), prefs.uniqueId().get()
        );
        // process filter text (keyword or RegEx)
        if (!TextUtils.isEmpty(syncUrl.getKeywords())) {
            String filterText = syncUrl.getKeywords();
            if (processSms.filterByKeywords(message.getMessage(), filterText)
                    || processSms.filterByRegex(message.getMessage(), filterText)) {
                Logger.log(TAG, syncUrl.getUrl());

                if (message.getMessageType() == PENDING) {
                    posted = syncReceivedSms(message, client);
                    if (!posted) {
                        // Note: HTTP Error code or custom error message
                        // will have been shown already

                        // attempt to make a data connection to sync
                        // the failed messages.
                        Util.connectToDataNetwork(context);

                    } else {
                        message.setMessageType(PENDING);
                        processSms.postToSentBox(message);
                    }
                } else {
                    // FIXME: `posted` always `true` but `sendSms()` may not work
                    sendSms(message);
                    posted = true;
                }

            }

        } else { // there is no filter text set up on a sync URL

            if (message.getMessageType() == PENDING) {
                posted = syncReceivedSms(message, client);
                setErrorMessage(syncUrl.getUrl());
                if (!posted) {

                    // attempt to make a data connection to the sync
                    // url
                    Util.connectToDataNetwork(context);

                } else {
                    message.setMessageType(PENDING);
                    processSms.postToSentBox(message);
                }
            } else {
                sendSms(message);
                posted = true;

            }
        }

        return posted;
    }

    /**
     * Routes both incoming SMS and pending messages.
     *
     * @param message The message to be routed
     */
    private boolean routeMessage(Message message) {

        // load preferences
        boolean posted = false;
        // is SMSSync service running?
        if (!prefs.serviceEnabled().get() || !Util.isConnected(context)) {
            return posted;
        }
        SyncUrl model = new SyncUrl();
        Filter filters = new Filter();
        // get enabled Sync URLs
        for (SyncUrl syncUrl : model.loadByStatus(ACTIVE_SYNC_URL)) {
            // white listed is enabled
            if (prefs.enableWhitelist().get()) {
                filters.loadByStatus(Filter.Status.WHITELIST);
                for (Filter filter : filters.getFilterList()) {
                    if (filter.getPhoneNumber().equals(message.getPhoneNumber())) {
                        return processMessage(message, syncUrl);
                    }
                }
                return false;
            }

            if (prefs.enableBlacklist().get()) {

                filters.loadByStatus(Filter.Status.BLACKLIST);
                for (Filter filter : filters.getFilterList()) {

                    if (filter.getPhoneNumber().equals(message.getPhoneNumber())) {
                        Logger.log("message", " from:" + message.getPhoneNumber() + " filter:" + filter
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

    public void logActivites(String message) {
        Util.logActivities(context, message);
    }

    private void sendSms(Message message) {
        processSms.sendSms(message.getPhoneNumber(), message.getMessage(), message.getUuid());
    }

}
