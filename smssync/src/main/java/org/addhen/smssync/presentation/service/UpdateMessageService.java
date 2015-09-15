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

package org.addhen.smssync.presentation.service;

import org.addhen.smssync.presentation.presenter.message.UpdateMessagePresenter;

import android.content.Intent;

import javax.inject.Inject;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class UpdateMessageService extends BaseWakefulIntentService {

    private static final String TAG = UpdateMessageService.class.getSimpleName();

    @Inject
    UpdateMessagePresenter mUpdateMessagePresenter;

    public UpdateMessageService() {
        super(TAG);
    }

    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void executeTask(Intent intent) {
        // Do nothing
    }
}
