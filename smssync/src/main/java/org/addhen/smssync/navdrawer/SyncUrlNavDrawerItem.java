/**
 ** Copyright (c) 2010 Ushahidi Inc
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
 **/

package org.addhen.smssync.navdrawer;

import org.addhen.smssync.fragments.SyncUrlFragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * Menu Item for SyncUrlFragment
 */
public class SyncUrlNavDrawerItem extends BaseNavDrawerItem {

    private static final String TAG = "sync";

    /**
     * @param title
     * @param iconRes
     */
    public SyncUrlNavDrawerItem(String title, int iconRes,
            SherlockFragmentActivity activity) {
        super(title, iconRes, activity);
    }

    @Override
    protected void onSelectItem() {
        fragment = new SyncUrlFragment();
        showFragment(TAG);
    }

    @Override
    public void setCounter() {
        mCounter = new org.addhen.smssync.models.SyncUrl() .totalActiveSynUrl();
    }

}
