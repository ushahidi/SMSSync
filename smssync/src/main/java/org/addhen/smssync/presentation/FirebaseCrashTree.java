/*
 * Copyright (c) 2010 - 2017 Ushahidi Inc
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

package org.addhen.smssync.presentation;

import com.google.firebase.crash.FirebaseCrash;

import android.util.Log;

import timber.log.Timber;

/**
 * A {@link Timber.Tree} that logs and sends crash reports to firebase's crash
 * report console.
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class FirebaseCrashTree extends Timber.Tree {

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            FirebaseCrash.log((priority == Log.DEBUG ? "[debug] " : "[verbose] ") + tag + ": "
                    + message);
            return;
        }
        FirebaseCrash.logcat(priority, tag, message);
        if (t == null) {
            return;
        }

        if (priority == Log.ERROR || priority == Log.WARN) {
            FirebaseCrash.report(t);
        }
    }
}
