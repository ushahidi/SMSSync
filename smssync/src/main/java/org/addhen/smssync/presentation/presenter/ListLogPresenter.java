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

package org.addhen.smssync.presentation.presenter;

import com.addhen.android.raiburari.domain.exception.DefaultErrorHandler;
import com.addhen.android.raiburari.domain.exception.ErrorHandler;
import com.addhen.android.raiburari.domain.usecase.DefaultSubscriber;
import com.addhen.android.raiburari.domain.usecase.Usecase;
import com.addhen.android.raiburari.presentation.di.qualifier.ActivityScope;
import com.addhen.android.raiburari.presentation.presenter.Presenter;

import org.addhen.smssync.domain.entity.LogEntity;
import org.addhen.smssync.presentation.exception.ErrorMessageFactory;
import org.addhen.smssync.presentation.model.mapper.LogModelDataMapper;
import org.addhen.smssync.presentation.view.log.ListLogView;

import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
@ActivityScope
public class ListLogPresenter implements Presenter {

    private final Usecase mListLogUsecase;

    private final LogModelDataMapper mLogModelDataMapper;

    private ListLogView mListLogView;

    @Inject
    public ListLogPresenter(@Named("logList") Usecase listLogUsecase,
            LogModelDataMapper logModelDataMapper) {
        mListLogUsecase = listLogUsecase;
        mLogModelDataMapper = logModelDataMapper;
    }

    @Override
    public void resume() {
        loadLogs();
    }

    @Override
    public void pause() {
        // Do nothing
    }

    @Override
    public void destroy() {
        mListLogUsecase.unsubscribe();
    }

    public void setView(@NonNull ListLogView listLogView) {
        mListLogView = listLogView;
    }

    public void loadLogs() {
        mListLogUsecase.execute(new DefaultSubscriber<List<LogEntity>>() {
            @Override
            public void onStart() {
                mListLogView.hideRetry();
                mListLogView.showLoading();
            }

            @Override
            public void onCompleted() {
                mListLogView.hideLoading();
            }

            @Override
            public void onNext(List<LogEntity> logList) {
                mListLogView.hideLoading();
                mListLogView.showLogs(mLogModelDataMapper.map(logList));
            }

            @Override
            public void onError(Throwable e) {
                mListLogView.hideLoading();
                showErrorMessage(new DefaultErrorHandler((Exception) e));
                mListLogView.showRetry();
            }
        });
    }

    private void showErrorMessage(ErrorHandler errorHandler) {
        String errorMessage = ErrorMessageFactory.create(mListLogView.getAppContext(),
                errorHandler.getException());
        mListLogView.showError(errorMessage);
    }
}