/*
 * Copyright (c) 2010 - 2017 Ushahidi Inc
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

package org.addhen.smssync.presentation.presenter.message;

import com.addhen.android.raiburari.domain.exception.DefaultErrorHandler;
import com.addhen.android.raiburari.domain.exception.ErrorHandler;
import com.addhen.android.raiburari.domain.usecase.DefaultSubscriber;
import com.addhen.android.raiburari.presentation.presenter.Presenter;

import org.addhen.smssync.R;
import org.addhen.smssync.data.PrefsFactory;
import org.addhen.smssync.domain.usecase.message.PublishMessageUsecase;
import org.addhen.smssync.presentation.exception.ErrorMessageFactory;
import org.addhen.smssync.presentation.model.MessageModel;
import org.addhen.smssync.presentation.model.mapper.MessageModelDataMapper;
import org.addhen.smssync.presentation.view.message.PublishMessageView;

import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class PublishMessagePresenter implements Presenter {

    private final PublishMessageUsecase mPublishMessageUsecase;

    private final MessageModelDataMapper mMessageModelDataMapper;

    private PublishMessageView mPublishMessageView;

    private PrefsFactory mPrefsFactory;

    @Inject
    public PublishMessagePresenter(
            @Named("messagePublish") PublishMessageUsecase publishMessageUsecase,
            MessageModelDataMapper messageModelDataMapper,
            PrefsFactory prefsFactory) {
        mPublishMessageUsecase = publishMessageUsecase;
        mMessageModelDataMapper = messageModelDataMapper;
        mPrefsFactory = prefsFactory;
    }

    public void setView(@NonNull PublishMessageView publishMessageView) {
        mPublishMessageView = publishMessageView;
    }

    @Override
    public void resume() {
        // Do nothing
    }

    @Override
    public void pause() {
        // Do nothing
    }

    @Override
    public void destroy() {
        mPublishMessageUsecase.unsubscribe();
    }

    public void publishMessage(MessageModel messageModels) {
        if (!mPrefsFactory.serviceEnabled().get()) {
            mPublishMessageView.showEnableServiceMessage(
                    mPublishMessageView.getAppContext().getString(R.string.smssync_not_enabled));
            return;
        }
        mPublishMessageUsecase.setMessageEntity(mMessageModelDataMapper.map(messageModels));
        mPublishMessageUsecase.execute(new PublishMessageSubscriber());
    }

    private void showErrorMessage(ErrorHandler errorHandler) {
        String errorMessage = ErrorMessageFactory.create(mPublishMessageView.getAppContext(),
                errorHandler.getException());
        mPublishMessageView.showError(errorMessage);
    }

    private class PublishMessageSubscriber extends DefaultSubscriber<Boolean> {

        @Override
        public void onStart() {
            mPublishMessageView.hideRetry();
            mPublishMessageView.showLoading();
        }

        @Override
        public void onCompleted() {
            mPublishMessageView.hideLoading();
        }

        @Override
        public void onNext(Boolean status) {
            mPublishMessageView.hideLoading();
            mPublishMessageView.successfullyPublished(status);
        }

        @Override
        public void onError(Throwable e) {
            mPublishMessageView.hideLoading();
            showErrorMessage(new DefaultErrorHandler((Exception) e));
            mPublishMessageView.showRetry();
        }
    }
}
