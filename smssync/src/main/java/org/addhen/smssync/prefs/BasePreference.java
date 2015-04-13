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
 * Base class for creating concrete Preference type.
 */
public abstract class BasePreference<T> implements IPreference<T> {

    private final SharedPreferences mSharedPreferences;

    private final String mKey;

    private final T mDefaultValue;

    public BasePreference(SharedPreferences sharedPreferences, String key, T defaultValue) {
        if (sharedPreferences == null || key == null) {
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

