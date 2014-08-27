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

package org.addhen.smssync;

import android.content.Context;
import android.content.SharedPreferences;

import org.addhen.smssync.util.TimeFrequencyUtil;

/**
 * This class instantiate static variables to hold values of the settings / preference fields.
 *
 * @author eyedol
 */
public class Prefs {

    public static final String PREF_NAME = "SMS_SYNC_PREF";

    public static String autoTime;

    public static String taskCheckTime;

    public static String website = "";

    public static String apiKey = "";

    public static String reply = "";

    public static String uniqueId = "";

    public static String alertPhoneNumber = "";

    public static Boolean enabled = false;

    public static Boolean autoDelete = false;

    public static Boolean enableReply = false;

    public static Boolean enableReplyFrmServer = false;

    public static Boolean enableAutoSync = false;

    public static Boolean useSmsPortals = false;

    public static Boolean enableTaskCheck = false;

    public static long lastSyncDate = 0;

    public static Boolean enableWhitelist = false;

    public static Boolean enableBlacklist = false;

    
    public static Boolean enableLog = false;

    private static SharedPreferences.Editor editor;

    public static int batteryLevel = 0;

    /**
     * Load the value of the settings / preference variable.
     *
     * @param context - The context of the calling activity.
     * @return void
     */
    public static void loadPreferences(Context context) {

        final SharedPreferences settings = context.getSharedPreferences(
                PREF_NAME, 0);

        timeKeyValueUpdate(settings);

        website = settings.getString("WebsitePref", "");
        apiKey = settings.getString("ApiKey", "");
        reply = settings.getString("ReplyPref",
                context.getString(R.string.edittxt_reply_default));
        enabled = settings.getBoolean("EnableSmsSync", false);
        autoDelete = settings.getBoolean("EnableAutoDelete", false);
        enableReply = settings.getBoolean("EnableReply", false);
        enableReplyFrmServer = settings.getBoolean("EnableReplyFrmServer",
                false);
        enableAutoSync = settings.getBoolean("AutoSync", false);
        useSmsPortals =  settings.getBoolean("UseSmsPortals", false);
        enableTaskCheck = settings.getBoolean("EnableTaskCheck", false);
        autoTime = settings.getString("AutoTime", TimeFrequencyUtil.DEFAULT_TIME_FREQUENCY);
        uniqueId = settings.getString("UniqueId", "");
        taskCheckTime = settings.getString("taskCheck", TimeFrequencyUtil.DEFAULT_TIME_FREQUENCY);
        lastSyncDate = settings.getLong("LastSyncDate", 0);
        enableBlacklist = settings.getBoolean("EnableBlacklist", false);
        enableWhitelist = settings.getBoolean("EnableWhitelist", false);
        enableLog = settings.getBoolean("EnableLog", false);
        batteryLevel = settings.getInt("BatteryLevel", 0);
        alertPhoneNumber = settings.getString("AlertPhoneNumber","");
    }

    /**
     * Save settings changes.
     *
     * @return void
     */
    public static void savePreferences(Context context) {

        final SharedPreferences settings = context.getSharedPreferences(
                PREF_NAME, 0);
        editor = settings.edit();
        editor.putBoolean("EnableSmsSync", enabled);
        editor.putBoolean("EnableAutoDelete", autoDelete);
        editor.putBoolean("EnableReply", enableReply);
        editor.putBoolean("EnableReplyFrmServer", enableReplyFrmServer);
        editor.putBoolean("AutoSync", enableAutoSync);
        editor.putString("AutoTime", autoTime);
        editor.putString("taskCheck", taskCheckTime);
        editor.putBoolean("UseSmsPortals", useSmsPortals);
        editor.putString("UniqueId", uniqueId);
        editor.putLong("LastSyncDate", lastSyncDate);
        editor.putBoolean("EnableBlacklist", enableBlacklist);
        editor.putBoolean("EnableWhitelist", enableWhitelist);
        editor.putBoolean("EnableLog", enableLog);
        editor.putInt("BatteryLevel", batteryLevel);
        editor.putString("AlertPhoneNumber", alertPhoneNumber);
        editor.commit();
    }

    /**
     * This methods removes old preferences to omit problem caused by
     * AutoTime and taskCheck values changed (was int changed into String)
     * @param settings
     */
    private static void timeKeyValueUpdate(final SharedPreferences settings) {
        Boolean autoTimeUpdate = settings.getBoolean("AutoTimeUpdate", false);
        if (null == autoTimeUpdate || autoTimeUpdate.equals(false)) {
            editor = settings.edit();
            editor.remove("AutoTime");
            editor.remove("taskCheck");
            editor.putBoolean("AutoTimeUpdate", true);
            editor.commit();
        }
    }
}
