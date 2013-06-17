package org.addhen.smssync.tasks;

/**
 * Configures the sync task
 * 
 *
 */
public class SyncConfig {
    
    public final boolean skip;
    
    public final int tries;
    
    public final SyncType syncType;
    
    public SyncConfig(int tries, boolean skip, SyncType syncType) {
        this.tries = tries;
        this.skip = skip;
        this.syncType = syncType;
    }
}
