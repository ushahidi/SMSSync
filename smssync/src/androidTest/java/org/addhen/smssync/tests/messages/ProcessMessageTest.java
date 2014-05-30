package org.addhen.smssync.tests.messages;

import org.addhen.smssync.Prefs;
import org.addhen.smssync.messages.ProcessMessage;
import org.addhen.smssync.messages.ProcessSms;
import org.addhen.smssync.models.Message;
import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.tests.BaseTest;

import android.preference.Preference;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * Test Process message
 */
public class ProcessMessageTest extends BaseTest {

    private ProcessMessage mProcessMessage;

    private Message mMessage;

    private ProcessSms mProcessSms;
    @Override
    public void setUp() throws Exception {
        super.setUp();
        mProcessMessage = new ProcessMessage(getContext());
        mProcessSms = new ProcessSms(getContext());
        mMessage = new Message();
        mMessage.setFrom("0243581806");
        mMessage.setUuid(mProcessSms.getUuid());
        mMessage.setTimestamp("1370831690572");
        mMessage.setBody("foo bar");
    }

    @SmallTest
    public void testShouldSaveMessage() throws Exception {
        assertTrue("Could not add a new message ", mProcessMessage.saveMessage(mMessage));
        assertTrue("Could not delete the message",mMessage.deleteAllMessages());
    }

    @MediumTest
    public void testShouldSyncReceivedSms() throws Exception {
        SyncUrl syncUrl = new SyncUrl();
        syncUrl.setKeywords("");
        syncUrl.setSecret("demo");
        syncUrl.setTitle("ushahidi demo");
        syncUrl.setUrl("http://demo.ushahidi.com/smssync");
        final boolean posted = mProcessMessage.syncReceivedSms(mMessage, syncUrl);
        assertTrue(posted);
    }

    @SmallTest
    public void testShouldSyncPendingMessagesByItsUuid() throws Exception {

    }

    @MediumTest
    public void testShouldPerformTaskEnabledSyncUrl() throws Exception {
        SyncUrl syncUrlDemo = new SyncUrl();
        syncUrlDemo.setKeywords("");
        syncUrlDemo.setSecret("demo");
        syncUrlDemo.setTitle("ushahidi demo");
        syncUrlDemo.setUrl("http://demo.ushahidi.com/smssync");
        mProcessMessage.performTask(syncUrlDemo);

        // there is no proper way of testing if a task went successfuly. Right now falling on error
        // message to assert the status. ProcessMessage.performTask needs refactoring
        assertNull("Could not perform task on enabled sync url ", mProcessMessage.getErrorMessage());
    }

    @MediumTest
    public void ShouldPerformTaskEnabledOnLocalInstall() throws Exception {
        SyncUrl syncUrlDemo = new SyncUrl();
        syncUrlDemo.setKeywords("");
        syncUrlDemo.setSecret("");
        syncUrlDemo.setTitle("ushahidi demo");
        syncUrlDemo.setUrl("http://192.168.6.13/smssync2.php");
        mProcessMessage.performTask(syncUrlDemo);

        // there is no proper way of testing if a task went successfuly. Right now falling on error
        // message to assert the status. ProcessMessage.performTask needs refactoring
        assertNull("Could not perform task on enabled sync url ", mProcessMessage.getErrorMessage());
    }

    @MediumTest
    public void testShouldRouteMessage() throws Exception {
        SyncUrl syncUrlEyedol = new SyncUrl();
        syncUrlEyedol.setKeywords("");
        syncUrlEyedol.setSecret("");
        syncUrlEyedol.setTitle("eyedol demo");
        syncUrlEyedol.setUrl("https://eyedol.crowdmap.com/smssync");
        syncUrlEyedol.setStatus(1);
        assertTrue("Could not save demo sync URL", syncUrlEyedol.save());

        mMessage = new Message();
        mMessage.setFrom("0243581806");
        mMessage.setUuid(mProcessSms.getUuid());
        mMessage.setTimestamp("1370831690572");
        mMessage.setBody("Routing to one sync URL");
        assertTrue(mMessage.save());

        // enable smssync
        Prefs.enabled = true;
        Prefs.savePreferences(getContext());
        final boolean posted = mProcessMessage.routePendingMessage(mMessage);
        assertTrue("Could not sync pending messages, ",
                posted);
        assertTrue(syncUrlEyedol.deleteAllSyncUrl());
    }

