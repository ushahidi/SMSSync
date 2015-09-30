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

package org.addhen.smssync.presentation.di.component;

import com.addhen.android.raiburari.presentation.di.component.ApplicationComponent;
import com.addhen.android.raiburari.presentation.di.module.ApplicationModule;

import org.addhen.smssync.data.PrefsFactory;
import org.addhen.smssync.data.cache.FileManager;
import org.addhen.smssync.data.message.PostMessage;
import org.addhen.smssync.data.message.ProcessMessageResult;
import org.addhen.smssync.data.message.TweetMessage;
import org.addhen.smssync.data.net.AppHttpClient;
import org.addhen.smssync.data.net.MessageHttpClient;
import org.addhen.smssync.data.twitter.TwitterClient;
import org.addhen.smssync.domain.repository.FilterRepository;
import org.addhen.smssync.domain.repository.LogRepository;
import org.addhen.smssync.domain.repository.MessageRepository;
import org.addhen.smssync.domain.repository.WebServiceRepository;
import org.addhen.smssync.presentation.App;
import org.addhen.smssync.presentation.di.module.AppModule;
import org.addhen.smssync.presentation.di.module.WithAnalyticsAppModule;
import org.addhen.smssync.presentation.presenter.AlertPresenter;
import org.addhen.smssync.presentation.presenter.DebugPresenter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
@Singleton
@Component(modules = {AppModule.class, WithAnalyticsAppModule.class})
public interface AppComponent extends ApplicationComponent {

    FilterRepository filterRepository();

    MessageRepository messageRepository();

    WebServiceRepository webServiceRepository();

    LogRepository logRepository();

    FileManager fileManager();

    PrefsFactory prefsFactory();

    AppHttpClient appHttpClient();

    MessageHttpClient messageHttpClient();

    TwitterClient twitterClient();

    ProcessMessageResult processMessageResult();

    PostMessage processMessage();

    TweetMessage tweetMessage();

    DebugPresenter debugPresenter();

    AlertPresenter alertPresenter();

    final class Initializer {

        private Initializer() {
        } // No instances.

        public static AppComponent init(App app) {
            return DaggerAppComponent.builder()
                    .applicationModule(new ApplicationModule(app))
                    .appModule(new AppModule(app))
                    .build();
        }
    }
}