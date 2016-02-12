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

import org.addhen.smssync.domain.entity.FilterEntity;
import org.addhen.smssync.domain.repository.FilterRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
@Singleton
public class InternalFilterDataRepository implements FilterRepository {

    @Inject
    public InternalFilterDataRepository() {

    }

    @Override
    public Observable<Integer> deleteAllWhiteList() {
        return Observable.defer(() -> {
            return Observable.just(1);
        });
    }

    @Override
    public Observable<Integer> deleteAllBlackList() {
        return Observable.defer(() -> {
            return Observable.just(3);
        });
    }

    @Override
    public Observable<List<FilterEntity>> fetchByStatus(FilterEntity.Status status) {
        if (status.equals(FilterEntity.Status.BLACKLIST)) {
            return Observable.defer(() -> {
                List<FilterEntity> filterEntityList = new ArrayList<>();
                filterEntityList.add(getFilterEntityOne());
                return Observable.just(filterEntityList);
            });
        }

        return Observable.defer(() -> {
            List<FilterEntity> filterEntityList = new ArrayList<>();
            filterEntityList.add(getFilterEntityTwo());
            return Observable.just(filterEntityList);
        });
    }

    @Override
    public Observable<List<FilterEntity>> getEntities() {
        return Observable.defer(() -> {
            return Observable.just(getFilterEntities());
        });
    }

    @Override
    public Observable<FilterEntity> getEntity(Long entityId) {
        return Observable.defer(() -> {
            return Observable.just(getFilterEntityOne());
        });
    }

    @Override
    public Observable<Long> addEntity(FilterEntity entity) {
        return Observable.defer(() -> {
            return Observable.just(1l);
        });
    }

    @Override
    public Observable<Long> updateEntity(FilterEntity entity) {
        return Observable.defer(() -> {
            return Observable.just(1l);
        });
    }

    @Override
    public Observable<Long> deleteEntity(Long id) {
        return Observable.defer(() -> {
            return Observable.just(1l);
        });
    }

    private List<FilterEntity> getFilterEntities() {
        List<FilterEntity> filterEntities = new ArrayList<>();
        filterEntities.add(getFilterEntityOne());
        filterEntities.add(getFilterEntityTwo());
        filterEntities.add(getFilterEntityThree());
        return filterEntities;
    }

    private FilterEntity getFilterEntityOne() {
        FilterEntity filterEntity = new FilterEntity();
        filterEntity.setId(1l);
        filterEntity.setPhoneNumber("090909392");
        filterEntity.setStatus(FilterEntity.Status.BLACKLIST);
        return filterEntity;
    }

    private FilterEntity getFilterEntityTwo() {
        FilterEntity filterEntity = new FilterEntity();
        filterEntity.setId(2l);
        filterEntity.setPhoneNumber("0909145895");
        filterEntity.setStatus(FilterEntity.Status.WHITELIST);
        return filterEntity;
    }

    private FilterEntity getFilterEntityThree() {
        FilterEntity filterEntity = new FilterEntity();
        filterEntity._id = 3l;
        filterEntity.setPhoneNumber("0904632489");
        filterEntity.setStatus(FilterEntity.Status.WHITELIST);
        return filterEntity;
    }
}

