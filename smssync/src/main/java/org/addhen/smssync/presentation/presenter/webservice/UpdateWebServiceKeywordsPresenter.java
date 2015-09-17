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

import org.addhen.smssync.domain.usecase.webservice.UpdateWebServiceUsecase;
import org.addhen.smssync.presentation.exception.ErrorMessageFactory;
import org.addhen.smssync.presentation.model.WebServiceModel;
import org.addhen.smssync.presentation.model.mapper.WebServiceModelDataMapper;
import org.addhen.smssync.presentation.view.webservice.UpdateWebServiceKeywordsView;

import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class UpdateWebServiceKeywordsPresenter implements Presenter {

    private final UpdateWebServiceUsecase mUpdateWebServiceUsecase;

    private final WebServiceModelDataMapper mWebServiceModelDataMapper;

    private UpdateWebServiceKeywordsView mUpdateWebServiceKeywordsView;


    /**
     * Default use case.
     *
     * @param updateWebServiceUsecase   The update deployment use case
     * @param deploymentModelDataMapper The deployment model data mapper
     */
    @Inject
    public UpdateWebServiceKeywordsPresenter(
            @Named("webServiceUpdate") UpdateWebServiceUsecase updateWebServiceUsecase,
            WebServiceModelDataMapper deploymentModelDataMapper) {
        mUpdateWebServiceUsecase = updateWebServiceUsecase;
        mWebServiceModelDataMapper = deploymentModelDataMapper;
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
    }

    public void setView(@NonNull UpdateWebServiceKeywordsView addWebServiceKeywordsView) {
        mUpdateWebServiceKeywordsView = addWebServiceKeywordsView;
    }

    /**
     * Updates {@link WebServiceModel}
     *
     * @param deploymentModel The deployment model to be updated
     */
    public void updateWebService(WebServiceModel deploymentModel) {
        mUpdateWebServiceUsecase.setWebServiceEntity(
                mWebServiceModelDataMapper.map(deploymentModel));
        mUpdateWebServiceUsecase.execute(new DefaultSubscriber<Long>() {

            @Override
            public void onError(Throwable e) {
                showErrorMessage(new DefaultErrorHandler((Exception) e));
            }

            @Override
            public void onNext(Long row) {
                mUpdateWebServiceKeywordsView.onWebServiceSuccessfullyUpdated(row);
            }
        });
    }

    private void showErrorMessage(ErrorHandler errorHandler) {
        String errorMessage = ErrorMessageFactory
                .create(mUpdateWebServiceKeywordsView.getAppContext(),
                        errorHandler.getException());
        mUpdateWebServiceKeywordsView.showError(errorMessage);
    }
}
