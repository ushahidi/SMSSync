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

package org.addhen.smssync.data;

import com.addhen.android.raiburari.data.pref.BooleanPreference;
import com.addhen.android.raiburari.data.pref.IntPreference;
import com.addhen.android.raiburari.data.pref.LongPreference;
import com.addhen.android.raiburari.data.pref.StringPreference;

import org.addhen.smssync.R;
import org.addhen.smssync.presentation.util.TimeFrequencyUtil;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;

/**
 * Since the base library doesn't allow the different Type SharedPreferences to be injectable, use
 * {@link PrefsFactory} to create new instances of them which are specific to this app's
 * Preferences
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class PrefsFactory {

    private SharedPreferences mSharedPreferences;

    private Context mContext;

    /**
     * Provide various Typed SharedPreferences
     *
     * @param sharedPreferences The sharedPreference
     * @param context           The calling context
     */
    @Inject
    public PrefsFactory(Context context, SharedPreferences sharedPreferences) {
        mContext = context;
        mSharedPreferences = sharedPreferences;
    }

    public StringPreference website() {
        return new StringPreference(mSharedPreferences, "WebsitePref", "");
    }

    public StringPreference apiKey() {
        return new StringPreference(mSharedPreferences, "ApiKey", "");
    }

    public StringPreference reply() {
        return new StringPreference(mSharedPreferences, "ReplyPref",
                mContext.getString(R.string.edittxt_reply_default));
    }

    public BooleanPreference serviceEnabled() {
        return new BooleanPreference(mSharedPreferences, "EnableSmsSync", false);
    }

    public BooleanPreference autoDelete() {
        return new BooleanPreference(mSharedPreferences, "EnableAutoDelete", false);
    }

    public BooleanPreference enableReply() {
        return new BooleanPreference(mSharedPreferences, "EnableReply", false);
    }

    public BooleanPreference enableReplyFrmServer() {
        return new BooleanPreference(mSharedPreferences, "EnableReplyFrmServer", false);
    }

    public BooleanPreference enableAutoSync() {
        return new BooleanPreference(mSharedPreferences, "AutoSync", false);
    }

    public BooleanPreference useSmsPortals() {
        return new BooleanPreference(mSharedPreferences, "UseSmsPortals", false);
    }

    public BooleanPreference enableTaskCheck() {
        return new BooleanPreference(mSharedPreferences, "EnableTaskCheck", false);
    }

    public StringPreference autoTime() {
        return new StringPreference(mSharedPreferences, "AutoTime",
                TimeFrequencyUtil.DEFAULT_TIME_FREQUENCY);
    }

    public BooleanPreference enableRetry() {
        return new BooleanPreference(mSharedPreferences, "EnableRetry", false);
    }

    public IntPreference retries() {
        return new IntPreference(mSharedPreferences, "Retries", 3);
    }

    public StringPreference uniqueId() {
        return new StringPreference(mSharedPreferences, "UniqueId", "");
    }

    public StringPreference uniqueName() {
        return new StringPreference(mSharedPreferences, "UniqueName", "");
    }

    public StringPreference taskCheckTime() {
        return new StringPreference(mSharedPreferences, "taskCheck",
                TimeFrequencyUtil.DEFAULT_TIME_FREQUENCY);
    }

    public LongPreference lastSyncDate() {
        return new LongPreference(mSharedPreferences, "LastSyncDate", 0l);
    }

    public BooleanPreference enableBlacklist() {
        return new BooleanPreference(mSharedPreferences, "EnableBlacklist", false);
    }

    public BooleanPreference enableWhitelist() {
        return new BooleanPreference(mSharedPreferences, "EnableWhitelist", false);
    }

    public BooleanPreference enableLog() {
        return new BooleanPreference(mSharedPreferences, "EnableLog", false);
    }

    public IntPreference batteryLevel() {
        return new IntPreference(mSharedPreferences, "BatteryLevel", 0);
    }

    public StringPreference alertPhoneNumber() {
        return new StringPreference(mSharedPreferences, "AlertPhoneNumber", "");
    }

    public BooleanPreference smsReportDelivery() {
        return new BooleanPreference(mSharedPreferences, "SmsReportDelivery", false);
    }

    public BooleanPreference messageResultsAPIEnable() {
        return new BooleanPreference(mSharedPreferences, "MessageResultsAPIEnable", false);
    }

    public BooleanPreference isFirstTimeLaunched() {
        return new BooleanPreference(mSharedPreferences, "AppFirstLaunched", true);
    }

    public StringPreference twitterKeywords() {
        return new StringPreference(mSharedPreferences, "TwitterKeyword", "");
    }

    public BooleanPreference enableTwitterKeywords() {
        return new BooleanPreference(mSharedPreferences, "EnableTwitterKeyword", false);
    }
}
