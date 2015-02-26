package org.addhen.smssync.services;

import org.addhen.smssync.App;
import org.addhen.smssync.controllers.MessageResultsController;
import org.addhen.smssync.models.Message;
import org.addhen.smssync.models.MessageResult;
import org.addhen.smssync.models.MessagesUUIDSResponse;
import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.util.Util;

import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamil Kalfas(kkalfas@soldevelo.com) on 29.04.14.
 */
public class MessageResultsScheduledService extends SmsSyncServices {

    private static final String CLASS_TAG = MessageResultsScheduledService.class.getSimpleName();

    private MessageResultsController mMessageResultsController;

    private SyncUrl model;

    public MessageResultsScheduledService() {
        super(CLASS_TAG);
        model = new SyncUrl();
        mMessageResultsController = new MessageResultsController(this);
    }

    @Override
    public void executeTask(Intent intent) {
        log("checking scheduled message result services");
        Util.logActivities(this, "Checking scheduled message result services");
        List<SyncUrl> syncUrls = App.getDatabaseInstance().getSyncUrlInstance().fetchSyncUrlByStatus(SyncUrl.Status.ENABLED);
        for(SyncUrl syncUrl: syncUrls) {
            MessagesUUIDSResponse response = mMessageResultsController
                    .sendMessageResultGETRequest(syncUrl);
            if(response.isSuccess()) {
                final List<MessageResult> messageResults = new ArrayList<>();
                for(String uuids: response.getUuids()) {

                    Message msg = App.getDatabaseInstance().getMessageInstance().fetchByUuid(uuids);
                    MessageResult messageResult = new MessageResult(msg.getUuid(), msg.getSentResultCode(), msg.getSentResultMessage(),msg.getDeliveryResultCode(), msg.getDeliveryResultMessage());
                    messageResults.add(messageResult);
                }
            }
        }
    }
}
