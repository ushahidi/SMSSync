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

package org.addhen.smssync.database;

import org.addhen.smssync.models.Filter;

import java.util.List;

public interface FilterDatabase {

    void fetchAll(BaseDatabseHelper.DatabaseCallback<List<Filter>> callback);

    void fetchById(Long id, BaseDatabseHelper.DatabaseCallback<Filter> callback);

    void fetchByStatus(Filter.Status status, BaseDatabseHelper.DatabaseCallback<List<Filter>> callback);

    void put(Filter filter, BaseDatabseHelper.DatabaseCallback<Void> callback);

    void put(List<Filter> filterLists, BaseDatabseHelper.DatabaseCallback<Void> callback);

    void deleteAllBlackList(BaseDatabseHelper.DatabaseCallback<Void> callback);

    void deleteAllWhiteList(BaseDatabseHelper.DatabaseCallback<Void> callback);

    void deleteById(Long id, BaseDatabseHelper.DatabaseCallback<Void> callback);

    void total(BaseDatabseHelper.DatabaseCallback<Integer> callback);

}
