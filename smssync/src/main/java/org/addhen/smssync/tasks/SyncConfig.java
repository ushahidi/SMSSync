
package org.addhen.smssync.tasks;

import java.util.ArrayList;

/**
 * Configures the sync task
 */
public class SyncConfig {

    public final boolean skip;

    public final int tries;

    public final ArrayList<String> messageUuids;

    public final SyncType syncType;

    public SyncConfig(int tries, boolean skip, ArrayList<String> messageUuids, SyncType syncType) {
        this.tries = tries;
        this.skip = skip;
        this.syncType = syncType;
        this.messageUuids = messageUuids;
    }
}
