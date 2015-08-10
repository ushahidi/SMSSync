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
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class AddWebServiceUsecase extends Usecase {

    private final WebServiceRepository mWebServiceRepository;

    private WebServiceEntity mWebServiceEntity;

    /**
     * Default constructor
     *
     * @param webServiceRepository The webservice repository
     * @param threadExecutor       The thread executor
     * @param postExecutionThread  The post execution thread
     */
    @Inject
    protected AddWebServiceUsecase(WebServiceRepository webServiceRepository,
                                   ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        mWebServiceRepository = webServiceRepository;
    }

    /**
     * Sets the webservice
     *
     * @param webServiceEntity The webservice to be added
     */
    public void setWebServiceEntity(WebServiceEntity webServiceEntity) {
        mWebServiceEntity = webServiceEntity;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        if (mWebServiceEntity == null) {
            throw new RuntimeException("WebServiceEntity is null. You must call setWebServiceEntity(...)");
        }
        return mWebServiceRepository.addEntity(mWebServiceEntity);
    }
}
