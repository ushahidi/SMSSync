
package org.addhen.smssync.tests;

import java.util.Collection;

import org.addhen.smssync.database.Database;

import android.test.AndroidTestCase;

/**
 * Base test class for handling all unit test that doesn't call any of Android's
 * specific API.
 * 
 * @author eyedol
 */
public abstract class BaseTest extends AndroidTestCase {

    public static Database mDb;

    @Override
    public void setUp() throws Exception {

        mDb = new Database(getContext());
        mDb.open();
        super.setUp();

    }

    @Override
    public void tearDown() throws Exception {

        mDb.close();
        mDb = null;
        super.tearDown();

    }

    /**
     * Assert not null or empty.
     * 
     * @param message the message
     * @param value the value
     */
    protected static void assertNotNullOrEmpty(String message, String value) {
        assertNotNull(message, value);
        assertFalse(message, "".equals(value));
    }

    /**
     * Assert not null or empty.
     * 
     * @param message the message
     * @param value the value
     */
    protected static void assertNotNullOrEmpty(String message,
            Collection<?> value) {
        assertNotNull(message, value);
        assertFalse(message, value.isEmpty());
    }

    protected static void assertNotNullOrZero(String message, int value) {
        assertNotNull(message, value);
        assertEquals(message, 0, value);
    }

    protected static void assertNotNullOrEqual(String message, String expected, String actual) {
        assertNotNullOrEmpty(message, actual);
        assertEquals(message, expected, actual);
    }

}
