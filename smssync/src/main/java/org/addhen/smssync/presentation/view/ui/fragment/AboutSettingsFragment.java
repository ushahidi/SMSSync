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

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.preference.Preference;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class AboutSettingsFragment extends BasePreferenceFragmentCompat {

    public static String ABOUT_SETTINGS_FRAGMENT = "about_settings_fragment";

    private static final String KEY_ABOUT = "about_preference";

    private static final String KEY_TRANSLATE = "translate_preference";

    private static final String KEY_PRIVACY = "privacy_preference";

    private static final String KEY_FORUMS = "forums_preference";

    private static final String KEY_GOOGLE_PLUS = "google_plus_preference";

    private static final String SMSSYNC_WEB_PAGE = "http://smssync.ushahidi.com";

    private static final String PRIVACY_NOTICE_PAGE = "https://www.ushahidi.com/privacy";

    private static final String SMSSYNC_GOOGLE_PLUS_PAGE
            = "https://plus.google.com/communities/117573393008661621052";

    private static final String SMSSYNC_FORUMS = "https://forums.ushahidi.com/c/smssync";

    private static final String TRANSLATION_PAGE = "https://www.transifex.com/projects/p/smssync/";

    private StringBuilder mVersionLabel;

    private Preference mAboutPreference;

    public AboutSettingsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        // Get app's version name
        getVersionNumber();
        mAboutPreference = findPreference(KEY_ABOUT);
        mAboutPreference.setTitle(getString(R.string.app_name));
        mAboutPreference.setSummary(getString(R.string.powered_by, mVersionLabel.toString()));
        launchSMSsyncWebsite();
        launchPrivacy();
        launchTransifex();
        launchForums();
        launchGooglePlus();
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String key) {
        addPreferencesFromResource(R.xml.about_preferences);
    }

    private void launchSMSsyncWebsite() {
        // When the about us item is clicked at the Settings screen, open a URL
        mAboutPreference.setOnPreferenceClickListener(preference -> {
            openUrl(SMSSYNC_WEB_PAGE);
            return true;
        });
    }

    private void launchPrivacy() {
        Preference privacyPreference = findPreference(KEY_PRIVACY);
        privacyPreference.setOnPreferenceClickListener(preference -> {
            openUrl(PRIVACY_NOTICE_PAGE);
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
            versionName = getContext().getPackageManager()
                    .getPackageInfo(getContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mVersionLabel.append(versionName);
    }

    private void openUrl(String url) {
        final Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(i);
    }
}
