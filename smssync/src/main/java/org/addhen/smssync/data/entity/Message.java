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

import com.addhen.android.raiburari.data.entity.DataEntity;

import java.io.Serializable;
import java.util.Date;

import nl.qbusict.cupboard.annotation.Column;

/**
 * @author Henry Addo
 */
public class Message extends DataEntity implements Serializable {

    private static final long serialVersionUID = 1094372288105228610L;

    @SerializedName("message")
    @Column("messages_body")
    private String messageBody;

    @SerializedName("to")
    @Column("messages_from")
    private String messageFrom;

    @SerializedName("timestamp")
    @Column("messages_date")
    private Date messageDate;

    @SerializedName("uuid")
    @Column("message_uuid")
    private String messageUuid;

    @SerializedName("type")
    @Column("message_type")
    private Type messageType;

    @SerializedName("sent_result_code")
    @Column("sent_result_code")
    private int sentResultCode;

    @SerializedName("sent_result_message")
    @Column("sent_result_message")
    private String sentResultMessage;

    @SerializedName("delivery_result_code")
    @Column("delivery_result_code")
    private int deliveryResultCode;

    @SerializedName("delivered_result_message")
    @Column("delivery_result_message")
    private String deliveryResultMessage;

    @SerializedName("delivered_timestamp")
    @Column("delivered_timestamp")
    private Date deliveredDate;

    @Column("retries")
    private int retries;

    @Column("status")
    private Status status;

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        _id = id;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getMessageFrom() {
        return messageFrom;
    }

    public void setMessageFrom(String messageFrom) {
        this.messageFrom = messageFrom;
    }

    public Date getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(Date messageDate) {
        this.messageDate = messageDate;
    }

    public String getMessageUuid() {
        return messageUuid;
    }

    public void setMessageUuid(String messageUuid) {
        this.messageUuid = messageUuid;
    }

    public Type getMessageType() {
        return messageType;
    }

    public void setMessageType(Type messageType) {
        this.messageType = messageType;
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

    public Date getDeliveredDate() {
        return deliveredDate;
    }

    public void setDeliveredDate(Date deliveredDate) {
        this.deliveredDate = deliveredDate;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Message{"
                + "messageBody='" + messageBody + '\''
                + ", messageFrom='" + messageFrom + '\''
                + ", messageDate=" + messageDate
                + ", messageUuid='" + messageUuid + '\''
                + ", messageType=" + messageType
                + ", sentResultCode=" + sentResultCode
                + ", sentResultMessage='" + sentResultMessage + '\''
                + ", deliveryResultCode=" + deliveryResultCode
                + ", deliveryResultMessage='" + deliveryResultMessage + '\''
                + ", deliveredDate=" + deliveredDate
                + ", retries=" + retries
                + ", status=" + status
                + '}';
    }

    public enum Status {
        @SerializedName("unconfirmed")
        UNCONFIRMED,
        @SerializedName("failed")
        FAILED,
        @SerializedName("sent")
        SENT
    }

    public enum Type {
        @SerializedName("task")
        TASK,
        @SerializedName("pending")
        PENDING,
        @SerializedName("alert")
        ALERT,
    }
}
