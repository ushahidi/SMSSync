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
import org.addhen.smssync.presentation.di.component.DaggerFilterComponent;
import org.addhen.smssync.presentation.di.component.DaggerLogComponent;
import org.addhen.smssync.presentation.di.component.DaggerMessageComponent;
import org.addhen.smssync.presentation.di.component.FilterComponent;
import org.addhen.smssync.presentation.di.component.LogComponent;
import org.addhen.smssync.presentation.di.component.MessageComponent;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.Bind;

/**
 * @author Henry Addo
 */
public class MainActivity extends BaseActivity implements HasComponent<AppActivityComponent> {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind((R.id.nav_view))
    NavigationView mNavigationView;

    private static final String BUNDLE_STATE_PARAM_CURRENT_MENU
            = "org.addhen.smssync.presentation.view.ui.activity.BUNDLE_STATE_PARAM_CURRENT_MENU";

    private AppActivityComponent mAppComponent;

    private MessageComponent mMessageComponent;

    private LogComponent mLogComponent;

    private FilterComponent mFilterComponent;

    private ActionBarDrawerToggle mDrawerToggle;

    private int mCurrentMenu;

    public MainActivity() {
        super(R.layout.activity_main, R.menu.menu_main);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            mCurrentMenu = R.id.nav_incoming_messages;
        } else {
            mCurrentMenu = savedInstanceState.getInt(BUNDLE_STATE_PARAM_CURRENT_MENU,
                    R.id.nav_incoming_messages);
        }
        injector();
        initViews();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(BUNDLE_STATE_PARAM_CURRENT_MENU, mCurrentMenu);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void initViews() {
        setSupportActionBar(mToolbar);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name,
                R.string.app_name);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        if (mNavigationView != null) {
            setupDrawerContent(mNavigationView);
        }
        setupFragment(mCurrentMenu);
    }

    private void injector() {
        //getAppComponent().inject(this);
        mAppComponent = DaggerAppActivityComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .build();

        mMessageComponent = DaggerMessageComponent.builder()
                .appComponent(getAppComponent())
                .activityModule(getActivityModule())
                .build();

        mLogComponent = DaggerLogComponent.builder()
                .appComponent(getAppComponent())
                .activityModule(getActivityModule())
                .build();

        mFilterComponent = DaggerFilterComponent.builder()
                .appComponent(getAppComponent())
                .activityModule(getActivityModule())
                .build();
        mAppComponent.launcher().launchGettingStarted();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.settings:
                mAppComponent.launcher().launchSettings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    mCurrentMenu = menuItem.getItemId();
                    setupFragment(mCurrentMenu);
                    menuItem.setChecked(menuItem.isChecked());
                    mToolbar.setTitle(menuItem.getTitle());
                    mDrawerLayout.closeDrawers();
                    return true;
                });
    }

    private void setupFragment(int menuItem) {
        switch (menuItem) {
            case R.id.nav_incoming_messages:
                replaceFragment(R.id.fragment_main_content,
                        mAppComponent.launcher().launchMessages(), "incoming_messages");
                break;
            case R.id.nav_published_messages:
                replaceFragment(R.id.fragment_main_content,
                        mAppComponent.launcher().launchPublishedMessages(), "published_messages");
            case R.id.nav_filters:
                replaceFragment(R.id.fragment_main_content,
                        mAppComponent.launcher().launchFilters(), "filters");
                break;
            case R.id.nav_integration:
                replaceFragment(R.id.fragment_main_content,
                        mAppComponent.launcher().launchIntegrations(), "integrations");
                break;
            case R.id.nav_settings:
                mAppComponent.launcher().launchSettings();
                break;
            default:
                break;
        }
    }

    public void replaceFragment(int containerViewId, Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = this.getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.replace(containerViewId, fragment, tag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public AppActivityComponent getComponent() {
        return mAppComponent;
    }

    /**
     * Gets the Main Application component for dependency injection.
     *
     * @return {@link com.addhen.android.raiburari.presentation.di.component.ApplicationComponent}
     */
    public AppComponent getAppComponent() {
        return ((App) getApplication()).getAppComponent();
    }

    public MessageComponent getMessageComponent() {
        return mMessageComponent;
    }

    public LogComponent getLogComponent() {
        return mLogComponent;
    }

    public FilterComponent getFilterComponent() {
        return mFilterComponent;
    }
}
