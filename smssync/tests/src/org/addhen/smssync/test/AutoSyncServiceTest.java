
package org.addhen.smssync.test;

import org.addhen.smssync.services.AutoSyncService;

import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.LargeTest;

/**
 * Testcase for testing submission of pending messages when SMS fails to submit
 * to the configured URL.
 * 
 * @author eyedol
 */
public class AutoSyncServiceTest extends ServiceTestCase<AutoSyncService> {

    public AutoSyncServiceTest() {
        super(AutoSyncService.class);

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
