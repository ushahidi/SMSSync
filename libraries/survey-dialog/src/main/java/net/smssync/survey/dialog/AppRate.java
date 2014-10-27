package net.smssync.survey.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import java.util.Date;

public class AppRate {

    private static AppRate singleton;

    private int installDate = 10;

    private int launchTimes = 10;

    private int remindInterval = 1;

    private int eventsTimes = -1;

    private boolean isShowNeutralButton = true;

    private boolean isDebug = false;

    private Context context;

    private View view;

    private OnClickButtonListener listener;

    private AppRate(Context context) {
        this.context = context.getApplicationContext();
    }

    public static AppRate with(Context context) {
        if (singleton == null) {
            synchronized (AppRate.class) {
                if (singleton == null) {
                    singleton = new AppRate(context);
                }
            }
        }
        return singleton;
    }

    public AppRate setLaunchTimes(int launchTimes) {
        this.launchTimes = launchTimes;
        return this;
    }

    public AppRate setInstallDays(int installDate) {
        this.installDate = installDate;
        return this;
    }

    public AppRate setRemindInterval(int remindInterval) {
        this.remindInterval = remindInterval;
        return this;
    }

    public AppRate setShowNeutralButton(boolean isShowNeutralButton) {
        this.isShowNeutralButton = isShowNeutralButton;
        return this;
    }

    public AppRate setEventsTimes(int eventsTimes) {
        this.eventsTimes = eventsTimes;
        return this;
    }

    public AppRate clearAgreeShowDialog() {
        PreferenceHelper.setAgreeShowDialog(context, true);
        return this;
    }

    public AppRate setDebug(boolean isDebug) {
        this.isDebug = isDebug;
        return this;
    }

    public AppRate setView(View view) {
        this.view = view;
        return this;
    }

    public AppRate setOnClickButtonListener(OnClickButtonListener listener) {
        this.listener = listener;
        return this;
    }

    public void monitor() {
        if (PreferenceHelper.isFirstLaunch(context)) {
            PreferenceHelper.setInstallDate(context);
        }
        PreferenceHelper.setLaunchTimes(context, PreferenceHelper.getLaunchTimes(context) + 1);
    }

    public static boolean showRateDialogIfMeetsConditions(Activity activity) {
        boolean isMeetsConditions = singleton.isDebug || singleton.shouldShowRateDialog();
        if (isMeetsConditions) {
            singleton.showRateDialog(activity);
        }
        return isMeetsConditions;
    }

    public static boolean passSignificantEvent(Activity activity) {
        boolean isMeetsConditions = singleton.isDebug || singleton.isOverEventPass();
        if (isMeetsConditions) {
            singleton.showRateDialog(activity);
        } else {
            Context context = activity.getApplicationContext();
            int eventTimes = PreferenceHelper.getEventTimes(context);
            PreferenceHelper.setEventTimes(context, ++eventTimes);
        }
        return isMeetsConditions;
    }

    public void showRateDialog(Activity activity) {
        DialogManager.create(activity, isShowNeutralButton, listener, view).show();
    }

    public boolean isOverEventPass() {
        return eventsTimes != -1 && PreferenceHelper.getEventTimes(context) > eventsTimes;
    }

    public boolean shouldShowRateDialog() {
        return PreferenceHelper.getIsAgreeShowDialog(context) &&
                isOverLaunchTimes() &&
                isOverInstallDate() &&
                isOverRemindDate();
    }

    private boolean isOverLaunchTimes() {
        return PreferenceHelper.getLaunchTimes(context) >= launchTimes;
    }

    private boolean isOverInstallDate() {
        return isOverDate(PreferenceHelper.getInstallDate(context), installDate);
    }

    private boolean isOverRemindDate() {
        return isOverDate(PreferenceHelper.getRemindInterval(context), remindInterval);
    }

    private boolean isOverDate(long targetDate, int threshold) {
        return new Date().getTime() - targetDate >= threshold * 24 * 60 * 60 * 1000;
    }

}