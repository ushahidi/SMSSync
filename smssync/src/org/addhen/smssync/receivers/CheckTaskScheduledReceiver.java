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

package org.addhen.smssync.receivers;

import org.addhen.smssync.services.SmsSyncServices;
import org.addhen.smssync.services.CheckTaskScheduledService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This Receiver class is used to listen for Broadcast Intents from the 
 * Alarm manager so it executes all task that exist.
 */
public class CheckTaskScheduledReceiver extends BroadcastReceiver{
    
    
    @Override
    public void onReceive(Context context, Intent intent) {
        SmsSyncServices.sendWakefulTask(context, CheckTaskScheduledService.class);
    }
    
}
