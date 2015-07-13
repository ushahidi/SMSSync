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

package org.addhen.smssync.presentation.ui.activity;

import org.addhen.smssync.R;
import org.addhen.smssync.data.twitter.OAuthResponse;
import org.addhen.smssync.data.twitter.RequestUtils;
import org.addhen.smssync.data.twitter.TwitterAuthConfig;
import org.addhen.smssync.data.twitter.TwitterAuthToken;
import org.addhen.smssync.data.twitter.TwitterSession;
import org.addhen.smssync.presentation.App;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;


/**
 * Activity for signing in a Twitter User
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class OauthActivity extends Activity {

    private static final String CALLBACK_URL = "http://smssync.twitter.com";

    private static final String STATE_PROGRESS = "progress";

    public static String INTENT_EXTRA_TWITTER_CONFIG
            = "org.addhen.smssync.presentation.ui.TWITTER_CONFIG";

    private WebView mWebView;

    private OAuthService mService;

    private Token mRequestToken;

    private ProgressBar mSpinner;

    TwitterAuthConfig authConfig;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitter_auth_login);
        authConfig = getIntent().getParcelableExtra(INTENT_EXTRA_TWITTER_CONFIG);
        mService = new ServiceBuilder()
                .provider(TwitterApi.class)
                .apiKey(authConfig.consumerKey)
                .apiSecret(authConfig.consumerSecret)
                .callback(CALLBACK_URL)
                .debug()
                .build();
        mSpinner = (ProgressBar) findViewById(R.id.twitter_spinner);
        mWebView = (WebView) findViewById(R.id.twitter_webView);
        mWebView.clearCache(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(new OAuthWebChromeClient());

        final boolean showProgress;
        if (savedInstanceState != null) {
            showProgress = savedInstanceState.getBoolean(STATE_PROGRESS, false);
        } else {
            showProgress = true;
        }
        mSpinner.setVisibility(showProgress ? View.VISIBLE : View.GONE);

        startAuthorize();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mSpinner.getVisibility() == View.VISIBLE) {
            outState.putBoolean(STATE_PROGRESS, true);
        }
        super.onSaveInstanceState(outState);
    }

    private void startAuthorize() {
        (new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                mRequestToken = mService.getRequestToken();
                return mService.getAuthorizationUrl(mRequestToken);
            }

            @Override
            protected void onPostExecute(String url) {
                mSpinner.setVisibility(View.GONE);
                mWebView.setVisibility(View.VISIBLE);
                mWebView.loadUrl(url);
            }
        }).execute();
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if ((url != null) && (url.startsWith(CALLBACK_URL))) {
                // Override webview when user came back to CALLBACK_URL
                mWebView.stopLoading();
                mWebView.setVisibility(View.INVISIBLE); // Hide webview if necessary
                Uri uri = Uri.parse(url);
                final Verifier verifier = new Verifier(uri.getQueryParameter("oauth_verifier"));
                (new AsyncTask<Void, Void, Token>() {
                    @Override
                    protected Token doInBackground(Void... params) {
                        return mService.getAccessToken(mRequestToken, verifier);
                    }

                    @Override
                    protected void onPostExecute(Token accessToken) {
                        // Because we need the screen name and user id values, we're going
                        // process the raw response to get those values.
                        Log.w("TwitterAuth", "AccessToken: " + accessToken.getRawResponse());
                        OAuthResponse authResponse = RequestUtils
                                .extract(accessToken.getRawResponse());

                        TwitterAuthToken authToken = new TwitterAuthToken(authResponse.oauthToken,
                                authResponse.oauthTokenSecret);
                        // Persist user session so we can perform actions on protected resources
                        App.getTwitterIntance().getSessionManager()
                                .setActiveSession(new TwitterSession(authToken, authResponse.userId,
                                        authResponse.screenName));
                        finish();
                    }
                }).execute();
            } else {
                super.onPageStarted(view, url, favicon);
            }
        }
    };

    class OAuthWebChromeClient extends WebChromeClient {

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            //Do not log
            return true;
        }
    }
}