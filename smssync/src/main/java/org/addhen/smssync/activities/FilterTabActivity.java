/*******************************************************************************
 *  Copyright (c) 2010 - 2013 Ushahidi Inc
 *  All rights reserved
 *  Contact: team@ushahidi.com
 *  Website: http://www.ushahidi.com
 *  GNU Lesser General Public License Usage
 *  This file may be used under the terms of the GNU Lesser
 *  General Public License version 3 as published by the Free Software
 *  Foundation and appearing in the file LICENSE.LGPL included in the
 *  packaging of this file. Please review the following information to
 *  ensure the GNU Lesser General Public License version 3 requirements
 *  will be met: http://www.gnu.org/licenses/lgpl.html.
 *
 * If you have questions regarding the use of this file, please contact
 * Ushahidi developers at team@ushahidi.com.
 ******************************************************************************/

package org.addhen.smssync.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;

import org.addhen.smssync.R;
import org.addhen.smssync.adapters.TabAdapter;
import org.addhen.smssync.fragments.BlacklistFragment;
import org.addhen.smssync.fragments.WhitelistFragment;
import org.addhen.smssync.views.FilterTabView;

public class FilterTabActivity extends BaseActivity<FilterTabView> {

    private ViewPager mViewPager;

    public TabAdapter mTabsAdapter;

    public FilterTabActivity() {
        super(FilterTabView.class, R.layout.filter_tab, R.menu.main_activity, R.id.drawer_layout,
                R.id.left_drawer);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab whitelistTab = getSupportActionBar().newTab().setText(
                getString(R.string.whitelist));

        ActionBar.Tab blacklistTab = getSupportActionBar().newTab().setText(
                getString(R.string.blacklist));

        mViewPager = (ViewPager) findViewById(R.id.pager);

        mTabsAdapter = new TabAdapter(this, getSupportActionBar(), mViewPager);
        mTabsAdapter.addTab(whitelistTab, WhitelistFragment.class);
        mTabsAdapter.addTab(blacklistTab, BlacklistFragment.class);

        if (savedInstanceState != null) {
            final int index = savedInstanceState.getInt("index");
            getSupportActionBar().setSelectedNavigationItem(index
            );

        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("index", getSupportActionBar()
                .getSelectedNavigationIndex());
    }

    // Context Menu Stuff
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {

    }

    public void OnDestory() {
        super.onDestroy();

    }

}
