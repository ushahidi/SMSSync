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

package org.addhen.smssync;

import android.os.Handler;
import android.os.Looper;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class UiThread {

    private final Handler handler;

    private UiThread() {
        this.handler = new Handler(Looper.getMainLooper());
    }

    public static UiThread getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * Causes the Runnable r to be added to the message queue. The runnable will be run on the main
     * thread.
     *
     * @param runnable {@link Runnable} to be executed.
     */
    public void post(Runnable runnable) {
        handler.post(runnable);
    }

    private static class LazyHolder {

        private static final UiThread INSTANCE = new UiThread();
    }
}
