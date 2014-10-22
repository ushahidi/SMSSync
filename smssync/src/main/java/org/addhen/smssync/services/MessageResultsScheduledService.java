package org.addhen.smssync.services;

import android.content.Intent;

import org.addhen.smssync.MainApplication;
import org.addhen.smssync.R;
import org.addhen.smssync.controllers.MessageResultsController;
import org.addhen.smssync.models.MessageResult;
import org.addhen.smssync.models.MessagesUUIDSResponse;
import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.util.ServicesConstants;
import org.addhen.smssync.util.Util;

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
        log("checking scheduled task services");
        Util.logActivities(this, getString(R.string.task_scheduler_running));
        for (SyncUrl syncUrl : model.loadByStatus(ServicesConstants.ACTIVE_SYNC_URL)) {
            MessagesUUIDSResponse response = mMessageResultsController.sendMessageResultGETRequest(syncUrl);
            if (response.isSuccess()) {
                List<MessageResult> messageResults = MainApplication.mDb.messagesContentProvider.fetchMessageResultsByUuid(response.getUuids());
                mMessageResultsController.sendMessageResultPOSTRequest(syncUrl, messageResults);
            }
        }
    }

}
