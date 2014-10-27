package org.addhen.smssync;

import android.app.Activity;

/**
 * Analytics tracker
 */
public interface AppTracker {

    public void activityStart(Activity activity);

    public void activityStop(Activity stop);

}
