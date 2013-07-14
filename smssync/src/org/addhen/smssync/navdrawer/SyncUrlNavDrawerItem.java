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

import org.addhen.smssync.fragments.SyncUrl;
import org.addhen.smssync.models.SyncUrlModel;

import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * @author eyedol
 */
public class SyncUrlNavDrawerItem extends BaseNavDrawerItem {

    private static final String TAG = "sync";

    /**
     * @param itemId
     * @param title
     * @param iconRes
     * @param counter
     * @param counterBgColor
     */
    public SyncUrlNavDrawerItem(String title, int iconRes,
            SherlockFragmentActivity activity) {
        super(NO_ITEM_ID, title, iconRes, null, activity);
    }

    public SyncUrlNavDrawerItem(String title, int iconRes, String counterBgColor,
            SherlockFragmentActivity activity) {
        super(NO_ITEM_ID, title, iconRes, counterBgColor, activity);
    }

    @Override
    protected void onSelectItem() {
        fragment = new SyncUrl();
        showFragment(TAG);
    }

    /* (non-Javadoc)
     * @see org.addhen.smssync.navdrawer.BaseNavDrawerItem#setCounter()
     */
    @Override
    public void setCounter() {
        mCounter = new SyncUrlModel().totalActiveSynUrl();
    }

}
