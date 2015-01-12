package org.addhen.smssync.tests;

import org.addhen.smssync.SyncDate;
import org.addhen.smssync.prefs.Prefs;

import android.test.suitebuilder.annotation.SmallTest;

/**
 * Test sync date
 */
public class SyncDateTest extends BaseTest {

    SyncDate mSyncDate;

    Prefs mPrefs;

    @Override
    public void setUp() throws Exception {
        mPrefs = new Prefs(getContext());
        mSyncDate = new SyncDate(mPrefs);
    }

    @SmallTest
    public void testShouldGetLastSyncDate() throws Exception{
        mSyncDate.setLastSyncedDate(1370831690572l);
        long  timestamp = mSyncDate.getLastSyncedDate();
        assertEquals(1370831690572l, timestamp);
    }

    @Override
    public void tearDown() throws Exception {
        mSyncDate.setLastSyncedDate(0l);
        mSyncDate = null;
    }
}
