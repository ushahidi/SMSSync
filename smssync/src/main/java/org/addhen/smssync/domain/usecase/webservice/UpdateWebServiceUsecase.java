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

package org.addhen.smssync.domain.usecase.webservice;

import com.addhen.android.raiburari.domain.executor.PostExecutionThread;
import com.addhen.android.raiburari.domain.executor.ThreadExecutor;
import com.addhen.android.raiburari.domain.usecase.Usecase;

import org.addhen.smssync.domain.entity.WebServiceEntity;
import org.addhen.smssync.domain.repository.WebServiceRepository;

import javax.inject.Inject;

import rx.Observable;

/**
 * Update use case for {@link WebServiceEntity}
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class UpdateWebServiceUsecase extends Usecase {

    private final WebServiceRepository mWebServiceRepository;

    private WebServiceEntity mWebService;

    /**
     * Default constructor
     *
     * @param webServiceEntityRepository The webServiceEntity repository
     * @param threadExecutor             The thread executor
     * @param postExecutionThread        The post execution thread
     */
    @Inject
    protected UpdateWebServiceUsecase(WebServiceRepository webServiceEntityRepository,
                                      ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        mWebServiceRepository = webServiceEntityRepository;
    }

    /**
     * Sets the webServiceEntity
     *
     * @param webServiceEntity The webServiceEntity to be updated
     */
    public void setWebServiceEntity(WebServiceEntity webServiceEntity) {
        mWebService = webServiceEntity;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        if (mWebService == null) {
            throw new RuntimeException("WebService is null. You must call setWebService(...)");
        }
        return mWebServiceRepository.updateEntity(mWebService);
    }
}
