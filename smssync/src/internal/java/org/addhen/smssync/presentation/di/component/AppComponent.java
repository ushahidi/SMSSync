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

import org.addhen.smssync.data.cache.FileManager;
import org.addhen.smssync.domain.repository.FilterRepository;
import org.addhen.smssync.domain.repository.LogRepository;
import org.addhen.smssync.domain.repository.MessageRepository;
import org.addhen.smssync.presentation.di.module.AppModule;
import org.addhen.smssync.presentation.di.module.InternalAppModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
@Singleton
@Component(modules = {AppModule.class, InternalAppModule.class})
public interface AppComponent extends ApplicationComponent {

    FilterRepository filterRepository();

    MessageRepository messageRepository();

    LogRepository logRepository();

    FileManager fileManager();
}
