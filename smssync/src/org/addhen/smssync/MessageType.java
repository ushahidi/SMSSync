
package org.addhen.smssync;

import android.content.Context;

public enum MessageType {
    SMS("sms"),
    PENDING("pending"),
    TASK("task");

    public final String type;

    private MessageType(String type) {
        this.type = type;
    }

    public long getLastSyncedDate(Context context) {
        Prefs.loadPreferences(context);
        return Prefs.lastSyncDate;
    }

    public void setLastSyncedDate(Context context, long lastSyncDate) {
        Prefs.lastSyncDate = lastSyncDate;
        Prefs.savePreferences(context);

    }

}
