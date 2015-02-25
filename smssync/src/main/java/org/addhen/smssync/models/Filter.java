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

package org.addhen.smssync.models;

import org.addhen.smssync.database.Database;
import org.addhen.smssync.util.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Filter.
 */
public class Filter extends Model {

    private String phoneNumber;

    private Status status;

    private int id;

    private List<Filter> mFilterList;

    public Filter() {
        this.mFilterList = new ArrayList<Filter>();
    }

    /**
     * Delete all pending messages.
     *
     * @return boolean
     */
    public boolean deleteAll() {
        return Database.filterContentProvider.deleteAll();
    }

    /**
     * Delete sync url by ID
     *
     * @param id The ID to delete by
     * @return boolean
     */
    public boolean deleteById(int id) {
        return Database.filterContentProvider.deleteById(id);
    }

    @Override
    public boolean load() {
        return false;
    }

    public boolean loadById(int id) {
        Logger.log("Filter", " ID: " + id);
        mFilterList = Database.filterContentProvider.fetchById(id);
        return mFilterList != null;
    }

    public boolean loadByStatus(Status status) {
        mFilterList = Database.filterContentProvider.fetchByStatus(status.code);
        return mFilterList != null;
    }

    public boolean save(List<Filter> filters) {
        return filters != null && filters.size() > 0 && Database.filterContentProvider
                .add(filters);
    }

    /**
     * Add a filter
     *
     * @return boolean
     */
    @Override
    public boolean save() {
        return Database.filterContentProvider.add(this);
    }

    /**
     * Update an existing filter
     *
     * @return boolean
     */
    public boolean update() {
        return Database.filterContentProvider.update(this);
    }

    /**
     * The total number of active or enabled Sync URLs.
     *
     * @return int The total number of Sync URLs that have been enabled.
     */
    public int total() {
        return Database.filterContentProvider.total();
    }

    public List<Filter> getFilterList() {
        return mFilterList;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
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
        return "Filter {" +
                "id:" + id +
                ", phone_number:" + phoneNumber +
                ", status:" + status +
                "}";
    }

    /**
     * The status of the filtered phone number.
     */
    public enum Status {

        WHITELIST(0),
        BLACKLIST(1);

        public final int code;

        Status(int code) {
            this.code = code;
        }

    }
}
