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

package org.addhen.smssync.presentation.model;

import com.addhen.android.raiburari.presentation.model.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * @author Henry Addo
 */
public class MessageModel extends Model implements Parcelable {

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

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public Date getDeliveredMessageDate() {
        return deliveredMessageDate;
    }

    public void setDeliveredMessageDate(Date deliveredMessageDate) {
        this.deliveredMessageDate = deliveredMessageDate;
    }

    public String getDeliveryResultMessage() {
        return deliveryResultMessage;
    }

    public void setDeliveryResultMessage(String deliveryResultMessage) {
        this.deliveryResultMessage = deliveryResultMessage;
    }

    public int getDeliveryResultCode() {
        return deliveryResultCode;
    }

    public void setDeliveryResultCode(int deliveryResultCode) {
        this.deliveryResultCode = deliveryResultCode;
    }

    public String getSentResultMessage() {
        return sentResultMessage;
    }

    public void setSentResultMessage(String sentResultMessage) {
        this.sentResultMessage = sentResultMessage;
    }

    public int getSentResultCode() {
        return sentResultCode;
    }

    public void setSentResultCode(int sentResultCode) {
        this.sentResultCode = sentResultCode;
    }

    public Type getMessageType() {
        return messageType;
    }

    public void setMessageType(Type messageType) {
        this.messageType = messageType;
    }

    public String getMessageUuid() {
        return messageUuid;
    }

    public void setMessageUuid(String messageUuid) {
        this.messageUuid = messageUuid;
    }

    public Date getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(Date messageDate) {
        this.messageDate = messageDate;
    }

    public String getMessageFrom() {
        return messageFrom;
    }

    public void setMessageFrom(String messageFrom) {
        this.messageFrom = messageFrom;
    }

    public enum Status {
        UNCONFIRMED, FAILED, SENT
    }

    public enum Type {
        TASK, PENDING, ALERT
    }

    public MessageModel() {
        // Do nothing
    }

    protected MessageModel(Parcel in) {
        _id = in.readByte() == 0x00 ? null : in.readLong();
        messageBody = in.readString();
        messageFrom = in.readString();
        long tmpMessageDate = in.readLong();
        messageDate = tmpMessageDate != -1 ? new Date(tmpMessageDate) : null;
        messageUuid = in.readString();
        messageType = (Type) in.readValue(Type.class.getClassLoader());
        sentResultCode = in.readInt();
        sentResultMessage = in.readString();
        deliveryResultCode = in.readInt();
        deliveryResultMessage = in.readString();
        long tmpDeliveredDate = in.readLong();
        deliveredMessageDate = tmpDeliveredDate != -1 ? new Date(tmpDeliveredDate) : null;
        retries = in.readInt();
        status = (Status) in.readValue(Status.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (_id == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(_id);
        }
        dest.writeString(messageBody);
        dest.writeString(messageFrom);
        dest.writeLong(messageDate != null ? messageDate.getTime() : -1L);
        dest.writeString(messageUuid);
        dest.writeValue(messageType);
        dest.writeInt(sentResultCode);
        dest.writeString(sentResultMessage);
        dest.writeInt(deliveryResultCode);
        dest.writeString(deliveryResultMessage);
        dest.writeLong(deliveredMessageDate != null ? deliveredMessageDate.getTime() : -1L);
        dest.writeInt(retries);
        dest.writeValue(status);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MessageModel> CREATOR
            = new Parcelable.Creator<MessageModel>() {
        @Override
        public MessageModel createFromParcel(Parcel in) {
            return new MessageModel(in);
        }

        @Override
        public MessageModel[] newArray(int size) {
            return new MessageModel[size];
        }
    };
}
