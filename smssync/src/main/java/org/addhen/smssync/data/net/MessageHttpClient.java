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

package org.addhen.smssync.data.net;

import com.google.gson.Gson;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.addhen.smssync.R;
import org.addhen.smssync.data.cache.FileManager;
import org.addhen.smssync.data.entity.Message;
import org.addhen.smssync.data.entity.SmssyncResponse;
import org.addhen.smssync.data.entity.SyncScheme;
import org.addhen.smssync.data.entity.SyncUrl;
import org.addhen.smssync.data.util.Logger;
import org.addhen.smssync.domain.entity.HttpNameValuePair;
import org.addhen.smssync.domain.util.DataFormatUtil;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

import static com.squareup.okhttp.internal.Util.UTF_8;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
@Singleton
public class MessageHttpClient extends BaseHttpClient {

    private String mServerError;

    private String mClientError;

    private SmssyncResponse mSmssyncResponse;

    private FileManager mFileManager;

    @Inject
    public MessageHttpClient(Context context, FileManager fileManager) {
        super(context);
        mFileManager = fileManager;
    }

    /**
     * Post sms to the configured sync URL
     *
     * @return boolean
     */
    public boolean postSmsToWebService(SyncUrl syncUrl, Message message, String toNumber,
            String deviceId) {
        Logger.log(MessageHttpClient.class.getSimpleName(), "posting messages");
        initRequest(syncUrl, message, toNumber, deviceId);
        final Gson gson = new Gson();
        try {
            execute();
            Response response = getResponse();
            int statusCode = response.code();
            if (statusCode != 200 && statusCode != 201) {
                setServerError("bad http return code", statusCode);
                return false;
            }

            SmssyncResponse smssyncResponses = gson.fromJson(response.body().string(),
                    SmssyncResponse.class);
            if (smssyncResponses.getPayload().isSuccess()) {
                // auto response message is enabled to be received from the
                // server.
                setServerSuccessResp(smssyncResponses);
                return true;
            }

            String payloadError = smssyncResponses.getPayload().getError();
            if (!TextUtils.isEmpty(payloadError)) {
                setServerError(payloadError, statusCode);
            } else {
                setServerError(response.body().string(), statusCode);
            }
        } catch (Exception e) {
            Observable.error(e);
            log("Request failed", e);
            setClientError("Request failed. " + e.getMessage());
        }
        return false;

    }

    private void initRequest(SyncUrl syncUrl, Message message, String toNumber,
            String deviceId) {
        setUrl(syncUrl.getUrl());
        SyncScheme syncScheme = syncUrl.getSyncScheme();
        SyncScheme.SyncMethod method = syncScheme.getMethod();
        SyncScheme.SyncDataFormat format = syncScheme.getDataFormat();
        // Clear set params before adding new one to clear the previous one
        getParams().clear();
        setHeader("Content-Type", syncScheme.getContentType());
        addParam(syncScheme.getKey(SyncScheme.SyncDataKey.SECRET), syncUrl.getSecret());
        addParam(syncScheme.getKey(SyncScheme.SyncDataKey.FROM), message.getMessageFrom());
        addParam(syncScheme.getKey(SyncScheme.SyncDataKey.MESSAGE), message.getMessageBody());
        addParam(syncScheme.getKey(SyncScheme.SyncDataKey.SENT_TIMESTAMP),
                String.valueOf(message.getMessageDate().getTime())
        );
        addParam(syncScheme.getKey(SyncScheme.SyncDataKey.SENT_TO), toNumber);
        addParam(syncScheme.getKey(SyncScheme.SyncDataKey.MESSAGE_ID), message.getMessageUuid());
        addParam(syncScheme.getKey(SyncScheme.SyncDataKey.DEVICE_ID), deviceId);
        try {
            setHttpEntity(format);
        } catch (Exception e) {
            log("Failed to set request body", e);
            setClientError("Failed to format request body" + e.getLocalizedMessage());
        }

        try {
            switch (method) {
                case POST:
                    setMethod(HttpMethod.POST);
                    break;
                case PUT:
                    setMethod(HttpMethod.PUT);
                    break;
                default:
                    log("Invalid server method");
                    setClientError("Failed to set request method.");
            }
        } catch (Exception e) {
            log("failed to set request method.", e);
            setClientError("Failed to set request method.");
        }

    }

    /**
     * Get HTTP Entity populated with data in a format specified by the current sync scheme
     */
    private void setHttpEntity(SyncScheme.SyncDataFormat format) throws Exception {
        RequestBody body;
        switch (format) {
            case JSON:
                body = RequestBody.create(JSON, DataFormatUtil.makeJSONString(getParams()));
                log("setHttpEntity format JSON");
                mFileManager.appendAndClose("setHttpEntity format JSON");
                break;
            case XML:
                body = RequestBody.create(XML,
                        DataFormatUtil.makeXMLString(getParams(), "payload", UTF_8.name()));
                log("setHttpEntity format XML");
                mFileManager.appendAndClose(mContext.getString(R.string.http_entity_format, "XML"));
                break;
            case URLEncoded:
                log("setHttpEntity format URLEncoded");
                FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
                List<HttpNameValuePair> params = getParams();
                for (HttpNameValuePair pair : params) {
                    formEncodingBuilder.add(pair.getName(), pair.getValue());
                }
                mFileManager.appendAndClose(
                        mContext.getString(R.string.http_entity_format, "URLEncoded"));
                body = formEncodingBuilder.build();
                break;
            default:
                mFileManager.appendAndClose(mContext.getString(R.string.invalid_data_format));
                throw new Exception("Invalid data format");
        }

        setRequestBody(body);

    }

    public String getClientError() {
        return mClientError;
    }

    public void setClientError(String error) {
        log("Client error " + error);
        Resources res = mContext.getResources();
        mClientError = String.format(Locale.getDefault(), "%s",
                res.getString(R.string.sending_failed_custom_error, error));
        mFileManager.appendAndClose(mClientError);
    }

    public String getServerError() {
        return mServerError;
    }

    public void setServerError(String error, int statusCode) {
        log("Server error " + error);
        Resources res = mContext.getResources();
        mServerError = String
                .format(res.getString(R.string.sending_failed_custom_error, error),
                        res.getString(R.string.sending_failed_http_code, statusCode));
        mFileManager.appendAndClose(mServerError);
    }

    public SmssyncResponse getServerSuccessResp() {
        return mSmssyncResponse;
    }

    public void setServerSuccessResp(SmssyncResponse smssyncResponse) {
        mSmssyncResponse = smssyncResponse;
    }
}
