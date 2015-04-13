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

import android.app.Application;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import org.addhen.smssync.database.Database;

/**
 * This class is for maintaining global application state.
 *
 * @author eyedol
 */
public class App extends Application {

    public static final String TAG = "SmsSyncApplication";

    public static final SyncBus bus = new SyncBus(new Bus(ThreadEnforcer.ANY));

    public static Database mDb;

    public static Application app = null;

    /**
     * Return the application tracker
     */
    public static AppTracker getInstance() {
        return TrackerResolver.getInstance();
    }

    public static Database getDatabaseInstance() {

        if (mDb == null) {
            mDb = new Database(app);
        }

        return mDb;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Open database connection when the application starts.
        app = this;
        mDb = new Database(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
