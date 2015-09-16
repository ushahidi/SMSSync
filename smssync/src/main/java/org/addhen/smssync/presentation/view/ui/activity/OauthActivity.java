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
import org.addhen.smssync.data.twitter.TwitterAuthConfig;
import org.addhen.smssync.data.twitter.TwitterAuthToken;
import org.addhen.smssync.data.twitter.TwitterOAuthView;
import org.addhen.smssync.data.twitter.TwitterSession;
import org.addhen.smssync.presentation.App;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import twitter4j.auth.AccessToken;


/**
 * Activity for signing in a Twitter User
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class OauthActivity extends Activity implements TwitterOAuthView.Listener {

    private static final String CALLBACK_URL = "http://smssync.ushahidi.com";

    private static final boolean DUMMY_CALLBACK_URL = true;

    public static String INTENT_EXTRA_TWITTER_CONFIG
            = "org.addhen.smssync.presentation.view.ui.TWITTER_CONFIG";

    private TwitterOAuthView view;

    private boolean oauthStarted;

    TwitterAuthConfig authConfig;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitter_auth_login);
        authConfig = getIntent().getParcelableExtra(INTENT_EXTRA_TWITTER_CONFIG);
        // Create an instance of TwitterOAuthView.
        view = new TwitterOAuthView(this);
        view.setDebugEnabled(true);
        setContentView(view);
        oauthStarted = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (oauthStarted) {
            return;
        }
        oauthStarted = true;
        // Start Twitter OAuth process. Its result will be notified via
        // TwitterOAuthView.Listener interface.
        view.start(authConfig.consumerKey, authConfig.consumerSecret, CALLBACK_URL,
                DUMMY_CALLBACK_URL, this);
    }

    @Override
    public void onSuccess(TwitterOAuthView view, AccessToken accessToken) {
        TwitterAuthToken authToken = new TwitterAuthToken(accessToken.getToken(),
                accessToken.getTokenSecret());
        // Persist user session so we can perform actions on protected resources
        App.getTwitterInstance().getSessionManager().setActiveSession(
                new TwitterSession(authToken, accessToken.getUserId(),
                        accessToken.getScreenName()));
        finish();
    }

    @Override
    public void onFailure(TwitterOAuthView view, TwitterOAuthView.Result result) {
        Toast.makeText(this, result.name(), Toast.LENGTH_LONG).show();
    }
}