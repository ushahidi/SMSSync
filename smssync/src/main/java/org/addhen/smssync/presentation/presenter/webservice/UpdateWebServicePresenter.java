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

import org.addhen.smssync.domain.usecase.webservice.TestWebServiceUsecase;
import org.addhen.smssync.domain.usecase.webservice.UpdateWebServiceUsecase;
import org.addhen.smssync.presentation.exception.ErrorMessageFactory;
import org.addhen.smssync.presentation.model.WebServiceModel;
import org.addhen.smssync.presentation.model.mapper.WebServiceModelDataMapper;
import org.addhen.smssync.presentation.view.webservice.TestWebServiceView;
import org.addhen.smssync.presentation.view.webservice.UpdateWebServiceView;

import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class UpdateWebServicePresenter implements Presenter {

    private final UpdateWebServiceUsecase mUpdateWebServiceUsecase;

    private final WebServiceModelDataMapper mWebServiceModelDataMapper;

    private UpdateWebServiceView mUpdateWebServiceView;

    private final TestWebServiceUsecase mTestWebServiceUsecase;

    private TestWebServiceView mTestWebServiceView;

    /**
     * Default use case.
     *
     * @param updateWebServiceUsecase   The update deployment use case
     * @param deploymentModelDataMapper The deployment model data mapper
     */
    @Inject
    public UpdateWebServicePresenter(
            @Named("webServiceUpdate") UpdateWebServiceUsecase updateWebServiceUsecase,
            WebServiceModelDataMapper deploymentModelDataMapper,
            @Named("testWebService") TestWebServiceUsecase testWebServiceUsecase) {
        mUpdateWebServiceUsecase = updateWebServiceUsecase;
        mWebServiceModelDataMapper = deploymentModelDataMapper;
        mTestWebServiceUsecase = testWebServiceUsecase;
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
        mUpdateWebServiceUsecase.unsubscribe();
        mTestWebServiceUsecase.unsubscribe();
    }

    public void setView(@NonNull UpdateWebServiceView addWebServiceView) {
        mUpdateWebServiceView = addWebServiceView;
    }

    public void setTestWebServiceView(@NonNull TestWebServiceView testWebServiceView) {
        mTestWebServiceView = testWebServiceView;
    }

    /**
     * Updates {@link WebServiceModel}
     *
     * @param deploymentModel The deployment model to be updated
     */
    public void updateWebService(WebServiceModel deploymentModel) {
        mUpdateWebServiceView.hideRetry();
        mUpdateWebServiceView.showLoading();
        mUpdateWebServiceUsecase.setWebServiceEntity(
                mWebServiceModelDataMapper.map(deploymentModel));
        mUpdateWebServiceUsecase.execute(new DefaultSubscriber<Long>() {
            @Override
            public void onCompleted() {
                mUpdateWebServiceView.hideLoading();
            }

            @Override
            public void onError(Throwable e) {
                mUpdateWebServiceView.hideLoading();
                showErrorMessage(new DefaultErrorHandler((Exception) e));
                mUpdateWebServiceView.showRetry();
            }

            @Override
            public void onNext(Long row) {
                mUpdateWebServiceView.onWebServiceSuccessfullyUpdated(row);
            }
        });
    }

    public void testWebService(WebServiceModel webServiceModel) {
        mTestWebServiceUsecase.setWebServiceEntity(mWebServiceModelDataMapper.map(webServiceModel));
        mTestWebServiceUsecase.execute(new DefaultSubscriber<Boolean>() {
            @Override
            public void onStart() {
                mTestWebServiceView.showLoading();
            }

            @Override
            public void onCompleted() {
                mTestWebServiceView.hideLoading();
            }

            @Override
            public void onError(Throwable e) {
                mTestWebServiceView.hideLoading();
                showErrorMessage(new DefaultErrorHandler((Exception) e));
                mTestWebServiceView.showRetry();
            }

            @Override
            public void onNext(Boolean status) {
                mTestWebServiceView.webServiceTested(status);
            }
        });
    }

    private void showErrorMessage(ErrorHandler errorHandler) {
        String errorMessage = ErrorMessageFactory.create(mUpdateWebServiceView.getAppContext(),
                errorHandler.getException());
        mUpdateWebServiceView.showError(errorMessage);
    }
}