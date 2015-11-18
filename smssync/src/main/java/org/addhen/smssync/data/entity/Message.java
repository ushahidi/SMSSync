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
    public String messageBody;

    @SerializedName("to")
    @Column("messages_from")
    public String messageFrom;

    @SerializedName("timestamp")
    @Column("messages_date")
    public Date messageDate;

    @SerializedName("uuid")
    @Column("message_uuid")
    public String messageUuid;

    @SerializedName("type")
    @Column("message_type")
    public Type messageType;

    @SerializedName("sent_result_code")
    @Column("sent_result_code")
    public int sentResultCode;

    @SerializedName("sent_result_message")
    @Column("sent_result_message")
    public String sentResultMessage;

    @SerializedName("delivery_result_code")
    @Column("delivery_result_code")
    public int deliveryResultCode;

    @SerializedName("delivered_result_message")
    @Column("delivery_result_message")
    public String deliveryResultMessage;

    @SerializedName("delivered_timestamp")
    @Column("delivered_timestamp")
    public Date deliveredDate;

    @Column("retries")
    public int retries;

    @Column("status")
    public Status status;

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
