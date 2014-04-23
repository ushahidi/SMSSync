package org.addhen.smssync.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.addhen.smssync.R;
import org.addhen.smssync.models.Message;
import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.net.MainHttpClient;
import org.addhen.smssync.models.MessageResult;
import org.addhen.smssync.util.MessageResultKeys;
import org.addhen.smssync.util.ServicesConstants;
import org.addhen.smssync.util.Util;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kamil Kalfas(kkalfas@soldevelo.com) on 23.04.14.
 *
 * This class handling Message Result API
 *
 */
public class MessageResultsController {

    private Context mContext;

    private Util util;

    public MessageResultsController(Context mContext) {
        this.mContext = mContext;
        util = new Util();
    }

    /**
     * This method is handling POST message_result
     *
     * @param intent                - intent with data of sent sms
     * @param code                  - result code which is value of getResultCode
     * @param message               - information about message status
     * @param receiverTypeCode      - is the type of result with value save or delivered
     */
    public void sentNotificationToWebService(Intent intent, int code, String message, String receiverTypeCode) {
        Bundle extras = intent.getExtras();
        MessageResult result = null;
        if (null != extras) {
            Message sms = (Message) extras.getSerializable(receiverTypeCode);
            result = new MessageResult(sms.getUuid(), receiverTypeCode, code, message);
        }
        if (null != result) {
            SyncUrl syncUrl = new SyncUrl();
            for (SyncUrl url : syncUrl.loadByStatus(ServicesConstants.ACTIVE_SYNC_URL)) {
                SyncUrl newEndPointURL = url;
                newEndPointURL.setUrl(url.getUrl().concat(ServicesConstants.TASK_SENT_URL_PARAM));
                MainHttpClient client = new MainHttpClient(newEndPointURL.getUrl(), mContext);
                try {
                    client.setMethod("POST");
                    client.setEntity(createMessageResultJSON(result));
                    client.execute();
                } catch (JSONException e) {
                    util.log(mContext.getString(R.string.message_processed_json_failed));
                } catch (Exception e) {
                    util.log(mContext.getString(R.string.message_processed_failed));
                } finally {
                    if (HttpStatus.SC_OK != client.getResponseCode()) {
                        //TODO: save result somewhere and try to send request again
                    } else {
                        util.log(mContext.getString(R.string.message_processed_success));
                    }
                }
            }
        }
    }

    /**
     * Creates JSON response for POST message_result
     *
     */
    private String createMessageResultJSON(MessageResult messageResult) throws JSONException {
        JSONObject messageResultsObject = new JSONObject();
        JSONObject messageInfoObject = new JSONObject();
        messageInfoObject.put(MessageResultKeys.KEY_ID, messageResult.getId());
        messageInfoObject.put(MessageResultKeys.KEY_TYPE, messageResult.getType());
        messageInfoObject.put(MessageResultKeys.KEY_CODE, messageResult.getResultCode());
        messageInfoObject.put(MessageResultKeys.KEY_MESSAGE, messageResult.getMessage());
        messageResultsObject.put(MessageResultKeys.KEY_MESSAGE_RESULT, messageInfoObject);
        return messageResultsObject.toString();
    }
}
