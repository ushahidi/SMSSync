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

import org.addhen.smssync.BuildConfig;
import org.addhen.smssync.data.twitter.TwitterBuilder;
import org.addhen.smssync.data.twitter.TwitterClient;
import org.addhen.smssync.presentation.di.component.AppComponent;

import timber.log.Timber;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class App extends BaseApplication {

    private static AppComponent mAppComponent;

    private static TwitterClient mTwitter;

    private static App mApp;


    public static synchronized TwitterClient getTwitterInstance() {
        if (mTwitter == null) {
            mTwitter = new TwitterBuilder(mApp,
                    BuildConfig.TWITTER_CONSUMER_KEY,
                    BuildConfig.TWITTER_CONSUMER_SECRET)
                    .build();
        }
        return mTwitter;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initializeInjector();
        mApp = this;
        Timber.plant(new FirebaseCrashTree());
    }

    private void initializeInjector() {
        mAppComponent = AppComponent.Initializer.init(this);
    }

    public static AppComponent getAppComponent() {
        return mAppComponent;
    }

    /**
     * Return the application tracker
     */
    public static AppTracker getInstance() {
        return TrackerResolver.getInstance();
    }
}
