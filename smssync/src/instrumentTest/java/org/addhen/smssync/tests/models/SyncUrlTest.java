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

package org.addhen.smssync.tests.models;

import java.util.ArrayList;
import java.util.List;

import org.addhen.smssync.database.Database;
import org.addhen.smssync.database.ISyncUrlSchema;
import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.tests.BaseTest;

import android.database.Cursor;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * @author eyedol
 */
public class SyncUrlTest extends BaseTest {

    private SyncUrl syncUrl;

    private List<SyncUrl> listSyncUrl;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        syncUrl = new SyncUrl();
    }

    /**
     * Test adding a new sync url to the db
     */
    @SmallTest
    public void testShouldSaveNewSyncUrl() throws Exception{
        // add demo sync url
        syncUrl = new SyncUrl();
        syncUrl.setKeywords("demo,ushahidi,smssync");
        syncUrl.setSecret("demo");
        syncUrl.setTitle("ushahidi demo");
        syncUrl.setUrl("http://demo.ushahidi.com/smssync");
        final boolean status = syncUrl.save();
        assertTrue("Could not add a new sync url", status);
    }

    /**
     * Test updating an existing sync URL
     */
    @SmallTest
    public void testShouldUpdateSyuncUrlTitle() throws Exception{
        syncUrl = new SyncUrl();
        syncUrl.setKeywords("demo,ushahidi,smssync");
        syncUrl.setSecret("demo2");
        syncUrl.setTitle("ushahidi demo2");
        syncUrl.setUrl("http://demo.ushahidi.com/smssync2");
        final boolean status = syncUrl.save();
        assertTrue("Couldn't add a new sync url",status);

        // get added sync url
        setSyncUrls("ushahidi demo2");

        syncUrl = listSyncUrl.get(0);
        syncUrl.setTitle("Demo updated");
        final boolean updated = syncUrl.update();
        assertTrue("Couldn't update sync url title ", updated);

    }

    /**
     * Test making a Sync URL active
     */
    public void testShouldUpdateSyncUrlStatusToActive() throws Exception{
        syncUrl = new SyncUrl();
        syncUrl.setKeywords("demo,ushahidi,smssync");
        syncUrl.setSecret("demo3");
        syncUrl.setTitle("ushahidi demo3");
        syncUrl.setUrl("http://demo.ushahidi.com/smssync3");
        final boolean status = syncUrl.save();
        assertTrue("could not add sync url",status);

        setSyncUrls("ushahidi demo3");

        syncUrl = listSyncUrl.get(0);
        syncUrl.setStatus(1);
        final boolean updated = syncUrl.update();
        assertTrue("Could not update sync url to be active", updated);

    }

    @SmallTest
    public void testShouldLoadAllSyncUrl() throws Exception{
        syncUrl = new SyncUrl();
        syncUrl.setKeywords("demo,ushahidi,smssync");
        syncUrl.setSecret("demo4");
        syncUrl.setTitle("ushahidi demo4");
        syncUrl.setUrl("http://demo.ushahidi.com/smssync4");
        final boolean status = syncUrl.save();
        assertTrue("could not add sync url",status);

        boolean loaded = syncUrl.load();
        assertTrue("Couldn't load all saved sync URL", loaded);
    }

    @SmallTest
    public void testShouldLoadById() throws Exception{
        syncUrl = new SyncUrl();
        syncUrl.setKeywords("demo,ushahidi,smssync5");
        syncUrl.setSecret("demo5");
        syncUrl.setTitle("ushahidi demo5");
        syncUrl.setUrl("http://demo.ushahidi.com/smssync5");
        final boolean status = syncUrl.save();
        assertTrue("could not add sync url",status);

        setSyncUrls("ushahidi demo5");

        syncUrl = listSyncUrl.get(0);
        listSyncUrl = syncUrl.loadById(syncUrl.getId());
        assertNotNull("Couldn't Sync URL by ID ", listSyncUrl);

    }

    @SmallTest
    public void testShouldLoadAllActiveSyncUrl() throws Exception{
        listSyncUrl = syncUrl.loadByStatus(1);
        assertNotNull("Couldn't load active Sync URL", listSyncUrl);
    }

    @SmallTest
    public void testShouldLoadAllInActiveSyncUrl() throws Exception{
        listSyncUrl = syncUrl.loadByStatus(0);
        assertNotNull("Couldn't load inactive Sync URLs", listSyncUrl);
    }

    /**
     * Test the total number of active or enabled Sync URLs.
     */
    @SmallTest
    public void testShouldGiveTotalActiveSyncUrl() throws Exception{
        int count = Database.syncUrlContentProvider.totalActiveSyncUrl();
        assertNotNullOrZero("There are no active SyncUrl", count);
    }

    @SmallTest
    public void testShouldDeleteAllSyncUrl() throws Exception{
        syncUrl = new SyncUrl();
        syncUrl.setKeywords("demo,ushahidi,smssync");
        syncUrl.setSecret("demo6");
        syncUrl.setTitle("ushahidi demo6");
        syncUrl.setUrl("http://demo.ushahidi.com/smssync6");
        final boolean status = syncUrl.save();
        assertTrue("could not add sync url",status);
        final boolean deleted = syncUrl.deleteAllSyncUrl();
        assertTrue("could not delete sync URL",deleted);
    }

    /**
     * Delete sync URL by id
     *
     * @return boolean
     */
    public void testShouldDeleteSyncUrlById() throws Exception{
        syncUrl = new SyncUrl();
        syncUrl.setKeywords("demo,ushahidi,smssync7");
        syncUrl.setSecret("demo7");
        syncUrl.setTitle("ushahidi demo7");
        syncUrl.setUrl("http://demo.ushahidi.com/smssync7");
        final boolean status = syncUrl.save();
        assertTrue("could not add sync url",status);

        setSyncUrls("ushahidi demo7");

        syncUrl = listSyncUrl.get(0);
        listSyncUrl = syncUrl.loadById(syncUrl.getId());

        //TODO remplement this
        boolean deleted = syncUrl.deleteSyncUrlById(syncUrl.getId());
        assertTrue("couldn't delete sync url with id " , deleted);
    }

    @Override
    public void tearDown() throws Exception {
        syncUrl.deleteAllSyncUrl();
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
        listSyncUrl = new ArrayList<SyncUrl>();
        if (cursor != null) {
            try {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    SyncUrl syncUrl = Database.syncUrlContentProvider.cursorToEntity(cursor);
                    listSyncUrl.add(syncUrl);
                    cursor.moveToNext();
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }

        }

    }

    private void setSyncUrls(String title) {
        final String selectionArgs[] = {
                title
        };
        Cursor cursor = Database.syncUrlContentProvider.rawQuery("SELECT * FROM "
                + ISyncUrlSchema.TABLE + " WHERE " + ISyncUrlSchema.TITLE + " =?", selectionArgs);
        getEntity(cursor);
    }
}
