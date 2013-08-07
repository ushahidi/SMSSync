
package org.addhen.smssync;

import android.content.Context;

/**
 * Sets and returns the sync date of an executed task
 */
public class SyncDate {

    public long getLastSyncedDate(Context context) {
        Prefs.loadPreferences(context);
        return Prefs.lastSyncDate;
    }

    public void setLastSyncedDate(Context context, long lastSyncDate) {
        Prefs.lastSyncDate = lastSyncDate;
        Prefs.savePreferences(context);

    }

}
