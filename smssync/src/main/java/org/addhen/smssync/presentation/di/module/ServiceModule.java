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

import org.addhen.smssync.domain.usecase.message.DeleteMessageUsecase;
import org.addhen.smssync.domain.usecase.message.PublishMessageUsecase;
import org.addhen.smssync.domain.usecase.message.UpdateMessageUsecase;
import org.addhen.smssync.presentation.di.qualifier.ServiceScope;

import android.app.Service;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
@Module
public class ServiceModule {

    private final Service mService;

    public ServiceModule(Service service) {
        mService = service;
    }

    /**
     * Expose the activity to dependents in the graph.
     */
    @Provides
    @ServiceScope
    Service service() {
        return mService;
    }

    @Provides
    @ServiceScope
    @Named("messageUpdate")
    UpdateMessageUsecase provideUpdateMessageUsecase(UpdateMessageUsecase updateMessageUsecase) {
        return updateMessageUsecase;
    }

    @Provides
    @ServiceScope
    @Named("messageDelete")
    DeleteMessageUsecase providesDeleteMessageUsecase(DeleteMessageUsecase deleteMessageUsecase) {
        return deleteMessageUsecase;
    }

    @Provides
    @ServiceScope
    @Named("messagePublish")
    PublishMessageUsecase providePublishedMessageUsecase(
            PublishMessageUsecase publishMessageUsecase) {
        return publishMessageUsecase;
    }
}
