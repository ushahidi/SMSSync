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
 * Preference for saving Integer values
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class IntPreference extends BasePreference<Integer> {

    /**
     * Constructs a new {@link org.addhen.smssync.prefs.IntPreference}
     *
     * @param sharedPreferences SharedPreferences to be used for storing the value.
     * @param key               The key for the preference
     */
    public IntPreference(SharedPreferences sharedPreferences, String key) {
        this(sharedPreferences, key, 0);
    }

    /**
     * Constructs a new {@link org.addhen.smssync.prefs.IntPreference}
     *
     * @param sharedPreferences SharedPreferences to be used for storing the value.
     * @param key               The key for the preference
     * @param defaultValue      The default value
     */
    public IntPreference(SharedPreferences sharedPreferences, String key,
                         Integer defaultValue) {
        super(sharedPreferences, key, defaultValue);
    }

    @Override
    public Integer get() {
        return getSharedPreferences().getInt(getKey(), getDefaultValue());
    }

    /**
     * Sets the Integer to be saved
     *
     * @param value The Integer value to be saved
     */
    @Override
    public void set(final Integer value) {

        if (value == null) {
            throw new IllegalArgumentException("Integer cannot be null");
        }

        getSharedPreferences().edit().putInt(getKey(), value).commit();

    }
}
