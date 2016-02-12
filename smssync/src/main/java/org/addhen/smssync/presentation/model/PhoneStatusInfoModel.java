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

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class PhoneStatusInfoModel extends Model {

    private String mPhoneNumber;

    private boolean mDataConnection;

    private int mBatteryLevel;

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        _id = id;
    }

    public int getBatteryLevel() {
        return mBatteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        mBatteryLevel = batteryLevel;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.mPhoneNumber = phoneNumber;
    }

    public boolean isDataConnection() {
        return mDataConnection;
    }

    public void setDataConnection(boolean dataConnection) {
        this.mDataConnection = dataConnection;
    }

    @Override
    public String toString() {
        return "PhoneStatusInfo{" +
                "mPhoneNumber='" + mPhoneNumber + '\'' +
                ", mDataConnection=" + mDataConnection +
                ", mBatteryLevel=" + mBatteryLevel +
                '}';
    }
}
