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
import static org.junit.Assert.assertNotNull;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class HttpNameValuePairEntityTest extends BaseRobolectricTestCase {

    private HttpNameValuePair mHttpNameValuePair;

    @Before
    public void setUp() {
        mHttpNameValuePair = DomainEntityFixture.getHttpNameValuePair();
    }

    @Test
    public void shouldSetFilterEntity() {
        assertNotNull(mHttpNameValuePair);
        assertEquals(DomainEntityFixture.getHttpNameValuePair().getName(),
                mHttpNameValuePair.getName());
        assertEquals(DomainEntityFixture.getHttpNameValuePair().getValue(),
                mHttpNameValuePair.getValue());
    }
}
