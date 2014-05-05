package org.addhen.smssync.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamil Kalfas(kkalfas@soldevelo.com) on 24.04.14.
 */
public class MessagesUUIDSResponse {

    private boolean success;

    private int statusCode;

    @SerializedName("message_uuids")
    private List<String> uuids;

    public MessagesUUIDSResponse(int statusCode) {
        this.success = false;
        this.uuids = new ArrayList<String>();
        this.statusCode = statusCode;
    }

    public MessagesUUIDSResponse(boolean success, List<String> uuids, int statusCode) {
        this.success = success;
        this.uuids = uuids;
        this.statusCode = statusCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<String> getUuids() {
        return uuids;
    }

    public void setUuids(List<String> uuids) {
        this.uuids = uuids;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public boolean hasUUIDs() {
        return null != uuids && !uuids.isEmpty();
    }
}
