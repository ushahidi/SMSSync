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

import org.addhen.smssync.prefs.Prefs;
import org.addhen.smssync.util.Logger;
import org.addhen.smssync.util.RunServicesUtil;
import org.addhen.smssync.util.TimePreference;
import org.addhen.smssync.util.Util;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Messenger;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles all related task for settings on SMSSync. TODO // move the UI code into it's
 * own xml file
 *
 * @author eyedol
 */
public class Settings extends PreferenceActivity implements
        OnSharedPreferenceChangeListener {

    public static final String KEY_ENABLE_SMS_REPORT_DELIVERY
            = "enable_sms_report_delivery_preference";

    public static final String KEY_POWERED_PREFERENCE = "powered_preference";

    public static final String KEY_AUTO_DELETE_MESSAGE = "auto_delete_preference";

    public static final String KEY_ENABLE_REPLY = "enable_reply_preference";

    public static final String KEY_ENABLE_REPLY_FRM_SERVER = "enable_reply_frm_server_preference";

    public static final String KEY_REPLY = "reply_preference";

    public static final String KEY_UNIQUE_ID = "unique_id_preference";

    public static final String KEY_ALERT_PHONE_NUMBER = "alert_phone_number_preference";

    public static final String AUTO_SYNC = "auto_sync_preference";

    public static final String AUTO_SYNC_TIMES = "auto_sync_times";

   // public static final String KEY_ENABLE_SMS_PORTALS = "enable_sms_portals";

    public static final String KEY_ENABLE_RETRIES = "auto_delete_pending_messages_preference";

    public static final String KEY_LIST_RETRIES  = "auto_delete_pending_messages_retries_preference";

    public static final String TASK_CHECK = "task_check_preference";

    public static final String TASK_CHECK_TIMES = "task_check_times";

    public static final String MESSAGE_RESULTS_API = "message_results_api_preference";

    public static final String ABOUT = "powered_preference";

    private static final String URL = "http://smssync.ushahidi.com";

    public static ArrayList<Messenger> availableConnections = new ArrayList<Messenger>();

    public static int currentConnectionIndex = -1;

    final Runnable mMessageResultsAPIEnabled = new Runnable() {

        public void run() {

            if (!prefs.serviceEnabled().get()) {
                Util.showToast(Settings.this, R.string.no_configured_url);
                enableMessageResultsAPI.setChecked(false);
            } else {
                enableMessageResultsAPI.setChecked(true);
                runServicesUtil.runMessageResultsService();
            }
        }
    };

    /**
     * Create runnable for validating callback URL. Putting the validation process in it own thread
     * provides efficiency.
     */
    final Runnable mTaskCheckEnabled = new Runnable() {

        public void run() {

            if (!prefs.serviceEnabled().get()) {

                Util.showToast(Settings.this, R.string.no_configured_url);

                taskCheck.setChecked(false);
                if (enableMessageResultsAPI.isChecked()) {
                    enableMessageResultsAPI.setChecked(false);
                }
            } else {

                taskCheck.setChecked(true);

                // start the scheduler for task checking service
                runServicesUtil.runCheckTaskService();
            }
        }
    };

    /**
     *
     */
    final Runnable mAutoSyncEnabled = new Runnable() {

        public void run() {

            if (!prefs.serviceEnabled().get()) {

                Util.showToast(Settings.this, R.string.no_configured_url);
                autoSync.setChecked(false);

            } else {

                autoSync.setChecked(true);

                // Initialize the selected time to frequently sync pending
                // messages
                autoSyncTimes.setEnabled(true);
                runServicesUtil.runAutoSyncService();
            }
        }
    };

    private final Handler mHandler = new Handler();

    private EditTextPreference replyPref;

    private CheckBoxPreference enableReplyFrmServer;

    private CheckBoxPreference enableAutoDelete;

    private CheckBoxPreference enableSmsReportDelivery;

    private CheckBoxPreference enableReply;

    private CheckBoxPreference autoSync;

    private CheckBoxPreference useSmsPortals;

    private CheckBoxPreference taskCheck;

    private TimePreference autoSyncTimes;

    private CheckBoxPreference enableMessageResultsAPI;

    private ListPreference retry;

    private CheckBoxPreference enableRetry;

    private TimePreference taskCheckTimes;

    private EditTextPreference uniqueId;

    private EditTextPreference alertPhoneNumber;

    private Preference about;

    private Prefs prefs;

    private int uniqueIdValidityStatus = 1;

    private RunServicesUtil runServicesUtil;

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
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        enableAutoDelete = (CheckBoxPreference) getPreferenceScreen()
                .findPreference(KEY_AUTO_DELETE_MESSAGE);

        enableSmsReportDelivery = (CheckBoxPreference) getPreferenceScreen()
                .findPreference(KEY_ENABLE_SMS_REPORT_DELIVERY);

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

        alertPhoneNumber = (EditTextPreference) getPreferenceScreen().findPreference(
                KEY_ALERT_PHONE_NUMBER);

        autoSyncTimes = (TimePreference) getPreferenceScreen().findPreference(
                AUTO_SYNC_TIMES);

        taskCheckTimes = (TimePreference) getPreferenceScreen().findPreference(TASK_CHECK_TIMES);

        /*useSmsPortals = (CheckBoxPreference) getPreferenceScreen()
                .findPreference(KEY_ENABLE_SMS_PORTALS);*/

        enableRetry  = (CheckBoxPreference) getPreferenceScreen().findPreference(KEY_ENABLE_RETRIES);

        retry = (ListPreference) getPreferenceScreen().findPreference(KEY_LIST_RETRIES);

        enableMessageResultsAPI = (CheckBoxPreference) getPreferenceScreen().findPreference(
                MESSAGE_RESULTS_API);

        about = getPreferenceScreen().findPreference(ABOUT);

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

        prefs = new Prefs(this);

        runServicesUtil = new RunServicesUtil(prefs);
        // Save settings changes.
        this.savePreferences();
    }

    /**
     * Save settings changes.
     *
     * @return void
     */
    protected void savePreferences() {

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

        if (taskCheck.isChecked()) {
            taskCheckTimes.setEnabled(true);
            enableMessageResultsAPI.setEnabled(true);
        } else {
            taskCheckTimes.setEnabled(false);
            enableMessageResultsAPI.setEnabled(false);
        }

        if(enableRetry.isChecked()) {
            retry.setEnabled(true);
        } else {
            retry.setEnabled(false);
        }


        // log reply changes.
        if (!prefs.reply().get().equals(replyPref.getText().toString())) {
            // Log old value and new value.
            Util.logActivities(this, getString(R.string.settings_changed,
                    replyPref.getDialogTitle().toString(), prefs.reply().get(),
                    replyPref.getText().toString()));
        }
        prefs.reply().set(replyPref.getText().toString());

        if (prefs.autoDelete().get() != enableAutoDelete.isChecked()) {
            boolean checked = enableAutoDelete.isChecked() ? true : false;
            String check = getCheckedStatus(checked);

            String status = getCheckedStatus(prefs.autoDelete().get());

            Util.logActivities(Settings.this, getString(R.string.settings_changed,
                    enableAutoDelete.getTitle().toString(), status,
                    check));
        }
        prefs.autoDelete().set(enableAutoDelete.isChecked());

        if (prefs.smsReportDelivery().get() != enableSmsReportDelivery.isChecked()) {
            boolean checked = enableSmsReportDelivery.isChecked() ? true : false;
            String check = getCheckedStatus(checked);

            String status = getCheckedStatus(prefs.smsReportDelivery().get());

            Util.logActivities(Settings.this, getString(R.string.settings_changed,
                    enableSmsReportDelivery.getTitle().toString(), status,
                    check));
        }
        prefs.smsReportDelivery().set(enableSmsReportDelivery.isChecked());

        if (prefs.enableReply().get() != enableReply.isChecked()) {
            boolean checked = enableReply.isChecked() ? true : false;
            String check = getCheckedStatus(checked);

            String status = getCheckedStatus(prefs.enableReply().get());

            Util.logActivities(Settings.this, getString(R.string.settings_changed,
                    enableReply.getTitle().toString(), status,
                    check));
        }
        prefs.enableReply().set(enableReply.isChecked());

        if (prefs.enableReplyFrmServer().get() != enableReplyFrmServer.isChecked()) {
            boolean checked = enableReplyFrmServer.isChecked() ? true : false;
            String check = getCheckedStatus(checked);

            String status = getCheckedStatus(prefs.enableReplyFrmServer().get());

            Util.logActivities(Settings.this, getString(R.string.settings_changed,
                    enableReplyFrmServer.getTitle().toString(), status,
                    check));
        }
        prefs.enableReplyFrmServer().set(enableReplyFrmServer.isChecked());

        if (prefs.enableTaskCheck().get() != taskCheck.isChecked()) {
            boolean checked = taskCheck.isChecked() ? true : false;
            String check = getCheckedStatus(checked);

            String status = getCheckedStatus(prefs.enableTaskCheck().get());

            Util.logActivities(Settings.this, getString(R.string.settings_changed,
                    taskCheck.getTitle().toString(), status,
                    check));
        }
        prefs.enableTaskCheck().set(taskCheck.isChecked());

        if (prefs.enableAutoSync().get() != autoSync.isChecked()) {
            boolean checked = autoSync.isChecked() ? true : false;
            String check = getCheckedStatus(checked);

            String status = getCheckedStatus(prefs.enableAutoSync().get());

            Util.logActivities(Settings.this, getString(R.string.settings_changed,
                    autoSync.getTitle().toString(), status,
                    check));
        }
        prefs.enableAutoSync().set(autoSync.isChecked());

        if (!prefs.autoTime().get().equals(autoSyncTimes.getTimeValueAsString())) {
            Util.logActivities(this, getString(R.string.settings_changed,
                    autoSyncTimes.getTitle().toString(),
                    prefs.autoTime().get(), autoSyncTimes.getTimeValueAsString()));
        }
        prefs.autoTime().set(autoSyncTimes.getTimeValueAsString());

        // Enable or Disable Pending messages delete retries.
        if(prefs.enableRetry().get() != enableRetry.isChecked()) {
            boolean checked = enableRetry.isChecked() ? true : false;

            String check = getCheckedStatus(checked);

            String status = getCheckedStatus(prefs.enableRetry().get());

            Util.logActivities(Settings.this, getString(R.string.settings_changed,
                    enableRetry.getTitle().toString(), status,
                    check));
        }
        prefs.enableRetry().set(enableRetry.isChecked());

        for(int i = 0; i < retry.getEntryValues().length; i++) {
            if(retry.getEntry() !=null ) {
                if (retry.getValue()
                        .matches(getResources().getStringArray(R.array.retry_entries)[i])) {
                    prefs.retries().set(getResources().getIntArray(R.array.retry_values)[i]);
                    break;
                }
            }
        }

        /*
        prefs.useSmsPortals().set(useSmsPortals.isChecked());
        if (prefs.useSmsPortals().get() != useSmsPortals.isChecked()) {
            boolean checked = useSmsPortals.isChecked() ? true : false;
            String check = getCheckedStatus(checked);

            String status = getCheckedStatus(prefs.useSmsPortals().get());

            Util.logActivities(Settings.this, getString(R.string.settings_changed,
                    useSmsPortals.getTitle().toString(), status,
                    check));
        }*/


        if (!prefs.taskCheckTime().get().equals(taskCheckTimes.getTimeValueAsString())) {
            Util.logActivities(this, getString(R.string.settings_changed,
                    taskCheckTimes.getTitle().toString(),
                    prefs.taskCheckTime().get(), taskCheckTimes.getTimeValueAsString()));
        }
        prefs.taskCheckTime().set(taskCheckTimes.getTimeValueAsString());

        if (!TextUtils.isEmpty(uniqueId.getText())) {
            String id = Util.removeWhitespaces(uniqueId.getText());

            if (!prefs.uniqueId().get().equals(uniqueId.getText())) {
                Util.logActivities(this,
                        getString(R.string.settings_changed, uniqueId.getTitle(),
                                prefs.uniqueId().get(), id)
                );
            }
            prefs.uniqueId().set(id);
        } else {

            if (!prefs.uniqueId().get().equals("")) {
                Util.logActivities(this,
                        getString(R.string.settings_changed, uniqueId.getTitle(),
                                prefs.uniqueId().get(), "")
                );
            }
            prefs.uniqueId().set("");
        }

        if (!TextUtils.isEmpty(alertPhoneNumber.getText())) {
            String number = Util.removeWhitespaces(alertPhoneNumber.getText());

            if (!prefs.alertPhoneNumber().get().equals(alertPhoneNumber.getText())) {
                Util.logActivities(this,
                        getString(R.string.settings_changed, alertPhoneNumber.getTitle().toString(),
                                prefs.alertPhoneNumber().get(), number)
                );
            }
            prefs.alertPhoneNumber().set(number);
        } else {
            if (prefs.alertPhoneNumber().get() != null) {
                Util.logActivities(this,
                        getString(R.string.settings_changed, alertPhoneNumber.getTitle().toString(),
                                prefs.alertPhoneNumber().get(), "")
                );
            }
        }


        if (prefs.messageResultsAPIEnable().get() != enableMessageResultsAPI.isChecked()) {
            boolean checked = enableMessageResultsAPI.isChecked() ? true : false;
            String check = getCheckedStatus(checked);

            String status = getCheckedStatus(prefs.messageResultsAPIEnable().get());

            Util.logActivities(Settings.this, getString(R.string.settings_changed,
                    enableMessageResultsAPI.getTitle().toString(), status,
                    check));
        }
        prefs.messageResultsAPIEnable().set(enableMessageResultsAPI.isChecked());
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
                autoSyncTimes.setEnabled(false);

            } else {
                // stop scheduler
                runServicesUtil.stopAutoSyncService();

                autoSyncTimes.setEnabled(false);
            }
        }

        if (key.equals(AUTO_SYNC_TIMES)) {

            // restart service
            if (prefs.enableAutoSync().get()) {

                runServicesUtil.runAutoSyncService();

            }
        }

       /* if (key.equals(KEY_ENABLE_SMS_PORTALS)) {
            SmsPortal smsPortal = new SmsPortal(getApplicationContext());
            if (sharedPreferences.getBoolean(KEY_ENABLE_SMS_PORTALS, false)) {
                smsPortal.setNumber();
                smsPortal.bindToSmsPortals();
                availableConnections = smsPortal.getMessengers();
            } else {
                smsPortal.unbindFromSmsPortals();
                availableConnections.clear();
            }
        }*/
        // Enable task checking
        if (key.equals(TASK_CHECK)) {

            if (sharedPreferences.getBoolean(TASK_CHECK, false)) {
                autoTaskCheckValidateCallbackURL();

            } else {

                runServicesUtil.stopCheckTaskService();
                taskCheckTimes.setEnabled(false);
                if (enableMessageResultsAPI.isChecked()) {
                    runServicesUtil.stopMessageResultsService();
                    enableMessageResultsAPI.setChecked(false);
                    enableMessageResultsAPI.setEnabled(false);
                }
            }
        }

        // task frequency
        if (key.equals(TASK_CHECK_TIMES)) {

            runServicesUtil.runCheckTaskService();
        }

        // Enable SMS delivery report
        if (key.equals(KEY_ENABLE_SMS_REPORT_DELIVERY)) {
            if(sharedPreferences.getBoolean(KEY_ENABLE_SMS_REPORT_DELIVERY, false)) {
                enableSmsReportDelivery.setChecked(true);
            } else {
                if (!enableSmsReportDelivery.isChecked() && enableMessageResultsAPI.isChecked()) {
                    enableSmsReportDelivery.setChecked(true);
                    Util.showToast(Settings.this, R.string.validate_message_result_api);
                }else {
                    enableSmsReportDelivery.setChecked(false);
                }
            }
        }

        // Enable message result checking
        if (key.equals(MESSAGE_RESULTS_API)) {
            if(!enableSmsReportDelivery.isChecked()) {
                enableMessageResultsAPI.setChecked(false);
                Util.showToast(Settings.this, R.string.validate_sms_delivery_report_status);
            } else {
                if (sharedPreferences.getBoolean(MESSAGE_RESULTS_API, false)) {
                    messageResultsAPIEnable();
                } else {
                    runServicesUtil.stopMessageResultsService();
                }
            }
        }
        this.savePreferences();
    }

    /**
     * Create a child thread and validate the callback URL in it when enabling auto task check
     * preference.
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

    public void messageResultsAPIEnable() {
        Thread t = new Thread() {
            public void run() {
                mHandler.post(mMessageResultsAPIEnabled);
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
