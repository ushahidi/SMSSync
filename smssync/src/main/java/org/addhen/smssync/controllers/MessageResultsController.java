/*
 * Copyright (c) 2010 - 2015 Ushahidi Inc
 * All rights reserved
 * Contact: team@ushahidi.com
 * Website: http://www.ushahidi.com
 * GNU Lesser General Public License Usage
 * This file may be used under the terms of the GNU Lesser
 * General Public License version 3 as published by the Free Software
 * Foundation and appearing in the file LICENSE.LGPL included in the
 * packaging of this file. Please review the following information to
 * ensure the GNU Lesser General Public License version 3 requirements
 * will be met: http://www.gnu.org/licenses/lgpl.html.
 *
 * If you have questions regarding the use of this file, please contact
 * Ushahidi developers at team@ushahidi.com.
 */

package org.addhen.smssync.controllers;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;

import org.addhen.smssync.R;
import org.addhen.smssync.models.MessageResult;
import org.addhen.smssync.models.MessagesUUIDSResponse;
import org.addhen.smssync.models.QueuedMessages;
import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.net.HttpMethod;
import org.addhen.smssync.net.MainHttpClient;
import org.addhen.smssync.util.JsonUtils;
import org.addhen.smssync.util.Util;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.List;

/**
 * Created by Kamil Kalfas(kkalfas@soldevelo.com) on 23.04.14.
 * <p/>
 * This class handling Message Results API
 * <p/>
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

            client.setMethod(HttpMethod.POST);
            client.setStringEntity(createMessageResultJSON(results));
            client.setHeader("Accept", "application/json");
            client.setHeader("Content-type", "application/json");
            client.execute();
        } catch (Exception e) {
            mUtil.log(mContext.getString(R.string.message_processed_failed));
        } finally {
            if (client != null) {
                if (HttpStatus.SC_OK == client.responseCode()) {
                    mUtil.log(mContext.getString(R.string.message_processed_success));
                }
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
                client.setMethod(HttpMethod.POST);
                client.setStringEntity(createQueuedMessagesJSON(messages));
                client.setHeader("Accept", "application/json");
                client.setHeader("Content-type", "application/json");
                client.execute();
            } catch (Exception e) {
                e.printStackTrace();
                mUtil.log("process crashed");
                mUtil.log(mContext.getString(R.string.message_processed_failed));
                Util.logActivities(mContext,
                        mContext.getString(R.string.message_processed_failed) + " " + e
                                .getMessage());
            } finally {
                if (client != null) {
                    if (HttpStatus.SC_OK == client.responseCode()) {

                        mUtil.log(mContext.getString(R.string.message_processed_success));
                        response = parseMessagesUUIDSResponse(client);
                        response.setSuccess(true);
                        Util.logActivities(mContext,
                                mContext.getString(R.string.message_processed_success));

                    } else {
                        response = new MessagesUUIDSResponse(client.responseCode());
                        Util.logActivities(mContext,
                                mContext.getString(R.string.queued_messages_request_status,
                                        client.responseCode(), client.getResponse()));
                    }
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
        MessagesUUIDSResponse response = null;
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
            client.setMethod(HttpMethod.GET);
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
            if (client != null) {
                if (HttpStatus.SC_OK == client.responseCode()) {
                    response = parseMessagesUUIDSResponse(client);
                    response.setSuccess(true);
                } else {
                    response = new MessagesUUIDSResponse(client.responseCode());
                    Util.logActivities(mContext,
                            mContext.getString(R.string.messages_result_request_status,
                                    client.responseCode(), client.getResponse()));
                }
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
            response = gson.fromJson(client.getResponse(), MessagesUUIDSResponse.class);
            if (response == null) {
                response = new MessagesUUIDSResponse(client.responseCode());
            } else {
                response.setStatusCode(client.responseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = new MessagesUUIDSResponse(client.responseCode());
            mUtil.log(mContext.getString(R.string.message_processed_json_failed));
        }
        return response;
    }

}
