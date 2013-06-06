package org.addhen.smssync.test;

import java.util.Collection;

import junit.framework.TestCase;

/**
 * Base test class for handling all unit test that doesn't call any of Android's
 * specific API.
 * 
 * @author eyedol
 * 
 */
public class BaseTestCase extends TestCase {

	/**
	 * Assert not null or empty.
	 * 
	 * @param message
	 *            the message
	 * @param value
	 *            the value
	 */
	protected static void assertNotNullOrEmpty(String message, String value) {
		assertNotNull(message, value);
		assertFalse(message, "".equals(value));
	}

	/**
	 * Assert not null or empty.
	 * 
	 * @param message
	 *            the message
	 * @param value
	 *            the value
	 */
	protected static void assertNotNullOrEmpty(String message,
			Collection<?> value) {
		assertNotNull(message, value);
		assertFalse(message, value.isEmpty());
	}

	protected static void assetNotNullOrZero(String message, int value) {
		assertNotNull(message, value);
		assertEquals(message, 0, value);
	}
}
