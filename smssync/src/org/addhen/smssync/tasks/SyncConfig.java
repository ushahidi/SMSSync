
package org.addhen.smssync.tasks;

/**
 * Configures the sync task
 */
public class SyncConfig {

    public final boolean skip;

    public final int tries;

    public final String messageUuid;

    public final SyncType syncType;

    public SyncConfig(int tries, boolean skip, String messageUuid, SyncType syncType) {
        this.tries = tries;
        this.skip = skip;
        this.syncType = syncType;
        this.messageUuid = messageUuid;
    }
}
