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
import com.addhen.android.raiburari.presentation.di.qualifier.ActivityScope;
import com.addhen.android.raiburari.presentation.presenter.Presenter;

import org.addhen.smssync.domain.entity.LogEntity;
import org.addhen.smssync.domain.usecase.log.AddLogUsecase;
import org.addhen.smssync.presentation.exception.ErrorMessageFactory;
import org.addhen.smssync.presentation.view.log.AddLogView;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
@ActivityScope
public class AddLogPresenter implements Presenter {

    private final AddLogUsecase mAddLogUsecase;

    private AddLogView mAddLogView;

    @Inject
    public AddLogPresenter(@Named("logAdd") AddLogUsecase addLogUsecase) {
        mAddLogUsecase = addLogUsecase;
    }

    public void setView(AddLogView addLogView) {
        mAddLogView = addLogView;
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
        mAddLogUsecase.unsubscribe();
    }


    public void addLog(String message) {
        LogEntity logEntity = new LogEntity();
        logEntity.setMessage(message);
        mAddLogUsecase.setLog(logEntity);
        mAddLogUsecase.execute(new DefaultSubscriber<Long>() {
            @Override
            public void onCompleted() {
                // Do nothing
            }

            @Override
            public void onNext(Long row) {
                mAddLogView.onAdded(row);
            }

            @Override
            public void onError(Throwable e) {
                showErrorMessage(new DefaultErrorHandler((Exception) e));
            }
        });
    }

    private void showErrorMessage(ErrorHandler errorHandler) {
        String errorMessage = ErrorMessageFactory.create(mAddLogView.getAppContext(),
                errorHandler.getException());
        mAddLogView.showError(errorMessage);
    }
}