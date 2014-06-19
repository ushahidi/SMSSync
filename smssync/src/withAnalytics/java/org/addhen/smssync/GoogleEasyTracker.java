package org.addhen.smssync;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.content.Context;

/**
 * Google analytics
 */
public class GoogleEasyTracker implements AppTracker {

    @Override
    public void activityStart(Activity activity) {
        EasyTracker.getInstance(activity).activityStart(activity);
    }

    @Override
    public void activityStop(Activity activity) {
        EasyTracker.getInstance(activity).activityStop(activity);
    }
}
