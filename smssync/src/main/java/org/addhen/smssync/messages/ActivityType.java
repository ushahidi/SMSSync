package org.addhen.smssync.messages;

import org.addhen.smssync.R;

/**
 * The activity type. Pending / Task.
 */
public enum ActivityType {

    PENDING(R.string.pending_messages),
    TASK(R.string.task);

    public final int resId;

    ActivityType(int resId) {
        this.resId = resId;
    }
}
