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

import java.util.List;

import rx.Observable;

/**
 * Implementation of {@link FilterRepository} for manipulating deployment data
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class FilterDataRepository implements FilterRepository {

    @Override
    public Observable<Boolean> deleteAllWhiteList() {
        return null;
    }

    @Override
    public Observable<Boolean> deleteAllBlackList() {
        return null;
    }

    @Override
    public Observable<Boolean> deleteById() {
        return null;
    }

    @Override
    public Observable<List<FilterEntity>> fetchByStatus() {
        return null;
    }

    @Override
    public Observable<List<FilterEntity>> getEntities() {
        return null;
    }

    @Override
    public Observable<FilterEntity> getEntity(Long aLong) {
        return null;
    }

    @Override
    public Observable<Long> addEntity(FilterEntity filterEntity) {
        return null;
    }

    @Override
    public Observable<Long> updateEntity(FilterEntity filterEntity) {
        return null;
    }

    @Override
    public Observable<Long> deleteEntity(Long aLong) {
        return null;
    }
}
