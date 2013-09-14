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

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.MenuItem;

import org.addhen.smssync.R;
import org.addhen.smssync.fragments.SentMessageFragment;

import android.widget.ListView;

/**
 * Sent messages action mode listener
 */
public class SentMessagesActionModeListener extends BaseActionModeListener {

    private SentMessageFragment mHost;

    public SentMessagesActionModeListener(final SentMessageFragment host,
            ListView modeView) {
        super(host.getSherlockActivity(), modeView, R.menu.sent_messages_context_menu);
        mHost = host;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        boolean result = false;
        if (host != null) {
            result = mHost.performAction(item, getSelectedItemPositions().size());
        }
        if (activeMode != null) {
            activeMode.finish();
        }
        return result;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        activeMode = null;
        modeView.clearChoices();
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
