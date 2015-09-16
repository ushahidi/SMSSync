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

import org.addhen.smssync.R;

import android.content.Intent;

/**
 * The synchronization types
 *
 * @author Henry Addo
 */
public enum SyncType {
    UNKNOWN(R.string.unknown),
    MANUAL(R.string.manual);

    public static final String EXTRA = "org.addhen.smssync.SyncTypeAsString";

    public final int resId;

    SyncType(int resId) {
        this.resId = resId;
    }

    public static SyncType fromIntent(Intent intent) {
        if (intent.hasExtra(EXTRA)) {
            final String name = intent.getStringExtra(EXTRA);
            for (SyncType type : values()) {
                if (type.name().equals(name)) {
                    return type;
                }
            }
        }
        return UNKNOWN;
    }

    public boolean isBackground() {
        return this != MANUAL;
    }
}
