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

package org.addhen.smssync.presentation.util;

import com.addhen.android.raiburari.presentation.util.Utils;

import org.addhen.smssync.R;
import org.addhen.smssync.data.PrefsFactory;
import org.addhen.smssync.presentation.receiver.ConnectivityChangedReceiver;
import org.addhen.smssync.presentation.view.ui.activity.MainActivity;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Telephony;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class Utility {

    public static final int NOTIFICATION_PROGRESS_BAR_MAX = 100;

    private static final String URL_PATTERN
            = "\\b(https?|ftp|file)://[-a-zA-Z0-9+\\$&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

    private static Pattern pattern;

    private static Matcher matcher;

    public static final int SET_DEFAULT_SMS_REQUEST = 1;

    private static final int NOTIFY_RUNNING = 100;

    private static final String TIME_FORMAT_12_HOUR = "h:mm a";

    private static final String TIME_FORMAT_24_HOUR = "H:mm";

    public static String formatDate(Date messageDate) {
        SimpleDateFormat formatter = new SimpleDateFormat();
        formatter.applyLocalizedPattern("hh:mm a");
        return formatter.format(messageDate);
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Format an Unix timestamp to a string suitable for display to the user according to their
     * system settings (12 or 24 hour time).
     *
     * @param context   - The context of the calling activity.
     * @param timestamp - The human unfriendly timestamp.
     * @return String
     */
    public static String formatTimestamp(Context context, long timestamp) {
        final boolean is24Hr = DateFormat.is24HourFormat(context);

        SimpleDateFormat mSDF = new SimpleDateFormat();
        if (is24Hr) {
            mSDF.applyLocalizedPattern(TIME_FORMAT_24_HOUR);
        } else {
            mSDF.applyLocalizedPattern(TIME_FORMAT_12_HOUR);
        }
        return mSDF.format(new Date(timestamp));
    }

    /**
     * This method removes all whitespaces from passed string
     *
     * @param s String to be trimmed
     * @return String without whitespaces
     */
    public static String removeWhitespaces(String s) {
        if (TextUtils.isEmpty(s)) {
            return "";
        }
        String withoutWhiteChars = s.replaceAll("\\s+", "");
        return withoutWhiteChars;
    }

    /**
     * Validate the callback URL
     *
     * @param url - The callback URL to be validated.
     * @return boolean True when URL is valid False otherwise
     */
    public static boolean validateUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }

        pattern = Pattern.compile(URL_PATTERN);
        matcher = pattern.matcher(url);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    /**
     * Checks if there is Internet connection or data connection on the device.
     *
     * @param context - The activity calling this method.
     * @return boolean
     */
    public static boolean isConnected(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;

    }

    /**
     * Show notification
     */
    public static void showNotification(Context context) {

        Intent baseIntent = new Intent(context, MainActivity.class);
        baseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                baseIntent, 0);

        buildNotification(context, R.drawable.ic_stat_notfiy,
                context.getString(R.string.notification_summary),
                context.getString(R.string.app_name), pendingIntent, true);

    }

    /**
     * Show a notification when a sync is in progress
     *
     * @param message to display
     */
    public static BuildNotification getSyncNotificationStatus(Context context,
            String message) {

        Intent baseIntent = new Intent(context, MainActivity.class);
        baseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                baseIntent, 0);

        BuildNotification notification = new BuildNotification(context, R.drawable.ic_stat_notfiy,
                message, context.getString(R.string.sync_in_progress), pendingIntent)
                .invoke();
        notification.getBuilder().setOngoing(true)
                .setProgress(NOTIFICATION_PROGRESS_BAR_MAX, 0, false);
        NotificationManager notificationManager = notification.getNotificationManager();
        notificationManager.notify(NOTIFY_RUNNING, notification.getBuilder().build());
        return notification;
    }

    public static void showSyncNotificationStatus(Context context, String message,
            BuildNotification buildNotification) {
        buildNotification.getBuilder()
                .setContentTitle(context.getString(R.string.sync_in_completed));
        buildNotification.getBuilder().setContentText(message)
                .setProgress(0, 0, false); // Remove progress bar
        NotificationManager notificationManager = buildNotification.getNotificationManager();
        notificationManager.notify(NOTIFY_RUNNING, buildNotification.getBuilder().build());
    }

    /**
     * Build notification info
     *
     * @param context  The calling activity
     * @param drawable The notification icon
     * @param message  The message
     * @param title    The title for the notification
     * @param intent   The pending intent
     * @param ongoing  True if you don't want the user to clear the notification
     */
    public static void buildNotification(Context context, int drawable,
            String message, String title, PendingIntent intent, boolean ongoing) {

        BuildNotification buildNotification = new BuildNotification(context, drawable, message,
                title, intent).invoke();
        NotificationCompat.Builder builder = buildNotification.getBuilder();
        NotificationManager notificationManager = buildNotification.getNotificationManager();

        if (ongoing) {
            builder.setOngoing(ongoing);
        }

        notificationManager.notify(NOTIFY_RUNNING, builder.build());
    }


    /**
     * Clear all notifications shown to the user.
     *
     * @param context - The context of the calling activity.
     * @return void.
     */
    public static void clearAll(Context context) {
        NotificationManager myNM = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        myNM.cancelAll();
    }

    /**
     * Clear a running notification.
     *
     * @param context - The context of the calling activity.
     * @return void
     */
    public static void clearNotify(Context context) {
        NotificationManager myNM = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        myNM.cancel(NOTIFY_RUNNING);
    }

    /**
     * Makes an attempt to connect to a data network.
     */
    public static void connectToDataNetwork(@NonNull Context context) {
        // Enable the Connectivity Changed Receiver to listen for
        // connection to a network so we can send pending messages.
        PackageManager pm = context.getPackageManager();
        ComponentName connectivityReceiver = new ComponentName(context,
                ConnectivityChangedReceiver.class);
        pm.setComponentEnabledSetting(connectivityReceiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public static String getPhoneNumber(@NonNull Context context, @NonNull PrefsFactory prefs) {

        TelephonyManager mTelephonyMgr;
        mTelephonyMgr = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        String number = mTelephonyMgr.getLine1Number();
        if (number != null) {
            return number;
        }
        return prefs.uniqueId().get();

    }

    /**
     * Capitalize any String given to it.
     *
     * @param text - The string to be capitalized.
     * @return String
     */
    public static String capitalizeFirstLetter(String text) {
        if (text == null) {
            return null;
        } else if (text.length() == 0) {
            return "";
        } else if (text.length() == 1) {
            return text.toUpperCase();
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    /**
     * Gets the first character of a name
     *
     * @param name The name to get its first letter
     * @return The first character
     */
    public static char getFirstCharacter(String name) {
        final char firstChar = name.charAt(0);
        if (isEnglishLetterOrDigit(firstChar)) {
            return Character.toUpperCase(firstChar);
        }
        return 0;
    }

    /**
     * @param c The char to check
     * @return True if <code>c</code> is in the English alphabet or is a digit,
     * false otherwise
     */
    private static boolean isEnglishLetterOrDigit(char c) {
        return 'A' <= c && c <= 'Z' || 'a' <= c && c <= 'z' || '0' <= c && c <= '9';
    }

    public static void makeDefaultSmsApp(Activity activity) {

        if (!isDefaultSmsApp(activity)) {
            final Intent changeDefaultIntent = new Intent(
                    Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            changeDefaultIntent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                    activity.getPackageName());
            activity.startActivityForResult(changeDefaultIntent, SET_DEFAULT_SMS_REQUEST);
        }
    }

    public static boolean isDefaultSmsApp(Context context) {
        if (Utils.isKitKatOrHigher()) {
            return context.getPackageName().equals(Telephony.Sms.getDefaultSmsPackage(context));
        }
        return true;
    }

    public static int calculateBatteryLevel(int level, int scale) {
        if (level >= 0 && scale > 0) {
            return (level * 100) / scale;
        }

        return -1;
    }

    public static int dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public static int keywordColor() {
        return Color.parseColor("#00796B");
    }

    @DrawableRes
    public static int keywordIcon() {
        return R.drawable.ic_highlight_remove_white_18dp;
    }

    public static class BuildNotification {

        private Context mContext;

        private int mDrawable;

        private String mMessage;

        private String mTitle;

        private PendingIntent mIntent;

        private NotificationManager mNotificationManager;

        private NotificationCompat.Builder mBuilder;

        public BuildNotification(Context context, int drawable, String message, String title,
                PendingIntent intent) {
            mContext = context;
            mDrawable = drawable;
            mMessage = message;
            mTitle = title;
            mIntent = intent;
        }

        public NotificationManager getNotificationManager() {
            return mNotificationManager;
        }

        public NotificationCompat.Builder getBuilder() {
            return mBuilder;
        }

        public BuildNotification invoke() {
            mNotificationManager = (NotificationManager) mContext
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            mBuilder = new NotificationCompat.Builder(
                    mContext);
            mBuilder.setContentTitle(mTitle);
            mBuilder.setContentText(mMessage);
            mBuilder.setSmallIcon(mDrawable);
            mBuilder.setContentIntent(mIntent);
            return this;
        }
    }
}
