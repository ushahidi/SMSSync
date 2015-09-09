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

import org.addhen.smssync.data.PrefsFactory;
import org.addhen.smssync.data.cache.FileManager;
import org.addhen.smssync.data.database.FilterDatabaseHelper;
import org.addhen.smssync.data.database.MessageDatabaseHelper;
import org.addhen.smssync.data.database.WebServiceDatabaseHelper;
import org.addhen.smssync.data.net.AppHttpClient;
import org.addhen.smssync.data.net.MessageHttpClient;
import org.addhen.smssync.data.process.ProcessMessage;
import org.addhen.smssync.data.process.ProcessMessageResult;
import org.addhen.smssync.data.repository.FilterDataRepository;
import org.addhen.smssync.data.repository.LogDataRepository;
import org.addhen.smssync.domain.repository.FilterRepository;
import org.addhen.smssync.domain.repository.LogRepository;
import org.addhen.smssync.presentation.App;
import org.addhen.smssync.presentation.Prefs;
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

    App mApp;

    public AppModule(App application) {
        mApp = application;
    }

    @Provides
    @Singleton
    FilterRepository provideFilterRepository(
            FilterDataRepository filterDataRepository) {
        return filterDataRepository;
    }

    @Provides
    @Singleton
    LogRepository provideLogRepository(LogDataRepository logDataRepository) {
        return logDataRepository;
    }

    @Provides
    @Singleton
    FileManager provideFileManager(Context context, PrefsFactory prefsFactory) {
        return new FileManager(context, prefsFactory);
    }

    @Provides
    @Singleton
    Prefs providePrefs(Context context) {
        return new Prefs(context);
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
    ProcessMessage provideProcessMessage(Context context, PrefsFactory prefsFactory,
            MessageHttpClient messageHttpClient,
            MessageDatabaseHelper messageDatabaseHelper,
            WebServiceDatabaseHelper webServiceDatabaseHelper,
            FilterDatabaseHelper filterDatabaseHelper,
            ProcessSms processSms,
            FileManager fileManager,
            ProcessMessageResult processMessageResult) {
        return new ProcessMessage(context, prefsFactory, messageHttpClient, messageDatabaseHelper,
                webServiceDatabaseHelper, filterDatabaseHelper, processSms, fileManager,
                processMessageResult);
    }
}
