package org.addhen.smssync.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Kamil Kalfas(kkalfas@soldevelo.com) on 24.04.14.
 */

public class TaskMessage implements Serializable {

    private String message;

    @SerializedName("to")
    private String sentTo;

    @SerializedName("sent_by")
    private String sentBy;

    private String uuid;

    public String getMessage() {
        return message;
    }

    public String getSentTo() {
        return sentTo;
    }

    public String getSentBy() {
        return sentBy;
    }

    public String getUuid() {
        return uuid;
    }

    public TaskMessage(String message, String sentTo, String sentBy, String uuid) {
        this.message = message;
        this.sentTo = sentTo;
        this.sentBy = sentBy;
        this.uuid = uuid;
    }
}
