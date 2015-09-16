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
import com.addhen.android.raiburari.presentation.presenter.Presenter;

import org.addhen.smssync.domain.usecase.message.DeleteMessageUsecase;
import org.addhen.smssync.presentation.exception.ErrorMessageFactory;
import org.addhen.smssync.presentation.model.mapper.MessageModelDataMapper;
import org.addhen.smssync.presentation.view.message.DeleteMessageView;

import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class DeleteMessagePresenter implements Presenter {

    private final DeleteMessageUsecase mDeleteMessageUsecase;

    private DeleteMessageView mDeleteMessageView;

    @Inject
    public DeleteMessagePresenter(
            @Named("messageDelete") DeleteMessageUsecase deleteMessageUsecase,
            MessageModelDataMapper messageModelDataMapper) {
        mDeleteMessageUsecase = deleteMessageUsecase;
    }

    public void setView(@NonNull DeleteMessageView deleteMessageView) {
        mDeleteMessageView = deleteMessageView;
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
        mDeleteMessageUsecase.unsubscribe();
    }

    public void deleteMessage(String messageUuid) {
        mDeleteMessageUsecase.setMessageUuid(messageUuid);
        mDeleteMessageUsecase.execute(new DeleteMessageSubscriber());
    }

    private void showErrorMessage(ErrorHandler errorHandler) {
        String errorMessage = ErrorMessageFactory.create(mDeleteMessageView.getAppContext(),
                errorHandler.getException());
        mDeleteMessageView.showError(errorMessage);
    }

    private class DeleteMessageSubscriber extends DefaultSubscriber<Integer> {

        @Override
        public void onStart() {
            mDeleteMessageView.hideRetry();
            mDeleteMessageView.showLoading();
        }

        @Override
        public void onCompleted() {
            mDeleteMessageView.hideLoading();
        }

        @Override
        public void onNext(Integer row) {
            mDeleteMessageView.hideLoading();
            mDeleteMessageView.onMessageDeleted();
        }

        @Override
        public void onError(Throwable e) {
            mDeleteMessageView.hideLoading();
            showErrorMessage(new DefaultErrorHandler((Exception) e));
            mDeleteMessageView.showRetry();
        }
    }
}
