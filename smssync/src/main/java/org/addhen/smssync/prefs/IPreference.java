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

/**
 * SharedPreferences interface
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public interface IPreference<T> {

    /**
     * Gets the value of the preference
     */
    T get();

    /**
     * Checks if the preference is set.
     *
     * @return The status of the preferences. Whether it has been set or not.
     */
    boolean isSet();

    /**
     * Set the value for the preference
     */
    void set(T value);

    /**
     * Deletes the set preference.
     */
    void delete();

}