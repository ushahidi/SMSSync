package org.addhen.smssync.tests;

import org.addhen.smssync.SyncDate;

import android.test.suitebuilder.annotation.SmallTest;

/**
 * Test sync date
 */
public class SyncDateTest extends BaseTest {

    SyncDate mSyncDate;

    @Override
    public void setUp() throws Exception {
        mSyncDate = new SyncDate();
    }

    @SmallTest
    public void testShouldGetLastSyncDate() throws Exception{
        mSyncDate.setLastSyncedDate(getContext(), 1370831690572l);
        long  timestamp = mSyncDate.getLastSyncedDate(getContext());
        assertEquals(1370831690572l, timestamp);
    }

    @Override
    public void tearDown() throws Exception {
        mSyncDate.setLastSyncedDate(getContext(), 0l);
        mSyncDate = null;
    }
}
