package org.addhen.smssync.services;

import android.content.Intent;

import org.addhen.smssync.R;
import org.addhen.smssync.controllers.MessageDeliveryController;
import org.addhen.smssync.messages.ProcessMessage;
import org.addhen.smssync.models.MessageResult;
import org.addhen.smssync.models.MessagesUUIDSResponse;
import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.util.ServicesConstants;
import org.addhen.smssync.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamil Kalfas(kkalfas@soldevelo.com) on 29.04.14.
 */
public class MessageDeliveryScheduledService extends SmsSyncServices {

    private static final String CLASS_TAG = MessageDeliveryScheduledService.class.getSimpleName();

    private MessageDeliveryController mMessageDeliveryController;

    private SyncUrl model;

    public MessageDeliveryScheduledService() {
        super(CLASS_TAG);
        model = new SyncUrl();
        mMessageDeliveryController = new MessageDeliveryController(this);
    }

    @Override
    public void executeTask(Intent intent) {
        log("checking scheduled task services");
        Util.logActivities(this, getString(R.string.task_scheduler_running));
        for (SyncUrl syncUrl : model.loadByStatus(ServicesConstants.ACTIVE_SYNC_URL)) {
            MessagesUUIDSResponse response = mMessageDeliveryController.sendMessageResultGETRequest(syncUrl);
            if (response.isSuccess()) {
                List<MessageResult> messageResults = new ArrayList<MessageResult>();
                //TODO: add data fetching DB_TBD.getResultForUIIDs(response.getUuids());
                mMessageDeliveryController.sendMessageResultPOSTRequest(syncUrl, messageResults);
            }
        }
    }

}
