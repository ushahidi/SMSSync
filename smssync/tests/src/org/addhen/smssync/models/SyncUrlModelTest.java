/*****************************************************************************
 ** Copyright (c) 2010 - 2012 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 **
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.
 **
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 **
 *****************************************************************************/

package org.addhen.smssync.models;

import java.util.List;

import org.addhen.smssync.database.Database;
import org.addhen.smssync.test.BaseTestCase;

import android.test.suitebuilder.annotation.SmallTest;

/**
 * @author eyedol
 */
public class SyncUrlModelTest extends BaseTestCase {
    private SyncUrlModel syncUrl;

    private int id;

    private List<SyncUrlModel> listSyncUrl;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        syncUrl = new SyncUrlModel();
        id = 1;
    }

    @SmallTest
    public void testLoad() {

        boolean status = syncUrl.load();
        assertTrue("Load all saved sync URL", status);
    }

    @SmallTest
    public void testLoadById() {
        listSyncUrl = syncUrl.loadById(id);
        assertNotNull("Sync URL by ID " + id + " has been loaded", listSyncUrl);

    }

    @SmallTest
    public void loadByStatusActive() {
        listSyncUrl = syncUrl.loadByStatus(1);
        assertNotNull("Sync URL by status active", listSyncUrl);

    }

    @SmallTest
    public void loadByStatusInActive() {
        listSyncUrl = syncUrl.loadByStatus(0);
        assertNotNull("Sync URL by status inactive", listSyncUrl);

    }

    @SmallTest
    public boolean save() {
        if (listSyncUrl != null && listSyncUrl.size() > 0) {
            return Database.mSyncUrlContentProvider.addSyncUrl(listSyncUrl);
        }
        return false;
    }

    /**
     * Update an existing sync URL
     * 
     * @param syncUrl
     * @return boolean
     */
    public boolean update(SyncUrlModel syncUrl) {
        if (syncUrl != null) {
            return Database.mSyncUrlContentProvider.updateSyncUrl(syncUrl);
        }
        return false;
    }

    /**
     * Update status of a sync URL
     * 
     * @param int stauts The 0 for inactive and 1 for active. This determine
     *        whether the sync URL is active or not.
     * @param int id The unique id of the sync URL to update its status.
     * @return
     */
    public boolean updateStatus(SyncUrlModel syncUrl) {
        return Database.mSyncUrlContentProvider.updateStatus(syncUrl);
    }

    /**
     * The total number of active or enabled Sync URLs.
     * 
     * @return int The total number of Sync URLs that have been enabled.
     */
    public int totalActiveSynUrl() {
        return Database.mSyncUrlContentProvider.totalActiveSyncUrl();
    }

    public void testDeleteAllSyncUrl() {
        boolean status = syncUrl.deleteAllSyncUrl();
        assertTrue("All sync URL have been deleted", status);
    }

    /**
     * Delete sync URL by id
     * 
     * @param int id The unique ID to use to delete the sync URL.
     * @return boolean
     */
    public void testDeleteSyncUrlById() {
        boolean status = syncUrl.deleteSyncUrlById(id);
        assertTrue("Deleted sync URL with ID " + id, status);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }
}
