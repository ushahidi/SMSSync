package org.addhen.smssync.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Kamil Kalfas(kkalfas@soldevelo.com) on 16.04.14.
 */
public class MessageResult implements Serializable {

    @SerializedName("id")
    private String messageUUID;

    @SerializedName("type")
    private String type;

    @SerializedName("code")
    private int resultCode;

    @SerializedName("message")
    private String message;

    public MessageResult(String messageUUID, String type, int resultCode, String message) {
        this.messageUUID = messageUUID;
        this.type = type;
        this.resultCode = resultCode;
        this.message = message;
    }

    public String getMessageUUID() {
        return messageUUID;
    }

    public void setMessageUUID(String messageUUID) {
        this.messageUUID = messageUUID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
