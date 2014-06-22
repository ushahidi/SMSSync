package org.addhen.smssync.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import org.addhen.smssync.Prefs;
import org.addhen.smssync.Settings;

/**
 * Created by Kamil Kalfas(kkalfas@soldevelo.com) on 19.05.14.
 *
 * Fields and methods are inherited from DialogPreference and TimePicker
 * so DO NOT BE MISLED by those names
 */
public class TimePreference extends DialogPreference {
    //fist picker field
    private int lastHour = 0;
    //second picker field
    private int lastMinute = 0;
    private TimePicker picker = null;

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());

        return (picker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        picker.setIs24HourView(true);
        picker.setCurrentHour(lastHour);
        picker.setCurrentMinute(lastMinute);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            lastHour = picker.getCurrentHour();
            lastMinute = picker.getCurrentMinute();
            if (callChangeListener( getTimeValueAsString())) {
                persistString(getTimeValueAsString());
                saveTimeFrequency(this.getContext());
            }
        }
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time;

        if (restoreValue) {
            if (defaultValue==null) {
                time=getPersistedString(loadTimeFrequency(this.getContext()));
            }
            else {
                time=getPersistedString(defaultValue.toString());
            }
        }
        else {
            time=defaultValue.toString();
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

    private static int getHour(String time) {
        String[] pieces = time.split(":");
        return (Integer.parseInt(pieces[0]));
    }

    private static int getMinute(String time) {
        String[] pieces = time.split(":");
        return (Integer.parseInt(pieces[1]));
    }

    private void saveTimeFrequency(Context context) {
        if (Settings.TASK_CHECK_TIMES.equals(this.getKey())) {
            Prefs.taskCheckTime = getTimeValueAsString();
        } else if (Settings.AUTO_SYNC_TIMES.equals(this.getKey())) {
            Prefs.autoTime = getTimeValueAsString();
        }
        Prefs.savePreferences(context);
    }

    private String loadTimeFrequency(Context context) {
        String time;
        if (Settings.TASK_CHECK_TIMES.equals(this.getKey())) {
            Prefs.savePreferences(context);
            time = Prefs.taskCheckTime;
        } else if (Settings.AUTO_SYNC_TIMES.equals(this.getKey())) {
            Prefs.savePreferences(context);
            time = Prefs.autoTime;
        } else {
            time = TimeFrequencyUtil.DEFAULT_TIME_FREQUENCY;
        }

        Logger.log("TimePreferences", "Save time "+ time );
        return time;
    }

}