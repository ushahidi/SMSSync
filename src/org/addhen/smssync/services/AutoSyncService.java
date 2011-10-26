/** 
 ** Copyright (c) 2010 Ushahidi Inc
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
 **/
package org.addhen.smssync.services;

import android.content.Intent;
import android.util.Log;

import org.addhen.smssync.MainApplication;
import org.addhen.smssync.util.ServicesConstants;
import org.addhen.smssync.util.Util;

/**
 * A this class handles background services for periodic synchronization of
 * pending messages.
 * 
 * @author eyedol
 */

public class AutoSyncService extends SmsSyncServices {
    
    private static String CLASS_TAG = AutoSyncService.class.getSimpleName();
    private Intent statusIntent; // holds the status of the sync and sends it to
    // another smssyncoutbox activity to update the ui
    
    public AutoSyncService() {
        super(CLASS_TAG);
        statusIntent = new Intent(ServicesConstants.AUTO_SYNC_ACTION);
    }
    
    @Override
    protected void executeTask(Intent intent) {
        // SmsSyncPref.loadPreferences(SmsSyncAutoSyncService.this);
        Log.i(CLASS_TAG, "executeTask() executing this task");
        if (MainApplication.mDb.fetchMessagesCount() > 0) {
            int status = Util.snycToWeb(AutoSyncService.this,0);
            statusIntent.putExtra("status", status);
            sendBroadcast(statusIntent);
        }
        
    }


}
