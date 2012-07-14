/*****************************************************************************
 ** Copyright (c) 2010 - 2012 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 **
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.
 **
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 **
 *****************************************************************************/

package org.addhen.smssync.database;

import java.util.List;

import org.addhen.smssync.models.SyncUrlModel;

public interface ISyncUrlContentProvider {

	public List<SyncUrlModel> fetchSyncUrl();

	public List<SyncUrlModel> fetchSyncUrlById(int id);

	public List<SyncUrlModel> fetchSyncUrlByStatus(int status);

	public boolean addSyncUrl(SyncUrlModel syncUrl);

	public boolean addSyncUrl(List<SyncUrlModel> syncUrl);

	public boolean deleteAllSyncUrl();

	public boolean deleteSyncUrlById(int id);

	public boolean updateSyncUrl(SyncUrlModel syncUrl);

	public boolean updateStatus(SyncUrlModel syncUrl);

}
