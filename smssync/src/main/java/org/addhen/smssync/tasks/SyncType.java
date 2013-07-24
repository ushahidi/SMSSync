
package org.addhen.smssync.tasks;

import org.addhen.smssync.R;

import android.content.Intent;

/**
 * Determine which sync type to execute
 * 
 */
public enum SyncType {
    UNKNOWN(R.string.unknown),
    MANUAL(R.string.manual);

    public final int resId;

    SyncType(int resId) {
        this.resId = resId;
    }

    public static final String EXTRA = "org.addhen.smssync.SyncTypeAsString";

    public static SyncType fromIntent(Intent intent) {
        if (intent.hasExtra(EXTRA)) {
            final String name = intent.getStringExtra(EXTRA);
            for (SyncType type : values()) {
                if (type.name().equals(name)) {
                    return type;
                }
            }
        }
        return UNKNOWN;

    }

    public boolean isBackground() {
        return this != MANUAL;
    }
}
