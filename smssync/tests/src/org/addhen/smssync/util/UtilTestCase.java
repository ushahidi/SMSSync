
package org.addhen.smssync.util;

import org.addhen.smssync.test.BaseTestCase;

import android.test.suitebuilder.annotation.SmallTest;

import org.addhen.smssync.util.Util;

public class UtilTestCase extends BaseTestCase {

    Long timestamp;

    String expected;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        timestamp = 1370831690572l;
        expected = "Jun 10, 2013 at 11:34 AM";

    }

    /**
     * Test date and time formatter
     */
    @SmallTest
    public void testFormatDate() {

        try {
            String formatted = Util.formatDateTime(timestamp,
                    "MMM dd, yyyy 'at' hh:mm a");

            assertNotNullOrEqual("Timestamp cannot be null or empty", expected, formatted);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void tearDown() throws Exception{
        super.tearDown();
        timestamp = null;
        expected = null;
    }

}
