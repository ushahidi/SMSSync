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

import org.addhen.smssync.fragments.SentMessages;

import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * @author eyedol
 */
public class SentMessagesNavDrawerItem extends BaseNavDrawerItem {

    Fragment fragment;

    /**
     * @param itemId
     * @param title
     * @param iconRes
     * @param counter
     * @param counterBgColor
     */
    public SentMessagesNavDrawerItem(String title, int iconRes, int counter,
            SherlockFragmentActivity activity) {
        super(NO_ITEM_ID, title, iconRes, counter, null, activity);
    }

    public SentMessagesNavDrawerItem(String title, int iconRes,
            SherlockFragmentActivity activity) {
        super(NO_ITEM_ID, title, iconRes, NO_COUNTER, null, activity);
    }

    @Override
    public boolean isSelected() {
        return fragment instanceof SentMessages;
    }

    @Override
    protected void onSelectItem() {
        fragment = new SentMessages();
        showFragment(fragment);
    }

}
