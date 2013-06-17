
package org.addhen.smssync.tasks.state;

import org.addhen.smssync.tasks.SyncType;

import android.content.res.Resources;

import static org.addhen.smssync.tasks.state.SyncState.INITIAL;
import static org.addhen.smssync.tasks.state.SyncState.SYNC;
import static org.addhen.smssync.tasks.SyncType.UNKNOWN;

public class PendingMessagesState extends State {

    public final int currentSyncedItems;
    public final int itemsToSync;
    public final SyncType syncType;

    public PendingMessagesState(SyncState state, int currentSyncedItems,
            int itemsToSync, SyncType syncType, Exception exception) {
        super(state, exception);
        this.currentSyncedItems = currentSyncedItems;
        this.itemsToSync = itemsToSync;
        this.syncType = syncType;
    }

    public PendingMessagesState() {
        this(INITIAL, 0, 0, UNKNOWN, null);
    }

    @Override
    public PendingMessagesState transition(SyncState newState, Exception exception) {
        return new PendingMessagesState(newState, currentSyncedItems, itemsToSync, syncType,
                exception);
    }

    public String getNotification(Resources resources, int resourceId) {

        String msg = super.getErrorMessage(resources, resourceId);
        if (msg != null)
            return msg;
        if (state == SYNC) {
            msg = resources.getString(resourceId,
                    currentSyncedItems,
                    itemsToSync);

            return msg;
        } else {
            return "";
        }

    }

    @Override
    public String toString() {
        return "BackupStateChanged[" +
                "state=" + state +
                ", currentSyncedItems=" + currentSyncedItems +
                ", itemsToSync=" + itemsToSync +
                ", backupType=" + syncType +
                ']';
    }

}
