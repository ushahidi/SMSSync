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

package org.addhen.smssync.presentation.view.ui.fragment;

import org.addhen.smssync.R;

import android.os.Bundle;

/**
 * Settings Fragment.
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class SettingsFragment extends BasePreferenceFragmentCompat {

    public static final String SETTINGS_FRAGMENT_TAG = "settings_fragment_tag";

    public SettingsFragment() {
        // Do nothing
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String key) {
        setPreferencesFromResource(R.xml.preference_headers_legacy, key);
    }
}