    @MediumTest
    public void testShouldRouteMessageToTwoEnabledSyncUrl() throws Exception {

        SyncUrl syncUrlEyedol = new SyncUrl();
        syncUrlEyedol.setKeywords("");
        syncUrlEyedol.setSecret("");
        syncUrlEyedol.setTitle("eyedol demo");
        syncUrlEyedol.setUrl("https://eyedol.crowdmap.com/smssync");
        syncUrlEyedol.setStatus(1);
        assertTrue("Could not save demo sync URL",syncUrlEyedol.save());

        SyncUrl syncUrlDemo = new SyncUrl();
        syncUrlDemo.setKeywords("");
        syncUrlDemo.setSecret("demo");
        syncUrlDemo.setTitle("ushahidi demo");
        syncUrlDemo.setStatus(1);
        syncUrlDemo.setUrl("http://demo.ushahidi.com/smssync");
        assertTrue("Could not save demo sync URL",syncUrlDemo.save());

        mMessage = new Message();
        mMessage.setFrom("0243581806");
        mMessage.setUuid(mProcessSms.getUuid());
        mMessage.setTimestamp("1370831690572");
        mMessage.setBody("Routing to two sync URLs");
        assertTrue(mMessage.save());

        // enable smssync
        Prefs.enabled = true;
        Prefs.savePreferences(getContext());
        final boolean posted = mProcessMessage.routePendingMessage(mMessage);
        assertTrue("Could not sync pending messages, ",
                posted);
        assertTrue(syncUrlDemo.deleteAllSyncUrl());

    }

    @MediumTest
    public void testShouldRouteMessageToThreeEnabledSyncUrl() throws Exception {
        SyncUrl syncUrlEyedol = new SyncUrl();
        syncUrlEyedol.setKeywords("");
        syncUrlEyedol.setSecret("");
        syncUrlEyedol.setTitle("eyedol demo");
        syncUrlEyedol.setUrl("https://eyedol.crowdmap.com/smssync");
        syncUrlEyedol.setStatus(1);
        assertTrue("Could not save demo sync URL",syncUrlEyedol.save());

        SyncUrl syncUrlDemo = new SyncUrl();
        syncUrlDemo.setKeywords("");
        syncUrlDemo.setSecret("demo");
        syncUrlDemo.setTitle("ushahidi demo");
        syncUrlDemo.setStatus(1);
        syncUrlDemo.setUrl("http://demo.ushahidi.com/smssync");
        assertTrue("Could not save demo sync URL",syncUrlDemo.save());

        SyncUrl syncUrlTest = new SyncUrl();
        syncUrlTest.setKeywords("");
        syncUrlTest.setSecret("test");
        syncUrlTest.setTitle("ushahidi demo");
        syncUrlTest.setStatus(1);
        syncUrlTest.setUrl("http://testing.ushahidi.com/smssync");
        assertTrue("Could not save demo sync URL",syncUrlTest.save());

        mMessage = new Message();
        mMessage.setFrom("0243581806");
        mMessage.setUuid(mProcessSms.getUuid());
        mMessage.setTimestamp("1370831690572");
        mMessage.setBody("Routing to three sync URLs");
        assertTrue(mMessage.save());

        // enable smssync
        Prefs.enabled = true;
        Prefs.savePreferences(getContext());
        final boolean posted = mProcessMessage.routePendingMessage(mMessage);
        assertTrue("Could not sync pending messages, ",
                posted);
        assertTrue(syncUrlTest.deleteAllSyncUrl());
    }

    @MediumTest
    public void testShouldFailToRouteMessageToASingleEnabledSyncUrl() throws Exception {

    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }
}
