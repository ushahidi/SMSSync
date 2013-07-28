package org.addhen.smssync.tests.messages;

import org.addhen.smssync.messages.ProcessSms;
import org.addhen.smssync.tests.BaseTest;

import android.test.suitebuilder.annotation.SmallTest;

/**
 * Test process sms
 */
public class ProcessSmsTest extends BaseTest {

    private String longText;

    private ProcessSms mProcessSms;

    private static final String REGEX = "\\d{2}(am|pm)";

    @Override
    public void setUp() throws Exception {
        longText = "Hello, See you at tomorrow at the accra mall";
        mProcessSms = new ProcessSms(getContext());
    }
    @SmallTest
    public void testShouldFindMessageId() throws Exception{

    }

    @SmallTest
    public void testShouldFilterTextByKeyword() throws Exception {
        final String keyword = "hello, accra, go, home , yes";
        final boolean filtered = mProcessSms.filterByKeywords(longText,keyword);
        assertTrue(filtered);
    }

    @SmallTest
    public void testShouldFailToFilterTextByKeyword() throws Exception {
        final String keyword = "foo, bar";
        final boolean filtered = mProcessSms.filterByKeywords(longText,keyword);
        assertFalse(filtered);
    }

    @SmallTest
    public void testShouldFilterTextByRegex() throws Exception {
        StringBuilder message = new StringBuilder(longText);
        message.append(" at 12pm");
        final boolean filtered = mProcessSms.filterByRegex(message.toString(), REGEX);
        assertTrue(" failed at "+message.toString(),filtered);
    }

    @SmallTest
    public void testShouldFailToFilterTextByRegex() throws Exception {
        final boolean filtered = mProcessSms.filterByRegex(longText, REGEX);
        assertFalse(filtered);
    }

    @SmallTest
    public void testShouldGetMessageThreadId() throws Exception {

    }

    @SmallTest
    public void testShouldGetUuid() throws Exception {
        assertNotNullOrEmpty("Could not get UUID", mProcessSms.getUuid());
    }

    @SmallTest
    public void testShouldDeleteSmsFromSmsInbox() throws Exception {

    }

    @SmallTest
    public void testShouldPostSmsToSentInbox() throws Exception {

    }

    @Override
    public void tearDown() throws Exception {

    }
}
