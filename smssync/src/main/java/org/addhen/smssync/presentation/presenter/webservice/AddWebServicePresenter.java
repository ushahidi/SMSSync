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

package org.addhen.smssync.presentation.presenter.webservice;

import com.addhen.android.raiburari.domain.exception.DefaultErrorHandler;
import com.addhen.android.raiburari.domain.exception.ErrorHandler;
import com.addhen.android.raiburari.domain.usecase.DefaultSubscriber;
import com.addhen.android.raiburari.presentation.presenter.Presenter;

import org.addhen.smssync.domain.usecase.webservice.AddWebServiceUsecase;
import org.addhen.smssync.presentation.exception.ErrorMessageFactory;
import org.addhen.smssync.presentation.model.WebServiceModel;
import org.addhen.smssync.presentation.model.mapper.WebServiceModelDataMapper;
import org.addhen.smssync.presentation.view.webservice.AddWebServiceView;

import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class AddWebServicePresenter implements Presenter {

    private final AddWebServiceUsecase mAddWebServiceUsecase;

    private final WebServiceModelDataMapper mWebServiceModelDataMapper;

    private AddWebServiceView mAddWebServiceView;

    /**
     * Default constructor
     *
     * @param addWebServiceUsecase      The add webService use case
     * @param webServiceModelDataMapper the webService model data mapper
     */
    @Inject
    public AddWebServicePresenter(@Named("webServiceAdd") AddWebServiceUsecase addWebServiceUsecase,
            WebServiceModelDataMapper webServiceModelDataMapper) {
        mAddWebServiceUsecase = addWebServiceUsecase;
        mWebServiceModelDataMapper = webServiceModelDataMapper;
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
        mAddWebServiceUsecase.unsubscribe();
    }

    public void setView(@NonNull AddWebServiceView addWebServiceView) {
        mAddWebServiceView = addWebServiceView;
    }

    /**
     * Save a webService model into storage
     *
     * @param webServiceModel The webService model to be saved
     */
    public void addWebService(WebServiceModel webServiceModel) {
        mAddWebServiceView.hideRetry();
        mAddWebServiceView.showLoading();
        mAddWebServiceUsecase.setWebServiceEntity(mWebServiceModelDataMapper.map(webServiceModel));
        mAddWebServiceUsecase.execute(new DefaultSubscriber<Long>() {
            @Override
            public void onCompleted() {
                mAddWebServiceView.hideLoading();
            }

            @Override
            public void onError(Throwable e) {
                mAddWebServiceView.hideLoading();
                showErrorMessage(new DefaultErrorHandler((Exception) e));
                mAddWebServiceView.showRetry();
            }

            @Override
            public void onNext(Long row) {
                mAddWebServiceView.onWebServiceSuccessfullyAdded(row);
            }
        });
    }

    private void showErrorMessage(ErrorHandler errorHandler) {
        String errorMessage = ErrorMessageFactory.create(mAddWebServiceView.getAppContext(),
                errorHandler.getException());
        mAddWebServiceView.showError(errorMessage);
    }
}