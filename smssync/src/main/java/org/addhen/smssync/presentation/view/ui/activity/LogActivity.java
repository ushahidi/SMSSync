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

import com.addhen.android.raiburari.presentation.di.HasComponent;
import com.addhen.android.raiburari.presentation.ui.activity.BaseActivity;

import org.addhen.smssync.R;
import org.addhen.smssync.presentation.App;
import org.addhen.smssync.presentation.di.component.AppComponent;
import org.addhen.smssync.presentation.di.component.DaggerLogComponent;
import org.addhen.smssync.presentation.di.component.LogComponent;
import org.addhen.smssync.presentation.di.component.WebServiceComponent;
import org.addhen.smssync.presentation.view.ui.fragment.LogFragment;
import org.addhen.smssync.presentation.view.ui.navigation.Launcher;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import javax.inject.Inject;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class LogActivity extends BaseActivity implements HasComponent<LogComponent> {

    private static final String FRAG_TAG = "list_logs";

    @Inject
    Launcher mLauncher;

    private LogFragment mLogFragment;

    private WebServiceComponent mWebServiceComponent;

    private LogComponent mLogComponent;


    /**
     * Default constructor
     */
    public LogActivity() {
        super(R.layout.activity_list_web_service, 0);
    }

    /**
     * Provides {@link Intent} launching this activity
     *
     * @param context The calling context
     * @return The intent to be launched
     */
    public static Intent getIntent(final Context context) {
        return new Intent(context, LogActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injector();
        mLogFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentByTag(FRAG_TAG);
        if (mLogFragment == null) {
            mLogFragment = LogFragment.newInstance();
            replaceFragment(R.id.add_fragment_container, mLogFragment);
        }
    }

    private void injector() {
        getAppComponent().inject(this);
        mLogComponent = DaggerLogComponent.builder()
                .appComponent(getAppComponent())
                .activityModule(getActivityModule())
                .build();
    }

    @Override
    public LogComponent getComponent() {
        return mLogComponent;
    }

    /**
     * Gets the Main Application component for dependency injection.
     *
     * @return {@link com.addhen.android.raiburari.presentation.di.component.ApplicationComponent}
     */
    public AppComponent getAppComponent() {
        return ((App) getApplication()).getAppComponent();
    }
}