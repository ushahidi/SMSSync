/*****************************************************************************
 ** Copyright (c) 2010 - 2012 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 **
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.
 **
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 **
 *****************************************************************************/

package org.addhen.smssync.activities;

import com.actionbarsherlock.app.ActionBar;

import org.addhen.smssync.R;
import org.addhen.smssync.adapters.TabAdapter;
import org.addhen.smssync.fragments.PendingMessages;
import org.addhen.smssync.fragments.SentMessageFragment;
import org.addhen.smssync.fragments.SyncUrlFragment;
import org.addhen.smssync.views.FilterTabView;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;

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
        setContentView(R.layout.filter_tab);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.Tab pendingTab = getSupportActionBar().newTab().setText(
                getString(R.string.pending_messages));
        ActionBar.Tab sentTab = getSupportActionBar().newTab().setText(
                getString(R.string.sent_messages));

        ActionBar.Tab syncTab = getSupportActionBar().newTab().setText(
                getString(R.string.sync_url));

        mViewPager = (ViewPager) findViewById(R.id.pager);

        mTabsAdapter = new TabAdapter(this, getSupportActionBar(), mViewPager);
        mTabsAdapter.addTab(pendingTab, PendingMessages.class);
        mTabsAdapter.addTab(sentTab, SentMessageFragment.class);
        mTabsAdapter.addTab(syncTab, SyncUrlFragment.class);

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
