package org.addhen.smssync.models;

import org.addhen.smssync.database.Database;
import org.addhen.smssync.util.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Sync URl.
 */
public class SyncUrl extends Model {

    private String title;

    private String keywords;

    private String url;

    private String secret;

    private int status;

    private int id;

    private List<SyncUrl> mSyncUrlList;

    public SyncUrl() {
        this.mSyncUrlList = new ArrayList<SyncUrl>();
    }

    /**
     * Delete all pending messages.
     *
     * @return boolean
     */
    public boolean deleteAllSyncUrl() {
        return Database.mSyncUrlContentProvider.deleteAllSyncUrl();
    }

    /**
     * Delete sync url by ID
     *
     * @param id The ID to delete by
     * @return boolean
     */
    public boolean deleteSyncUrlById(int id) {
        return Database.mSyncUrlContentProvider.deleteSyncUrlById(id);
    }

    @Override
    public boolean load() {

        mSyncUrlList = Database.mSyncUrlContentProvider.fetchSyncUrl();
        return mSyncUrlList != null;

    }

    public List<SyncUrl> loadById(int id) {
        return Database.mSyncUrlContentProvider.fetchSyncUrlById(id);
    }

    public List<SyncUrl> loadByStatus(int status) {
        return Database.mSyncUrlContentProvider.fetchSyncUrlByStatus(status);
    }

    public boolean saveSyncUrls(List<SyncUrl> syncUrls) {
        return syncUrls != null && syncUrls.size() > 0 && Database.mSyncUrlContentProvider
                .addSyncUrl(syncUrls);
    }

    /**
     * Add sync url
     *
     * @return boolean
     */
    @Override
    public boolean save() {
        return Database.mSyncUrlContentProvider.addSyncUrl(this);
    }

    /**
     * Update an existing sync URL
     *
     * @return boolean
     */
    public boolean update() {
        return Database.mSyncUrlContentProvider.updateSyncUrl(this);
    }

    /**
     * Update status of a sync URL
     *
     * @param syncUrl The sync url to update
     * @return boolean
     */
    public boolean updateStatus(SyncUrl syncUrl) {
        return Database.mSyncUrlContentProvider.updateStatus(syncUrl);
    }

    /**
     * The total number of active or enabled Sync URLs.
     *
     * @return int The total number of Sync URLs that have been enabled.
     */
    public int totalActiveSynUrl() {
        return Database.mSyncUrlContentProvider.totalActiveSyncUrl();
    }

    public List<SyncUrl> getSyncUrlList() {
        return mSyncUrlList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;

    }

    @Override
    public String toString() {
        return "SynclUrl{" +
                "id:" + id +
                ", title:" + title +
                ", keywords:" + keywords +
                ", secret:" + secret +
                ", status:" + status +
                ", url:" + url +
                "}";
    }
}
