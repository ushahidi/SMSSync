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
import org.addhen.smssync.presentation.di.component.AppActivityComponent;
import org.addhen.smssync.presentation.di.component.AppComponent;
import org.addhen.smssync.presentation.di.component.DaggerAppActivityComponent;
import org.addhen.smssync.presentation.di.component.DaggerSettingsComponent;
import org.addhen.smssync.presentation.di.component.SettingsComponent;
import org.addhen.smssync.presentation.view.ui.fragment.AboutSettingsFragment;
import org.addhen.smssync.presentation.view.ui.fragment.AutomationSettingsFragment;
import org.addhen.smssync.presentation.view.ui.fragment.GeneralSettingsFragment;
import org.addhen.smssync.presentation.view.ui.fragment.MessagesSettingsFragment;
import org.addhen.smssync.presentation.view.ui.fragment.SettingsFragment;
import org.addhen.smssync.presentation.view.ui.fragment.TaskSettingsFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class SettingsActivity extends BaseActivity implements HasComponent<SettingsComponent>,
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private AppActivityComponent mAppComponent;

    private SettingsComponent mSettingsComponent;


    /**
     * Default constructor
     */
    public SettingsActivity() {
        super(R.layout.activity_settings, 0);
    }

    public static Intent getIntent(final Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injector();
        initViews();
        if (savedInstanceState == null) {
            // Create the fragment only when the activity is created for the first time.
            // ie. not after orientation changes
            Fragment fragment = getSupportFragmentManager()
                    .findFragmentByTag(SettingsFragment.SETTINGS_FRAGMENT_TAG);
            if (fragment == null) {
                fragment = new SettingsFragment();
            }

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.fragment_container, fragment, SettingsFragment.SETTINGS_FRAGMENT_TAG);
            ft.commit();
        }
    }

    private void navigateUp() {
        super.onBackPressed();

    }

    private Intent getParentIntent() {
        return NavUtils.getParentActivityIntent(this);
    }

    protected void setToolbarTitle(String title) {
        setTitle(getString(R.string.settings_title_format, getString(R.string.action_settings),
                title));
    }

    private void injector() {
        mAppComponent = DaggerAppActivityComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .build();

        mSettingsComponent = DaggerSettingsComponent.builder()
                .appComponent(getAppComponent())
                .activityModule(getActivityModule())
                .build();
    }

    private void initViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mToolbar.setNavigationOnClickListener(v -> navigateUp());
    }

    /**
     * Gets the Main Application component for dependency injection.
     *
     * @return {@link com.addhen.android.raiburari.presentation.di.component.ApplicationComponent}
     */
    public AppComponent getAppComponent() {
        return ((App) getApplication()).getAppComponent();
    }

    @Override
    public SettingsComponent getComponent() {
        return mSettingsComponent;
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat preferenceFragmentCompat,
            Preference preference) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, preference.getKey());
        setToolbarTitle(preference.getTitle().toString());
        if (preference.getKey().equals(GeneralSettingsFragment.GENERAL_SETTINGS_FRAGMENT_TAG)) {
            GeneralSettingsFragment fragment = new GeneralSettingsFragment();
            fragment.setArguments(args);
            ft.replace(R.id.fragment_container, fragment, preference.getKey());
        } else if (preference.getKey()
                .equals(AutomationSettingsFragment.AUTOMATION_SETTINGS_FRAG)) {
            AutomationSettingsFragment fragment = new AutomationSettingsFragment();
            fragment.setArguments(args);
            ft.replace(R.id.fragment_container, fragment, preference.getKey());

        } else if (preference.getKey().equals(TaskSettingsFragment.TASK_SETTINGS_FRAGMENT)) {
            TaskSettingsFragment fragment = new TaskSettingsFragment();
            fragment.setArguments(args);
            ft.replace(R.id.fragment_container, fragment, preference.getKey());
        } else if (preference.getKey()
                .equals(MessagesSettingsFragment.MESSAGES_FRAGMENT_SETTINGS)) {
            MessagesSettingsFragment fragment = new MessagesSettingsFragment();
            fragment.setArguments(args);
            ft.replace(R.id.fragment_container, fragment, preference.getKey());
        } else if (preference.getKey().equals(AboutSettingsFragment.ABOUT_SETTINGS_FRAGMENT)) {
            AboutSettingsFragment fragment = new AboutSettingsFragment();
            fragment.setArguments(args);
            ft.replace(R.id.fragment_container, fragment, preference.getKey());
        }
        ft.addToBackStack(preference.getKey());
        ft.commit();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
