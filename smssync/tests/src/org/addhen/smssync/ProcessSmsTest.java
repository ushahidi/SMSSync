/*****************************************************************************
 ** Copyright (c) 2010 - 2012 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 **
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.
 **
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 **
 *****************************************************************************/

package org.addhen.smssync;

import org.addhen.smssync.test.BaseTestCase;

import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * @author eyedol
 */
public class ProcessSmsTest extends BaseTestCase {

    ProcessSms processSms;
    String from;
    String body;
    String timestamp;
    String uuid;
    String filterText;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        processSms = new ProcessSms(getContext());
        from = "58484";
        body = "crowdmap new hello dummy world who is coming";
        timestamp = "1372082306000";
        uuid = "4b6a7fb2-6e5a-4cc6-bfde-53b5af9cf850";
        filterText = "hello,world";
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        processSms = null;
        from = null;
        body = null;
        timestamp = null;
        uuid = null;
        filterText = null;
    }
    
    @SmallTest
    public void testRouteSms() {

        boolean routed = processSms.routeSms(from, body, timestamp, uuid);
        assertTrue(routed);
        
    }

    /**
     * Test that a pending message can successfully be routed to the sync URL.
     */
    @MediumTest
    public void testRoutePendingMessagesSuccessful() {
        // processSms.routePendingMessages(from, body, timestamp, uuid);
    }

    /**
     * Test that a string can be filtered by keywords
     */
    @SmallTest
    public void testFilterByKeywords() {
        boolean status = processSms.filterByKeywords(body, filterText);
        assertTrue("Passed testing filter by keywords", status);
    }
    
    @SmallTest
    public void testRouteSmsByKeyWord() {
        
    }
}
