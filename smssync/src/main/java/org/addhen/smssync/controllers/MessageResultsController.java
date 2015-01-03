package org.addhen.smssync.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.squareup.okhttp.RequestBody;

import org.addhen.smssync.R;
import org.addhen.smssync.models.MessageResult;
import org.addhen.smssync.models.MessagesUUIDSResponse;
import org.addhen.smssync.models.QueuedMessages;
import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.net.BaseHttpClient;
import org.addhen.smssync.net.MainHttpClient;
import org.addhen.smssync.util.JsonUtils;
import org.addhen.smssync.util.Logger;
import org.addhen.smssync.util.Util;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import java.net.URLEncoder;
import java.util.List;

import static org.addhen.smssync.net.BaseHttpClient.JSON;

/**
 * Created by Kamil Kalfas(kkalfas@soldevelo.com) on 23.04.14.
 *
 * This class handling Message Results API
 *
 * POST ?task=sent queued_messages {@link #sendQueuedMessagesPOSTRequest(org.addhen.smssync.models.SyncUrl,
 * org.addhen.smssync.models.QueuedMessages)} POST ?task=results message_results {@link
 * #sendMessageResultPOSTRequest(org.addhen.smssync.models.SyncUrl, java.util.List)} GET
 * ?task=results {@link #sendMessageResultGETRequest(org.addhen.smssync.models.SyncUrl)}
 */
public class MessageResultsController {

    private static final String MESSAGE_RESULT_JSON_KEY = "message_result";

    private static final String TASK_SENT_URL_PARAM = "?task=sent";

    private static final String TASK_RESULT_URL_PARAM = "?task=result";

    private Context mContext;

    private Util mUtil;

    public MessageResultsController(Context mContext) {
        this.mContext = mContext;
        mUtil = new Util();
    }

    /**
     * This method is handling POST ?task=result message_result
     *
     * @param syncUrl url to web server
     * @param results list of message result data
     */
    public void sendMessageResultPOSTRequest(SyncUrl syncUrl, List<MessageResult> results) {
        String newEndPointURL = syncUrl.getUrl().concat(TASK_RESULT_URL_PARAM);

        final String urlSecret = syncUrl.getSecret();

        if (!TextUtils.isEmpty(urlSecret)) {
            String urlSecretEncoded = urlSecret;
            newEndPointURL = newEndPointURL.concat("&secret=");
            try {
                urlSecretEncoded = URLEncoder.encode(urlSecret, "UTF-8");
            } catch (java.io.UnsupportedEncodingException e) {
                mUtil.log(e.getMessage());
            }
            newEndPointURL = newEndPointURL.concat(urlSecretEncoded);
        }

        MainHttpClient client = new MainHttpClient(newEndPointURL, mContext);
        try {
            RequestBody body = RequestBody.create(JSON, createMessageResultJSON(results));
            client.setMethod(BaseHttpClient.HttpMethod.POST);
            client.setRequestBody(body);
            client.execute();
        } catch (Exception e) {
            mUtil.log(mContext.getString(R.string.message_processed_failed));
        } finally {
            if (HttpStatus.SC_OK == client.getResponse().code()) {
                mUtil.log(mContext.getString(R.string.message_processed_success));
            }
        }
    }

