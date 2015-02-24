/*
 * Copyright (c) 2014 Ushahidi.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program in the file LICENSE-AGPL. If not, see
 * https://www.gnu.org/licenses/agpl-3.0.html
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
