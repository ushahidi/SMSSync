package org.addhen.smssync.tests.messages;

import android.test.suitebuilder.annotation.SmallTest;

import org.addhen.smssync.messages.ProcessSms;
import org.addhen.smssync.tests.BaseTest;

/**
 * Test process sms
 */
public class ProcessSmsTest extends BaseTest {

    private static final String REGEX = "\\d{2}(am|pm)";

    private String longText;

    private ProcessSms mProcessSms;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        longText = "Hello, See you tomorrow at the accra mall";
        mProcessSms = new ProcessSms(getContext());
    }

    @SmallTest
    public void testShouldFilterTextByKeyword() throws Exception {
        final String keyword = "hello, accra, go, home , yes";
        final boolean filtered = mProcessSms.filterByKeywords(longText, keyword);
        assertTrue(filtered);
    }

    @SmallTest
    public void testShouldFailToFilterTextByKeyword() throws Exception {
        final String keyword = "foo, bar";
        final boolean filtered = mProcessSms.filterByKeywords(longText, keyword);
        assertFalse(filtered);
    }

    @SmallTest
    public void testShouldFilterTextByRegex() throws Exception {
        StringBuilder message = new StringBuilder(longText);
        message.append(" at 12pm");
        final boolean filtered = mProcessSms.filterByRegex(message.toString(), REGEX);
        assertTrue(" failed at " + message.toString(), filtered);
    }

    @SmallTest
    public void testShouldFailToFilterTextByRegex() throws Exception {
        final boolean filtered = mProcessSms.filterByRegex(longText, REGEX);
        assertFalse(filtered);
    }

    @SmallTest
    public void testShouldFilterTextNumbersAndUTF8Keywords() throws Exception {
        final String keyword = "09777,65907,238501,2167,긴급점검요망";

        final String message = " What is happening here at 09777. We thought this number 65907 was "
                + "deleted from the system. It also appear that number 238501 never got deleted. "
                + "Create an issue and tag it as 2167. "
                + "In Korea they write this as 긴급점검요망 ";

        final boolean filtered = mProcessSms.filterByKeywords(message, keyword);
        assertTrue(filtered);
    }

    @SmallTest
    public void testShouldGetUuid() throws Exception {
        assertNotNullOrEmpty("Could not get UUID", mProcessSms.getUuid());
    }
}
