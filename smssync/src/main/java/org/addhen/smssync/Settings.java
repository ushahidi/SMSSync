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

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.squareup.otto.Produce;

import org.addhen.smssync.util.Logger;
import org.addhen.smssync.util.RunServicesUtil;
import org.addhen.smssync.util.Util;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.text.TextUtils;

/**
 * This class handles all related task for settings on SMSSync. TODO // move the UI code into it's
 * own xml file
 *
 * @author eyedol
 */
public class Settings extends SherlockPreferenceActivity implements
        OnSharedPreferenceChangeListener {

    public static final String KEY_ENABLE_SMS_SYNC_PREF = "enable_sms_sync_preference";

    public static final String KEY_POWERED_PREFERENCE = "powered_preference";

    public static final String KEY_AUTO_DELETE_MESSAGE = "auto_delete_preference";

    public static final String KEY_ENABLE_REPLY = "enable_reply_preference";

    public static final String KEY_ENABLE_REPLY_FRM_SERVER = "enable_reply_frm_server_preference";

    public static final String KEY_REPLY = "reply_preference";

    public static final String KEY_UNIQUE_ID = "unique_id_preference";

    public static final String AUTO_SYNC = "auto_sync_preference";

    public static final String AUTO_SYNC_TIMES = "auto_sync_times";

    public static final String TASK_CHECK = "task_check_preference";

    public static final String TASK_CHECK_TIMES = "task_check_times";

    public static final String ABOUT = "powered_preference";

    private EditTextPreference replyPref;

    private CheckBoxPreference enableReplyFrmServer;

    private CheckBoxPreference enableAutoDelete;

    private CheckBoxPreference enableReply;

    private CheckBoxPreference autoSync;

    private CheckBoxPreference taskCheck;

    private ListPreference autoSyncTimes;

    private ListPreference taskCheckTimes;

    private EditTextPreference uniqueId;

    private Preference about;

    private SharedPreferences settings;

    private SharedPreferences.Editor editor;

    private static final String URL = "http://smssync.ushahidi.com";

    private CharSequence[] autoSyncEntries = {
            "1 Minute", "2 Minutes",
            "3 Minutes", "4 Minutes", "5 Minutes", "10 Minutes", "15 Minutes",
            "30 Minutes", "60 Minutes"
    };

    private CharSequence[] autoSyncValues = {
            "1", "2", "3", "4", "5", "10",
            "15", "30", "60"
    };

    private int autoTime = 5;

    private int taskCheckTime = 5;

    private int uniqueIdValidityStatus = 1;

    private final Handler mHandler = new Handler();

    private String versionName;

    private StringBuilder versionLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        try {
            versionName = getPackageManager().getPackageInfo(
                    this.getPackageName(), 0).versionName;
            // add app name to verstion number
            versionLabel = new StringBuilder(getString(R.string.app_name));
            versionLabel.append(" ");
            versionLabel.append("v");
            versionLabel.append(versionName);
            versionLabel.append(" ");
            versionLabel.append(getString(R.string.version_status));
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        enableAutoDelete = (CheckBoxPreference) getPreferenceScreen()
                .findPreference(KEY_AUTO_DELETE_MESSAGE);

        enableReply = (CheckBoxPreference) getPreferenceScreen()
                .findPreference(KEY_ENABLE_REPLY);
        enableReplyFrmServer = (CheckBoxPreference) getPreferenceScreen()
                .findPreference(KEY_ENABLE_REPLY_FRM_SERVER);

        autoSync = (CheckBoxPreference) getPreferenceScreen().findPreference(
                AUTO_SYNC);

        taskCheck = (CheckBoxPreference) getPreferenceScreen().findPreference(
                TASK_CHECK);

        replyPref = (EditTextPreference) getPreferenceScreen().findPreference(
                KEY_REPLY);

        uniqueId = (EditTextPreference) getPreferenceScreen().findPreference(
                KEY_UNIQUE_ID);

        autoSyncTimes = (ListPreference) getPreferenceScreen().findPreference(
                AUTO_SYNC_TIMES);
        autoSyncTimes.setEntries(autoSyncEntries);
        autoSyncTimes.setEntryValues(autoSyncValues);

        taskCheckTimes = (ListPreference) getPreferenceScreen().findPreference(
                TASK_CHECK_TIMES);
        taskCheckTimes.setEntries(autoSyncEntries);
        taskCheckTimes.setEntryValues(autoSyncValues);

        about = (Preference) getPreferenceScreen().findPreference(ABOUT);

        about.setTitle(versionLabel.toString());
        about.setSummary(R.string.powered_by);

        // When the about us item is clicked at the Settings screen, open a URL
        Preference poweredPreference = findPreference(KEY_POWERED_PREFERENCE);
        poweredPreference
                .setOnPreferenceClickListener(new OnPreferenceClickListener() {
                    public boolean onPreferenceClick(Preference preference) {
                        final Intent i = new Intent(
                                android.content.Intent.ACTION_VIEW, Uri
                                .parse(URL));
                        startActivity(i);
                        return true;
                    }
                });

        // Save settings changes.
        this.savePreferences();
    }


    /**
     * Get the time frequency selected by the user for auto synchronization.
     *
     * @return int
     */
    private int initializeAutoSyncTime() {

        // Initialize the selected time to frequently sync pending messages
        if (autoSyncTimes.getValue().matches("1")) {
            return 1;
        } else if (autoSyncTimes.getValue().matches("2")) {
            return 2;
        } else if (autoSyncTimes.getValue().matches("3")) {
            return 3;
        } else if (autoSyncTimes.getValue().matches("4")) {
            return 4;
        } else if (autoSyncTimes.getValue().matches("10")) {
            return 10;
        } else if (autoSyncTimes.getValue().matches("15")) {
            return 15;
        } else if (autoSyncTimes.getValue().matches("30")) {
            return 30;
        } else if (autoSyncTimes.getValue().matches("60")) {
            return 60;
        } else {
            return 5;
        }
    }

    /**
     * Get the time frequency selected by the user for auto task checking.
     *
     * @return int
     */
    private int initializeAutoTaskTime() {

        // "1 Minutes", 2 Minutes", "3 Minutes", "4 Minutes", "5 Minutes", "10
        // Minutes", "15 Minutes", "30", "60 Minutes"
        if (taskCheckTimes.getValue().matches("1")) {
            return 1;
        } else if (taskCheckTimes.getValue().matches("2")) {
            return 2;
        } else if (taskCheckTimes.getValue().matches("3")) {
            return 3;
        } else if (taskCheckTimes.getValue().matches("4")) {
            return 4;
        } else if (taskCheckTimes.getValue().matches("10")) {
            return 10;
        } else if (taskCheckTimes.getValue().matches("15")) {
            return 15;
        } else if (taskCheckTimes.getValue().matches("30")) {
            return 30;
        } else if (taskCheckTimes.getValue().matches("60")) {
            return 60;
        } else {
            return 5;
        }
    }

    /**
     * Save settings changes.
     *
     * @return void
     */
    protected void savePreferences() {

        settings = getSharedPreferences(Prefs.PREF_NAME, 0);

        if (replyPref.getText().equals("")) {
            replyPref.setText(getString(R.string.edittxt_reply_default));
        }

        if (enableReply.isChecked()) {
            replyPref.setEnabled(true);
            enableReplyFrmServer.setChecked(false);
            enableReplyFrmServer.setEnabled(false);
        } else {
            replyPref.setEnabled(false);
            enableReplyFrmServer.setEnabled(true);
        }

        if (enableReplyFrmServer.isChecked()) {
            enableReply.setChecked(false);
            enableReply.setEnabled(false);
        } else {
            enableReply.setEnabled(true);
        }

        if (autoSync.isChecked()) {
            autoSyncTimes.setEnabled(true);
        } else {
            autoSyncTimes.setEnabled(false);
        }

        // Initialize the selected time to frequently sync pending messages
        autoTime = initializeAutoSyncTime();

        if (taskCheck.isChecked()) {
            taskCheckTimes.setEnabled(true);
        } else {
            taskCheckTimes.setEnabled(false);
        }

        // Initialize the selected frequency to automatically check for tasks
        taskCheckTime = initializeAutoTaskTime();

        editor = settings.edit();
        editor.putString("ReplyPref", replyPref.getText());
        // log reply changes.
        if (!Prefs.reply.equals(replyPref.getText().toString())) {
            // Log old value and new value.
            Util.logActivities(this, getString(R.string.settings_changed,
                    replyPref.getDialogTitle().toString(), Prefs.reply,
                    replyPref.getText().toString()));
        }
        editor.putBoolean("EnableAutoDelete", enableAutoDelete.isChecked());
        if (Prefs.autoDelete != enableAutoDelete.isChecked()) {
            boolean checked = enableAutoDelete.isChecked() ? true : false;
            String check = getCheckedStatus(checked);

            String status = getCheckedStatus(Prefs.autoDelete);

            Util.logActivities(Settings.this, getString(R.string.settings_changed,
                    enableAutoDelete.getTitle().toString(), status,
                    check));
        }

        editor.putBoolean("EnableReply", enableReply.isChecked());
        if (Prefs.enableReply != enableReply.isChecked()) {
            boolean checked = enableReply.isChecked() ? true : false;
            String check = getCheckedStatus(checked);

            String status = getCheckedStatus(Prefs.enableReply);

            Util.logActivities(Settings.this, getString(R.string.settings_changed,
                    enableReply.getTitle().toString(), status,
                    check));
        }

        editor.putBoolean("EnableReplyFrmServer",
                enableReplyFrmServer.isChecked());
        if (Prefs.enableReplyFrmServer != enableReplyFrmServer.isChecked()) {
            boolean checked = enableReplyFrmServer.isChecked() ? true : false;
            String check = getCheckedStatus(checked);

            String status = getCheckedStatus(Prefs.enableReplyFrmServer);

            Util.logActivities(Settings.this, getString(R.string.settings_changed,
                    enableReplyFrmServer.getTitle().toString(), status,
                    check));
        }

        editor.putBoolean("EnableTaskCheck", taskCheck.isChecked());
        if (Prefs.enableTaskCheck != taskCheck.isChecked()) {
            boolean checked = taskCheck.isChecked() ? true : false;
            String check = getCheckedStatus(checked);

            String status = getCheckedStatus(Prefs.enableTaskCheck);

            Util.logActivities(Settings.this, getString(R.string.settings_changed,
                    taskCheck.getTitle().toString(), status,
                    check));
        }

        editor.putBoolean("AutoSync", autoSync.isChecked());
        if (Prefs.enableAutoSync != autoSync.isChecked()) {
            boolean checked = autoSync.isChecked() ? true : false;
            String check = getCheckedStatus(checked);

            String status = getCheckedStatus(Prefs.enableAutoSync);

            Util.logActivities(Settings.this, getString(R.string.settings_changed,
                    autoSync.getTitle().toString(), status,
                    check));
        }

        editor.putInt("AutoTime", autoTime);
        if (Prefs.autoTime != autoTime) {
            Util.logActivities(this, getString(R.string.settings_changed,
                    autoSyncTimes.getTitle().toString(),
                    autoSyncTimes.getEntries()[Prefs.autoTime - 1],
                    autoSyncTimes.getEntries()[autoTime - 1]));
        }

        editor.putInt("taskCheck", taskCheckTime);

        if (Prefs.taskCheckTime != taskCheckTime) {
            Util.logActivities(this, getString(R.string.settings_changed,
                    taskCheckTimes.getTitle().toString(),
                    taskCheckTimes.getEntries()[Prefs.taskCheckTime - 1],
                    taskCheckTimes.getEntries()[taskCheckTime - 1]));
        }

        if (!TextUtils.isEmpty(uniqueId.getText())) {
            uniqueIdValidate(uniqueId.getText());
            editor.putString("UniqueId", "");
            if (uniqueIdValidityStatus == 0) {
                editor.putString("UniqueId", uniqueId.getText());
            }
        }
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    /**
     * Perform sanity checks on settings changes.
     *
     * @param sharedPreferences -
     * @return void
     */
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {

        // Unique ID
        if (key.equals(KEY_UNIQUE_ID)) {
            final String savedId = sharedPreferences.getString(KEY_UNIQUE_ID,
                    "");
            if (!TextUtils.isEmpty(savedId))
                uniqueIdValidate(savedId);
        }

        if (key.equals(KEY_ENABLE_REPLY)) {

            if (sharedPreferences.getBoolean(KEY_ENABLE_REPLY, false)) {
                replyPref.setEnabled(true);
            } else {
                replyPref.setEnabled(false);
            }
        }

        // Auto sync enable
        if (key.equals(AUTO_SYNC)) {

            if (sharedPreferences.getBoolean(AUTO_SYNC, false)) {

                autoSyncEnable();

            } else {
                // stop scheduler
                RunServicesUtil.stopAutoSyncService(Settings.this);

                autoSyncTimes.setEnabled(false);
            }
        }

        if (key.equals(AUTO_SYNC_TIMES)) {

            // restart service
            if (Prefs.enableAutoSync) {

                // Initialize the selected time to frequently sync pending
                // messages
                Prefs.autoTime = initializeAutoSyncTime();

                RunServicesUtil.runAutoSyncService(Settings.this);

            }
        }

        // Enable task checking
        if (key.equals(TASK_CHECK)) {

            if (sharedPreferences.getBoolean(TASK_CHECK, false)) {
                autoTaskCheckValidateCallbackURL();

            } else {

                RunServicesUtil.stopCheckTaskService(Settings.this);
                taskCheckTimes.setEnabled(false);
            }
        }

        // task frequency
        if (key.equals(TASK_CHECK_TIMES)) {

            Prefs.taskCheckTime = initializeAutoTaskTime();
            RunServicesUtil.runCheckTaskService(Settings.this);
        }

        this.savePreferences();
    }

    /**
     * Create runnable for validating callback URL. Putting the validation
     * process in it own thread provides efficiency.
     */
    final Runnable mTaskCheckEnabled = new Runnable() {

        public void run() {

            if (!Prefs.enabled) {

                Util.showToast(Settings.this, R.string.no_configured_url);
                taskCheck.setChecked(false);

            } else {

                taskCheck.setChecked(true);

                Prefs.taskCheckTime = initializeAutoTaskTime();

                // start the scheduler for task checking service
                RunServicesUtil.runCheckTaskService(Settings.this);
            }
        }
    };

    /**
     *
     */
    final Runnable mAutoSyncEnabled = new Runnable() {

        public void run() {

            if (!Prefs.enabled) {

                Util.showToast(Settings.this, R.string.no_configured_url);
                autoSync.setChecked(false);

            } else {

                autoSync.setChecked(true);

                // Initialize the selected time to frequently sync pending
                // messages
                Prefs.autoTime = initializeAutoSyncTime();
                autoSyncTimes.setEnabled(true);

                RunServicesUtil.runAutoSyncService(Settings.this);
            }
        }
    };

    /**
     * Create a child thread and validate the callback URL in it when enabling
     * auto task check preference.
     *
     * @return void
     */
    public void autoTaskCheckValidateCallbackURL() {

        Thread t = new Thread() {
            public void run() {
                mHandler.post(mTaskCheckEnabled);
            }
        };
        t.start();
    }

    public void autoSyncEnable() {

        Thread t = new Thread() {
            public void run() {
                mHandler.post(mAutoSyncEnabled);
            }
        };
        t.start();
    }

    /**
     * Create runnable to validate unique ID.
     */
    Runnable mUniqueId = new Runnable() {
        public void run() {

            if (uniqueIdValidityStatus == 1) {

                Util.showToast(Settings.this, R.string.unique_id_length_error);
                uniqueId.setText("");
            } else if (uniqueIdValidityStatus == 2) {
                Util.showToast(Settings.this, R.string.unique_id_numeric_error);
                uniqueId.setText("");
            }
        }
    };

    /**
     * Thread to validate unique id
     *
     * @param uniqueId The Callback Url to be validated.
     * @return void
     */
    public void uniqueIdValidate(final String uniqueId) {

        Thread t = new Thread() {
            public void run() {

                // validate number of digits
                if ((uniqueId.length() == 0) || TextUtils.isEmpty(uniqueId)) {
                    uniqueIdValidityStatus = 1;
                    mHandler.post(mUniqueId);
                } else {
                    // validate if it's a numeric value
                    try {
                        Integer.parseInt(uniqueId);
                        uniqueIdValidityStatus = 0;
                    } catch (NumberFormatException ex) {
                        uniqueIdValidityStatus = 2;
                        mHandler.post(mUniqueId);
                    }
                    mHandler.post(mUniqueId);
                }
            }
        };
        t.start();
    }

    /**
     * A convenient method to return boolean values to a more meaningful format
     *
     * @param status The boolean value
     * @return The meaningful format
     */
    private String getCheckedStatus(boolean status) {
        if (status) {
            return getString(R.string.enabled);
        }
        return getString(R.string.disabled);
    }

}
