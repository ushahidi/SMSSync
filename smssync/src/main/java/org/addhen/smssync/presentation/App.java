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

package org.addhen.smssync.presentation;

import com.addhen.android.raiburari.presentation.BaseApplication;

import org.addhen.smssync.presentation.di.component.AppComponent;
import org.addhen.smssync.presentation.di.component.DaggerAppComponent;
import org.addhen.smssync.presentation.di.module.AppModule;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class App extends BaseApplication {

    AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeInjector();
    }

    private void initializeInjector() {
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    /**
     * Return the application tracker
     */
    public static AppTracker getInstance() {
        return TrackerResolver.getInstance();
    }
}
