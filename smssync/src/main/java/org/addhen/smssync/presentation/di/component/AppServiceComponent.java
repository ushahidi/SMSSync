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

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */

import org.addhen.smssync.presentation.di.module.ServiceModule;
import org.addhen.smssync.presentation.di.qualifier.ServiceScope;
import org.addhen.smssync.presentation.presenter.message.DeleteMessagePresenter;
import org.addhen.smssync.presentation.presenter.message.PublishMessagePresenter;
import org.addhen.smssync.presentation.presenter.message.UpdateMessagePresenter;
import org.addhen.smssync.presentation.service.AutoSyncScheduledService;
import org.addhen.smssync.presentation.service.BaseWakefulIntentService;
import org.addhen.smssync.presentation.service.CheckTaskService;
import org.addhen.smssync.presentation.service.DeleteMessageService;
import org.addhen.smssync.presentation.service.MessageResultsService;
import org.addhen.smssync.presentation.service.SmsReceiverService;
import org.addhen.smssync.presentation.service.SyncPendingMessagesService;
import org.addhen.smssync.presentation.service.UpdateMessageService;

import dagger.Component;

@ServiceScope
@Component(dependencies = {AppComponent.class}, modules = {ServiceModule.class})
public interface AppServiceComponent {

    void inject(SmsReceiverService smsReceiverService);

    void inject(BaseWakefulIntentService baseWakefulIntentService);

    void inject(CheckTaskService baseWakefulIntentService);

    void inject(DeleteMessageService deleteMessageService);

    void inject(MessageResultsService messageResultsService);

    void inject(UpdateMessageService updateMessageService);

    void inject(SyncPendingMessagesService syncPendingMessagesService);

    void inject(AutoSyncScheduledService autoSyncScheduledService);

    UpdateMessagePresenter updateMessagePresenter();

    DeleteMessagePresenter deleteMessagePresenter();

    PublishMessagePresenter publishMessagePresenter();
}
