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

import org.addhen.smssync.domain.entity.FilterEntity;
import org.addhen.smssync.domain.usecase.filter.ListFilterUsecase;
import org.addhen.smssync.presentation.exception.ErrorMessageFactory;
import org.addhen.smssync.presentation.model.FilterModel;
import org.addhen.smssync.presentation.model.mapper.FilterModelDataMapper;
import org.addhen.smssync.presentation.view.filters.ListFilterView;

import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
@ActivityScope
public class ListFilterPresenter implements Presenter {

    private final ListFilterUsecase mListFiltersUsecase;

    private final FilterModelDataMapper mFilterModelDataMapper;

    private ListFilterView mListFilterView;

    @Inject
    public ListFilterPresenter(@Named("filterList") ListFilterUsecase listUsecase,
            FilterModelDataMapper filterModelDataMapper) {
        mListFiltersUsecase = listUsecase;
        mFilterModelDataMapper = filterModelDataMapper;
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
        // Do nothing
    }

    @Override
    public void destroy() {
        mListFiltersUsecase.unsubscribe();
    }

    public void setView(@NonNull ListFilterView listFilterView) {
        mListFilterView = listFilterView;
    }

    public void loadFilters(FilterModel.Status status) {
        mListFilterView.hideRetry();
        mListFilterView.showLoading();
        mListFiltersUsecase.setStatus(mFilterModelDataMapper.map(status));
        mListFiltersUsecase.execute(new DefaultSubscriber<List<FilterEntity>>() {
            @Override
            public void onCompleted() {
                mListFilterView.hideLoading();
            }

            @Override
            public void onNext(List<FilterEntity> filterList) {
                mListFilterView.hideLoading();
                mListFilterView.showFilters(mFilterModelDataMapper.map(filterList));
            }

            @Override
            public void onError(Throwable e) {
                mListFilterView.hideLoading();
                showErrorMessage(new DefaultErrorHandler((Exception) e));
                mListFilterView.showRetry();
            }
        });
    }

    private void showErrorMessage(ErrorHandler errorHandler) {
        String errorMessage = ErrorMessageFactory.create(mListFilterView.getAppContext(),
                errorHandler.getException());
        mListFilterView.showError(errorMessage);
    }
}
