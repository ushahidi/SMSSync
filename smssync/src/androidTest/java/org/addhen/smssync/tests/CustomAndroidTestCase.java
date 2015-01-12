package org.addhen.smssync.tests;

import android.test.AndroidTestCase;

/**
 * A workaround for this bug https://code.google.com/p/dexmaker/issues/detail?id=2
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
// TODO: Follow up on bug to see if it has been fixed and update accordingly.
public class CustomAndroidTestCase extends BaseTest {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", getContext().getCacheDir().toString());
    }
}