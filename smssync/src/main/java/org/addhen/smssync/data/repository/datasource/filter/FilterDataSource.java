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

import org.addhen.smssync.data.entity.Filter;

import java.util.List;

import rx.Observable;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public interface FilterDataSource {

    Observable<Integer> deleteAllWhiteList();

    Observable<Integer> deleteAllBlackList();

    Observable<List<Filter>> fetchByStatus(Filter.Status status);

    Observable<List<Filter>> getEntities();

    Observable<Filter> getEntity(Long aLong);

    Observable<Long> addEntity(Filter filterEntity);

    Observable<Long> updateEntity(Filter filterEntity);

    Observable<Long> deleteEntity(Long id);

    List<Filter> getFilters();
}
