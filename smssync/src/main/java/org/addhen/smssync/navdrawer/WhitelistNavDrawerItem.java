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

package org.addhen.smssync.navdrawer;

import android.support.v7.app.ActionBarActivity;

import org.addhen.smssync.App;
import org.addhen.smssync.activities.FilterTabActivity;
import org.addhen.smssync.fragments.WhitelistFragment;

/**
 * Filter Nav Drawer Item
 */
public class WhitelistNavDrawerItem extends BaseNavDrawerItem {

    private static final String TAG = "whitelist";

    /**
     * Filter Nav Drawer
     */
    public WhitelistNavDrawerItem(String title, int iconRes,
                                  ActionBarActivity activity) {
        super(title, iconRes, activity);
    }

    @Override
    protected void onSelectItem() {
        fragment = new WhitelistFragment();
        showFragment(TAG);
    }

    @Override
    public boolean isSelected() {
        return mActivity instanceof FilterTabActivity;
    }


    @Override
    public void setCounter() {
        mCounter = App.getDatabaseInstance().getFilterInstance().getWhiteListTotal();
    }
}
