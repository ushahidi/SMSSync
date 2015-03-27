package org.addhen.smssync.messages;

import com.google.gson.Gson;

import org.addhen.smssync.App;
import org.addhen.smssync.R;
import org.addhen.smssync.controllers.MessageResultsController;
import org.addhen.smssync.database.BaseDatabseHelper;
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
import org.addhen.smssync.util.SentMessagesUtil;
import org.addhen.smssync.util.Util;

import android.content.Context;
import android.text.TextUtils;

import java.net.URLEncoder;
import java.util.Date;
import java.util.List;


/**
 * Process messages
 */
public class ProcessMessage {

    private static final String TAG = ProcessMessage.class
            .getSimpleName();

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
        App.getDatabaseInstance().getMessageInstance()
                .put(message, new BaseDatabseHelper.DatabaseCallback<Void>() {
                    @Override
                    public void onFinished(Void result) {
                        // Do nothing
                    }

                    @Override
                    public void onError(Exception exception) {
                        // Do nothing
                    }
                });
        return true;
    }

    /**
     * Sync received SMS to a configured sync URL.
     *
     * @param message The sms to be sync
     * @param client  The http client
     * @return boolean
     */
    public boolean syncReceivedSms(Message message, MessageSyncHttpClient client) {
        Logger.log(TAG, "syncReceivedSms(): Post received SMS to configured URL:" +
                message.toString() + " SyncUrlFragment: ");

        final boolean posted = client.postSmsToWebService();

        if (posted) {
            logActivites(context.getString(R.string.sms_sent_to_webserivce, message.getBody(),
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
    public boolean syncPendingMessages(final String uuid) {
        Logger.log(TAG, "syncPendingMessages: push pending messages to the Sync URL" + uuid);
        boolean status = false;
        // check if it should sync by id
        if (!TextUtils.isEmpty(uuid)) {
            final Message message = App.getDatabaseInstance().getMessageInstance()
                    .fetchByUuid(uuid);
            status = routePendingMessage(message);
        } else {
            final List<Message> messages = App.getDatabaseInstance().getMessageInstance()
                    .fetchPending();
            if (messages != null && messages.size() > 0) {
                for (Message message : messages) {
                    status = routePendingMessage(message);
                }
            }
        }

        return status;

    }

    /**
     * Synchronizes pending messages to configured sync URL. This takes into account
     * keyword filtering
     * @param message The message to be sync'd to the Sync URL.
     *
     * @return boolean
     */
    public boolean routePendingMessage(Message message) {
        boolean status = false;
        Logger.log(TAG,"message by uuid ");
        if (!Util.isConnected(context)) {
            return status;
        }
            List<SyncUrl> result = fetchEnabledSyncUrl();

            for (final SyncUrl syncUrl : result) {
                // white listed is enabled
                if (prefs.enableWhitelist().get()) {
                    List<Filter> filters = App.getDatabaseInstance().getFilterInstance().fetchByStatus(
                            Filter.Status.WHITELIST);
                    for (Filter filter : filters) {
                        if (filter.getPhoneNumber()
                                .equals(message.getPhoneNumber())) {
                            if(processMessage(message, syncUrl)) {
                                postToSentBox(message);
                            }
                        }
                    }
                    return true;
                }

                if (prefs.enableBlacklist().get()) {
                    List<Filter> filters = App.getDatabaseInstance().getFilterInstance().fetchByStatus(Filter.Status.BLACKLIST);
                    for(Filter filter : filters) {
                        if (filter.getPhoneNumber().equals(message.getPhoneNumber())) {
                            Logger.log("message",
                                    " from:" + message.getPhoneNumber() + " filter:" + filter
                                            .getPhoneNumber());
                            return false;
                        }
                    }
                } else {
                    if(processMessage(message,syncUrl)) {
                        postToSentBox(message);
                    }
                }
            }

        status = true;

        return status;
    }

    private void deleteMessage(Message message) {
        Logger.log(TAG, " message ID " + message.getUuid());
        App.getDatabaseInstance().getMessageInstance()
                .deleteByUuid(message.getUuid());
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

        if (response != null && response.getPayload() != null
                && response.getPayload().getMessages().size() > 0) {
            for (Message msg : response.getPayload().getMessages()) {
                sendTaskSms(msg);
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
                smssyncResponses = gson.fromJson(client.getResponse(),
                        SmssyncResponse.class);
            } catch (Exception e) {
                e.printStackTrace();
                Util.logActivities(context, "Task crashed: " + e.getMessage());
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

        Util.logActivities(context,
                context.getString(R.string.finish_task_check) + " " + getErrorMessage());
    }

    private void sendSMSWithMessageResultsAPIEnabled(SyncUrl syncUrl,
            List<Message> msgs) {
        QueuedMessages messagesUUIDs = new QueuedMessages();
        for (Message msg : msgs) {
            msg.setType(Message.Type.TASK);
            messagesUUIDs.getQueuedMessages().add(msg.getUuid());
        }

        MessagesUUIDSResponse response = mMessageResultsController
                .sendQueuedMessagesPOSTRequest(syncUrl, messagesUUIDs);
        if (null != response && response.isSuccess() && response.hasUUIDs()) {
            for (Message msg : msgs) {
                msg.setType(Message.Type.TASK);
                if (response.getUuids().contains(msg.getUuid())) {
                    sendTaskSms(msg);
                    Util.logActivities(context,
                            context.getString(R.string.processed_task,
                                    msg.getBody()));
                }
            }
        }
    }

    private void sendSMSWithMessageResultsAPIDisabled(List<Message> msgs) {
        for (Message msg : msgs) {
            msg.setType(Message.Type.TASK);
            sendTaskSms(msg);
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
                Message msg = new Message();
                msg.setBody(prefs.reply().get());
                msg.setPhoneNumber(message.getPhoneNumber());
                msg.setType(message.getType());
                processSms.sendSms(msg);
            }

            if (Util.isConnected(context)) {
                List<SyncUrl> result = fetchEnabledSyncUrl();
                for (final SyncUrl syncUrl : result) {
                    // white listed is enabled
                    if (prefs.enableWhitelist().get()) {
                        List<Filter> filters = App.getDatabaseInstance().getFilterInstance().fetchByStatus(
                                Filter.Status.WHITELIST);
                        for (Filter filter : filters) {
                            if (filter.getPhoneNumber()
                                    .equals(message.getPhoneNumber())) {
                                if( processMessage(message, syncUrl)) {
                                    deleteFromSmsInbox(message);
                                } else {
                                    savePendingMessage(message);
                                }
                            }
                        }
                        return true;
                    }

                    if (prefs.enableBlacklist().get()) {
                        List<Filter> filters = App.getDatabaseInstance().getFilterInstance().fetchByStatus(Filter.Status.BLACKLIST);
                        for(Filter filter : filters) {
                            if (filter.getPhoneNumber().equals(message.getPhoneNumber())) {
                                Logger.log("message",
                                        " from:" + message.getPhoneNumber() + " filter:" + filter
                                                .getPhoneNumber());
                                return false;
                            }
                        }
                    } else {
                        if(processMessage(message,syncUrl)){
                            deleteFromSmsInbox(message);
                        } else {
                            savePendingMessage(message);
                        }
                    }
                }
                return true;
            } else {
                // There is no internet so save in the pending list
                savePendingMessage(message);
            }
        }
        return false;

    }

    private void savePendingMessage(Message message) {
        //only save to pending when the number is not blacklisted
        if (!prefs.enableBlacklist().get()) {
            message.setStatus(Message.Status.FAILED);
            saveMessage(message);
        }
    }

    private void deleteFromSmsInbox(Message message) {
        if (prefs.autoDelete().get()) {

            processSms.delSmsFromInbox(message.getBody(), message.getPhoneNumber());
            Util.logActivities(context,
                    context.getString(R.string.auto_message_deleted, message.getBody()));
        }
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
            if (processSms.filterByKeywords(message.getBody(), filterText)
                    || processSms.filterByRegex(message.getBody(), filterText)) {
                Logger.log(TAG, syncUrl.getUrl());

                if (message.getType() == Message.Type.PENDING) {
                    posted = syncReceivedSms(message, client);
                    if (posted) {
                        postToSentBox(message);
                    }
                } else {
                    posted = sendTaskSms(message);
                }

            }

        } else { // there is no filter text set up on a sync URL

            if (message.getType() == Message.Type.PENDING) {
                posted = syncReceivedSms(message, client);
                setErrorMessage(syncUrl.getUrl());
                if (posted) {
                    postToSentBox(message);
                }
            } else {
                posted = sendTaskSms(message);
            }
        }

        // Update number of tries.

        if(!posted) {
            Logger.log(TAG, "Messages Not posted: " + message);
            if (message.getRetries() > prefs.retries().get()) {
                // Delete from db
                deleteMessage(message);
            } else {
                // Increase message's number of tries for future comparison to know when to delete it.
                int retries = message.getRetries() + 1;
                message.setRetries(retries);
                App.getDatabaseInstance().getMessageInstance().update(message);

            }
        }
        return posted;
    }


    private List<SyncUrl> fetchEnabledSyncUrl() {
        return App.getDatabaseInstance().getSyncUrlInstance().fetchSyncUrlByStatus(
                SyncUrl.Status.ENABLED);
    }

    /**
     * Saves successfully sent messages into the db
     *
     * @param message the message
     */
    public boolean postToSentBox(Message message) {
        Logger.log(TAG, "postToSentBox(): post message to sentbox "+message.toString());
        return SentMessagesUtil.processSentMessages(message);

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

    private boolean sendTaskSms(Message message) {

        if (message.getDate() == null || !TextUtils.isEmpty(message.getUuid())) {
            final Long timeMills = System.currentTimeMillis();
            message.setDate(new Date(timeMills));
        }
        if(message.getUuid() ==null || TextUtils.isEmpty(message.getUuid())) {
            message.setUuid(processSms.getUuid());
        }

        message.setType(Message.Type.TASK);
        return processSms
                .sendSms(message);
    }

}
