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

package org.addhen.smssync.presentation.receivers;

import org.addhen.smssync.data.PrefsFactory;
import org.addhen.smssync.presentation.di.component.AppComponent;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * This Receiver class is designed to listen for changes in connectivity. When we receive
 * connectivity the relevant Service classes will automatically push pending messages and check for
 * tasks. This class will restart the service that pushes pending messages to the configured URL
 * and check for tasks from the configured URL.
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class ConnectivityChangedReceiver extends BroadcastReceiver {

    AppComponent mAppComponent;

    private boolean isConnected;

    private PackageManager pm;

    private ComponentName connectivityReceiver;

    @Override
    public void onReceive(Context context, Intent intent) {

        // load current settings
        PrefsFactory prefs = mAppComponent.prefsFactory();
       
    }
}
