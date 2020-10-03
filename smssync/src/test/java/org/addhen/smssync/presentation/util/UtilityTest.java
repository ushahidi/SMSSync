/*
 * Copyright (c) 2010 - 2016 Ushahidi Inc
 * All rights reserved
 * Contact: team@ushahidi.com
 * Website: http://www.ushahidi.com
 * GNU Lesser General Public License Usage
 * This file may be used under the terms of the GNU Lesser
 * General Public License version 3 as published by the Free Software
 * Foundation and appearing in the file LICENSE.LGPL included in the
 * packaging of this file. Please review the following information to
 * ensure the GNU Lesser General Public License version 3 requirements
 * will be met: http://www.gnu.org/licenses/lgpl.html.
 *
 * If you have questions regarding the use of this file, please contact
 * Ushahidi developers at team@ushahidi.com.
 */

package org.addhen.smssync.presentation.util;

import org.addhen.smssync.BaseRobolectricTestCase;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * @author Henry Addo
 */
public class UtilityTest extends BaseRobolectricTestCase {

    @Test
    public void shouldCheckUrlIsValid() {
        boolean actual = Utility.validateUrl("http://demo.ushahidi.com/smssync");
        assertTrue("The provided URL is not a valid one", actual);
    }

    @Test
    public void shouldCheckForIPBasedUrlToBeValid() {
        boolean actual = Utility.validateUrl("http://192.168.1.4:3000/");
        assertTrue("The provided URL is not a valid one", actual);
    }

    @Test
    public void stringCapitalisationShouldWorkForNull() {
        // expect
        assertNull(Utility.capitalizeFirstLetter(null));
    }

    @Test
    public void stringCapitalisationShouldWorkForEmptyString() {
        // expect
        assertEquals("", Utility.capitalizeFirstLetter(""));
    }

    @Test
    public void stringCapitalisationShouldWorkForSingleCharacterString() {
        // expect
        assertEquals("A", Utility.capitalizeFirstLetter("a"));
    }

    @Test
    public void stringCapitalisationShouldWorkForTwoCharacterString() {
        // expect
        assertEquals("Ab", Utility.capitalizeFirstLetter("ab"));
    }

    @Test
    public void stringCapitalisationShouldWorkForLongerString() {
        // expect
        assertEquals("AbRaCaDaBrA", Utility.capitalizeFirstLetter("abRaCaDaBrA"));
    }
}
