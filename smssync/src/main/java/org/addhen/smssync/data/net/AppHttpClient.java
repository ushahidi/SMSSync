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
import com.google.gson.JsonSyntaxException;

import org.addhen.smssync.data.entity.SmssyncResponse;
import org.addhen.smssync.data.entity.SyncScheme;
import org.addhen.smssync.data.entity.SyncUrl;
import org.addhen.smssync.domain.entity.HttpNameValuePair;
import org.addhen.smssync.domain.util.DataFormatUtil;

import android.content.Context;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import timber.log.Timber;

import static okhttp3.internal.Util.UTF_8;

/**
 * Basic HTTP client for making a request to URL pass to it
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
@Singleton
public class AppHttpClient extends BaseHttpClient {

    private static final String TEST_MESSAGE = "Test Message From SMSSync Integration";

    private static final String TEST_FROM = "+000000000000";

    @Inject
    public AppHttpClient(Context context) {
        super(context);
    }

    public Observable<Boolean> makeRequest(SyncUrl syncUrl) {
        return Observable.defer(() -> {
            boolean status = request(syncUrl);
            return Observable.just(status);
        });
    }

    private Boolean request(SyncUrl syncUrl) {
        initTestRequest(syncUrl);
        Boolean status = false;
        try {
            execute();
        } catch (Exception e) {
            log("Request failed", e);
        }
        Response response = getResponse();
        if (response != null) {
            int statusCode = response.code();
            if (statusCode == 200) {
                final Gson gson = new Gson();
                SmssyncResponse smssyncResponses = null;
                try {
                    smssyncResponses = gson.fromJson(response.body().charStream(),
                            SmssyncResponse.class);
                } catch (JsonSyntaxException e) {
                    Timber.e(e.getMessage(), e);
                }
                if (smssyncResponses != null && smssyncResponses.getPayload() != null) {
                    if (smssyncResponses.getPayload().isSuccess() || !smssyncResponses.getPayload()
                            .isSuccess()) {
                        status = true;
                    }
                }
            }
        }
        return status;
    }

    private void initTestRequest(SyncUrl syncUrl) {
        setUrl(syncUrl.getUrl());
        SyncScheme syncScheme = syncUrl.getSyncScheme();
        SyncScheme.SyncMethod method = syncScheme.getMethod();
        SyncScheme.SyncDataFormat format = syncScheme.getDataFormat();

        setHeader("Content-Type", syncScheme.getContentType());
        addParam(syncScheme.getKey(SyncScheme.SyncDataKey.SECRET), syncUrl.getSecret());
        addParam(syncScheme.getKey(SyncScheme.SyncDataKey.FROM), TEST_FROM);
        addParam(syncScheme.getKey(SyncScheme.SyncDataKey.MESSAGE), TEST_MESSAGE);
        try {
            setHttpEntity(format);
        } catch (Exception e) {
            log("Failed to set request body", e);
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
            }
        } catch (Exception e) {
            log("failed to set request method.", e);
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
                break;
            case XML:
                body = RequestBody.create(XML,
                        DataFormatUtil.makeXMLString(getParams(), "payload", UTF_8.name()));
                log("setHttpEntity format XML");
                break;
            case URLEncoded:
                log("setHttpEntity format URLEncoded");
                FormBody.Builder builder = new FormBody.Builder();
                List<HttpNameValuePair> params = getParams();
                for (HttpNameValuePair pair : params) {
                    builder.add(pair.getName(), pair.getValue());
                }
                body = builder.build();
                break;
            default:
                throw new Exception("Invalid data format");
        }
        setRequestBody(body);
    }
}
