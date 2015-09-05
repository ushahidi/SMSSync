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

package org.addhen.smssync.smslib.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Message model
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class SmsMessage implements Parcelable {

    // Use this to make message unique
    public long id;

    public String phone;

    public long timestamp;

    public String body;

    public String uuid;

    public SmsMessage() {

    }

    protected SmsMessage(Parcel in) {
        id = in.readLong();
        phone = in.readString();
        timestamp = in.readLong();
        body = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(phone);
        dest.writeLong(timestamp);
        dest.writeString(body);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SmsMessage> CREATOR
            = new Parcelable.Creator<SmsMessage>() {
        @Override
        public SmsMessage createFromParcel(Parcel in) {
            return new SmsMessage(in);
        }

        @Override
        public SmsMessage[] newArray(int size) {
            return new SmsMessage[size];
        }
    };
}
