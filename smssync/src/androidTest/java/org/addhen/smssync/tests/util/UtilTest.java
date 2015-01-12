
package org.addhen.smssync.tests.util;

import android.test.suitebuilder.annotation.SmallTest;

import org.addhen.smssync.tests.BaseTest;
import org.addhen.smssync.util.Util;

public class UtilTest extends BaseTest {

    Long timestamp;
    String expected;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        timestamp = 1370831690572l;
        expected = "Jun 10, 2013 at 02:34 AM";
    }

    /**
     * Test that two strings can be joined together
     */
    @SmallTest
    public void testShouldJoinTwoStrings() {
        final String expected = "Hello World!";
        assertNotNullOrEqual("Two strings couldn't be joined", expected, Util.joinString("Hello ", "World!"));
    }

    /**
     * Test should check that device is connected to the
     * network and has internet.
     */
    @SmallTest
    public void testShouldCheckDeviceHasInternet() {
        final boolean connected = Util.isConnected(getContext());
        assertTrue("The device is not connected to the internet", connected);
    }

    /**
     * Test that a string value is converted to it's int value
     */
    @SmallTest
    public void testShouldReturnIntValueOfAString() {
        final int actual = Util.toInt("2");
        assertEquals(2, actual);
    }

    /**
     * Test that a string first letter is capitalized
     */
    @SmallTest
    public void testShouldCapitalizeFirstLetterOfAText(){
        final String actual = Util.capitalizeFirstLetter("hello world where are you");
        assertNotNullOrEqual("Could not capitalize the string ", "Hello world where are you", actual);
    }

    /**
     * Test that a URL should be valid
     */
    @SmallTest
    public void testShouldCheckUrlIsValid() {
        final int actual = Util.validateCallbackUrl("http://demo.ushahidi.com/smssync");
        assertNotNullOrZero("The provided URL is not a valid one", actual);
    }

    /**
     * Test that a URL should not be valid
     */
    @SmallTest
    public void testShouldFailCheckUrlIsInvalid() {
        final int actual = Util.validateCallbackUrl("demo.ushahidi.com/smssync");
        assertEquals(1,actual);
    }

    /**
     * Test that email address is valid
     */
    @SmallTest
    public void testThatEmailIsValid() {
        final boolean valid = Util.validateEmail("foo@bar.com");
        assertTrue(valid);
    }

    /**
     * Test that an email address is not valid
     */
    @SmallTest
    public void testThatEmailIsNotValid() {
        final boolean invalid = Util.validateEmail("foo@bar");
        assertFalse(invalid);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        timestamp = null;
        expected = null;
    }
}
