/*******************************************************************************
 *  Copyright (c) 2010 - 2013 Ushahidi Inc
 *  All rights reserved
 *  Contact: team@ushahidi.com
 *  Website: http://www.ushahidi.com
 *  GNU Lesser General Public License Usage
 *  This file may be used under the terms of the GNU Lesser
 *  General Public License version 3 as published by the Free Software
 *  Foundation and appearing in the file LICENSE.LGPL included in the
 *  packaging of this file. Please review the following information to
 *  ensure the GNU Lesser General Public License version 3 requirements
 *  will be met: http://www.gnu.org/licenses/lgpl.html.
 *
 * If you have questions regarding the use of this file, please contact
 * Ushahidi developers at team@ushahidi.com.
 ******************************************************************************/

package org.addhen.smssync.database;

import org.addhen.smssync.models.Filter;

import java.util.List;

public interface IFilterContentProvider {

    public List<Filter> fetchAll();

    public List<Filter> fetchById(int id);

    public List<Filter> fetchByStatus(int status);

    public boolean add(Filter syncUrl);

    public boolean add(List<Filter> filterLists);

    public boolean deleteAll();

    public boolean deleteById(int id);

    public boolean update(Filter filter);

    public int total();

}
