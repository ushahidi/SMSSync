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

package org.addhen.smssync.data.entity;

import com.addhen.android.raiburari.data.entity.DataEntity;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
// TODO: Rename this web service to match the new nomenclature for sync url
// TODO: Update database to match the name
public class SyncUrl extends DataEntity {

    private String title;

    private String url;

    private String secret;

    private String syncScheme;

    private Status status;

    private String keywords;

    private KeywordStatus keywordStatus;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public SyncScheme getSyncScheme() {
        return new SyncScheme(syncScheme);
    }

    public void setSyncScheme(SyncScheme syncScheme) {
        this.syncScheme = syncScheme.toJSONString();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public KeywordStatus getKeywordStatus() {
        return keywordStatus;
    }

    public void setKeywordStatus(
            KeywordStatus keywordStatus) {
        this.keywordStatus = keywordStatus;
    }

    @Override
    public String toString() {
        return "SyncUrl{" +
                "id" + _id +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", secret='" + secret + '\'' +
                ", syncScheme=" + syncScheme +
                ", keyword=" + keywords +
                ", keywordStats=" + keywordStatus +
                ", status=" + status +
                '}';
    }

    public enum Status {
        ENABLED, DISABLED
    }

    public enum KeywordStatus {
        ENABLED, DISABLED
    }
}
