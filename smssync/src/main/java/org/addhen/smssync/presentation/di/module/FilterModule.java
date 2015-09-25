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

import com.addhen.android.raiburari.domain.usecase.Usecase;
import com.addhen.android.raiburari.presentation.di.qualifier.ActivityScope;

import org.addhen.smssync.domain.usecase.filter.AddFilterUsecase;
import org.addhen.smssync.domain.usecase.filter.DeleteFilterUsecase;
import org.addhen.smssync.domain.usecase.filter.ListFilterUsecase;
import org.addhen.smssync.domain.usecase.webservice.GetActiveWebServiceUsecase;
import org.addhen.smssync.domain.usecase.webservice.UpdateWebServiceUsecase;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
@Module
public class FilterModule {

    @Provides
    @ActivityScope
    @Named("filterList")
    Usecase provideListFilterUseCase(ListFilterUsecase listFilterUsecase) {
        return listFilterUsecase;
    }

    @Provides
    @ActivityScope
    @Named("filterAdd")
    AddFilterUsecase provideAddFilterUseCase(AddFilterUsecase addFilterUsecase) {
        return addFilterUsecase;
    }

    @Provides
    @ActivityScope
    @Named("getActiveWebService")
    Usecase provideGetActiveWebServiceUseCase(
            GetActiveWebServiceUsecase getActiveWebServiceUsecase) {
        return getActiveWebServiceUsecase;
    }

    @Provides
    @ActivityScope
    @Named("filterDelete")
    DeleteFilterUsecase provideDeleteFilterUsecase(DeleteFilterUsecase deleteFilterUsecase) {
        return deleteFilterUsecase;
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

}
