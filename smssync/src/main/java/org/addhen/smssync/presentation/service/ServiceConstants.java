/*
 * Copyright (c) 2010 - 2015 Ushahidi Inc
 * All rights reserved
 * Contact: team@ushahidi.com
 * Website: http://www.ushahidi.com
 * GNU Lesser General Public License Usage
 * This file may be used under the terms of the GNU Lesser
 * General Public License version 3 as published by the Free Software
 * Foundation and appearing in the file LICENSE.LGPL included in the
 * packaging of this file. Please review the following information to
 * ensure the GNU Lesser General Public License version 3 requirements
 * will be met: http://www.gnu.org/licenses/lgpl.html.
 *
 * If you have questions regarding the use of this file, please contact
 * Ushahidi developers at team@ushahidi.com.
 */

package org.addhen.smssync.presentation.service;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class ServiceConstants {

    public static final int ACTIVE_SYNC_URL = 1;

    public static final int INACTIVE_SYNC_URL = 0;

    public static final int ACTIVE_SYNC = 1;

    public static final int INACTIVE_SYNC = 0;

    public static final String SYNC_STATUS = "sync_status";

    public static final String DEFAULT_SMS_PROVIDER = "org.addhen.smssync.defaultsmsprovider";

    public static int CHECK_TASK_SERVICE_REQUEST_CODE = 0;

    public static int CHECK_TASK_SCHEDULED_SERVICE_REQUEST_CODE = 1;

    public static int AUTO_SYNC_SERVICE_REQUEST_CODE = 2;

    public static int AUTO_SYNC_SCHEDULED_SERVICE_REQUEST_CODE = 3;

    public static int MESSAGE_RESULTS_SCHEDULED_SERVICE_REQUEST_CODE = 4;

    public static String AUTO_SYNC_ACTION = "org.addhen.smssync.syncservices.autosync";

    public static String CHECT_TASK_ACTION = "org.addhen.smssync.syncservices.checktask";

    public static String AUTO_SYNC_SCHEDULED_ACTION
            = "org.addhen.smssync.syncservices.autosyncscheduled";

    public static String CHECT_TASK_SCHEDULED_ACTION
            = "org.addhen.smssync.syncservices.checktaskscheduled";

    public static String FAILED_ACTION = "org.addhen.smssync.syncservices.failed";

    public static String BATTERY_LEVEL = "org.addhen.smssync.syncservices.batterylevel";

    public static String MESSAGE_UUID = "message_uuid";

    public static String UPDATE_MESSAGE = "UPDATE_MESSAGE";

    public static String DELETE_MESSAGE = "DELETE_MESSAGE";
}
