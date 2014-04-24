package org.addhen.smssync.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamil Kalfas(kkalfas@soldevelo.com) on 24.04.14.
 */
public class QueuedMessages implements Serializable {
    @SerializedName("queued_messages")
    private List<String> queuedMessages;

    public QueuedMessages() {
        queuedMessages = new ArrayList<String>();
    }

    public List<String> getQueuedMessages() {
        return queuedMessages;
    }

    public void setQueuedMessages(List<String> queuedMessages) {
        this.queuedMessages = queuedMessages;
    }
}
