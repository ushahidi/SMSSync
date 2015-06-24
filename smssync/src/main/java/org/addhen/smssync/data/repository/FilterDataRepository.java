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

package org.addhen.smssync.data.repository;

import org.addhen.smssync.data.entity.mapper.FilterDataMapper;
import org.addhen.smssync.data.repository.datasource.filter.FilterDataSource;
import org.addhen.smssync.data.repository.datasource.filter.FilterDataSourceFactory;
import org.addhen.smssync.domain.entity.FilterEntity;
import org.addhen.smssync.domain.repository.FilterRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Implementation of {@link FilterRepository} for manipulating deployment data
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
@Singleton
public class FilterDataRepository implements FilterRepository {

    private final FilterDataMapper mFilterEntityDataMapper;

    private final FilterDataSourceFactory mFilterDataSourceFactory;

    private FilterDataSource mFilterDataSource;

    @Inject
    public FilterDataRepository(FilterDataMapper filterEntityDataMapper,
            FilterDataSourceFactory filterDataSourceFactory) {
        mFilterEntityDataMapper = filterEntityDataMapper;
        mFilterDataSourceFactory = filterDataSourceFactory;
    }

    @Override
    public Observable<Integer> deleteAllWhiteList() {
        mFilterDataSource = mFilterDataSourceFactory.createFilterDataSource();
        return mFilterDataSource.deleteAllWhiteList();
    }

    @Override
    public Observable<Integer> deleteAllBlackList() {
        mFilterDataSource = mFilterDataSourceFactory.createFilterDataSource();
        return mFilterDataSource.deleteAllBlackList();
    }

    @Override
    public Observable<List<FilterEntity>> fetchByStatus(FilterEntity.Status status) {
        mFilterDataSource = mFilterDataSourceFactory.createFilterDataSource();
        return mFilterDataSource.fetchByStatus(mFilterEntityDataMapper.map(status))
                .map((filters -> mFilterEntityDataMapper.map(filters)));
    }

    @Override
    public Observable<List<FilterEntity>> getEntities() {
        mFilterDataSource = mFilterDataSourceFactory.createFilterDataSource();
        return mFilterDataSource.getEntities()
                .map((filters -> mFilterEntityDataMapper.map(filters)));
    }

    @Override
    public Observable<FilterEntity> getEntity(Long id) {
        mFilterDataSource = mFilterDataSourceFactory.createFilterDataSource();
        return mFilterDataSource.getEntity(id).map((filter -> mFilterEntityDataMapper.map(filter)));
    }

    @Override
    public Observable<Long> addEntity(FilterEntity filterEntity) {
        mFilterDataSource = mFilterDataSourceFactory.createFilterDataSource();
        return mFilterDataSource.addEntity(mFilterEntityDataMapper.map(filterEntity));
    }

    @Override
    public Observable<Long> updateEntity(FilterEntity filterEntity) {
        mFilterDataSource = mFilterDataSourceFactory.createFilterDataSource();
        return mFilterDataSource.updateEntity(mFilterEntityDataMapper.map(filterEntity));
    }

    @Override
    public Observable<Long> deleteEntity(Long id) {
        mFilterDataSource = mFilterDataSourceFactory.createFilterDataSource();
        return mFilterDataSource.deleteEntity(id);
    }
}
