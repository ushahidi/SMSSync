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

import org.addhen.smssync.presentation.presenter.message.DeleteMessagePresenter;
import org.addhen.smssync.presentation.view.message.DeleteMessageView;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class DeleteMessageService extends BaseWakefulIntentService implements DeleteMessageView {

    private static final String TAG = DeleteMessageService.class.getSimpleName();

    private DelayedStopHandler mDelayedStopHandler = new DelayedStopHandler(this);

    private static final int STOP_DELAY = 30000;

    private boolean mServiceStarted;

    @Inject
    DeleteMessagePresenter mDeleteMessagePresenter;

    public DeleteMessageService() {
        super(TAG);
    }

    public void onCreate() {
        super.onCreate();
        getComponent().inject(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mDeleteMessagePresenter.destroy();
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mDelayedStopHandler.sendEmptyMessageDelayed(0, STOP_DELAY);
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mServiceStarted = false;

    }
    // Release resources

    @Override
    protected void executeTask(Intent intent) {
        mServiceStarted = true;
        String uuid = intent.getStringExtra(ServiceConstants.DELETE_MESSAGE);
        mDeleteMessagePresenter.deleteMessage(uuid);

    }

    @Override
    public void onMessageDeleted() {
        stopSelf();
        mServiceStarted = false;
    }

    @Override
    public void showLoading() {
        // Do nothing
    }

    @Override
    public void hideLoading() {
        // Do nothing
    }

    @Override
    public void showRetry() {
        // Do nothing
    }

    @Override
    public void hideRetry() {
        // Do nothing
    }

    @Override
    public void showError(String message) {
        // Do nothing
    }

    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }

    /**
     * Makes sense to offer some form of self stopping mechanism in case the service keeps running
     * forever.
     *
     * Credits:https://goo.gl/9KZQon
     */
    private static class DelayedStopHandler extends Handler {

        private final WeakReference<DeleteMessageService> mWeakReference;

        private DelayedStopHandler(DeleteMessageService service) {
            mWeakReference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            DeleteMessageService service = mWeakReference.get();
            if (service != null) {
                service.stopSelf();
                service.mServiceStarted = false;
            }
        }
    }
}
