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

import com.actionbarsherlock.app.SherlockFragmentActivity;

import org.addhen.smssync.activities.DonationActivity;

import android.content.Intent;

/**
 * Donation nav drawer item
 */
public class DonationNavDrawerItem extends BaseNavDrawerItem {

    /**
     *
     * @param title
     * @param iconRes
     * @param activity
     */
    public DonationNavDrawerItem(String title, int iconRes,
            SherlockFragmentActivity activity) {
        super(title, iconRes, activity);
    }

    @Override
    protected void onSelectItem() {
        Intent i = new Intent(mActivity, DonationActivity.class);
        launchActivity(i);
    }

    @Override
    public void setCounter() {

    }
}