    /**
     * This method is handling POST ?task=sent queued_messages
     *
     * @param syncUrl url to web server
     * @return parsed server response whit information about request success or failure and list of
     * message uuids
     */
    public MessagesUUIDSResponse sendQueuedMessagesPOSTRequest(SyncUrl syncUrl,
            QueuedMessages messages) {
        MessagesUUIDSResponse response = null;
        if (null != messages && !messages.getQueuedMessages().isEmpty()) {
            String newEndPointURL = syncUrl.getUrl().concat(TASK_SENT_URL_PARAM);
            MainHttpClient client = new MainHttpClient(newEndPointURL, mContext);
            try {
                RequestBody body = RequestBody.create(JSON, createQueuedMessagesJSON(messages));
                client.setMethod(BaseHttpClient.HttpMethod.POST);
                client.setRequestBody(body);
                client.execute();
            }catch (Exception e) {
                e.printStackTrace();
                mUtil.log("process crashed");
                mUtil.log(mContext.getString(R.string.message_processed_failed));
                Util.logActivities(mContext,
                        mContext.getString(R.string.message_processed_failed) + " " + e
                                .getMessage());
            } finally {
                if (HttpStatus.SC_OK == client.getResponse().code()) {

                    mUtil.log(mContext.getString(R.string.message_processed_success));
                    response = parseMessagesUUIDSResponse(client);
                    response.setSuccess(true);
                    Util.logActivities(mContext,
                            mContext.getString(R.string.message_processed_success));

                } else {
                    response = new MessagesUUIDSResponse(client.getResponse().code());
                    Util.logActivities(mContext,
                            mContext.getString(R.string.queued_messages_request_status,
                                    client.getResponse().code(), client.getResponse()));
                }
            }
        }
        return response;
    }

    /**
     * This method for handling GET ?task=result
     *
     * @param syncUrl url to web server
     * @return MessagesUUIDSResponse parsed server response whit information about request success
     * or failure and list of message uuids
     */
    public MessagesUUIDSResponse sendMessageResultGETRequest(SyncUrl syncUrl) {
        MessagesUUIDSResponse response;
        String newEndPointURL = syncUrl.getUrl().concat(TASK_RESULT_URL_PARAM);

        final String urlSecret = syncUrl.getSecret();

        if (!TextUtils.isEmpty(urlSecret)) {
            String urlSecretEncoded = urlSecret;
            newEndPointURL = newEndPointURL.concat("&secret=");
            try {
                urlSecretEncoded = URLEncoder.encode(urlSecret, "UTF-8");
            } catch (java.io.UnsupportedEncodingException e) {
                mUtil.log(e.getMessage());
            }
            newEndPointURL = newEndPointURL.concat(urlSecretEncoded);
        }

        MainHttpClient client = new MainHttpClient(newEndPointURL, mContext);
        try {
            client.setMethod(BaseHttpClient.HttpMethod.GET);
            client.execute();
        } catch (JSONException e) {
            mUtil.log(mContext.getString(R.string.message_processed_json_failed));
            Util.logActivities(mContext,
                    mContext.getString(R.string.message_processed_json_failed) + " " + e
                            .getMessage());
        } catch (Exception e) {
            mUtil.log(mContext.getString(R.string.message_processed_failed));
            Util.logActivities(mContext,
                    mContext.getString(R.string.message_processed_failed) + " " + e.getMessage());
        } finally {
            if (HttpStatus.SC_OK == client.getResponse().code()) {
                response = parseMessagesUUIDSResponse(client);
                response.setSuccess(true);
            } else {
                response = new MessagesUUIDSResponse(client.getResponse().code());
                Util.logActivities(mContext,
                        mContext.getString(R.string.messages_result_request_status,
                                client.getResponse().code(), client.getResponse()));
            }
        }
        return response;
    }

    private String createMessageResultJSON(List<MessageResult> messageResults)
            throws JSONException {
        JSONObject messageResultsObject = new JSONObject();
        messageResultsObject.put(MESSAGE_RESULT_JSON_KEY, JsonUtils.objToJson(messageResults));
        return messageResultsObject.toString();
    }

    private String createQueuedMessagesJSON(QueuedMessages queuedMessages) throws JSONException {
        return JsonUtils.objToJson(queuedMessages);
    }

    private MessagesUUIDSResponse parseMessagesUUIDSResponse(MainHttpClient client) {
        MessagesUUIDSResponse response;
        try {

            final Gson gson = new Gson();
            final int code = client.getResponse().code();
            response = gson.fromJson(client.getResponse().body().charStream(),MessagesUUIDSResponse.class);
            response.setStatusCode(code);
        } catch (Exception e) {
            e.printStackTrace();
            response = new MessagesUUIDSResponse(client.getResponse().code());
            mUtil.log(mContext.getString(R.string.message_processed_json_failed));
        }
        return response;
    }

}
