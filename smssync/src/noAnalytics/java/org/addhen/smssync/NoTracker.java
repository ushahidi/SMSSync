package org.addhen.smssync;

import android.app.Activity;
import android.content.Context;

/**
 * No tracker
 */
public class NoTracker implements AppTracker {

    @Override
    public void setContext(Context context) {

    }

    @Override
    public void activityStart(Activity activity) {

    }

    @Override
    public void activityStop(Activity stop) {

    }
}
