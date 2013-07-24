
package org.addhen.smssync.tests.util;

import android.test.suitebuilder.annotation.SmallTest;

import org.addhen.smssync.tests.BaseTestCase;
import org.addhen.smssync.util.Util;

public class UtilTestCase extends BaseTestCase {

    Long timestamp;
    String expected;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        timestamp = 1370831690572l;
        expected = "Jun 10, 2013 at 02:34 AM";
    }

    /**
     * Test date and time formatter
     */
    @SmallTest
    public void testShouldFormatDate() throws NumberFormatException {
        final String formatted = Util.formatDateTime(timestamp, "MMM dd, yyyy 'at' hh:mm a");

        assertNotNullOrEqual("Timestamp cannot be null or empty", expected, formatted);

    }

    /**
     * Test that two strings can be joined together
     */
    public void testShouldJoinTwoStrings() {
        final String expected = "Hello World!";
        assertNotNullOrEqual("Two strings couldn't be joined", expected, Util.joinString("Hello ", "World!"));
    }

    /**
     * Test should check that device is connected to the
     * network and has internet.
     */
    public void testShouldCheckDeviceHasInternet() {
        final boolean connected = Util.isConnected(getContext());
        assertTrue("The device is not connected to the internet", connected);
    }

    public void testShouldReturnIntValueOfAString() {
        final int actual = Util.toInt("2");
        assertEquals(2, actual);
    }

    public void testShouldCapitalizeFirstLetterOfAText(){
        final String actual = Util.capitalizeFirstLetter("hello world where are you");
        assertNotNullOrEqual("Could not capitalize the string ", "Hello world where are you", actual);
    }

    //public void testShould

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        timestamp = null;
        expected = null;
    }

}
