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
import com.addhen.android.raiburari.presentation.di.qualifier.ActivityScope;
import com.addhen.android.raiburari.presentation.presenter.Presenter;

import org.addhen.smssync.domain.entity.MessageEntity;
import org.addhen.smssync.presentation.exception.ErrorMessageFactory;
import org.addhen.smssync.presentation.model.mapper.MessageModelDataMapper;
import org.addhen.smssync.presentation.view.message.ListMessageView;

import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
@ActivityScope
public class ListMessagePresenter implements Presenter {

    private final Usecase mListMessageUsecase;

    private final MessageModelDataMapper mMessageModelDataMapper;

    private ListMessageView mListMessageView;

    @Inject
    public ListMessagePresenter(@Named("messageList") Usecase listMessageUsecase,
            MessageModelDataMapper messageModelDataMapper) {
        mListMessageUsecase = listMessageUsecase;
        mMessageModelDataMapper = messageModelDataMapper;
    }

    @Override
    public void resume() {
        loadMessages();
    }

    @Override
    public void pause() {
        // Do nothing
    }

    @Override
    public void destroy() {
        mListMessageUsecase.unsubscribe();
    }

    public void setView(@NonNull ListMessageView listMessageView) {
        mListMessageView = listMessageView;
    }

    public void loadMessages() {
        mListMessageView.hideRetry();
        mListMessageView.showLoading();
        mListMessageUsecase.execute(new DefaultSubscriber<List<MessageEntity>>() {
            @Override
            public void onCompleted() {
                mListMessageView.hideLoading();
            }

            @Override
            public void onNext(List<MessageEntity> filterList) {
                mListMessageView.hideLoading();
                mListMessageView.showMessages(mMessageModelDataMapper.map(filterList));
            }

            @Override
            public void onError(Throwable e) {
                mListMessageView.hideLoading();
                showErrorMessage(new DefaultErrorHandler((Exception) e));
                mListMessageView.showRetry();
            }
        });
    }

    private void showErrorMessage(ErrorHandler errorHandler) {
        String errorMessage = ErrorMessageFactory.create(mListMessageView.getAppContext(),
                errorHandler.getException());
        mListMessageView.showError(errorMessage);
    }
}
