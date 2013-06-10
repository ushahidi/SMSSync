
package org.addhen.smssync.util;

import org.addhen.smssync.test.BaseTest;

import android.test.suitebuilder.annotation.SmallTest;

import org.addhen.smssync.util.Util;

public class UtilTest extends BaseTest {

    Long timestamp;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        timestamp = System.currentTimeMillis() / 1000;
    }

    /**
     * Test date and time formatter
     */
    @SmallTest
    public void testFormatDate() {

        try {
            String formatted = Util.formatDateTime(timestamp,
                    "MMM dd, yyyy 'at' hh:mm a");
            assertEquals(formatted, formatted);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void tearDown() {
    }

}
