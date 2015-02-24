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

package org.addhen.smssync.navdrawer;

import org.addhen.smssync.R;
import org.addhen.smssync.models.NavDrawerItem;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;


/**
 * @author eyedol
 */
public abstract class BaseNavDrawerItem extends NavDrawerItem {

    protected ActionBarActivity mActivity;

    protected Fragment fragment;

    protected FragmentManager fragmentManager;

    protected int count;

    protected Handler mHandler = new Handler();

    /**
     *
     * @param title
     * @param iconRes
     * @param activity
     */
    public BaseNavDrawerItem(String title, int iconRes, ActionBarActivity activity) {
        super(title, iconRes);
        mActivity = activity;
    }

    protected abstract void onSelectItem();

    public abstract void setCounter();

    public void selectItem() {
        onSelectItem();
    }

    /**
     * Determines if the item is selected. Default is always false.
     */
    public boolean isSelected() {
        return false;
    }

    /**
     * Determines if the menu item should be displayed in the menu. Default is always true.
     */
    public boolean isVisible() {
        return true;
    }

    ;

    protected void showFragment(String tag) {
        fragmentManager = mActivity.getSupportFragmentManager();
        fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_ENTER_MASK)
                .replace(R.id.content_frame, fragment, tag).commit();
    }


    protected void launchActivity(Intent intent) {
        mActivity.startActivity(intent);
    }

    public int getCounters() {
        return count;
    }

}
