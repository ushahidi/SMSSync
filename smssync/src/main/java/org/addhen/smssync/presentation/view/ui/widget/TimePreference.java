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


import org.addhen.smssync.data.PrefsFactory;
import org.addhen.smssync.data.util.Logger;
import org.addhen.smssync.presentation.App;
import org.addhen.smssync.presentation.util.TimeFrequencyUtil;
import org.addhen.smssync.presentation.view.ui.fragment.AutomationSettingsFragment;
import org.addhen.smssync.presentation.view.ui.fragment.GeneralSettingsFragment;
import org.addhen.smssync.presentation.view.ui.fragment.TaskSettingsFragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;
import android.widget.TimePicker;

/**
 * Created by Kamil Kalfas(kkalfas@soldevelo.com) on 19.05.14.
 * <p/>
 * Fields and methods are inherited from DialogPreference and TimePicker so DO NOT BE MISLED by
 * those names
 */
public class TimePreference extends DialogPreference {

    //fist picker field
    private int lastHour = 0;

    //second picker field
    private int lastMinute = 0;

    private TimePicker picker = null;

    private PrefsFactory prefs;

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        prefs = App.getAppComponent().prefsFactory();
        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }

    private static int getHour(String time) {
        String[] pieces = time.split(":");
        return (Integer.parseInt(pieces[0]));
    }

    private static int getMinute(String time) {
        String[] pieces = time.split(":");
        return (Integer.parseInt(pieces[1]));
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time;

        if (restoreValue) {
            if (defaultValue == null) {
                time = getPersistedString(loadTimeFrequency());
            } else {
                time = getPersistedString(defaultValue.toString());
            }
        } else {
            time = defaultValue.toString();
        }

        //This is needed for backward compatible with versions pre 2.6
        //It adjusts format of input string from "mm" to "hh:mm"
        if (!time.contains(":")) {
            int minutes = Integer.parseInt(time);
            time = Integer.toString((minutes / 60)) + ":" + Integer.toString((minutes % 60));
        }

        lastHour = getHour(time);
        lastMinute = getMinute(time);

    }

    public String getTimeValueAsString() {

        String h = String.valueOf(lastHour);
        String m = String.valueOf(lastMinute);

        String time = appendZeroAtBegin(h) + ":" + appendZeroAtBegin(m);
        return time;
    }

    private String appendZeroAtBegin(String time) {
        StringBuilder sb = new StringBuilder();
        if (time.length() == 1) {
            sb.append(0);
        }
        return sb.append(time).toString();
    }

    public void persistStringValue(String value) {
        persistString(value);
    }

    public void saveTimeFrequency() {
        if (TaskSettingsFragment.TASK_CHECK_TIMES.equals(this.getKey())) {
            prefs.taskCheckTime().set(getTimeValueAsString());
        } else if (AutomationSettingsFragment.AUTO_SYNC_TIMES.equals(this.getKey())) {
            prefs.autoTime().set(getTimeValueAsString());
        }
    }

    private String loadTimeFrequency() {
        String time = null;
        if (TaskSettingsFragment.TASK_CHECK_TIMES.equals(this.getKey())) {
            time = prefs.taskCheckTime().get();
        } else if (AutomationSettingsFragment.AUTO_SYNC_TIMES.equals(this.getKey())) {
            time = prefs.autoTime().get();
        } else {
            time = TimeFrequencyUtil.DEFAULT_TIME_FREQUENCY;
        }

        Logger.log("TimePreferences", "Save time " + time);
        return time;
    }
}
