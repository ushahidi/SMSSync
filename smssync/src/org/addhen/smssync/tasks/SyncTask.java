
package org.addhen.smssync.tasks;

import org.addhen.smssync.services.SyncPendingMessagesService;

/**
 * Provide a background service for synchronizing huge messages
 */
public class SyncTask extends Task<SyncConfig, SyncState> {
    private final SyncPendingMessagesService mService;

    SyncTask(SyncPendingMessagesService service) {
        this.mService = service;
    }
}
