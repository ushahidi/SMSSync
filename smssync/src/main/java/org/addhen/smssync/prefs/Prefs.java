package org.addhen.smssync.prefs;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class Prefs {

    private static final String PREF_NAME = "SMS_SYNC_PREF";

    private SharedPreferences sharedPreferences;

    public Prefs(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(
                PREF_NAME, 0);
    }
}
