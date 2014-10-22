package org.addhen.smssync.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Kamil Kalfas(kkalfas@soldevelo.com) on 16.04.14.
 */
public class MessageResult implements Serializable {

    @SerializedName("uuid")
    private String messageUUID;

    @SerializedName("sent_result_code")
    private int sentResultCode;

    @SerializedName("sent_result_message")
    private String sentResultMessage;

    @SerializedName("delivered_result_code")
    private int deliveryResultCode;

    @SerializedName("delivered_result_message")
    private String deliveryResultMessage;

    public MessageResult(String messageUUID, int sentResultCode, String sentResultMessage, int deliveryResultCode, String deliveryResultMessage) {
        this.messageUUID = messageUUID;
        this.sentResultCode = sentResultCode;
        this.sentResultMessage = sentResultMessage;
        this.deliveryResultCode = deliveryResultCode;
        this.deliveryResultMessage = deliveryResultMessage;
    }

    public String getMessageUUID() {
        return messageUUID;
    }

    public void setMessageUUID(String messageUUID) {
        this.messageUUID = messageUUID;
    }

    public int getSentResultCode() {
        return sentResultCode;
    }

    public void setSentResultCode(int sentResultCode) {
        this.sentResultCode = sentResultCode;
    }

    public String getSentResultMessage() {
        return sentResultMessage;
    }

    public void setSentResultMessage(String sentResultMessage) {
        this.sentResultMessage = sentResultMessage;
    }

    public int getDeliveryResultCode() {
        return deliveryResultCode;
    }

    public void setDeliveryResultCode(int deliveryResultCode) {
        this.deliveryResultCode = deliveryResultCode;
    }

    public String getDeliveryResultMessage() {
        return deliveryResultMessage;
    }

    public void setDeliveryResultMessage(String deliveryResultMessage) {
        this.deliveryResultMessage = deliveryResultMessage;
    }
}
