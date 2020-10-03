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

import org.addhen.smssync.R;
import org.addhen.smssync.data.PrefsFactory;
import org.addhen.smssync.data.cache.FileManager;
import org.addhen.smssync.data.message.PostMessage;
import org.addhen.smssync.data.util.Utility;
import org.addhen.smssync.presentation.presenter.AlertPresenter;

import android.content.Intent;

import javax.inject.Inject;

/**
 * @author Henry Addo
 */
public class CheckTaskService extends BaseWakefulIntentService {

    private final static String CLASS_TAG = CheckTaskService.class
            .getSimpleName();

    @Inject
    PostMessage mProcessMessage;

    @Inject
    PrefsFactory mPrefsFactory;

    @Inject
    FileManager mFileManager;

    @Inject
    AlertPresenter mAlertPresenter;

    public CheckTaskService() {
        super(CLASS_TAG);
    }

    public void onCreate() {
        super.onCreate();
        getComponent().inject(this);
    }

    protected void executeTask(Intent intent) {
        log("checkTaskService: check if a task has been enabled.");
        if (Utility.isConnected(this)) {
            if (mPrefsFactory.serviceEnabled().get() && mPrefsFactory.enableTaskCheck().get()) {
                mProcessMessage = getAppComponent().processMessage();
                mProcessMessage.performTask();
            }
            return;
        }
        mFileManager.append(getString(R.string.no_data_connection));
    }
}
