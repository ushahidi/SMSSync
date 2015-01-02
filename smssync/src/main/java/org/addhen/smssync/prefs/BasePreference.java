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
 * Base class for creating concrete Preference type.
 */
public abstract class BasePreference<T> implements IPreference<T> {

    private final SharedPreferences mSharedPreferences;

    private final String mKey;

    private final T mDefaultValue;

    public BasePreference(SharedPreferences sharedPreferences, String key, T defaultValue) {
        if(sharedPreferences == null || key == null) {
            throw new IllegalArgumentException("Constructor arguments cannot be null");
        }

        mSharedPreferences = sharedPreferences;
        mKey = key;
        mDefaultValue = defaultValue;
    }


    @Override
    public boolean isSet() {
        return getSharedPreferences().contains(mKey);
    }

    @Override
    public void delete() {
        getSharedPreferences().edit().remove(mKey).commit();
    }

    /**
     * Gets the key of the preference.
     *
     * @return The key
     */
    protected String getKey() {
        return mKey;
    }

    /**
     * Gets the default value of the preference
     *
     * @return The default value
     */
    protected T getDefaultValue() {
        return mDefaultValue;
    }

    /**
     * Gets the {@link android.content.SharedPreferences}
     *
     * @return The SharedPreferences
     */
    protected SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }
}

