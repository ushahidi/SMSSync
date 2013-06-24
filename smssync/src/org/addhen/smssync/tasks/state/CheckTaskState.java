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

package org.addhen.smssync.tasks.state;

import static org.addhen.smssync.tasks.state.SyncState.CHECK;
import static org.addhen.smssync.tasks.state.SyncState.INITIAL;

import org.addhen.smssync.R;
import org.addhen.smssync.tasks.SyncType;

import android.content.res.Resources;

/**
 * Check task state
 */
public class CheckTaskState extends State {

    public final int taskItemsReceived;

    public final int taskItemsExecuted;

    public final SyncType syncType;

    /**
     * @param state
     * @param exception
     */
    public CheckTaskState(SyncState state, int taskItemsReceived, int taskItemsExecuted,
            SyncType syncType, Exception exception) {
        super(state, exception);
        this.taskItemsReceived = taskItemsReceived;
        this.taskItemsExecuted = taskItemsExecuted;
        this.syncType = syncType;
    }

    public CheckTaskState() {
        this(INITIAL, 0, 0, null, null);
    }

    @Override
    public CheckTaskState transition(SyncState newState, Exception exception) {
        return new CheckTaskState(newState, taskItemsReceived, taskItemsExecuted, syncType,
                exception);
    }

    @Override
    public String toString() {
        return new StringBuilder("CheckTaskState").append("{")
                .append("state=")
                .append(state)
                .append(", taskItemsReceived")
                .append(taskItemsReceived)
                .append(", taskItemsExecuted")
                .append(taskItemsExecuted)
                .toString();
    }

    /**
     * Get the notification
     * 
     * @param resources
     * @return
     */
    public String getNotification(Resources resources) {

        String msg = super.getNotificationMessage(resources);
        if (msg != null)
            return msg;
        if (state == CHECK) {
            msg = resources.getString(R.string.status_check_details,
                    taskItemsExecuted,
                    taskItemsReceived);

            return msg;
        } else {
            return "";
        }

    }

}
