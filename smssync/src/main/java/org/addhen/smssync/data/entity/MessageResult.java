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

package org.addhen.smssync.data.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Kamil Kalfas(kkalfas@soldevelo.com) on 16.04.14.
 */
public class MessageResult implements Serializable {

    private static final long serialVersionUID = 1094372288105228610L;

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

    @SerializedName("sent_timestamp")
    private Date sentTimeStamp;

    @SerializedName("delivered_timestamp")
    private Date deliveredTimeStamp;

    public Date getDeliveredTimeStamp() {
        return deliveredTimeStamp;
    }

    public void setDeliveredTimeStamp(Date deliveredTimeStamp) {
        this.deliveredTimeStamp = deliveredTimeStamp;
    }

    public Date getSentTimeStamp() {
        return sentTimeStamp;
    }

    public void setSentTimeStamp(Date sentTimeStamp) {
        this.sentTimeStamp = sentTimeStamp;
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

    @Override
    public String toString() {
        return "MessageResult{" +
                "messageUUID='" + messageUUID + '\'' +
                ", sentResultCode=" + sentResultCode +
                ", sentResultMessage='" + sentResultMessage + '\'' +
                ", deliveryResultCode=" + deliveryResultCode +
                ", deliveryResultMessage='" + deliveryResultMessage + '\'' +
                ", sentTimeStamp='" + sentTimeStamp + '\'' +
                ", deliveredTimeStamp='" + deliveredTimeStamp + '\'' +
                '}';
    }
}
