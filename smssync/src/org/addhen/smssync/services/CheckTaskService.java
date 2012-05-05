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

import org.addhen.smssync.util.Util;

import android.content.Intent;
import android.util.Log;

/**
 * A this class handles background services for periodic checks of task that
 * needs to be executed by the app. Task for now is sending SMS. In the future,
 * it will support other tasks. Maybe send email.
 * 
 * @author eyedol
 */
public class CheckTaskService extends SmsSyncServices {

    private final static String CLASS_TAG = CheckTaskService.class.getSimpleName();

    public CheckTaskService() {
        super(CLASS_TAG);
    }

    /**
     * Starts the background service
     * 
     * @return void
     */
    protected void executeTask(Intent intent) {
        Log.i(CLASS_TAG, "checkTaskService: check if a task has been enabled.");
        // Perform a task
        Util.performTask(CheckTaskService.this);
    }

}
