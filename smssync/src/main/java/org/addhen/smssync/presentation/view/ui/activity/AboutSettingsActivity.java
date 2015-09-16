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

import org.addhen.smssync.R;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class AboutSettingsActivity extends BasePreferenceActivity {

    private static final String KEY_ABOUT = "about_preference";

    private static final String KEY_TRANSLATE = "translate_preference";

    private static final String KEY_FORUMS = "forums_preference";

    private static final String KEY_GOOGLE_PLUS = "google_plus_preference";

    private static final String SMSSYNC_WEB_PAGE = "http://smssync.ushahidi.com";

    private static final String SMSSYNC_GOOGLE_PLUS_PAGE
            = "https://plus.google.com/communities/117573393008661621052";

    private static final String SMSSYNC_FORUMS = "https://forums.ushahidi.com/c/smssync";

    private static final String TRANSLATION_PAGE = "https://www.transifex.com/projects/p/smssync/";

    private StringBuilder mVersionLabel;

    private Preference mAboutPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.about_preferences);
        setToolbarTitle(R.string.about);
        // Get app's version name
        getVersionNumber();
        mAboutPreference = findPreference(KEY_ABOUT);
        mAboutPreference.setTitle(getString(R.string.app_name));
        mAboutPreference.setSummary(getString(R.string.powered_by, mVersionLabel.toString()));
        launchSMSsyncWebsite();
        launchTransifex();
        launchForums();
        launchGooglePlus();
    }

    private void launchSMSsyncWebsite() {
        // When the about us item is clicked at the Settings screen, open a URL
        mAboutPreference.setOnPreferenceClickListener(preference -> {
            openUrl(SMSSYNC_WEB_PAGE);
            return true;
        });
    }

    private void launchTransifex() {
        Preference translatePreference = findPreference(KEY_TRANSLATE);
        translatePreference.setOnPreferenceClickListener(preference -> {
            openUrl(TRANSLATION_PAGE);
            return true;
        });
    }

    private void launchForums() {
        Preference forumsPreference = findPreference(KEY_FORUMS);
        forumsPreference.setOnPreferenceClickListener(preference -> {
            openUrl(SMSSYNC_FORUMS);
            return true;
        });
    }

    private void launchGooglePlus() {
        Preference googlePlusPreference = findPreference(KEY_GOOGLE_PLUS);
        googlePlusPreference.setOnPreferenceClickListener(preference -> {
            openUrl(SMSSYNC_GOOGLE_PLUS_PAGE);
            return true;
        });
    }

    private void getVersionNumber() {
        mVersionLabel = new StringBuilder();
        mVersionLabel.append("v");
        String versionName = null;
        try {
            versionName = getPackageManager().getPackageInfo(
                    this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mVersionLabel.append(versionName);
    }

    private void openUrl(String url) {
        final Intent i = new Intent(
                android.content.Intent.ACTION_VIEW, Uri
                .parse(url));
        startActivity(i);
    }
}
