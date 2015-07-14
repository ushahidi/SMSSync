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

package org.addhen.smssync.data.twitter;

import org.addhen.smssync.presentation.view.ui.activity.OauthActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class Twitter {

    static final String PREF_KEY_ACTIVE_TWITTER_SESSION = "active_twittersession";

    private static final String PREF_KEY_TWITTER_SESSION = "twittersession";

    private static SessionManager<TwitterSession> mTwitterSessionManager;

    private TwitterAuthConfig mAuthConfig;

    private Context mContext;

    public Twitter(Context context, TwitterAuthConfig config) {
        mContext = context;
        mAuthConfig = config;
        mTwitterSessionManager = new PersistedSessionManager<>(
                PreferenceManager.getDefaultSharedPreferences(mContext),
                new TwitterSession.Serializer(), PREF_KEY_ACTIVE_TWITTER_SESSION,
                PREF_KEY_TWITTER_SESSION);
    }

    /**
     */
    public SessionManager<TwitterSession> getSessionManager() {
        return mTwitterSessionManager;
    }

    public TwitterAuthConfig getAuthConfig() {
        return mAuthConfig;
    }

    /**
     * Performs a user login
     */
    public void login(Activity activity) {
        Intent intent = new Intent(activity, OauthActivity.class);
        intent.putExtra(OauthActivity.INTENT_EXTRA_TWITTER_CONFIG, getAuthConfig());
        activity.startActivity(intent);
    }

    /**
     * Logs out the user, clearing user session. This will not make a network request to invalidate
     * the session.
     */
    public void logout() {
        final SessionManager<TwitterSession> sessionManager = getSessionManager();
        if (sessionManager != null) {
            sessionManager.clearActiveSession();
        }
    }
}
