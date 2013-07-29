package org.addhen.smssync.tests.messages;

import org.addhen.smssync.messages.ProcessSms;
import org.addhen.smssync.tests.BaseTest;

import android.content.ContentValues;
import android.net.Uri;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * Test process sms
 */
public class ProcessSmsTest extends BaseTest {

    private static final String REGEX = "\\d{2}(am|pm)";

    private String longText;

    private ProcessSms mProcessSms;

    @Override
    public void setUp() throws Exception {
        longText = "Hello, See you at tomorrow at the accra mall";
        mProcessSms = new ProcessSms(getContext());
    }

    @SmallTest
    public void testShouldFindMessageId() throws Exception {

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
    public void testShouldGetMessageThreadId() throws Exception {
        final String body = "foo bar";
        final String address = "123456789";
        ContentValues values = new ContentValues();
        values.put("address", address);
        values.put("body", body);
        assertNotNull("Could not add sms to sms inbox", getContext().getContentResolver()
                .insert(Uri.parse(ProcessSms.SMS_CONTENT_INBOX), values));

        final long msgThreadId = mProcessSms.getThreadId(body, address);
        assertTrue("Could not get sms thread Id", msgThreadId > 0);
        assertTrue("Could not delete sms from  inbox ", mProcessSms.delSmsFromInbox(body, address));

    }

    @SmallTest
    public void testShouldGetUuid() throws Exception {
        assertNotNullOrEmpty("Could not get UUID", mProcessSms.getUuid());
    }

    @SmallTest
    public void testShouldDeleteSmsFromSmsInbox() throws Exception {
        final String body = "foo bar";
        final String address = "123443";
        ContentValues values = new ContentValues();
        values.put("address", address);
        values.put("body", body);
        assertNotNull("Could not add sms to sms inbox", getContext().getContentResolver()
                .insert(Uri.parse(ProcessSms.SMS_CONTENT_INBOX), values));
        final boolean rowDeleted = mProcessSms.delSmsFromInbox(body, address);
        assertTrue("Could not delete sms from sms inbox", rowDeleted);
    }

    @SmallTest
    public void testShouldPostSmsToSentInbox() throws Exception {

    }

    @SmallTest
    public void testShouldImportMessagesFromSmsInbox() throws Exception {
        
    }

    @Override
    public void tearDown() throws Exception {

    }
}
