package org.addhen.smssync.tests.messages;

import org.addhen.smssync.messages.ProcessMessage;
import org.addhen.smssync.messages.ProcessSms;
import org.addhen.smssync.models.Message;
import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.tests.BaseTest;

import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * Test Process message
 */
public class ProcessMessageTest extends BaseTest {

    private ProcessMessage mProcessMessage;

    private Message message;

    private ProcessSms mProcessSms;
    @Override
    public void setUp() throws Exception {
        super.setUp();
        mProcessMessage = new ProcessMessage(getContext());
        mProcessSms = new ProcessSms(getContext());
        message = new Message();
        message.setFrom("0243581806");
        message.setUuid(mProcessSms.getUuid());
        message.setTimestamp("1370831690572");
        message.setBody("foo bar");
    }

    @SmallTest
    public void testShouldSaveMessage() throws Exception {
        assertTrue("Could not add a new message ", mProcessMessage.saveMessage(message));
        assertTrue("Could not delete the message",message.deleteAllMessages());
    }

    @MediumTest
    public void testShouldSyncReceivedSms() throws Exception {
        SyncUrl syncUrl = new SyncUrl();
        syncUrl.setKeywords("demo,ushahidi,smssync");
        syncUrl.setSecret("demo");
        syncUrl.setTitle("ushahidi demo6");
        syncUrl.setUrl("http://demo.ushahidi.com/smssync");
        final boolean posted = mProcessMessage.syncReceivedSms(message, syncUrl);
        assertTrue(posted);
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
