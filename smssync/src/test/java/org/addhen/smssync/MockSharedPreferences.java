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

package org.addhen.smssync;

import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class MockSharedPreferences implements SharedPreferences, SharedPreferences.Editor {

    private HashMap<String, Object> mValues = new HashMap<String, Object>();

    private HashMap<String, Object> mTempValues = new HashMap<String, Object>();

    @Override
    public Editor edit() {
        return this;
    }

    @Override
    public boolean contains(String key) {
        return mValues.containsKey(key);
    }

    @Override
    public Map<String, ?> getAll() {
        return new HashMap<String, Object>(mValues);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        if (mValues.containsKey(key)) {
            return ((Boolean) mValues.get(key)).booleanValue();
        }
        return defValue;
    }

    @Override
    public float getFloat(String key, float defValue) {
        if (mValues.containsKey(key)) {
            return ((Float) mValues.get(key)).floatValue();
        }
        return defValue;
    }

    @Override
    public int getInt(String key, int defValue) {
        if (mValues.containsKey(key)) {
            return ((Integer) mValues.get(key)).intValue();
        }
        return defValue;
    }

    @Override
    public long getLong(String key, long defValue) {
        if (mValues.containsKey(key)) {
            return ((Long) mValues.get(key)).longValue();
        }
        return defValue;
    }

    @Override
    public String getString(String key, String defValue) {
        if (mValues.containsKey(key)) {
            return (String) mValues.get(key);
        }
        return defValue;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        if (mValues.containsKey(key)) {
            return (Set<String>) mValues.get(key);
        }
        return defValues;
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Editor putBoolean(String key, boolean value) {
        mTempValues.put(key, Boolean.valueOf(value));
        return this;
    }

    @Override
    public Editor putFloat(String key, float value) {
        mTempValues.put(key, value);
        return this;
    }

    @Override
    public Editor putInt(String key, int value) {
        mTempValues.put(key, value);
        return this;
    }

    @Override
    public Editor putLong(String key, long value) {
        mTempValues.put(key, value);
        return this;
    }

    @Override
    public Editor putString(String key, String value) {
        mTempValues.put(key, value);
        return this;
    }

    @Override
    public Editor putStringSet(String key, Set<String> values) {
        mTempValues.put(key, values);
        return this;
    }

    @Override
    public Editor remove(String key) {
        mTempValues.remove(key);
        return this;
    }

    @Override
    public Editor clear() {
        mTempValues.clear();
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean commit() {
        mValues = (HashMap<String, Object>) mTempValues.clone();
        return true;
    }

    @Override
    public void apply() {
        commit();
    }

}
