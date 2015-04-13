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

package org.addhen.smssync.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import org.addhen.smssync.R;
import org.addhen.smssync.util.TimeFrequencyUtil;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class Prefs {

    private static final String PREF_NAME = "SMS_SYNC_PREF";

    private SharedPreferences sharedPreferences;

    private Context context;

    public Prefs(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(
                PREF_NAME, 0);
        this.context = context;
    }

    public StringPreference website() {
        return new StringPreference(sharedPreferences, "WebsitePref", "");
    }

    public StringPreference apiKey() {
        return new StringPreference(sharedPreferences, "ApiKey", "");
    }

    public StringPreference reply() {
        return new StringPreference(sharedPreferences, "ReplyPref",
                context.getString(R.string.edittxt_reply_default));
    }

    public BooleanPreference serviceEnabled() {
        return new BooleanPreference(sharedPreferences, "EnableSmsSync", false);
    }

    public BooleanPreference autoDelete() {
        return new BooleanPreference(sharedPreferences, "EnableAutoDelete", false);
    }

    public BooleanPreference enableReply() {
        return new BooleanPreference(sharedPreferences, "EnableReply", false);
    }

    public BooleanPreference enableReplyFrmServer() {
        return new BooleanPreference(sharedPreferences, "EnableReplyFrmServer", false);
    }

    public BooleanPreference enableAutoSync() {
        return new BooleanPreference(sharedPreferences, "AutoSync", false);
    }

    public BooleanPreference useSmsPortals() {
        return new BooleanPreference(sharedPreferences, "UseSmsPortals", false);
    }

    public BooleanPreference enableTaskCheck() {
        return new BooleanPreference(sharedPreferences, "EnableTaskCheck", false);
    }

    public StringPreference autoTime() {
        return new StringPreference(sharedPreferences, "AutoTime",
                TimeFrequencyUtil.DEFAULT_TIME_FREQUENCY);
    }

    public BooleanPreference enableRetry() {
        return new BooleanPreference(sharedPreferences, "EnableRetry",
                false);
    }

    public IntPreference retries() {
        return new IntPreference(sharedPreferences, "Retries", 3);
    }

    public StringPreference uniqueId() {
        return new StringPreference(sharedPreferences, "UniqueId", "");
    }

    public StringPreference taskCheckTime() {
        return new StringPreference(sharedPreferences, "taskCheck",
                TimeFrequencyUtil.DEFAULT_TIME_FREQUENCY);
    }

    public LongPreference lastSyncDate() {
        return new LongPreference(sharedPreferences, "LastSyncDate", 0l);
    }

    public BooleanPreference enableBlacklist() {
        return new BooleanPreference(sharedPreferences, "EnableBlacklist", false);
    }

    public BooleanPreference enableWhitelist() {
        return new BooleanPreference(sharedPreferences, "EnableWhitelist", false);
    }

    public BooleanPreference enableLog() {
        return new BooleanPreference(sharedPreferences, "EnableLog", false);
    }

    public IntPreference batteryLevel() {
        return new IntPreference(sharedPreferences, "BatteryLevel", 0);
    }

    public StringPreference alertPhoneNumber() {
        return new StringPreference(sharedPreferences, "AlertPhoneNumber", "");
    }

    public BooleanPreference smsReportDelivery() {
        return new BooleanPreference(sharedPreferences, "SmsReportDelivery", false);
    }

    public BooleanPreference messageResultsAPIEnable() {
        return new BooleanPreference(sharedPreferences, "MessageResultsAPIEnable", false);
    }

    public Context getContext() {
        return context;
    }
}
