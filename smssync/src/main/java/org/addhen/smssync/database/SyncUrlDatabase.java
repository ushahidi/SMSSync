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

import org.addhen.smssync.database.BaseDatabseHelper.DatabaseCallback;
import org.addhen.smssync.models.SyncUrl;

import java.util.List;

import static org.addhen.smssync.models.SyncUrl.Status;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public interface SyncUrlDatabase {

    public void fetchSyncUrl(DatabaseCallback<List<SyncUrl>> callback);

    public void fetchSyncUrlById(Long id, DatabaseCallback<SyncUrl> callback);

    public void fetchSyncUrlByStatus(Status status, DatabaseCallback<List<SyncUrl>> callback);

    public void put(SyncUrl syncUrl, DatabaseCallback<Void> callback);

    public void put(List<SyncUrl> syncUrls, DatabaseCallback<Void> callback);

    public void deleteAllSyncUrl(DatabaseCallback<Void> callback);

    public void deleteSyncUrlById(Long id, DatabaseCallback<Void> callback);

    public void totalActiveSyncUrl(DatabaseCallback<Integer> callback);
}
