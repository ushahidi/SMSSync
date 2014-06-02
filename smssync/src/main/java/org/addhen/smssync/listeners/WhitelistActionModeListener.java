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

package org.addhen.smssync.listeners;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.view.MenuItem;
import android.widget.ListView;

import org.addhen.smssync.R;
import org.addhen.smssync.fragments.WhitelistFragment;
import org.addhen.smssync.util.Logger;

/**
 * Pending messages action mode listener
 */
public class WhitelistActionModeListener extends BaseActionModeListener {

    private WhitelistFragment mHost;

    public WhitelistActionModeListener(final WhitelistFragment host,
            ListView modeView) {
        super((ActionBarActivity) host.getActivity(), modeView, R.menu.filter_context_menu);
        this.mHost = host;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        boolean result = false;

        if (host != null) {
            result = mHost.performAction(item);
            Logger.log("ActionMode", "Log: " + getSelectedItemPositions().size());
        }

        return result;
    }

    @Override
    public void setTitle(CharSequence title) {
        if (activeMode != null) {
            activeMode.setTitle(title);
        }
    }

    @Override
    public void setTitle(int resId) {
        if (activeMode != null) {
            activeMode.setTitle(host.getString(resId));
        }
    }

}
