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
import org.addhen.smssync.presentation.util.Utility;
import org.addhen.smssync.presentation.view.log.AddLogView;
import org.addhen.smssync.presentation.view.ui.widget.TimePreference;
import org.addhen.smssync.presentation.view.ui.widget.TimePreferenceFragmentDialog;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Fragments for showing general settings
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class GeneralSettingsFragment extends BasePreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener, AddLogView {

    public static final String GENERAL_SETTINGS_FRAGMENT_TAG = "general_settings_fragment";

    public static final String KEY_UNIQUE_ID = "unique_id_preference";

    public static final String KEY_UNIQUE_NAME = "unique_name_preference";

    public static final String KEY_ENABLE_SMS_REPORT_DELIVERY
            = "enable_sms_report_delivery_preference";

    public static final String KEY_ALERT_PHONE_NUMBER = "alert_phone_number_preference";

    private SwitchPreferenceCompat mTaskCheck;

    private TimePreference mTaskCheckTimes;

    private ListPreference mTaskFrequencyList;

    private SwitchPreferenceCompat mEnableMessageResultsAPI;

    private EditTextPreference uniqueId;

    private EditTextPreference uniqueName;

    private EditTextPreference alertPhoneNumber;

    private SwitchPreferenceCompat enableSmsReportDelivery;

    public GeneralSettingsFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String key) {
        addPreferencesFromResource(R.xml.general_preferences);
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
        uniqueId = (EditTextPreference) getPreferenceScreen().findPreference(KEY_UNIQUE_ID);
        uniqueName = (EditTextPreference) getPreferenceScreen().findPreference(KEY_UNIQUE_NAME);
        alertPhoneNumber = (EditTextPreference) getPreferenceScreen().findPreference(
                KEY_ALERT_PHONE_NUMBER);
        enableSmsReportDelivery = (SwitchPreferenceCompat) getPreferenceScreen()
                .findPreference(KEY_ENABLE_SMS_REPORT_DELIVERY);
        savePreferences();
    }

    private void savePreferences() {
        if (!TextUtils.isEmpty(uniqueId.getText())) {
            String id = Utility.removeWhitespaces(uniqueId.getText());
            if (!mPrefs.uniqueId().get().equals(uniqueId.getText())) {
                mAddLogPresenter.addLog(getString(R.string.settings_changed, uniqueId.getTitle(),
                        mPrefs.uniqueId().get(), id));
            }
            mPrefs.uniqueId().set(id);
        } else {

            if (!mPrefs.uniqueId().get().equals("")) {
                mAddLogPresenter.addLog(getString(R.string.settings_changed, uniqueId.getTitle(),
                        mPrefs.uniqueId().get(), ""));
            }
            mPrefs.uniqueId().set("");
        }
        if (!TextUtils.isEmpty(uniqueName.getText())) {
            String name = Utility.removeWhitespaces(uniqueName.getText());
            if (!mPrefs.uniqueName().get().equals(uniqueName.getText())) {
                mAddLogPresenter.addLog(getString(R.string.settings_changed, uniqueName.getTitle(),
                        mPrefs.uniqueName().get(), name));
            }
            mPrefs.uniqueName().set(name);
        } else {

            if (!mPrefs.uniqueName().get().equals("")) {
                mAddLogPresenter.addLog(getString(R.string.settings_changed, uniqueName.getTitle(),
                        mPrefs.uniqueName().get(), ""));
            }
            mPrefs.uniqueName().set("");
        }
        if (!TextUtils.isEmpty(alertPhoneNumber.getText())) {
            String number = Utility.removeWhitespaces(alertPhoneNumber.getText());
            if (!mPrefs.alertPhoneNumber().get().equals(alertPhoneNumber.getText())) {
                mAddLogPresenter.addLog(
                        getString(R.string.settings_changed, alertPhoneNumber.getTitle().toString(),
                                mPrefs.alertPhoneNumber().get(), number));
            }
            mPrefs.alertPhoneNumber().set(number);
        } else {
            if (!mPrefs.alertPhoneNumber().get().equals("")) {
                mAddLogPresenter.addLog(getString(R.string.settings_changed,
                        alertPhoneNumber.getTitle().toString(),
                        mPrefs.alertPhoneNumber().get(), ""));
            }
            mPrefs.alertPhoneNumber().set("");
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        savePreferences();
        // Enable SMS delivery report
        if (key.equals(KEY_ENABLE_SMS_REPORT_DELIVERY)) {
            if (sharedPreferences.getBoolean(KEY_ENABLE_SMS_REPORT_DELIVERY, false)) {
                enableSmsReportDelivery.setChecked(true);
            } else {
                if (!enableSmsReportDelivery.isChecked()) {
                    Toast.makeText(getAppContext(),
                            R.string.validate_message_result_api, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
