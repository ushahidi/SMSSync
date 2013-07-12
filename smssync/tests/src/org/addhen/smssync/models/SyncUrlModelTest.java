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

import java.util.ArrayList;
import java.util.List;

import org.addhen.smssync.database.Database;
import org.addhen.smssync.database.ISyncUrlSchema;
import org.addhen.smssync.test.BaseTestCase;

import android.database.Cursor;
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

    /**
     * Test adding a new sync url to the db
     */
    @SmallTest
    public void testSave() {
        // add demo sync url
        syncUrl = new SyncUrlModel();
        syncUrl.setKeywords("demo,ushahidi,smssync");
        syncUrl.setSecret("demo");
        syncUrl.setTitle("ushahidi demo");
        syncUrl.setUrl("http://demo.ushahidi.com/smssync");
        syncUrl.listSyncUrl = new ArrayList<SyncUrlModel>();
        syncUrl.listSyncUrl.add(syncUrl);

        syncUrl.save();
        // check if ushahidi demo was added to the database

        final String selectionArgs[] = {
                "ushahidi demo"
        };
        Cursor cursor = Database.mSyncUrlContentProvider.rawQuery("SELECT COUNT("
                + ISyncUrlSchema.ID + ") FROM "
                + ISyncUrlSchema.TABLE + " WHERE " + ISyncUrlSchema.TITLE + " =?", selectionArgs);

        assertEquals("Ushahidi demo couldn't be added", 1, getDbCount(cursor));

    }

    /**
     * Test updating an existing sync URL
     */
    @SmallTest
    public void testUpdate() {
        syncUrl = new SyncUrlModel();
        // load item
        Cursor cursors = Database.mSyncUrlContentProvider.query(ISyncUrlSchema.TABLE,
                ISyncUrlSchema.COLUMNS, null, null,
                ISyncUrlSchema.ID);

        getEntity(cursors);
        SyncUrlModel model = listSyncUrl.get(0);
        model.setTitle("ushahidi demo updated");
        syncUrl.update(model);
        final String selectionArgs[] = {
                "ushahidi demo updated"
        };
        Cursor cursor = Database.mSyncUrlContentProvider.rawQuery("SELECT COUNT("
                + ISyncUrlSchema.ID + ") FROM "
                + ISyncUrlSchema.TABLE + " WHERE " + ISyncUrlSchema.TITLE + " =?", selectionArgs);

        assertEquals("Ushahidi demo updated couldn't be updated", 1, getDbCount(cursor));
    }

    /**
     * Test making a Sync URL active
     */
    public void testUpdateStatus() {
        syncUrl = new SyncUrlModel();

        // load item
        Cursor cursors = Database.mSyncUrlContentProvider.query(ISyncUrlSchema.TABLE,
                ISyncUrlSchema.COLUMNS, null, null,
                ISyncUrlSchema.ID);

        getEntity(cursors);
        SyncUrlModel model = listSyncUrl.get(0);
        model.setStatus(1);
        syncUrl.update(model);

        final String selectionArgs[] = {
                "1"
        };
        Cursor cursor = Database.mSyncUrlContentProvider.rawQuery("SELECT COUNT("
                + ISyncUrlSchema.ID + ") FROM "
                + ISyncUrlSchema.TABLE + " WHERE " + ISyncUrlSchema.STATUS + " =?", selectionArgs);

        assertEquals("Ushahidi demo failed to be updated as active", 1, getDbCount(cursor));

    }

    @SmallTest
    public void testLoad() {
        boolean status = syncUrl.load();
        assertTrue("Couldn't load all saved sync URL", status);
    }

    @SmallTest
    public void testLoadById() {
        listSyncUrl = syncUrl.loadById(id);
        assertNotNull("Couldn't Sync URL by ID " + id, listSyncUrl);

    }

    @SmallTest
    public void loadByStatusActive() {
        listSyncUrl = syncUrl.loadByStatus(1);
        assertNotNull("Couldn't load active Sync URL", listSyncUrl);

    }

    @SmallTest
    public void loadByStatusInActive() {
        listSyncUrl = syncUrl.loadByStatus(0);
        assertNotNull("Couldn't load inactive Sync URLs", listSyncUrl);

    }

    /**
     * Test the total number of active or enabled Sync URLs.
     */
    @SmallTest
    public void totalActiveSyncUrl() {
        int count = Database.mSyncUrlContentProvider.totalActiveSyncUrl();
        assetNotNullOrZero("There are no active SyncUrl", count);
    }

    @SmallTest
    public void testDeleteAllSyncUrl() {
        boolean status = syncUrl.deleteAllSyncUrl();
        assertTrue("All sync URL failed to be deleted", status);
    }

    /**
     * Delete sync URL by id
     * 
     * @param int id The unique ID to use to delete the sync URL.
     * @return boolean
     */
    public void testDeleteSyncUrlById() {
        boolean status = syncUrl.deleteSyncUrlById(id);
        assertTrue("couldn't delete sync url with id " + id, status);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    private int getDbCount(Cursor cursor) {
        int count = 0;
        try {

            cursor.moveToFirst();
            count = cursor.getInt(0);
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return count;
    }

    private void getEntity(final Cursor cursor) {
        listSyncUrl = new ArrayList<SyncUrlModel>();
        if (cursor != null) {
            try {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    SyncUrlModel syncUrl = Database.mSyncUrlContentProvider.cursorToEntity(cursor);
                    listSyncUrl.add(syncUrl);
                    cursor.moveToNext();
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }

        }

    }

}
