package org.addhen.smssync.database;

import java.util.List;

import org.addhen.smssync.models.SyncUrlModel;

public interface ISyncUrlContentProvider {

	public List<SyncUrlModel> fetchSyncUrl();

	public List<SyncUrlModel> fetchSyncUrlById(int id);
	
	public boolean addSyncUrl(SyncUrlModel syncUrl);

	public boolean addSyncUrl(List<SyncUrlModel> syncUrl);

	public boolean deleteAllSyncUrl();
	
	public boolean deleteSyncUrlById(int id);
	
	public boolean updateSyncUrl(SyncUrlModel syncUrl);

}
