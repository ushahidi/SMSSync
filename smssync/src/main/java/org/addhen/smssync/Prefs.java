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

/**
 * This class instantiate static variables to hold values of the settings / preference fields.
 *
 * @author eyedol
 */
public class Prefs {

    public static final String PREF_NAME = "SMS_SYNC_PREF";

    public static int autoTime = 5;

    public static int taskCheckTime = 5;

    public static String website = "";

    public static String apiKey = "";

    public static String reply = "";

    public static String uniqueId = "";

    public static Boolean enabled = false;

    public static Boolean autoDelete = false;

    public static Boolean enableReply = false;

    public static Boolean enableReplyFrmServer = false;

    public static Boolean enableAutoSync = false;

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
        enableTaskCheck = settings.getBoolean("EnableTaskCheck", false);
        autoTime = settings.getInt("AutoTime", autoTime);
        uniqueId = settings.getString("UniqueId", "");
        taskCheckTime = settings.getInt("taskCheck", taskCheckTime);
        lastSyncDate = settings.getLong("LastSyncDate", 0);
        enableBlacklist = settings.getBoolean("EnableBlacklist", false);
        enableWhitelist = settings.getBoolean("EnableWhitelist", false);
        enableLog = settings.getBoolean("EnableLog", false);
        batteryLevel = settings.getInt("BatteryLevel", 0);
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
        editor.putInt("AutoTime", autoTime);
        editor.putInt("taskCheck", taskCheckTime);
        editor.putString("UniqueId", uniqueId);
        editor.putLong("LastSyncDate", lastSyncDate);
        editor.putBoolean("EnableBlacklist", enableBlacklist);
        editor.putBoolean("EnableWhitelist", enableWhitelist);
        editor.putBoolean("EnableLog", enableLog);
        editor.putInt("BatteryLevel", batteryLevel);
        editor.commit();
    }
}
