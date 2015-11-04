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

package org.addhen.smssync.presentation.view.ui.widget;

import org.addhen.smssync.R;
import org.addhen.smssync.data.PrefsFactory;
import org.addhen.smssync.presentation.App;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.DialogPreference.TargetFragment;
import android.support.v7.preference.ListPreferenceDialogFragmentCompat;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.View;
import android.widget.TimePicker;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class TimePreferenceFragmentDialog extends PreferenceDialogFragmentCompat implements
        TargetFragment {

    public static final String DIALOG_FRAGMENT_TAG
            = "android.support.v7.preference.PreferenceFragment.DIALOG";

    // Fist picker field
    private int lastHour = 0;

    // Second picker field
    private int lastMinute = 0;

    private TimePicker picker = null;

    private PrefsFactory prefs;

    private ListPreferenceDialogFragmentCompat mListPreferenceDialogFragmentCompat;

    public TimePreferenceFragmentDialog() {

    }

    public static TimePreferenceFragmentDialog newInstance(String key) {
        TimePreferenceFragmentDialog fragment = new TimePreferenceFragmentDialog();
        Bundle bundle = new Bundle(1);
        bundle.putString("key", key);
        fragment.setArguments(bundle);
        return fragment;
    }

    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
        TimePreference preference = this.getTimePreference();
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            saveSelectedTime(preference);
            dialog.dismiss();
        });
    }


    @Override
    protected View onCreateDialogView(Context context) {
        prefs = App.getAppComponent().prefsFactory();
        picker = new TimePicker(context);
        return (picker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        picker.setIs24HourView(true);
        TimePreference timePreference = this.getTimePreference();
        picker.setCurrentHour(timePreference.getLastHour());
        picker.setCurrentMinute(timePreference.getLastMinute());
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        TimePreference timePreference = this.getTimePreference();
        if (positiveResult) {
            saveSelectedTime(timePreference);
        }
    }

    private void saveSelectedTime(TimePreference timePreference) {
        timePreference.setLastHour(picker.getCurrentHour());
        timePreference.setLastMinute(picker.getCurrentMinute());
        if (timePreference.callChangeListener(timePreference.getTimeValueAsString())) {
            timePreference.persistStringValue(timePreference.getTimeValueAsString());
            timePreference.saveTimeFrequency();
        }
    }

    private TimePreference getTimePreference() {
        return (TimePreference) this.getPreference();
    }

    @Override
    public Preference findPreference(CharSequence charSequence) {
        return getTimePreference();
    }
}
