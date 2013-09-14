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
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import org.addhen.smssync.R;
import org.addhen.smssync.fragments.SyncUrlFragment;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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
                activeMode = host.getSherlockActivity().startActionMode(this);
            }
        }

        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.actionbarsherlock.view.ActionMode.Callback#onCreateActionMode(com
     * .actionbarsherlock.view.ActionMode, com.actionbarsherlock.view.Menu)
     */
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        if (host != null) {
            new com.actionbarsherlock.view.MenuInflater(host.getActivity())
                    .inflate(R.menu.sync_url_context_menu, menu);
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.actionbarsherlock.view.ActionMode.Callback#onPrepareActionMode(com
     * .actionbarsherlock.view.ActionMode, com.actionbarsherlock.view.Menu)
     */
    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.actionbarsherlock.view.ActionMode.Callback#onActionItemClicked(com
     * .actionbarsherlock.view.ActionMode, com.actionbarsherlock.view.MenuItem)
     */
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

    /*
     * (non-Javadoc)
     *
     * @see
     * com.actionbarsherlock.view.ActionMode.Callback#onDestroyActionMode(com
     * .actionbarsherlock.view.ActionMode)
     */
    @Override
    public void onDestroyActionMode(ActionMode mode) {
        activeMode = null;
        modeView.clearChoices();
    }

}
