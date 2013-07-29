package org.addhen.smssync.tests.messages;

import org.addhen.smssync.messages.ProcessMessage;
import org.addhen.smssync.tests.BaseTest;

import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * Test Process message
 */
public class ProcessMessageTest extends BaseTest {

    private ProcessMessage mProcessMessage;
    @Override
    public void setUp() throws Exception {
        super.setUp();
        mProcessMessage = new ProcessMessage(getContext());
    }

    @SmallTest
    public void testShouldSaveMessage() throws Exception {

    }

    @SmallTest
    public void testShouldSyncReceivedSms() throws Exception {

    }

    @SmallTest
    public void testShouldSyncPendingMessagesByItsUuid() throws Exception {

    }

    @MediumTest
    public void testShouldPerformTaskOnOneEnabledSyncUrl() throws Exception {

    }

    @MediumTest
    public void testShouldPerformTaskOnTwoEnabledSyncUrl() throws Exception {

    }

    @MediumTest
    public void testShouldPerformTaskOnThreeEnabledSyncUrl() throws Exception {

    }

    @MediumTest
    public void testShouldRouteMessage() throws Exception {

    }

    @MediumTest
    public void testShouldRouteMessageToTwoEnabledSyncUrl() throws Exception {

    }

    @MediumTest
    public void testShouldRouteMessageToThreeEnabledSyncUrl() throws Exception {

    }

    @MediumTest
    public void testShouldRouteMessageToFourEnabledSyncUrl() throws Exception {

    }

    @MediumTest
    public void testShouldRouteMessageToFiveEnabledSyncUrl() throws Exception {

    }

    @MediumTest
    public void testShouldFailToRouteMessageToASingleEnabledSyncUrl() throws Exception {

    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }
}
