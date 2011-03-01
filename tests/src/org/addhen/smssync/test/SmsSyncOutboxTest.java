
package org.addhen.smssync.test;

import java.util.ArrayList;
import java.util.List;

import org.addhen.smssync.ListMessagesAdapter;
import org.addhen.smssync.R;
import org.addhen.smssync.SmsSyncApplication;
import org.addhen.smssync.SmsSyncOutbox;
import org.addhen.smssync.Util;
import org.addhen.smssync.data.Messages;
import org.addhen.smssync.data.SmsSyncDatabase;

import android.database.Cursor;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Testcase for testing submission of pending messages when SMS fails to submit
 * to the configured URL.
 * 
 * @author eyedol
 */
public class SmsSyncOutboxTest extends ActivityInstrumentationTestCase2<SmsSyncOutbox> {

    private int messageId = 0;

    private int listItemPosition = 0;

    private static ListView listMessages = null;

    private static List<Messages> mOldMessages;

    private static ListMessagesAdapter ila;

    private static TextView emptyListText;

    private boolean byId;

    private SmsSyncOutbox mSmsSyncOutbox;

    public static SmsSyncDatabase mDb;

    public SmsSyncOutboxTest() {
        super("org.addhen.smssync", SmsSyncOutbox.class);

    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mSmsSyncOutbox = this.getActivity();
        listMessages = (ListView)mSmsSyncOutbox.findViewById(R.id.view_messages);
        emptyListText = (TextView)mSmsSyncOutbox.findViewById(R.id.empty);

        mOldMessages = new ArrayList<Messages>();
        ila = new ListMessagesAdapter(mSmsSyncOutbox);
        byId = false;
    }

    @LargeTest
    public void testSyncMessages() {

        int status = 0;

        Cursor cursor;

        // check if it should sync by id
        if (byId) {
            cursor = SmsSyncApplication.mDb.fetchMessagesById(messageId);
        } else {
            cursor = SmsSyncApplication.mDb.fetchAllMessages();
        }
        String messagesFrom;
        String messagesBody;
        String messagesDate;

        if (cursor.getCount() == 0) {
            status = 2; // no pending messages to synchronize
        }

        mOldMessages.clear();
        if (cursor.moveToFirst()) {
            int messagesIdIndex = cursor.getColumnIndexOrThrow(SmsSyncDatabase.MESSAGES_ID);
            int messagesFromIndex = cursor.getColumnIndexOrThrow(SmsSyncDatabase.MESSAGES_FROM);

            int messagesBodyIndex = cursor.getColumnIndexOrThrow(SmsSyncDatabase.MESSAGES_BODY);

            int messagesDateIndex = cursor.getColumnIndexOrThrow(SmsSyncDatabase.MESSAGES_DATE);

            do {

                Messages messages = new Messages();
                mOldMessages.add(messages);

                int messageId = Util.toInt(cursor.getString(messagesIdIndex));
                messages.setMessageId(messageId);

                messagesFrom = Util.capitalizeString(cursor.getString(messagesFromIndex));
                messages.setMessageFrom(messagesFrom);

                messagesDate = cursor.getString(messagesDateIndex);
                messages.setMessageDate(messagesDate);

                messagesBody = cursor.getString(messagesBodyIndex);
                messages.setMessageBody(messagesBody);

                // post to web service
                if (Util.postToAWebService(messagesFrom, messagesBody, mSmsSyncOutbox)) {
                    // if it successfully pushes a message, delete message from
                    // the db.
                    if (byId) {
                        ila.removetItemAt(listItemPosition);
                    } else {
                        ila.removeItems();
                    }
                    ila.notifyDataSetChanged();
                    SmsSyncApplication.mDb.deleteMessagesById(messageId);
                    status = 0; // successfully posted messages to the web
                                // service.
                } else {
                    status = 1; // failed to post the messages to the web
                                // service.
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
        ila.notifyDataSetChanged();

        assertEquals(2, status); // nothing to synchronize.

        // assertEquals(0,status); //pending messages have been synchronized.

        // assertEquals(1,status); // failed to synchronized to the messages.

    }

    @Override
    public void tearDown() {
        SmsSyncApplication.mDb.close();
    }
}
