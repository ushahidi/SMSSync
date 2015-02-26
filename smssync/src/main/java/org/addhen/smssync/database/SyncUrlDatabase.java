package org.addhen.smssync.database;

import org.addhen.smssync.database.BaseDatabseHelper.DatabaseCallback;
import org.addhen.smssync.models.SyncUrl;

import static org.addhen.smssync.models.SyncUrl.Status;

import java.util.List;

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
