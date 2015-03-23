/*******************************************************************************
 *  Copyright (c) 2010 - 2013 Ushahidi Inc
 *  All rights reserved
 *  Contact: team@ushahidi.com
 *  Website: http://www.ushahidi.com
 *  GNU Lesser General Public License Usage
 *  This file may be used under the terms of the GNU Lesser
 *  General Public License version 3 as published by the Free Software
 *  Foundation and appearing in the file LICENSE.LGPL included in the
 *  packaging of this file. Please review the following information to
 *  ensure the GNU Lesser General Public License version 3 requirements
 *  will be met: http://www.gnu.org/licenses/lgpl.html.
 *
 * If you have questions regarding the use of this file, please contact
 * Ushahidi developers at team@ushahidi.com.
 ******************************************************************************/

package org.addhen.smssync.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import nl.qbusict.cupboard.annotation.Column;

/**
 * Class to handle set and getters.
 *
 * @author eyedol
 */
public class Message extends Model implements Serializable {

    private static final long serialVersionUID = -8314239850052623552L;

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

    @Column("message_type")
    private Type messageType;

    @Column("sent_result_code")
    private int sentResultCode;

    @Column("sent_result_message")
    private String sentResultMessage;

    @Column("delivery_result_code")
    private int deliveryResultCode;

    @Column("delivery_result_message")
    private String deliveryResultMessage;

    @Column("retries")
    private int retries;

    @Column("status")
    private Status status;

    public String getBody() {
        return messageBody;
    }

    public void setBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getPhoneNumber() {
        return messageFrom;
    }

    public void setPhoneNumber(String messageFrom) {
        this.messageFrom = messageFrom;
    }

    public Date getDate() {
        return messageDate;
    }

    public void setDate(Date messageDate) {
        this.messageDate = messageDate;
    }

    public String getUuid() {
        return messageUuid;
    }

    public void setUuid(String messageUuid) {
        this.messageUuid = messageUuid;
    }

    public Type getType() {
        return messageType;
    }

    public void setType(Type messageType) {
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

    public int getRetries() {
        return this.retries;
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

    public enum Status {
        UNCONFIRMED, FAILED, SENT;
    }

    public enum Type {
        TASK, PENDING;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + getId() + '\'' +
                "messageBody='" + getBody() + '\'' +
                ", messageFrom='" + getPhoneNumber() + '\'' +
                ", messageDate='" + getDate() + '\'' +
                ", messageUuid='" + getUuid() + '\'' +
                ", messageType=" + getType() +
                ", sentResultCode=" + getSentResultCode() +
                ", sentResultMessage='" + getSentResultMessage() + '\'' +
                ", deliveryResultCode=" + getDeliveryResultCode() +
                ", deliveryResultMessage='" + getDeliveryResultMessage() + '\'' +
                ", retries=" + getRetries() +
                ", status=" + getStatus() +
                '}';
    }
}
