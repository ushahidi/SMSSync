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

package org.addhen.smssync.presentation.di.module;

import com.addhen.android.raiburari.presentation.di.qualifier.ActivityScope;

import org.addhen.smssync.domain.usecase.webservice.AddWebServiceUsecase;
import org.addhen.smssync.domain.usecase.webservice.DeleteWebServiceUsecase;
import org.addhen.smssync.domain.usecase.webservice.ListWebServiceUsecase;
import org.addhen.smssync.domain.usecase.webservice.TestWebServiceUsecase;
import org.addhen.smssync.domain.usecase.webservice.UpdateWebServiceUsecase;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
@Module
public class WebServiceModule {

    /**
     * Provides {@link AddWebServiceUsecase} object with annotated name "webServiceAdd"
     *
     * @param addWebServiceUsecase The add webService use case
     * @return The add webService use case
     */
    @Provides
    @ActivityScope
    @Named("webServiceAdd")
    AddWebServiceUsecase provideAddWebServiceUseCase(AddWebServiceUsecase addWebServiceUsecase) {
        return addWebServiceUsecase;
    }

    /**
     * Provides {@link DeleteWebServiceUsecase} object with the annotated name "webServiceDelete"
     *
     * @param listWebServiceUsecase The delete webService use case
     * @return The delete webService use case
     */
    @Provides
    @ActivityScope
    @Named("webServiceDelete")
    DeleteWebServiceUsecase provideDeleteWebServiceUseCase(
            DeleteWebServiceUsecase listWebServiceUsecase) {
        return listWebServiceUsecase;
    }

    /**
     * Provides {@link ListWebServiceUsecase} object
     *
     * @param listWebServiceUsecase The list webService use case
     * @return The list webService use case
     */
    @Provides
    @ActivityScope
    @Named("webServiceList")
    ListWebServiceUsecase provideListWebServiceUseCase(
            ListWebServiceUsecase listWebServiceUsecase) {
        return listWebServiceUsecase;
    }

    /**
     * Provides {@link UpdateWebServiceUsecase} object
     *
     * @param updateWebServiceUsecase The update webService use case
     * @return The update webService use case
     */
    @Provides
    @ActivityScope
    @Named("webServiceUpdate")
    UpdateWebServiceUsecase provideUpdateWebServiceUseCase(
            UpdateWebServiceUsecase updateWebServiceUsecase) {
        return updateWebServiceUsecase;
    }

    @Provides
    @ActivityScope
    @Named("testWebService")
    TestWebServiceUsecase providTestWebServiceUsecase(TestWebServiceUsecase testWebServiceUsecase) {
        return testWebServiceUsecase;
    }

}
