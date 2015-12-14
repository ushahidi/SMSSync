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

import org.addhen.smssync.BaseRobolectricTestCase;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class MessageEntityTest extends BaseRobolectricTestCase {

    private MessageEntity mMessageEntity;

    @Before
    public void setUp() {
        mMessageEntity = DomainEntityFixture.getMessageEntity();
    }

    @Test
    public void shouldSetMessageEntity() {
        assertNotNull(mMessageEntity);
        assertEquals(DomainEntityFixture.ID, mMessageEntity.getId());
        assertEquals(DomainEntityFixture.getMessageEntity().getDeliveredMessageDate(),
                mMessageEntity.getDeliveredMessageDate());
        assertEquals(DomainEntityFixture.getMessageEntity().getDeliveryResultCode(),
                mMessageEntity.getDeliveryResultCode());
        assertEquals(DomainEntityFixture.getMessageEntity().getDeliveryResultMessage(),
                mMessageEntity.getDeliveryResultMessage());
        assertEquals(DomainEntityFixture.getMessageEntity().getMessageBody(),
                mMessageEntity.getMessageBody());
        assertEquals(DomainEntityFixture.getMessageEntity().getMessageFrom(),
                mMessageEntity.getMessageFrom());
        assertEquals(DomainEntityFixture.getMessageEntity().getMessageType(),
                mMessageEntity.getMessageType());
        assertEquals(DomainEntityFixture.getMessageEntity().getMessageUuid(),
                mMessageEntity.getMessageUuid());
        assertEquals(DomainEntityFixture.getMessageEntity().getRetries(),
                mMessageEntity.getRetries());
        assertEquals(DomainEntityFixture.getMessageEntity().getSentResultCode(),
                mMessageEntity.getSentResultCode());
        assertEquals(DomainEntityFixture.getMessageEntity().getMessageDate(),
                mMessageEntity.getMessageDate());
        assertEquals(DomainEntityFixture.getMessageEntity().getStatus(),
                mMessageEntity.getStatus());
    }
}
