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

    public String messageBody;

    public String messageFrom;

    // This holds the sent/received timestamp depending on the context used for retrieval or setting
    public Date messageDate;

    public String messageUuid;

    public Type messageType;

    public int sentResultCode;

    public String sentResultMessage;

    public int deliveryResultCode;

    public String deliveryResultMessage;

    public Date deliveredMessageDate;

    public int retries;

    public Status status;

    public enum Status {
        UNCONFIRMED, FAILED, SENT
    }

    public enum Type {
        TASK, PENDING, ALERT
    }
}
