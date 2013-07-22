
package org.addhen.smssync.tests.services;

import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import org.addhen.smssync.services.SyncPendingMessagesService;

/**
 * Testcase for testing submission of pending messages when SMS fails to submit
 * to the configured URL.
 * 
 * @author eyedol
 */
public class SyncPendingMessagesServiceTestCase extends ServiceTestCase<SyncPendingMessagesService> {

    public SyncPendingMessagesServiceTestCase() {
        super(SyncPendingMessagesService.class);

    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

    }

    @LargeTest
    public void testSyncMessages() {
    }

    @Override
    public void tearDown() {
    }
}
