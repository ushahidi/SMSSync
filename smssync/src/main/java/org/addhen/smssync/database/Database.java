/*******************************************************************************
 *  Copyright (c) 2010 - 2013 Ushahidi Inc
 *  All rights reserved
 *  Contact: team@ushahidi.com
 *  Website: http://www.ushahidi.com
 *  GNU Lesser General Public License Usage
 *  This file may be used under the terms of the GNU Lesser
 *  General Public License version 3 as published by the Free Software
 *  Foundation and appearing in the file LICENSE.LGPL included in the
 *  packaging of this file. Please review the following information to
 *  ensure the GNU Lesser General Public License version 3 requirements
 *  will be met: http://www.gnu.org/licenses/lgpl.html.
 *
 * If you have questions regarding the use of this file, please contact
 * Ushahidi developers at team@ushahidi.com.
 ******************************************************************************/

package org.addhen.smssync.database;

import org.addhen.smssync.tasks.TaskExecutor;
import org.addhen.smssync.util.Util;

import android.content.Context;

/**
 * Handles all database activities.
 *
 * @author eyedol
 */
public class Database {

    private Context mContext;

    public Database(Context context) {
        mContext = context;
    }

    public MessageDatabaseHelper getMessageInstance() {
        return MessageDatabaseHelper.getInstance(mContext, TaskExecutor.getInstance());
    }

    public SyncUrlDatabaseHelper getSyncUrlInstance() {
        return SyncUrlDatabaseHelper.getInstance(mContext, TaskExecutor.getInstance());
    }

    public FilterDatabaseHelper getFilterInstance() {
        return FilterDatabaseHelper.getInstance(mContext, TaskExecutor.getInstance());
    }
}
