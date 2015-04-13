/*
 * Copyright (c) 2010 - 2015 Ushahidi Inc
 * All rights reserved
 * Contact: team@ushahidi.com
 * Website: http://www.ushahidi.com
 * GNU Lesser General Public License Usage
 * This file may be used under the terms of the GNU Lesser
 * General Public License version 3 as published by the Free Software
 * Foundation and appearing in the file LICENSE.LGPL included in the
 * packaging of this file. Please review the following information to
 * ensure the GNU Lesser General Public License version 3 requirements
 * will be met: http://www.gnu.org/licenses/lgpl.html.
 *
 * If you have questions regarding the use of this file, please contact
 * Ushahidi developers at team@ushahidi.com.
 */

package org.addhen.smssync.prefs;


import android.content.SharedPreferences;

/**
 * Preference for saving String values
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class StringPreference extends BasePreference<String> {

    /**
     * Constructs a new {@link org.addhen.smssync.prefs.StringPreference}
     *
     * @param sharedPreferences SharedPreferences to be used for storing the value.
     * @param key               The key for the preference
     */
    public StringPreference(SharedPreferences sharedPreferences, String key) {
        this(sharedPreferences, key, null);
    }

    /**
     * Constructs a new {@link org.addhen.smssync.prefs.StringPreference}
     *
     * @param sharedPreferences SharedPreferences to be used for storing the value.
     * @param key               The key for the preference
     * @param defaultValue      The default value
     */
    public StringPreference(SharedPreferences sharedPreferences, String key,
                            String defaultValue) {
        super(sharedPreferences, key, defaultValue);
    }

    /**
     * Get the saved String
     *
     * @return The saved string
     */
    @Override
    public String get() {
        return getSharedPreferences().getString(getKey(), getDefaultValue());
    }

    /**
     * Set the string to be saved
     *
     * @param value The String value to be saved
     */
    @Override
    public void set(final String value) {
        //Fix for possible thread violation.

        if (value == null) {
            throw new IllegalArgumentException("String cannot be null");
        }

        getSharedPreferences().edit().putString(getKey(), value).commit();

    }
}
