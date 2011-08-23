
package org.addhen.smssync.services;

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

import org.addhen.smssync.util.Util;

import android.content.Intent;
import android.util.Log;

public class CheckTaskScheduledService extends SmsSyncServices {

    private static final String CLASS_TAG = CheckTaskScheduledService.class.getSimpleName();

    public CheckTaskScheduledService() {
        super(CLASS_TAG);
    }

    @Override
    public void executeTask(Intent intent) {
        Log.i(CLASS_TAG, "checkin scheduled task services");
        // Perform a task
        Util.performTask(CheckTaskScheduledService.this);
    }
}
