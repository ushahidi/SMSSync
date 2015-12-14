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

package org.addhen.smssync.domain.entity;

import com.addhen.android.raiburari.domain.entity.Entity;

import java.util.Date;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class MessageEntity extends Entity {

    private String messageBody;

    private String messageFrom;

    // This holds the sent/received timestamp depending on the context used for retrieval or setting
    private Date messageDate;

    private String messageUuid;

    private Type messageType;

    private int sentResultCode;

    private String sentResultMessage;

    private int deliveryResultCode;

    private String deliveryResultMessage;

    private Date deliveredMessageDate;

    private int retries;

    private Status status;

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        _id = id;
    }

    public String getMessageFrom() {
        return messageFrom;
    }

    public void setMessageFrom(String messageFrom) {
        this.messageFrom = messageFrom;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
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

    public Date getDeliveredMessageDate() {
        return deliveredMessageDate;
    }

    public void setDeliveredMessageDate(Date deliveredMessageDate) {
        this.deliveredMessageDate = deliveredMessageDate;
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
        return "MessageEntity{"
                + "messageBody='" + messageBody + '\''
                + ", messageFrom='" + messageFrom + '\''
                + ", messageDate=" + messageDate
                + ", messageUuid='" + messageUuid + '\''
                + ", messageType=" + messageType
                + ", sentResultCode=" + sentResultCode
                + ", sentResultMessage='" + sentResultMessage + '\''
                + ", deliveryResultCode=" + deliveryResultCode
                + ", deliveryResultMessage='" + deliveryResultMessage + '\''
                + ", deliveredMessageDate=" + deliveredMessageDate
                + ", retries=" + retries
                + ", status=" + status
                + '}';
    }

    public enum Status {
        UNCONFIRMED, FAILED, SENT
    }

    public enum Type {
        TASK, PENDING, ALERT
    }
}
