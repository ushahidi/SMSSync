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

import org.addhen.smssync.domain.usecase.message.UpdateMessageUsecase;
import org.addhen.smssync.presentation.exception.ErrorMessageFactory;
import org.addhen.smssync.presentation.model.MessageModel;
import org.addhen.smssync.presentation.model.mapper.MessageModelDataMapper;
import org.addhen.smssync.presentation.view.message.UpdateMessageView;

import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class UpdateMessagePresenter implements Presenter {

    private final UpdateMessageUsecase mUpdateMessageUsecase;

    private final MessageModelDataMapper mMessageModelDataMapper;

    private UpdateMessageView mUpdateMessageView;

    /**
     * Default use case.
     *
     * @param updateMessageUsecase   The update deployment use case
     * @param messageModelDataMapper The deployment model data mapper
     */
    @Inject
    public UpdateMessagePresenter(
            @Named("messageUpdate") UpdateMessageUsecase updateMessageUsecase,
            MessageModelDataMapper messageModelDataMapper) {
        mUpdateMessageUsecase = updateMessageUsecase;
        mMessageModelDataMapper = messageModelDataMapper;
    }

    public void setUpdateMessageView(@NonNull UpdateMessageView updateMessageView) {
        mUpdateMessageView = updateMessageView;
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
        mUpdateMessageUsecase.unsubscribe();
    }

    /**
     * Updates {@link MessageModel}
     *
     * @param messageModel The deployment model to be updated
     */
    public void updateMessage(MessageModel messageModel) {
        mUpdateMessageUsecase.setMessageEntity(
                mMessageModelDataMapper.map(messageModel));
        mUpdateMessageUsecase.execute(new DefaultSubscriber<Long>() {
            @Override
            public void onCompleted() {
                // Do nothing
                mUpdateMessageView.showLoading();
            }

            @Override
            public void onError(Throwable e) {
                // Do nothing
                mUpdateMessageView.hideLoading();
                showErrorMessage(new DefaultErrorHandler((Exception) e));
                mUpdateMessageView.showRetry();
            }

            @Override
            public void onNext(Long row) {
                // Do nothing
                mUpdateMessageView.onMessageUpdated();
            }
        });
    }

    private void showErrorMessage(ErrorHandler errorHandler) {
        String errorMessage = ErrorMessageFactory.create(mUpdateMessageView.getAppContext(),
                errorHandler.getException());
        mUpdateMessageView.showError(errorMessage);
    }

}