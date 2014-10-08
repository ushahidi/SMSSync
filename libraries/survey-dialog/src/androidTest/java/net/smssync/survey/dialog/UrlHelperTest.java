package net.smssync.survey.dialog;

import android.test.AndroidTestCase;

/**
 * Unit test for {@link UrlHelper}
 */
public class UrlHelperTest extends AndroidTestCase {

    private static final String GOOGLE_FORM_URL
            = "https://docs.google.com/forms/d/1lL4IEksja3r-ClEtCgBma4mB9iT1tcaxSJnriJgW2sM/formResponse";

    private UrlHelper mUriHelper;

    public void setUp() throws Exception {
        super.setUp();
        mUriHelper = new UrlHelperImpl();

    }

    public void testGetUrl() {
        assertEquals(mUriHelper.getUrl(), GOOGLE_FORM_URL);
    }
}