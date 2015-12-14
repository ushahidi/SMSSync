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

package org.addhen.smssync.domain.entity;

import com.addhen.android.raiburari.domain.entity.Entity;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class WebServiceEntity extends Entity {

    private String title;

    private String url;

    private String secret;

    private String syncScheme;

    private Status status;

    private KeywordStatus mKeywordStatus;

    private String keywords;

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        _id = id;
    }

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

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public SyncSchemeEntity getSyncScheme() {
        return new SyncSchemeEntity(syncScheme);
    }

    public void setSyncScheme(SyncSchemeEntity syncScheme) {
        this.syncScheme = syncScheme.toJSONString();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "WebServiceEntity{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", secret='" + secret + '\'' +
                ", syncScheme='" + syncScheme + '\'' +
                ", status=" + status +
                ", mKeywordStatus=" + mKeywordStatus +
                ", keywords='" + keywords + '\'' +
                '}';
    }

    public KeywordStatus getKeywordStatus() {
        return mKeywordStatus;
    }

    public void setKeywordStatus(
            KeywordStatus keywordStatus) {
        mKeywordStatus = keywordStatus;
    }

    public enum Status {
        ENABLED, DISABLED
    }

    public enum KeywordStatus {
        ENABLED, DISABLED
    }
}
