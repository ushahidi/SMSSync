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

package org.addhen.smssync.services;

import android.content.Intent;

import org.addhen.smssync.App;
import org.addhen.smssync.controllers.MessageResultsController;
import org.addhen.smssync.models.Message;
import org.addhen.smssync.models.MessageResult;
import org.addhen.smssync.models.MessagesUUIDSResponse;
import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamil Kalfas(kkalfas@soldevelo.com) on 29.04.14.
 */
public class MessageResultsScheduledService extends SmsSyncServices {

    private static final String CLASS_TAG = MessageResultsScheduledService.class.getSimpleName();

    private MessageResultsController mMessageResultsController;

    public MessageResultsScheduledService() {
        super(CLASS_TAG);
        mMessageResultsController = new MessageResultsController(this);
    }

    @Override
    public void executeTask(Intent intent) {
        log("checking scheduled message result services");
        Util.logActivities(this, "Checking scheduled message result services");
        List<SyncUrl> syncUrls = App.getDatabaseInstance().getSyncUrlInstance().fetchSyncUrlByStatus(SyncUrl.Status.ENABLED);
        for (SyncUrl syncUrl : syncUrls) {
            MessagesUUIDSResponse response = mMessageResultsController
                    .sendMessageResultGETRequest(syncUrl);
            if (response.isSuccess()) {
                final List<MessageResult> messageResults = new ArrayList<>();
                if (response.getUuids() != null) {
                    for (String uuids : response.getUuids()) {
                        Message msg = App.getDatabaseInstance().getMessageInstance().fetchByUuid(uuids);
                        if (msg != null) {
                            MessageResult messageResult = new MessageResult(msg.getUuid(),
                                    msg.getSentResultCode(), msg.getSentResultMessage(),
                                    msg.getDeliveryResultCode(), msg.getDeliveryResultMessage());
                            messageResults.add(messageResult);
                        }
                    }
                }
                mMessageResultsController.sendMessageResultPOSTRequest(syncUrl, messageResults);
            }
        }
    }
}
