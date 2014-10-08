package org.addhen.smssync.survey.dialog;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Unit test for {@link org.addhen.smssync.survey.dialog.UriHelper}
 */
public class UriHelperTest extends AndroidTestCase {

    private static final String GOOGLE_PLAY = "https://play.google.com/store/apps/details?id=";

    private final UriHelper mUriHelper = new UriHelperImpl();

    public void testGetGooglePlayUri() {
        {
            Uri uri = mUriHelper.getUri("");
            assertEquals(uri.toString(), GOOGLE_PLAY);
        }
        {
            Uri uri = mUriHelper.getUri(null);
            assertNull(uri);
        }
        {
            final String packageName = "org.addhen.smssync.survey.dialog";
            Uri uri = mUriHelper.getUri(packageName);
            assertEquals(uri.toString(), GOOGLE_PLAY + packageName);
        }
    }
}