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

package org.addhen.smssync.presentation.presenter.message;

import com.addhen.android.raiburari.domain.exception.DefaultErrorHandler;
import com.addhen.android.raiburari.domain.exception.ErrorHandler;
import com.addhen.android.raiburari.domain.usecase.DefaultSubscriber;
import com.addhen.android.raiburari.domain.usecase.Usecase;
import com.addhen.android.raiburari.presentation.presenter.Presenter;

import org.addhen.smssync.R;
import org.addhen.smssync.data.PrefsFactory;
import org.addhen.smssync.presentation.exception.ErrorMessageFactory;
import org.addhen.smssync.presentation.model.mapper.MessageModelDataMapper;
import org.addhen.smssync.presentation.view.message.PublishAllMessagesView;

import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class PublishAllMessagesPresenter implements Presenter {

    private final Usecase mPublishAllMessagesUsecase;

    private final MessageModelDataMapper mMessageModelDataMapper;

    private PublishAllMessagesView mPublishMessagesView;

    private PrefsFactory mPrefsFactory;

    @Inject
    public PublishAllMessagesPresenter(
            @Named("publishAllMessages") Usecase publishAllMessagesUsecase,
            MessageModelDataMapper messageModelDataMapper,
            PrefsFactory prefsFactory) {
        mPublishAllMessagesUsecase = publishAllMessagesUsecase;
        mMessageModelDataMapper = messageModelDataMapper;
        mPrefsFactory = prefsFactory;
    }

    public void setView(@NonNull PublishAllMessagesView publishMessageView) {
        mPublishMessagesView = publishMessageView;
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
        mPublishAllMessagesUsecase.unsubscribe();
    }

    public void publishMessages() {
        if (!mPrefsFactory.serviceEnabled().get()) {
            mPublishMessagesView.showEnableServiceMessage(
                    mPublishMessagesView.getAppContext().getString(R.string.smssync_not_enabled));
            return;
        }
        mPublishAllMessagesUsecase.execute(new PublishMessageSubscriber());
    }

    private void showErrorMessage(ErrorHandler errorHandler) {
        String errorMessage = ErrorMessageFactory.create(mPublishMessagesView.getAppContext(),
                errorHandler.getException());
        mPublishMessagesView.showError(errorMessage);
    }

    private class PublishMessageSubscriber extends DefaultSubscriber<Boolean> {

        @Override
        public void onStart() {
            mPublishMessagesView.hideRetry();
            mPublishMessagesView.showLoading();
        }

        @Override
        public void onCompleted() {
            mPublishMessagesView.hideLoading();
        }

        @Override
        public void onNext(Boolean status) {
            mPublishMessagesView.hideLoading();
            mPublishMessagesView.successfullyPublished(status);
        }

        @Override
        public void onError(Throwable e) {
            mPublishMessagesView.hideLoading();
            showErrorMessage(new DefaultErrorHandler((Exception) e));
            mPublishMessagesView.showRetry();
        }
    }
}
