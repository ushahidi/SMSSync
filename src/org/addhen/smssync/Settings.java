/** 
 ** Copyright (c) 2010 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 ** 
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.	
 **	
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 ** 
 **/

package org.addhen.smssync;

import org.addhen.smssync.receivers.AutoSyncScheduledReceiver;
import org.addhen.smssync.receivers.CheckTaskScheduledReceiver;
import org.addhen.smssync.receivers.SmsReceiver;
import org.addhen.smssync.services.AutoSyncScheduledService;
import org.addhen.smssync.services.CheckTaskScheduledService;
import org.addhen.smssync.services.CheckTaskService;
import org.addhen.smssync.services.ScheduleServices;
import org.addhen.smssync.util.ServicesConstants;
import org.addhen.smssync.util.Util;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

/**
 * This class handles all related task for settings on SMSSync.
 * 
 * @author eyedol
 */
public class Settings extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    public static final String KEY_WEBSITE_PREF = "website_preference";

    public static final String KEY_KEYWORD_PREF = "keyword_preference";

    public static final String KEY_ENABLE_SMS_SYNC_PREF = "enable_sms_sync_preference";

    public static final String KEY_ENABLE_MMS_SYNC_PREF = "enable_mms_sync_preference";

    public static final String KEY_ENABLE_GPS_SYNC_PREF = "enable_gps_sync_preference";

    public static final String KEY_API_KEY_PREF = "api_key_preference";

    public static final String KEY_POWERED_PREFERENCE = "powered_preference";

    public static final String KEY_AUTO_DELETE_MESSAGE = "auto_delete_preference";

    public static final String KEY_ENABLE_REPLY = "enable_reply_preference";

    public static final String KEY_ENABLE_REPLY_FRM_SERVER = "enable_reply_frm_server_preference";

    public static final String KEY_REPLY = "reply_preference";

    public static final String PREFS_NAME = "SMS_SYNC_PREF";

    public static final String HTTP_TEXT = "http://";

    public static final String AUTO_SYNC = "auto_sync_preference";

    public static final String AUTO_SYNC_TIMES = "auto_sync_times";

    public static final String TASK_CHECK = "task_check_preference";

    public static final String TASK_CHECK_TIMES = "task_check_times";
    
    public static final String ABOUT = "powered_preference";

    private EditTextPreference websitePref;

    private EditTextPreference apiKeyPref;

    private EditTextPreference keywordPref;

    private EditTextPreference replyPref;

    private CheckBoxPreference enableReplyFrmServer;

    private CheckBoxPreference enableSmsSync;

    private CheckBoxPreference enableAutoDelete;

    private CheckBoxPreference enableReply;

    private CheckBoxPreference autoSync;

    private CheckBoxPreference taskCheck;

    private ListPreference autoSyncTimes;

    private ListPreference taskCheckTimes;
    
    private Preference about;

    private SharedPreferences settings;

    private SharedPreferences.Editor editor;

    private static final String URL = "http://smssync.ushahidi.com";

    private CharSequence[] autoSyncEntries = {
            "5 Minutes", "10 Minutes", "15 Minutes", "30 Minutes", "60 Minutes"
    };

    private CharSequence[] autoSyncValues = {
            "5", "10", "15", "30", "60"
    };

    private int autoTime = 5;

    private int taskCheckTime = 5;

    private int callbackUrlValidityStatus = 1;

    private final Handler mHandler = new Handler();

    private PackageManager pm;

    private ComponentName cn;
    
    private String versionName;
    
    private StringBuilder versionLabel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        try {
            versionName = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            //add app name to verstion number
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
        websitePref = (EditTextPreference)getPreferenceScreen().findPreference(KEY_WEBSITE_PREF);

        apiKeyPref = (EditTextPreference)getPreferenceScreen().findPreference(KEY_API_KEY_PREF);

        keywordPref = (EditTextPreference)getPreferenceScreen().findPreference(KEY_KEYWORD_PREF);

        enableSmsSync = (CheckBoxPreference)getPreferenceScreen().findPreference(
                KEY_ENABLE_SMS_SYNC_PREF);

        enableAutoDelete = (CheckBoxPreference)getPreferenceScreen().findPreference(
                KEY_AUTO_DELETE_MESSAGE);

        enableReply = (CheckBoxPreference)getPreferenceScreen().findPreference(KEY_ENABLE_REPLY);
        enableReplyFrmServer = (CheckBoxPreference)getPreferenceScreen().findPreference(
                KEY_ENABLE_REPLY_FRM_SERVER);

        autoSync = (CheckBoxPreference)getPreferenceScreen().findPreference(AUTO_SYNC);

        taskCheck = (CheckBoxPreference)getPreferenceScreen().findPreference(TASK_CHECK);

        replyPref = (EditTextPreference)getPreferenceScreen().findPreference(KEY_REPLY);

        autoSyncTimes = (ListPreference)getPreferenceScreen().findPreference(AUTO_SYNC_TIMES);
        autoSyncTimes.setEntries(autoSyncEntries);
        autoSyncTimes.setEntryValues(autoSyncValues);

        taskCheckTimes = (ListPreference)getPreferenceScreen().findPreference(TASK_CHECK_TIMES);
        taskCheckTimes.setEntries(autoSyncEntries);
        taskCheckTimes.setEntryValues(autoSyncValues);
        
        about = (Preference)getPreferenceScreen().findPreference(ABOUT);
        
        about.setTitle(versionLabel.toString());
        about.setSummary(R.string.powered_by);
        pm = getPackageManager();
        cn = new ComponentName(Settings.this, SmsReceiver.class);

        // When the about us item is clicked at the Settings screen, open a URL
        Preference poweredPreference = findPreference(KEY_POWERED_PREFERENCE);
        poweredPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                final Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(URL));
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
        if (autoSyncTimes.getValue().matches("10")) {
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

        // "5 Minutes", "10 Minutes", "15 Minutes", "30", "60 Minutes"
        if (autoSyncTimes.getValue().matches("10")) {
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
     * Save settings changes.
     * 
     * @return void
     */
    protected void savePreferences() {

        settings = getSharedPreferences(PREFS_NAME, 0);

        if (websitePref.getText().equals("")) {
            websitePref.setText(HTTP_TEXT);
        }

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
        
        if( enableReplyFrmServer.isChecked()) {
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

        // Initialize the selected time to frequently to auto check for tasks
        taskCheckTime = initializeAutoTaskTime();

        editor = settings.edit();
        editor.putString("WebsitePref", websitePref.getText());
        editor.putString("ApiKey", apiKeyPref.getText());
        editor.putString("Keyword", keywordPref.getText());
        editor.putString("ReplyPref", replyPref.getText());
        editor.putBoolean("EnableSmsSync", enableSmsSync.isChecked());
        editor.putBoolean("EnableAutoDelete", enableAutoDelete.isChecked());
        editor.putBoolean("EnableReply", enableReply.isChecked());
        editor.putBoolean("EnableReplyFrmServer",enableReplyFrmServer.isChecked());
        editor.putBoolean("AutoSync", autoSync.isChecked());
        editor.putInt("AutoTime", autoTime);
        editor.putInt("taskCheck", taskCheckTime);
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(
                this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    /**
     * Perform sanity checks on settings changes.
     * 
     * @param SharedPreferences sharedPreferences -
     * @return void
     */
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(KEY_ENABLE_SMS_SYNC_PREF)) {

            if (sharedPreferences.getBoolean(KEY_ENABLE_SMS_SYNC_PREF, false)) {
                smssyncEnableCallbackUrlValidate(sharedPreferences.getString(KEY_WEBSITE_PREF, ""));

            } else {
                pm.setComponentEnabledSetting(cn, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);

                Util.clearNotify(this);
            }
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

                // Initialize the selected time to frequently sync pending
                // messages
                Prefrences.autoTime = initializeAutoSyncTime();
                autoSyncTimes.setEnabled(true);
                // start the scheduler for 'task check' service
                long interval = (Prefrences.autoTime * 60000);
                new ScheduleServices(this,
                        new Intent(Settings.this, AutoSyncScheduledService.class),
                        AutoSyncScheduledReceiver.class, interval,
                        ServicesConstants.CHECK_TASK_SCHEDULED_SERVICE_REQUEST_CODE, 0);

            } else {

                // Initialize the selected time to frequently to auto check for
                // tasks
                Prefrences.taskCheckTime = initializeAutoTaskTime();
                stopService(new Intent(Settings.this, AutoSyncScheduledService.class));

                // start the scheduler for 'task check' service
                long interval = (Prefrences.taskCheckTime * 60000);
                new ScheduleServices(this, new Intent(Settings.this,
                        CheckTaskScheduledService.class), CheckTaskScheduledReceiver.class,
                        interval, ServicesConstants.CHECK_TASK_SCHEDULED_SERVICE_REQUEST_CODE, 0);

                autoSyncTimes.setEnabled(false);
            }
        }

        // Enable task checking
        if (key.equals(TASK_CHECK)) {

            if (sharedPreferences.getBoolean(TASK_CHECK, false)) {
                autoTaskCheckValidateCallbackURL(sharedPreferences.getString(KEY_WEBSITE_PREF, ""));

            } else {

                stopService(new Intent(Settings.this, CheckTaskService.class));
                taskCheckTimes.setEnabled(false);
            }
        }

        if (key.equals(AUTO_SYNC_TIMES)) {

            // restart service
            if (Prefrences.enableAutoSync) {

                // Initialize the selected time to frequently sync pending
                // messages
                Prefrences.autoTime = initializeAutoSyncTime();
                stopService(new Intent(Settings.this, AutoSyncScheduledService.class));

                // start the scheduler for 'task check' service
                long interval = (Prefrences.autoTime * 60000);
                new ScheduleServices(this,
                        new Intent(Settings.this, AutoSyncScheduledService.class),
                        AutoSyncScheduledReceiver.class, interval,
                        ServicesConstants.CHECK_TASK_SCHEDULED_SERVICE_REQUEST_CODE, 0);
            }
        }

        if (key.equals(TASK_CHECK_TIMES)) {

            Prefrences.taskCheckTime = initializeAutoTaskTime();
            stopService(new Intent(Settings.this, CheckTaskScheduledService.class));

            // start the scheduler for 'task check' service
            long interval = (Prefrences.taskCheckTime * 60000);
            new ScheduleServices(this, new Intent(Settings.this, CheckTaskScheduledService.class),
                    CheckTaskScheduledReceiver.class, interval,
                    ServicesConstants.CHECK_TASK_SCHEDULED_SERVICE_REQUEST_CODE, 0);
        }

        if (key.equals(KEY_WEBSITE_PREF)) {
            Util.validateCallbackUrl(sharedPreferences.getString(KEY_WEBSITE_PREF, ""));
        }

        this.savePreferences();
    }

    /**
     * Create runnable for validating callback URL. Putting the validation
     * process in it own thread provides efficiency.
     */
    final Runnable mTaskCheckEnabled = new Runnable() {

        public void run() {

            if (callbackUrlValidityStatus == 1) {

                Util.showToast(Settings.this, R.string.no_configured_url);
                taskCheck.setChecked(false);

            } else if (callbackUrlValidityStatus == 2) {

                Util.showToast(Settings.this, R.string.invalid_url);
                taskCheck.setChecked(false);

            } else if (callbackUrlValidityStatus == 3) {

                Util.showToast(Settings.this, R.string.no_connection);
                taskCheck.setChecked(false);

            } else {

                taskCheck.setChecked(true);
                startService(new Intent(Settings.this, CheckTaskScheduledService.class));

            }
        }
    };

    /**
     * Create a child thread and validate the callback URL in it when enabling
     * auto task check preference.
     * 
     * @param String Url - The Callback URL to be validated.
     * @return void
     */
    public void autoTaskCheckValidateCallbackURL(final String Url) {

        Thread t = new Thread() {
            public void run() {

                callbackUrlValidityStatus = Util.validateCallbackUrl(Url);
                mHandler.post(mTaskCheckEnabled);
            }
        };
        t.start();
    }

    /**
     * Create runnable for validating callback URL.
     */
    Runnable mSmssyncEnabled = new Runnable() {
        public void run() {

            if (callbackUrlValidityStatus == 1) {

                Util.showToast(Settings.this, R.string.no_configured_url);
                enableSmsSync.setChecked(false);

            } else if (callbackUrlValidityStatus == 2) {

                Util.showToast(Settings.this, R.string.invalid_url);
                enableSmsSync.setChecked(false);

            } else if (callbackUrlValidityStatus == 3) {

                Util.showToast(Settings.this, R.string.no_connection);
                enableSmsSync.setChecked(false);

            } else {

                // Enable background service.
                pm.setComponentEnabledSetting(cn, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
                        PackageManager.DONT_KILL_APP);

                // show notification
                Util.showNotification(Settings.this);
                enableSmsSync.setChecked(true);
            }
        }
    };

    /**
     * Create a child thread and validate the callback URL in it when enabling
     * SMSSync.
     * 
     * @param String Url - The Callback Url to be validated.
     * @return void
     */
    public void smssyncEnableCallbackUrlValidate(final String Url) {

        Thread t = new Thread() {
            public void run() {

                callbackUrlValidityStatus = Util.validateCallbackUrl(Url);
                mHandler.post(mSmssyncEnabled);
            }
        };
        t.start();
    }
}
