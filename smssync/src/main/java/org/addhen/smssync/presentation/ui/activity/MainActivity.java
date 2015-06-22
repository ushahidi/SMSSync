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
import org.addhen.smssync.presentation.di.component.DaggerFilterComponent;
import org.addhen.smssync.presentation.di.component.FilterComponent;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.InjectView;

/**
 * @author Henry Addo
 */
public class MainActivity extends BaseActivity implements HasComponent<FilterComponent> {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @InjectView((R.id.nav_view))
    NavigationView mNavigationView;

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
                            mFilterComponent.launcher().launchSettings();
                            break;
                        case R.id.nav_sync_url:
                            replaceFragment(R.id.fragment_main_content,
                                    mFilterComponent.launcher().launchSyncUrls(),
                                    "syncurl");
                            break;
                        case R.id.nav_logs:
                            replaceFragment(R.id.fragment_main_content,
                                    mFilterComponent.launcher().launchSyncUrls(),
                                    "logs");
                        default:
                            setupMessagesFragment();
                    }
                    menuItem.setChecked(menuItem.isChecked());
                    mDrawerLayout.closeDrawers();
                    return true;
                });
        setupMessagesFragment();
    }

    private void setupMessagesFragment() {
        replaceFragment(R.id.fragment_main_content, mFilterComponent.launcher().launchMessages(),
                "messages");
    }

    @Override
    public FilterComponent getComponent() {
        return mFilterComponent;
    }
}
