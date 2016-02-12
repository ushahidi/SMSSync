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

import java.util.Date;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public final class DomainEntityFixture {

    public static final Long ID = 2l;

    private static Date mDate = new Date();

    public static FilterEntity getFilterEntity() {
        FilterEntity filterEntity = new FilterEntity();
        filterEntity._id = ID;
        filterEntity.setPhoneNumber("000000000");
        filterEntity.setStatus(FilterEntity.Status.WHITELIST);
        return filterEntity;
    }

    public static HttpNameValuePair getHttpNameValuePair() {
        return new HttpNameValuePair("key", "value");
    }

    public static LogEntity getLogEntity() {
        LogEntity logEntity = new LogEntity();
        logEntity._id = ID;
        logEntity.setMessage("Log message");
        return logEntity;
    }

    public static MessageEntity getMessageEntity() {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setId(ID);
        messageEntity.setDeliveredMessageDate(mDate);
        messageEntity.setDeliveryResultCode(1);
        messageEntity.setDeliveryResultMessage("delivered");
        messageEntity.setMessageBody("Message body");
        messageEntity.setMessageDate(mDate);
        messageEntity.setMessageType(MessageEntity.Type.PENDING);
        messageEntity.setMessageFrom("000000000");
        messageEntity.setMessageUuid("uuid0123456");
        messageEntity.setSentResultCode(1);
        messageEntity.setStatus(MessageEntity.Status.SENT);
        messageEntity.setRetries(3);
        return messageEntity;
    }

    public static WebServiceEntity getWebServiceEntity() {
        WebServiceEntity webServiceEntity = new WebServiceEntity();
        webServiceEntity.setId(ID);
        webServiceEntity.setSecret("secret");
        webServiceEntity.setKeywords("keywords");
        webServiceEntity.setTitle("title");
        webServiceEntity.setSyncScheme(new SyncSchemeEntity());
        webServiceEntity.setUrl("url");
        return webServiceEntity;
    }
}
