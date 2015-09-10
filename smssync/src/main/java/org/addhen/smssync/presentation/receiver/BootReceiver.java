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

package org.addhen.smssync.presentation.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This Receiver class listens for system boot. If smssync has been enabled run the app.
 *
 * @author Henry Addo
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean rebooted = intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED);
        boolean shutdown = intent.getAction().equals(Intent.ACTION_SHUTDOWN);
        if (shutdown) {
            // TODO: log when it's rebooted
        }

        if (rebooted) {
            // TODO: 9/10/15 activities if rebooted
        }
    }
}
