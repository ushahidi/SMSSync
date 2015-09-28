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

import org.addhen.smssync.data.repository.InternalFilterDataRepository;
import org.addhen.smssync.data.repository.InternalLogDataRepository;
import org.addhen.smssync.data.repository.InternalMessageDataRepository;
import org.addhen.smssync.data.repository.InternalWebServiceDataRepository;
import org.addhen.smssync.domain.repository.FilterRepository;
import org.addhen.smssync.domain.repository.LogRepository;
import org.addhen.smssync.domain.repository.MessageRepository;
import org.addhen.smssync.domain.repository.WebServiceRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
@Module
public class InternalAppModule {

    @Provides
    @Singleton
    MessageRepository provideInternalMessageRepository(
            InternalMessageDataRepository internalMessageDataRepository) {
        return internalMessageDataRepository;
    }

    @Provides
    @Singleton
    WebServiceRepository providesInternalWebServiceRepository(
            InternalWebServiceDataRepository webServiceRepository) {
        return webServiceRepository;
    }

    @Provides
    @Singleton
    LogRepository providesInternalLogRepository(
            InternalLogDataRepository logRepository) {
        return logRepository;
    }

    @Provides
    @Singleton
    FilterRepository providesInternalFilterRepository(
            InternalFilterDataRepository filterRepository) {
        return filterRepository;
    }
}
