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

package org.addhen.smssync.presentation.model;

import com.addhen.android.raiburari.presentation.model.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * WebService Model
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class WebServiceModel extends Model implements Parcelable {

    private String title;

    private String url;

    private String secret;

    private String syncScheme;

    private Status status;

    private KeywordStatus keywordStatus;

    private String keywords;

    public WebServiceModel() {
    }

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

    public SyncSchemeModel getSyncScheme() {
        return new SyncSchemeModel(syncScheme);
    }

    public void setSyncScheme(SyncSchemeModel syncScheme) {
        this.syncScheme = syncScheme.toJSONString();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getKeywords() {
        return keywords;
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
                "id=" + _id +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", secret='" + secret + '\'' +
                ", syncScheme=" + syncScheme +
                ", keywords=" + keywords +
                ", keywordStatus=" + keywordStatus +
                ", status=" + status +
                '}';
    }

    public enum Status {
        ENABLED, DISABLED
    }

    public enum KeywordStatus {
        ENABLED, DISABLED
    }

    protected WebServiceModel(Parcel in) {
        _id = in.readByte() == 0x00 ? null : in.readLong();
        title = in.readString();
        url = in.readString();
        secret = in.readString();
        syncScheme = in.readString();
        status = (Status) in.readValue(Status.class.getClassLoader());
        keywords = in.readString();
        keywordStatus = (KeywordStatus) in.readValue(KeywordStatus.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (_id == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(_id);
        }
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(secret);
        dest.writeString(syncScheme);
        dest.writeValue(status);
        dest.writeString(keywords);
        dest.writeValue(keywordStatus);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<WebServiceModel> CREATOR
            = new Parcelable.Creator<WebServiceModel>() {
        @Override
        public WebServiceModel createFromParcel(Parcel in) {
            return new WebServiceModel(in);
        }

        @Override
        public WebServiceModel[] newArray(int size) {
            return new WebServiceModel[size];
        }
    };
}