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

import org.addhen.smssync.models.SyncUrl;

import java.util.List;

public interface ISyncUrlContentProvider {

    public List<SyncUrl> fetchSyncUrl();

    public List<SyncUrl> fetchSyncUrlById(int id);

    public List<SyncUrl> fetchSyncUrlByStatus(int status);

    public boolean addSyncUrl(SyncUrl syncUrl);

    public boolean addSyncUrl(List<SyncUrl> syncUrl);

    public boolean deleteAllSyncUrl();

    public boolean deleteSyncUrlById(int id);

    public boolean updateSyncUrl(SyncUrl syncUrl);

    public boolean updateStatus(SyncUrl syncUrl);

    public int totalActiveSyncUrl();

}
