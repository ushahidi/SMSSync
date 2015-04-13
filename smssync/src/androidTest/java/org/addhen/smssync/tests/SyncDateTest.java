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

import org.addhen.smssync.SyncDate;
import org.addhen.smssync.prefs.Prefs;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * Test sync date
 */
public class SyncDateTest extends AndroidTestCase {

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
