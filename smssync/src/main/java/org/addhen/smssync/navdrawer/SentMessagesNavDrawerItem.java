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

import org.addhen.smssync.fragments.SentMessageFragment;
import org.addhen.smssync.models.SentMessagesModel;

import android.support.v7.app.ActionBarActivity;

/**
 * Menu Item for Sent messages
 */
public class SentMessagesNavDrawerItem extends BaseNavDrawerItem {

    private static final String TAG = "sent";

    /**
     * @param title
     * @param iconRes
     * @param counterBgColor
     */
    public SentMessagesNavDrawerItem(String title, int iconRes,
            ActionBarActivity activity) {
        super(title, iconRes, activity);
    }

    @Override
    protected void onSelectItem() {
        fragment = new SentMessageFragment();
        showFragment(TAG);
    }

    @Override
    public void setCounter() {
        SentMessagesModel sentMessages = new SentMessagesModel();
        sentMessages.load();
        mCounter = sentMessages.listMessages.size();
    }

}
