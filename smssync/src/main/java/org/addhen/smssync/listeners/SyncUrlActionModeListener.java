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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.addhen.smssync.R;
import org.addhen.smssync.fragments.SyncUrlFragment;

/**
 * @author eyedol
 */
public class SyncUrlActionModeListener implements ActionMode.Callback,
        AdapterView.OnItemLongClickListener {

    private SyncUrlFragment host;

    private ActionMode activeMode;

    private ListView modeView;

    private int lastPosition = -1;

    public SyncUrlActionModeListener(final SyncUrlFragment host, ListView modeView) {
        this.host = host;
        this.modeView = modeView;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> view, View row, int position,
            long id) {
        lastPosition = position;
        modeView.clearChoices();
        modeView.setItemChecked(lastPosition, true);

        if (activeMode == null) {
            if (host != null) {
                activeMode = ((ActionBarActivity) host.getActivity()).startSupportActionMode(this);
            }
        }

        return true;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        if (host != null) {
            new MenuInflater(host.getActivity())
                    .inflate(R.menu.sync_url_context_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        boolean result = false;
        if (host != null) {
            result = host.performAction(item, lastPosition);
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

}
