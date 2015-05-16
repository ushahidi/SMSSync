/*
 * Copyright (c) 2010 - 2015 Ushahidi Inc
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

package org.addhen.smssync.tests;

import net.smssync.survey.dialog.UrlHelper;

import org.addhen.smssync.UrlHelperImpl;

import android.test.AndroidTestCase;

/**
 * Unit test for {@link net.smssync.survey.dialog.UrlHelper}
 */
public class UrlHelperTest extends AndroidTestCase {

    private static final String GOOGLE_FORM_URL = "http://sample-google-form.example.com";

    private UrlHelper mUriHelper;

    public void setUp() throws Exception {
        super.setUp();
        mUriHelper = new UrlHelperImpl(GOOGLE_FORM_URL);

    }

    public void testGetUrl() {
        assertEquals(mUriHelper.getUrl(), GOOGLE_FORM_URL);
    }
}