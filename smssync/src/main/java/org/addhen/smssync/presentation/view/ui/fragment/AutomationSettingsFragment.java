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

package org.addhen.smssync.presentation.view.ui.fragment;

import org.addhen.smssync.R;
import org.addhen.smssync.presentation.view.ui.widget.TimePreference;
import org.addhen.smssync.presentation.view.ui.widget.TimePreferenceFragmentDialog;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.SwitchPreferenceCompat;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class AutomationSettingsFragment extends BasePreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String AUTOMATION_SETTINGS_FRAG = "automation_settings_fragment";

    public static final String KEY_AUTO_DELETE_MESSAGE = "auto_delete_preference";

    public static final String AUTO_SYNC = "auto_sync_preference";

    public static final String AUTO_SYNC_TIMES = "auto_sync_times";

    public static final String KEY_ENABLE_RETRIES = "auto_delete_pending_messages_preference";

    public static final String KEY_LIST_RETRIES = "auto_delete_pending_messages_retries_preference";

    private SwitchPreferenceCompat mEnableAutoDelete;

    private TimePreference mAutoSyncTimes;

    private ListPreference mRetryEntries;

    private SwitchPreferenceCompat mEnableRetry;

    private SwitchPreferenceCompat mEnableAutoSync;

    public AutomationSettingsFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String key) {
        addPreferencesFromResource(R.xml.automation_preferences);
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference instanceof TimePreference) {
            DialogFragment dialogFragment = TimePreferenceFragmentDialog.newInstance(
                    preference.getKey());
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(this.getFragmentManager(),
                    "android.support.v7.preference.PreferenceFragment.DIALOG");
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);

    }

    private void initialize() {
        mEnableAutoDelete = (SwitchPreferenceCompat) getPreferenceScreen()
                .findPreference(KEY_AUTO_DELETE_MESSAGE);
        mEnableAutoSync = (SwitchPreferenceCompat) getPreferenceScreen().findPreference(
                AUTO_SYNC);
        mAutoSyncTimes = (TimePreference) getPreferenceScreen().findPreference(AUTO_SYNC_TIMES);

        mEnableRetry = (SwitchPreferenceCompat) getPreferenceScreen()
                .findPreference(KEY_ENABLE_RETRIES);

        mRetryEntries = (ListPreference) getPreferenceScreen().findPreference(KEY_LIST_RETRIES);
        savePreference();
    }

    private void savePreference() {
        if (mPrefs.autoDelete().get() != mEnableAutoDelete.isChecked()) {
            boolean checked = mEnableAutoDelete.isChecked() ? true : false;

            String check = getCheckedStatus(checked);

            String status = getCheckedStatus(mPrefs.autoDelete().get());

            mAddLogPresenter.addLog(getString(R.string.settings_changed,
                    mEnableAutoDelete.getTitle().toString(), status, check));
        }
        mPrefs.autoDelete().set(mEnableAutoDelete.isChecked());

        if (mPrefs.enableAutoSync().get() != mEnableAutoSync.isChecked()) {
            boolean checked = mEnableAutoSync.isChecked() ? true : false;
            String check = getCheckedStatus(checked);
            String status = getCheckedStatus(mPrefs.enableAutoSync().get());
            mAddLogPresenter.addLog(getString(R.string.settings_changed,
                    mEnableAutoSync.getTitle().toString(), status, check));
        }
        mPrefs.enableAutoSync().set(mEnableAutoSync.isChecked());

        if (!mPrefs.autoTime().get().equals(mAutoSyncTimes.getTimeValueAsString())) {
            mAddLogPresenter.addLog(getString(R.string.settings_changed,
                    mAutoSyncTimes.getTitle().toString(), mPrefs.autoTime().get(),
                    mAutoSyncTimes.getTimeValueAsString()));
        }
        mPrefs.autoTime().set(mAutoSyncTimes.getTimeValueAsString());

        // Enable or Disable Pending messages delete retries.
        if (mPrefs.enableRetry().get() != mEnableRetry.isChecked()) {
            boolean checked = mEnableRetry.isChecked() ? true : false;
            String check = getCheckedStatus(checked);
            String status = getCheckedStatus(mPrefs.enableRetry().get());
            mAddLogPresenter.addLog(getString(R.string.settings_changed,
                    mEnableRetry.getTitle().toString(), status, check));
        }
        mPrefs.enableRetry().set(mEnableRetry.isChecked());
        for (int i = 0; i < mRetryEntries.getEntryValues().length; i++) {
            if (mRetryEntries.getEntry() != null) {
                if (mRetryEntries.getValue()
                        .matches(getResources().getStringArray(R.array.retry_entries)[i])) {
                    mPrefs.retries().set(getResources().getIntArray(R.array.retry_values)[i]);
                    break;
                }
            }
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
        savePreference();
        // Auto sync enable
        if (key.equals(AUTO_SYNC)) {
            if (sharedPreferences.getBoolean(AUTO_SYNC, false)) {
                autoSyncEnable();
            } else {
                // stop scheduler
                mServiceControl.stopAutoSyncService();
            }
        }
        if (key.equals(AUTO_SYNC_TIMES)) {

            // restart service
            if (mPrefs.enableAutoSync().get()) {
                mServiceControl.runAutoSyncService();
            }
        }
    }

    public void autoSyncEnable() {
        if (!mPrefs.serviceEnabled().get()) {
            showError(getString(R.string.no_configured_url));
            mEnableAutoSync.setChecked(false);
            return;
        }
        mEnableAutoSync.setChecked(true);
        mServiceControl.runAutoSyncService();
    }
}
