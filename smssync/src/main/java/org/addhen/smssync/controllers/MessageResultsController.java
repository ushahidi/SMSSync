package org.addhen.smssync.controllers;

import android.content.Context;

import com.google.gson.reflect.TypeToken;

import org.addhen.smssync.R;
import org.addhen.smssync.models.MessagesUUIDSResponse;
import org.addhen.smssync.models.QueuedMessages;
import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.net.MainHttpClient;
import org.addhen.smssync.models.MessageResult;
import org.addhen.smssync.util.JsonUtils;
import org.addhen.smssync.util.Util;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Kamil Kalfas(kkalfas@soldevelo.com) on 23.04.14.
 *
 * This class handling Message Results API
 *
 * POST ?task=sent queued_messages {@link #sendQueuedMessagesPOSTRequest(org.addhen.smssync.models.SyncUrl, org.addhen.smssync.models.QueuedMessages)}
 * POST ?task=results message_results {@link #sendMessageResultPOSTRequest(org.addhen.smssync.models.SyncUrl, java.util.List)}
 * GET  ?task=results {@link #sendMessageResultGETRequest(org.addhen.smssync.models.SyncUrl)}
 */
public class MessageResultsController {

    private static final String MESSAGE_RESULT_JSON_KEY = "message_result";
    private static final String TASK_SENT_URL_PARAM = "/add?task=sent";
    private static final String TASK_RESULT_URL_PARAM = "/add?task=result";
    private static final String POST_METHOD = "POST";
    private static final String GET_METHOD = "GET";

    private Context mContext;

    private Util mUtil;

    public MessageResultsController(Context mContext) {
        this.mContext = mContext;
        mUtil = new Util();
    }

    /**
     * This method is handling POST ?task=results message_result
     *
     * @param syncUrl   url to web server
     * @param results   list of message result data
     */
    public void sendMessageResultPOSTRequest(SyncUrl syncUrl, List<MessageResult> results) {
        SyncUrl newEndPointURL = syncUrl;
        newEndPointURL.setUrl(syncUrl.getUrl().substring(0, syncUrl.getUrl().lastIndexOf("/")).concat(TASK_SENT_URL_PARAM));
        MainHttpClient client = new MainHttpClient(newEndPointURL.getUrl(), mContext);
        try {
            client.setMethod(POST_METHOD);
            client.setEntity(createMessageResultJSON(results));
            client.execute();
        } catch (JSONException e) {
            mUtil.log(mContext.getString(R.string.message_processed_json_failed));
        } catch (Exception e) {
            mUtil.log(mContext.getString(R.string.message_processed_failed));
        } finally {
            if (HttpStatus.SC_OK == client.getResponseCode()) {
                mUtil.log(mContext.getString(R.string.message_processed_success));
            }
        }
    }

    /**
     * This method is handling POST ?task=sent queued_messages
     *
     * @param syncUrl   url to web server
     * @param messages
     * @return parsed server response whit information about request success or failure and list of message uuids
     */
    public MessagesUUIDSResponse sendQueuedMessagesPOSTRequest(SyncUrl syncUrl, QueuedMessages messages) {
        MessagesUUIDSResponse response = null;
        if (null != messages && !messages.getQueuedMessages().isEmpty()) {
            SyncUrl newEndPointURL = syncUrl;
            newEndPointURL.setUrl(syncUrl.getUrl().substring(0, syncUrl.getUrl().lastIndexOf("/")).concat(TASK_SENT_URL_PARAM));
            MainHttpClient client = new MainHttpClient(newEndPointURL.getUrl(), mContext);
            try {
                client.setMethod(POST_METHOD);
                client.setEntity(createQueuedMessagesJSON(messages));
                client.execute();
            } catch (JSONException e) {
                mUtil.log(mContext.getString(R.string.message_processed_json_failed));
            } catch (Exception e) {
                mUtil.log(mContext.getString(R.string.message_processed_failed));
            } finally {
                if (HttpStatus.SC_OK == client.getResponseCode()) {
                    mUtil.log(mContext.getString(R.string.message_processed_success));
                    response = parseMessagesUUIDSResponse(client);
                    response.setSuccess(true);
                } else {
                    response = new MessagesUUIDSResponse(client.getResponseCode());
                    Util.logActivities(mContext, mContext.getString(R.string.queued_messages_request_status, client.getResponseCode(), client.getResponse()));
                }
            }
        }
        return response;
    }

    /**
     * This method is handling GET ?task=results
     *
     * @param syncUrl url to web server
     * @return MessagesUUIDSResponse parsed server response whit information about request success or failure and list of message uuids
     */
    public MessagesUUIDSResponse sendMessageResultGETRequest(SyncUrl syncUrl) {
        MessagesUUIDSResponse response = null;
        SyncUrl newEndPointURL = syncUrl;
        newEndPointURL.setUrl(syncUrl.getUrl().substring(0, syncUrl.getUrl().lastIndexOf("/")).concat(TASK_RESULT_URL_PARAM));
        MainHttpClient client = new MainHttpClient(newEndPointURL.getUrl(), mContext);
        try {
            client.setMethod(GET_METHOD);
            client.execute();
        } catch (JSONException e) {
            mUtil.log(mContext.getString(R.string.message_processed_json_failed));
        } catch (Exception e) {
            mUtil.log(mContext.getString(R.string.message_processed_failed));
        } finally {
            if (HttpStatus.SC_OK == client.getResponseCode()) {
                response = parseMessagesUUIDSResponse(client);
                response.setSuccess(true);
            } else {
                response = new MessagesUUIDSResponse(client.getResponseCode());
                Util.logActivities(mContext, mContext.getString(R.string.messages_result_request_status, client.getResponseCode(), client.getResponse()));
            }
        }
        return response;
    }

    private String createMessageResultJSON(List<MessageResult> messageResults) throws JSONException {
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
            response = JsonUtils.getObj(client.getResponse(), MessagesUUIDSResponse.class);
            response.setStatusCode(client.getResponseCode());
        } catch (Exception e) {
            response = new MessagesUUIDSResponse(client.getResponseCode());
            mUtil.log(mContext.getString(R.string.message_processed_json_failed));
        }
        return response;
    }

}
