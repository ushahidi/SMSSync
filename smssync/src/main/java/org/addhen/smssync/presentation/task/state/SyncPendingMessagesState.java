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

package org.addhen.smssync.presentation.task.state;

import org.addhen.smssync.R;
import org.addhen.smssync.presentation.task.SyncType;

import android.content.res.Resources;

import static org.addhen.smssync.presentation.task.SyncType.UNKNOWN;
import static org.addhen.smssync.presentation.task.state.SyncState.INITIAL;
import static org.addhen.smssync.presentation.task.state.SyncState.SYNC;

/**
 * Defines the different states when synchronizing pending message
 *
 * @author Henry Addo
 */
public class SyncPendingMessagesState extends State {

    public final SyncType syncType;

    public SyncPendingMessagesState(SyncState state, SyncType syncType, Exception exception) {
        super(state, exception);
        this.syncType = syncType;
    }

    /**
     * Create default state
     */
    public SyncPendingMessagesState() {
        this(INITIAL, UNKNOWN, null);
    }

    @Override
    public SyncPendingMessagesState transition(SyncState newState, Exception exception) {
        return new SyncPendingMessagesState(newState, syncType, exception);
    }

    /**
     * Get the notification
     */
    public String getNotification(Resources resources) {

        String msg = super.getNotificationMessage(resources);
        if (msg != null) {
            return msg;
        }
        if (state == SYNC) {
            msg = resources.getString(R.string.status_sync_details);

            return msg;
        }
        return "";

    }

    @Override
    public String toString() {
        return "SyncStateChanged[" +
                "state=" + state +
                ", syncType=" + syncType +
                ']';
    }
}
