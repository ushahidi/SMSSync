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

/**
 * @author Henry Addo
 */
public class Message extends DataEntity implements Serializable {

    private static final long serialVersionUID = 1094372288105228610L;

    @SerializedName("message")
    public String messageBody;

    @SerializedName("to")
    public String messageFrom;

    @SerializedName("timestamp")
    public Date messageDate;

    @SerializedName("uuid")
    public String messageUuid;

    @SerializedName("type")
    public Type messageType;

    @SerializedName("sent_result_code")
    public int sentResultCode;

    @SerializedName("sent_result_message")
    public String sentResultMessage;

    @SerializedName("delivery_result_code")
    public int deliveryResultCode;

    @SerializedName("delivered_result_message")
    public String deliveryResultMessage;

    public int retries;

    public Status status;

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
