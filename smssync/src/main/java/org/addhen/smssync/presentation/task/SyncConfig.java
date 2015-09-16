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

package org.addhen.smssync.presentation.task;

import java.util.ArrayList;

/**
 * Configures synchronization task
 *
 * @author Henry Addo
 */
public class SyncConfig {

    public final boolean skip;

    public final int tries;

    public final ArrayList<String> messageUuids;

    public final SyncType syncType;

    public SyncConfig(int tries, boolean skip, ArrayList<String> messageUuids, SyncType syncType) {
        this.tries = tries;
        this.skip = skip;
        this.syncType = syncType;
        this.messageUuids = messageUuids;
    }
}
