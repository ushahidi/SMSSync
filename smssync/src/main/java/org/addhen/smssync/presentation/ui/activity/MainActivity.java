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

import com.addhen.android.raiburari.presentation.di.HasComponent;
import com.addhen.android.raiburari.presentation.ui.activity.BaseActivity;

import org.addhen.smssync.R;
import org.addhen.smssync.presentation.di.component.AppComponent;
import org.addhen.smssync.presentation.di.component.DaggerAppComponent;
import org.addhen.smssync.presentation.di.component.DaggerFilterComponent;
import org.addhen.smssync.presentation.di.component.DaggerLogComponent;
import org.addhen.smssync.presentation.di.component.DaggerMessageComponent;
import org.addhen.smssync.presentation.di.component.FilterComponent;
import org.addhen.smssync.presentation.di.component.LogComponent;
import org.addhen.smssync.presentation.di.component.MessageComponent;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.InjectView;

/**
 * @author Henry Addo
 */
public class MainActivity extends BaseActivity implements HasComponent<AppComponent> {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @InjectView((R.id.nav_view))
    NavigationView mNavigationView;

    private AppComponent mAppComponent;

    private MessageComponent mMessageComponent;

    private LogComponent mLogComponent;

    private FilterComponent mFilterComponent;

    public MainActivity() {
        super(R.layout.activity_main, 0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injector();
        initViews();
    }

    private void initViews() {
        setSupportActionBar(mToolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        if (mNavigationView != null) {
            setupDrawerContent(mNavigationView);
        }
    }

    private void injector() {
        mAppComponent = DaggerAppComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .build();

        mMessageComponent = DaggerMessageComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .build();

        mLogComponent = DaggerLogComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .build();

        mFilterComponent = DaggerFilterComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .build();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    final int id = menuItem.getItemId();
                    switch (id) {
                        case R.id.nav_settings:
                            mAppComponent.launcher().launchSettings();
                            break;
                        case R.id.nav_sync_url:
                            replaceFragment(R.id.fragment_main_content,
                                    mAppComponent.launcher().launchSyncUrls(),
                                    "syncurl");
                            break;
                        case R.id.nav_logs:
                            replaceFragment(R.id.fragment_main_content,
                                    mAppComponent.launcher().launchLogs(),
                                    "logs");
                            break;
                        case R.id.nav_filters:
                            replaceFragment(R.id.fragment_main_content,
                                    mAppComponent.launcher().launchFilters(),
                                    "filters");
                            break;
                        default:
                            setupMessagesFragment();
                    }
                    menuItem.setChecked(menuItem.isChecked());
                    mToolbar.setTitle(menuItem.getTitle());
                    mDrawerLayout.closeDrawers();
                    return true;
                });
        setupMessagesFragment();
    }

    private void setupMessagesFragment() {
        replaceFragment(R.id.fragment_main_content, mAppComponent.launcher().launchMessages(),
                "messages");
    }

    protected void replaceFragment(int containerViewId, Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = this.getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.replace(containerViewId, fragment, tag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    @Override
    public AppComponent getComponent() {
        return mAppComponent;
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
