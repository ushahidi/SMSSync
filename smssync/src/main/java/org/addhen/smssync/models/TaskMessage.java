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

package org.addhen.smssync.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Kamil Kalfas(kkalfas@soldevelo.com) on 24.04.14.
 */

public class TaskMessage implements Serializable {

    private String message;

    @SerializedName("to")
    private String sentTo;

    @SerializedName("sent_by")
    private String sentBy;

    private String uuid;

    public TaskMessage(String message, String sentTo, String sentBy, String uuid) {
        this.message = message;
        this.sentTo = sentTo;
        this.sentBy = sentBy;
        this.uuid = uuid;
    }

    public String getMessage() {
        return message;
    }

    public String getSentTo() {
        return sentTo;
    }

    public String getSentBy() {
        return sentBy;
    }

    public String getUuid() {
        return uuid;
    }
}
