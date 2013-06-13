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

import org.addhen.smssync.R;
import org.addhen.smssync.adapters.TabAdapter;
import org.addhen.smssync.fragments.PendingMessages;
import org.addhen.smssync.fragments.SentMessages;
import org.addhen.smssync.fragments.SyncUrl;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class MessagesTabActivity extends SherlockFragmentActivity implements
        ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;

    private TabAdapter mTabsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_messages_tab);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.Tab pendingTab = getSupportActionBar().newTab().setText(
                getString(R.string.pending_messages));
        ActionBar.Tab sentTab = getSupportActionBar().newTab().setText(
                getString(R.string.sent_messages));

        ActionBar.Tab syncTab = getSupportActionBar().newTab().setText(
                getString(R.string.sync_url));

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOnPageChangeListener(this);
        mTabsAdapter = new TabAdapter(this, getSupportActionBar(), mViewPager);

        mTabsAdapter.addTab(pendingTab, PendingMessages.class);
        mTabsAdapter.addTab(sentTab, SentMessages.class);
        mTabsAdapter.addTab(syncTab, SyncUrl.class);
        

        if (savedInstanceState != null) {
            getSupportActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt("index"));
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

    @Override
    public void onPageScrollStateChanged(int position) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int position) {
        // TODO Auto-generated method stub

    }

}
