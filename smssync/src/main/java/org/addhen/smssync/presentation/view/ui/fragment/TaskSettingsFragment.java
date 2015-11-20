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
import android.support.v7.preference.Preference;
import android.support.v7.preference.SwitchPreferenceCompat;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class TaskSettingsFragment extends BasePreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TASK_SETTINGS_FRAGMENT = "task_settings_fragment";

    public static final String TASK_CHECK = "task_check_preference";

    public static final String TASK_CHECK_TIMES = "task_check_times";

    public static final String MESSAGE_RESULTS_API = "message_results_api_preference";

    private SwitchPreferenceCompat mTaskCheck;

    private TimePreference mTaskCheckTimes;

    private SwitchPreferenceCompat mEnableMessageResultsAPI;

    public TaskSettingsFragment() {
        // Do nothing
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String key) {
        addPreferencesFromResource(R.xml.task_preferences);
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
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
        mTaskCheck = (SwitchPreferenceCompat) getPreferenceScreen().findPreference(TASK_CHECK);
        mTaskCheckTimes = (TimePreference) getPreferenceScreen().findPreference(TASK_CHECK_TIMES);
        mEnableMessageResultsAPI = (SwitchPreferenceCompat) getPreferenceScreen().findPreference(
                MESSAGE_RESULTS_API);
        savePreferences();
    }

    private void savePreferences() {

        if (mPrefs.enableTaskCheck().get() != mTaskCheck.isChecked()) {
            boolean checked = mTaskCheck.isChecked() ? true : false;
            String check = getCheckedStatus(checked);
            String status = getCheckedStatus(mPrefs.enableTaskCheck().get());
            mAddLogPresenter.addLog(getString(R.string.settings_changed,
                    mTaskCheck.getTitle().toString(), status, check));
        }
        mPrefs.enableTaskCheck().set(mTaskCheck.isChecked());

        if (!mPrefs.taskCheckTime().get().equals(mTaskCheckTimes.getTimeValueAsString())) {
            mAddLogPresenter.addLog(getString(R.string.settings_changed,
                    mTaskCheckTimes.getTitle().toString(), mPrefs.taskCheckTime().get(),
                    mTaskCheckTimes.getTimeValueAsString()));
        }
        mPrefs.taskCheckTime().set(mTaskCheckTimes.getTimeValueAsString());

        if (mPrefs.messageResultsAPIEnable().get() != mEnableMessageResultsAPI.isChecked()) {
            boolean checked = mEnableMessageResultsAPI.isChecked() ? true : false;
            String check = getCheckedStatus(checked);

            String status = getCheckedStatus(mPrefs.messageResultsAPIEnable().get());

            mAddLogPresenter.addLog(getString(R.string.settings_changed,
                    mEnableMessageResultsAPI.getTitle().toString(), status, check));
        }
        mPrefs.messageResultsAPIEnable().set(mEnableMessageResultsAPI.isChecked());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        savePreferences();

        if (key.equals(TASK_CHECK)) {

            if (sharedPreferences.getBoolean(TASK_CHECK, false)) {
                enableTaskChecking();

            } else {

                mServiceControl.stopCheckTaskService();
                if (sharedPreferences.getBoolean(MESSAGE_RESULTS_API, false)) {
                    mServiceControl.stopMessageResultsService();
                }
            }
        }

        // task frequency
        if (key.equals(TASK_CHECK_TIMES)) {
            mServiceControl.runCheckTaskService();
        }

        // Enable message result checking
        if (key.equals(MESSAGE_RESULTS_API)) {

            if (sharedPreferences.getBoolean(MESSAGE_RESULTS_API, false)) {
                messageResultsAPIEnable();
            } else {
                mServiceControl.stopMessageResultsService();
            }

        }
    }

    public void messageResultsAPIEnable() {
        if (!mPrefs.serviceEnabled().get()) {
            showError(getString(R.string.no_configured_url));
        } else {
            mServiceControl.runMessageResultsService();
        }
    }

    /**
     * Enable task checking service
     *
     * @return void
     */
    public void enableTaskChecking() {

        if (!mPrefs.serviceEnabled().get()) {
            showError(getString(R.string.no_configured_url));
        } else {
            // start the scheduler for task checking service
            mServiceControl.runCheckTaskService();
        }
    }
}
