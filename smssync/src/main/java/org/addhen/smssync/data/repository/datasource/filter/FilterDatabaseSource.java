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

package org.addhen.smssync.data.repository.datasource.filter;

import org.addhen.smssync.data.database.FilterDatabaseHelper;
import org.addhen.smssync.data.entity.Filter;

import android.support.annotation.NonNull;

import java.util.List;

import rx.Observable;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class FilterDatabaseSource implements FilterDataSource {

    final FilterDatabaseHelper mFilterDatabaseHelper;

    public FilterDatabaseSource(@NonNull FilterDatabaseHelper filterDatabaseHelper) {
        mFilterDatabaseHelper = filterDatabaseHelper;
    }

    @Override
    public Observable<Integer> deleteAllWhiteList() {
        return mFilterDatabaseHelper.deleteAllWhiteList();
    }

    @Override
    public Observable<Integer> deleteAllBlackList() {
        return mFilterDatabaseHelper.deleteAllBlackList();
    }

    @Override
    public Observable<List<Filter>> fetchByStatus(Filter.Status status) {
        return mFilterDatabaseHelper.fetchByStatus(status);
    }

    @Override
    public Observable<List<Filter>> getEntities() {
        return mFilterDatabaseHelper.getFilterList();
    }

    @Override
    public Observable<Filter> getEntity(Long id) {
        return mFilterDatabaseHelper.getFilter(id);
    }

    @Override
    public Observable<Long> addEntity(Filter filter) {
        return mFilterDatabaseHelper.put(filter);
    }

    @Override
    public Observable<Long> updateEntity(Filter filterEntity) {
        return mFilterDatabaseHelper.put(filterEntity);
    }

    @Override
    public Observable<Long> deleteEntity(Long id) {
        return mFilterDatabaseHelper.deleteById(id);
    }

    @Override
    public List<Filter> getFilters() {
        return mFilterDatabaseHelper.getFilters();
    }
}
