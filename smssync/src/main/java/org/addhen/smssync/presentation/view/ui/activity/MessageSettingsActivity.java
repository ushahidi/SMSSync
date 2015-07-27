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

package org.addhen.smssync.presentation.view.ui.activity;

import com.cgollner.unclouded.preferences.SwitchPreferenceCompat;

import org.addhen.smssync.R;

import android.os.Bundle;

/**
 * Settings related to messages
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class MessageSettingsActivity extends BasePreferenceActivity {
    private SwitchPreferenceCompat mEnableReplyFrmServer;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.messages_preferences);
        setToolbarTitle(R.string.title_bar_text);
    }
}
