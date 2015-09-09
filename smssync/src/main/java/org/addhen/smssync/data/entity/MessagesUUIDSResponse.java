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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamil Kalfas(kkalfas@soldevelo.com) on 24.04.14.
 */
public class MessagesUUIDSResponse {

    private boolean success;

    private int statusCode;

    @SerializedName("message_uuids")
    private List<String> uuids;

    public MessagesUUIDSResponse(int statusCode) {
        this.success = false;
        this.uuids = new ArrayList<>();
        this.statusCode = statusCode;
    }

    public MessagesUUIDSResponse(boolean success, List<String> uuids, int statusCode) {
        this.success = success;
        this.uuids = uuids;
        this.statusCode = statusCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<String> getUuids() {
        return uuids;
    }

    public void setUuids(List<String> uuids) {
        this.uuids = uuids;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public boolean hasUUIDs() {
        return null != uuids && !uuids.isEmpty();
    }

    @Override
    public String toString() {
        return "MessagesUUIDSResponse{" +
                "success=" + success +
                ", statusCode=" + statusCode +
                ", uuids=" + uuids +
                '}';
    }
}
