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

package org.addhen.smssync.presentation.di.module;

import com.addhen.android.raiburari.presentation.di.module.ApplicationModule;

import org.addhen.smssync.BuildConfig;
import org.addhen.smssync.data.PrefsFactory;
import org.addhen.smssync.data.cache.FileManager;
import org.addhen.smssync.data.message.PostMessage;
import org.addhen.smssync.data.message.ProcessMessageResult;
import org.addhen.smssync.data.message.TweetMessage;
import org.addhen.smssync.data.net.AppHttpClient;
import org.addhen.smssync.data.net.MessageHttpClient;
import org.addhen.smssync.data.repository.datasource.filter.FilterDataSourceFactory;
import org.addhen.smssync.data.repository.datasource.message.MessageDataSourceFactory;
import org.addhen.smssync.data.repository.datasource.webservice.WebServiceDataSourceFactory;
import org.addhen.smssync.data.twitter.TwitterBuilder;
import org.addhen.smssync.data.twitter.TwitterClient;
import org.addhen.smssync.presentation.App;
import org.addhen.smssync.smslib.sms.ProcessSms;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Reusable Dagger modules for the entire app
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
@Module(includes = ApplicationModule.class)
public class AppModule {

    private static final String PREF_NAME = "SMS_SYNC_PREF";

    private App mApp;

    public AppModule(App application) {
        mApp = application;
    }

    @Provides
    @Singleton
    FileManager provideFileManager(Context context, PrefsFactory prefsFactory) {
        return new FileManager(context, prefsFactory);
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreference(Context context) {
        return context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    PrefsFactory providePrefsFactory(Context context, SharedPreferences sharedPreferences) {
        return new PrefsFactory(context, sharedPreferences);
    }

    @Provides
    @Singleton
    MessageHttpClient provideMessageHttpClient(Context context, FileManager fileManager) {
        return new MessageHttpClient(context, fileManager);
    }

    @Provides
    @Singleton
    AppHttpClient provideAppHttpClient(Context context) {
        return new AppHttpClient(context);
    }

    @Provides
    @Singleton
    TwitterClient provideTwitterApp() {
        return new TwitterBuilder(mApp,
                BuildConfig.TWITTER_CONSUMER_KEY,
                BuildConfig.TWITTER_CONSUMER_SECRET)
                .build();
    }

    @Provides
    @Singleton
    ProcessMessageResult provideProcessMessageResult(Context context, AppHttpClient appHttpClient,
            FileManager fileManager, WebServiceDataSourceFactory webServiceDataSourceFactory,
            MessageDataSourceFactory messageDataSourceFactory) {
        return new ProcessMessageResult(context, appHttpClient, fileManager,
                webServiceDataSourceFactory.createDatabaseDataSource(),
                messageDataSourceFactory.createMessageDatabaseSource());
    }

    @Provides
    @Singleton
    PostMessage provideProcessMessage(Context context, PrefsFactory prefsFactory,
            MessageHttpClient messageHttpClient,
            MessageDataSourceFactory messageDataSourceFactory,
            WebServiceDataSourceFactory webServiceDataSourceFactory,
            FilterDataSourceFactory filterDataSourceFactory,
            ProcessSms processSms,
            FileManager fileManager,
            TwitterClient twitterApp,
            ProcessMessageResult processMessageResult) {
        return new PostMessage(
                context,
                prefsFactory,
                messageHttpClient,
                messageDataSourceFactory.createMessageDatabaseSource(),
                webServiceDataSourceFactory.createDatabaseDataSource(),
                filterDataSourceFactory.createFilterDataSource(),
                processSms,
                fileManager,
                processMessageResult
        );
    }

    @Provides
    @Singleton
    TweetMessage provideTweetMessage(Context context, PrefsFactory prefsFactory,
            MessageHttpClient messageHttpClient,
            MessageDataSourceFactory messageDataSourceFactory,
            WebServiceDataSourceFactory webServiceDataSourceFactory,
            FilterDataSourceFactory filterDataSourceFactory,
            ProcessSms processSms,
            FileManager fileManager,
            TwitterClient twitterApp,
            ProcessMessageResult processMessageResult) {
        return new TweetMessage(
                context,
                prefsFactory,
                twitterApp,
                messageDataSourceFactory.createMessageDatabaseSource(),
                webServiceDataSourceFactory.createDatabaseDataSource(),
                filterDataSourceFactory.createFilterDataSource(),
                processSms,
                fileManager
        );
    }
}
