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

package org.addhen.smssync.models;

import java.util.List;

import org.addhen.smssync.database.Database;

public class SyncUrlModel extends Model {

	private String title;

	private String keywords;

	private String url;

	private String secret;

	private int status;

	private int id;

	public List<SyncUrlModel> listSyncUrl;

	/**
	 * Set the title of the sync URL.
	 * 
	 * @param String
	 *            title - The title of the of the URL.
	 * @return void
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Get the title of the sync URL.
	 * 
	 * @return String
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Set the sync URL.
	 * 
	 * @param String
	 *            url The sync URL.
	 * @return void
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Get the sync URL.
	 * 
	 * @return String
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * Set the address of the SMS message.
	 * 
	 * @param String
	 *            keywords
	 * 
	 * @return void
	 */
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	/**
	 * Get the keywords for the sync URL
	 * 
	 * @return String
	 */
	public String getKeywords() {
		return this.keywords;
	}

	/**
	 * Set the secret key attached to the sync URL.
	 * 
	 * @param String
	 *            secret
	 * 
	 * 
	 * @return void
	 */
	public void setSecret(String secret) {
		this.secret = secret;
	}

	/**
	 * Get the secret key attached to the sync URL.
	 * 
	 * @return String
	 */
	public String getSecret() {
		return this.secret;
	}

	/**
	 * Set the status of a sync url. Whether active or inactive
	 * 
	 * @param int status
	 * 
	 * @return void
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * Get the message date
	 * 
	 * @return String
	 */
	public int getStatus() {
		return this.status;
	}

	/**
	 * Set the unique ID for the sync URL.
	 * 
	 * @param int id - The sync URL ID.
	 * @return void
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Get the unique ID for a particular sync URL.
	 * 
	 * @return int
	 */
	public int getId() {
		return this.id;
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
	 * Delete sync URL by id
	 * 
	 * @param int id The unique ID to use to delete the sync URL.
	 * @return boolean
	 */
	public boolean deleteSyncUrlById(int id) {
		return Database.mSyncUrlContentProvider.deleteSyncUrlById(id);
	}

	@Override
	public boolean load() {

		listSyncUrl = Database.mSyncUrlContentProvider.fetchSyncUrl();
		if (listSyncUrl != null) {
			return true;
		}

		return false;
	}

	public List<SyncUrlModel> loadById(int id) {
		return Database.mSyncUrlContentProvider.fetchSyncUrlById(id);
	}

	public List<SyncUrlModel> loadByStatus(int status) {
		return Database.mSyncUrlContentProvider.fetchSyncUrlByStatus(status);
	}

	@Override
	public boolean save() {
		if (listSyncUrl != null && listSyncUrl.size() > 0) {
			return Database.mSyncUrlContentProvider.addSyncUrl(listSyncUrl);
		}
		return false;
	}

	/**
	 * Update an existing sync URL
	 * 
	 * @param syncUrl
	 * 
	 * @return boolean
	 */
	public boolean update(SyncUrlModel syncUrl) {
		if (syncUrl != null) {
			return Database.mSyncUrlContentProvider.updateSyncUrl(syncUrl);
		}
		return false;
	}

	/**
	 * Update status of a sync URL
	 * 
	 * @param int stauts The 0 for inactive and 1 for active. This determine
	 *        whether the sync URL is active or not.
	 * @param int id The unique id of the sync URL to update its status.
	 * 
	 * @return
	 */
	public boolean updateStatus(SyncUrlModel syncUrl) {
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

}
