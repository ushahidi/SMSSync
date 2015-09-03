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

import com.squareup.okhttp.Response;

import org.addhen.smssync.data.entity.SmssyncResponse;

import android.content.Context;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Basic HTTP client for making a request to URL pass to it
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
@Singleton
public class AppHttpClient extends BaseHttpClient {

    @Inject
    public AppHttpClient(Context context) {
        super(context);
    }

    public Observable<Boolean> makeRequest(String url) {
        return Observable.defer(() -> {
            setUrl(url);
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
                    } catch (IOException e) {
                        e.printStackTrace();
                        Observable.error(e);
                    } catch (JsonSyntaxException e) {
                        status = false;
                    }
                    if (smssyncResponses != null && smssyncResponses.getPayload() != null) {
                        status = true;
                    }
                }
            }
            return Observable.just(status);
        });
    }
}
