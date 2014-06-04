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

package org.addhen.smssync.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import org.addhen.smssync.listeners.OnFragmentListViewRefreshListener;

import java.util.ArrayList;

public class TabAdapter extends FragmentPagerAdapter implements
        ViewPager.OnPageChangeListener, ActionBar.TabListener {

    private final Context mContext;

    private final ActionBar mActionBar;

    private final ViewPager mViewPager;

    private final ArrayList<String> mTabs = new ArrayList<String>();

    private OnFragmentListViewRefreshListener mListViewRefreshListener;

    public TabAdapter(ActionBarActivity activity, ActionBar actionBar,
            ViewPager pager) {
        super(activity.getSupportFragmentManager());
        mContext = activity;
        mActionBar = actionBar;
        mViewPager = pager;
        mViewPager.setAdapter(this);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setOnPageChangeListener(this);
    }

    public void addTab(ActionBar.Tab tab, Class<?> clss) {
        mTabs.add(clss.getName());
        mActionBar.addTab(tab.setTabListener(this));
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public Fragment getItem(int position) {
        return Fragment.instantiate(mContext, mTabs.get(position), null);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
            int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {

        mActionBar.setSelectedNavigationItem(position);

    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

        mViewPager.setCurrentItem(tab.getPosition());
        if (mListViewRefreshListener != null) {
            mListViewRefreshListener.OnFragmentListViewRefresh();
        }

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

}
