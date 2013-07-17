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

package org.addhen.smssync.tasks.state;

import static org.addhen.smssync.tasks.SyncType.UNKNOWN;
import static org.addhen.smssync.tasks.state.SyncState.INITIAL;
import static org.addhen.smssync.tasks.state.SyncState.SYNC;
import static org.addhen.smssync.tasks.state.SyncState.ERROR;

import org.addhen.smssync.R;
import org.addhen.smssync.tasks.SyncType;

import android.content.res.Resources;

public class SyncPendingMessagesState extends State {

    public final int currentSyncedItems;
    public final int currentFailedItems;
    public final int itemsToSync;
    public final int currentProgress;
    public final SyncType syncType;

    public SyncPendingMessagesState(SyncState state, int currentSyncedItems,
            int currentFailedItems,
            int currentProgress,
            int itemsToSync, SyncType syncType, Exception exception) {
        super(state, exception);
        this.currentSyncedItems = currentSyncedItems;
        this.currentFailedItems = currentFailedItems;
        this.itemsToSync = itemsToSync;
        this.syncType = syncType;
        this.currentProgress = currentProgress;
    }

    /**
     * Create default state
     */
    public SyncPendingMessagesState() {
        this(INITIAL, 0, 0, 0, 0,UNKNOWN, null);
    }

    @Override
    public SyncPendingMessagesState transition(SyncState newState, Exception exception) {
        return new SyncPendingMessagesState(newState, currentSyncedItems, currentFailedItems, currentProgress,
                itemsToSync, syncType,
                exception);
    }

    /**
     * Get the notification
     * 
     * @param resources
     * @return
     */
    public String getNotification(Resources resources) {

        if (state == SYNC) {
            return resources.getString(R.string.status_sync_details,
                    currentSyncedItems,
                    currentFailedItems,
                    itemsToSync);

        } else if (state == ERROR) {
            return resources.getString(R.string.sync_failed, currentFailedItems, itemsToSync);
        } else {
            return "";
        }

    }

    @Override
    public String toString() {
        return "SyncStateChanged[" +
                "state=" + state +
                ", currentSyncedItems=" + currentSyncedItems +
                ", itemsToSync=" + itemsToSync +
                ", syncType=" + syncType +
                ']';
    }

}
