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

package org.addhen.smssync.navdrawer;

import org.addhen.smssync.R;
import org.addhen.smssync.models.NavDrawerItem;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * @author eyedol
 */
public abstract class BaseNavDrawerItem extends NavDrawerItem {
    protected SherlockFragmentActivity mActivity;

    /**
     * @param itemId
     * @param title
     * @param iconRes
     * @param counter
     * @param counterBgColor
     */
    public BaseNavDrawerItem(int itemId, String title, int iconRes, int counter,
            String counterBgColor, SherlockFragmentActivity activity) {
        super(itemId, title, iconRes, counter, counterBgColor);
        mActivity = activity;
    }

    protected abstract void onSelectItem();

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
     * Determines if the menu item should be displayed in the menu. Default is
     * always true.
     */
    public boolean isVisible() {
        return true;
    };

    protected void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    protected void launchActivity(Intent intent) {
        mActivity.startActivity(intent);
    }
}
