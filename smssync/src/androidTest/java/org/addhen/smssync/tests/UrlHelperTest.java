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