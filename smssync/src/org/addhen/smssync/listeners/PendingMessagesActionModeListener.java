/*****************************************************************************
 ** Copyright (c) 2010 - 2012 Ushahidi Inc
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
 *****************************************************************************/

package org.addhen.smssync.listeners;

import java.util.LinkedHashSet;

import org.addhen.smssync.R;
import org.addhen.smssync.fragments.PendingMessages;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * @author eyedol
 */
public class PendingMessagesActionModeListener implements ActionMode.Callback,
        AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    private PendingMessages host;

    private ActionMode activeMode;

    private ListView modeView;

    private LinkedHashSet<Integer> mSelectedItemPositions = new LinkedHashSet<Integer>();

    public PendingMessagesActionModeListener(final PendingMessages host,
            ListView modeView) {
        this.host = host;
        this.modeView = modeView;
        this.modeView.setOnItemClickListener(this);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> view, View row, int position,
            long id) {

        if (activeMode == null) {
            if (host != null)
                activeMode = host.getSherlockActivity().startActionMode(this);
        }
        onItemCheckedStateChanged(position);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> view, View row, int position,
            long id) {
        onItemCheckedStateChanged(position);
    }

    private void onItemCheckedStateChanged(int position) {

        if (activeMode != null) {
            modeView.setItemChecked(position, true);
            if (!mSelectedItemPositions.add(position)) {
                mSelectedItemPositions.remove(position);
                modeView.setItemChecked(position, false);
            }

            activeMode.setTitle(String.valueOf(mSelectedItemPositions.size()));
        }

    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        if (host != null) {
            new com.actionbarsherlock.view.MenuInflater(host.getActivity())
                    .inflate(R.menu.pending_messages_context_menu, menu);
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
        if (activeMode != null)
            activeMode.finish();

        if (host != null) {
            result = host.performAction(item, mSelectedItemPositions);
            mSelectedItemPositions.clear();
        }
        return result;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        activeMode = null;
        modeView.clearChoices();

    }

}
