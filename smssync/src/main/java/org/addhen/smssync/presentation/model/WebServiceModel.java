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
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class WebServiceModel extends Model implements Parcelable {

    private String mTitle;

    private String mUrl;

    private String mSecret;

    private String mSyncScheme;

    private Status mStatus;

    public WebServiceModel() {
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getSecret() {
        return mSecret;
    }

    public void setSecret(String secret) {
        mSecret = secret;
    }

    public SyncSchemeModel getSyncScheme() {
        return new SyncSchemeModel(mSyncScheme);
    }

    public void setSyncScheme(SyncSchemeModel syncScheme) {
        mSyncScheme = syncScheme.toJSONString();
    }

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {
        mStatus = status;
    }

    @Override
    public String toString() {
        return "SyncUrl{" +
                "id=" + _id +
                "title='" + mTitle + '\'' +
                ", url='" + mUrl + '\'' +
                ", secret='" + mSecret + '\'' +
                ", syncScheme=" + mSyncScheme +
                ", status=" + mStatus +
                '}';
    }

    public enum Status {
        ENABLED, DISABLED
    }

    protected WebServiceModel(Parcel in) {
        _id = in.readLong();
        mTitle = in.readString();
        mUrl = in.readString();
        mSecret = in.readString();
        mSyncScheme = in.readString();
        mStatus = (Status) in.readValue(Status.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(_id);
        dest.writeString(mTitle);
        dest.writeString(mUrl);
        dest.writeString(mSecret);
        dest.writeString(mSyncScheme);
        dest.writeValue(mStatus);
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