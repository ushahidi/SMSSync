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

package org.addhen.smssync.presentation.view.ui.activity;


import org.addhen.smssync.R;
import org.addhen.smssync.data.PrefsFactory;
import org.addhen.smssync.presentation.presenter.AddLogPresenter;
import org.addhen.smssync.presentation.view.log.AddLogView;
import org.addhen.smssync.presentation.view.ui.widget.TimePreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.support.v7.preference.SwitchPreferenceCompat;

/**
 * Settings activity related to tasks
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class TaskSettingsActivity extends BasePreferenceActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener, AddLogView {

    public static final String TASK_CHECK = "task_check_preference";

    public static final String TASK_CHECK_TIMES = "task_check_times";

    public static final String TASK_FREQUENCY_LIST = "task_frequency_list_preference";

    public static final String MESSAGE_RESULTS_API = "message_results_api_preference";

    private SwitchPreferenceCompat mTaskCheck;

    private TimePreference mTaskCheckTimes;

    private ListPreference mTaskFrequencyList;

    private SwitchPreferenceCompat mEnableMessageResultsAPI;

    private PrefsFactory mPrefs;

    private AddLogPresenter mAddLogPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.task_preferences);
        setToolbarTitle(R.string.task);
        mPrefs = getAppComponent().prefsFactory();
        mAddLogPresenter = mSettingsComponent.addLogPresenter();

        //mTaskCheck = (SwitchPreferenceCompat) getPreferenceScreen().findPreference(TASK_CHECK);
        // mEnableMessageResultsAPI = (SwitchPreferenceCompat) getPreferenceScreen().findPreference(
        //       MESSAGE_RESULTS_API);
        //mTaskCheckTimes = (TimePreference) getPreferenceScreen().findPreference(TASK_CHECK_TIMES);
        mTaskFrequencyList = (ListPreference) getPreferenceScreen().findPreference(
                TASK_FREQUENCY_LIST);
        savePreferences();
    }

    private void savePreferences() {
        mAddLogPresenter.setView(this);

        // Enable task checking
        if (mPrefs.enableTaskCheck().get() != mTaskCheck.isChecked()) {
            boolean checked = mTaskCheck.isChecked() ? true : false;
            String check = getCheckedStatus(checked);
            String status = getCheckedStatus(mPrefs.enableTaskCheck().get());
            // Log the changes to the logger
            mAddLogPresenter.addLog(getString(R.string.settings_changed,
                    mTaskCheck.getTitle().toString(), status,
                    check));
        }
        mPrefs.enableTaskCheck().set(mTaskCheck.isChecked());

        // Message results
        if (mPrefs.messageResultsAPIEnable().get() != mEnableMessageResultsAPI.isChecked()) {
            boolean checked = mEnableMessageResultsAPI.isChecked() ? true : false;
            String check = getCheckedStatus(checked);

            String status = getCheckedStatus(mPrefs.messageResultsAPIEnable().get());
            mAddLogPresenter.addLog(getString(R.string.settings_changed,
                    mEnableMessageResultsAPI.getTitle().toString(), status,
                    check));
        }
        mPrefs.messageResultsAPIEnable().set(mEnableMessageResultsAPI.isChecked());
    }

    @Override
    public void onAdded(Long row) {
        // Do nothing
    }

    @Override
    public void showError(String s) {
        // Do nothing
    }

    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        savePreferences();

        if (key.equals(TASK_CHECK)) {
            if (sharedPreferences.getBoolean(TASK_CHECK, false)) {
                enableTaskChecking();
            } else {
                // Todo implement a utility for starting and stopping task service
                mTaskCheckTimes.setEnabled(false);
                if (mEnableMessageResultsAPI.isChecked()) {
                    // Todo stop message result service; Use a utility for this
                    mEnableMessageResultsAPI.setChecked(false);
                    mEnableMessageResultsAPI.setEnabled(false);
                }
            }
        }

        // Task frequency
        if (key.equals(TASK_CHECK_TIMES)) {
            // Todo implement a utility for starting and stopping task service
        }

        // Enable message result checking
        if (key.equals(MESSAGE_RESULTS_API)) {
            if (sharedPreferences.getBoolean(MESSAGE_RESULTS_API, false)) {
                messageResultsAPIEnable();
            } else {
                // Todo stop message result service; Use a utlity for this
            }
        }
    }

    /**
     * Enable task checking service
     */
    private void enableTaskChecking() {

        if (!mPrefs.serviceEnabled().get()) {
            showToast(R.string.no_configured_url);
            mTaskCheck.setChecked(false);
            if (mEnableMessageResultsAPI.isChecked()) {
                mEnableMessageResultsAPI.setChecked(false);
            }
        } else {

            mTaskCheck.setChecked(true);
            // Start the scheduler for task checking service
            // Todo implement a utility for starting and stopping task service
        }
    }

    private void messageResultsAPIEnable() {
        if (!mPrefs.serviceEnabled().get()) {
            showToast(R.string.no_configured_url);
            mEnableMessageResultsAPI.setChecked(false);
        } else {
            mEnableMessageResultsAPI.setChecked(true);
            // Todo implement a utility for starting and stopping message results service
        }
    }
}
