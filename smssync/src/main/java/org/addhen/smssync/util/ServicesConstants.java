/*****************************************************************************
 ** Copyright (c) 2010 - 2012 Ushahidi Inc
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
 *****************************************************************************/

package org.addhen.smssync.util;

public class ServicesConstants {

    public static int CHECK_TASK_SERVICE_REQUEST_CODE = 0;

    public static int CHECK_TASK_SCHEDULED_SERVICE_REQUEST_CODE = 1;

    public static int AUTO_SYNC_SERVICE_REQUEST_CODE = 2;

    public static int AUTO_SYNC_SCHEDULED_SERVICE_REQUEST_CODE = 3;

    public static String AUTO_SYNC_ACTION = "org.addhen.smssync.syncservices.autosync";

    public static String CHECT_TASK_ACTION = "org.addhen.smssync.syncservices.checktask";

    public static String AUTO_SYNC_SCHEDULED_ACTION = "org.addhen.smssync.syncservices.autosyncscheduled";

    public static String CHECT_TASK_SCHEDULED_ACTION = "org.addhen.smssync.syncservices.checktaskscheduled";
    
    public static String FAILED_ACTION = "org.addhen.smssync.syncservices.failed";
    
    public static String MESSAGE_UUID = "message_uuid";

    public static String SENT = "SMS_SENT";

    public static String DELIVERED = "SMS_DELIVERED";
    
    public static final int ACTIVE_SYNC_URL = 1;
    
    public static final int INACTIVE_SYNC_URL = 1;
}
