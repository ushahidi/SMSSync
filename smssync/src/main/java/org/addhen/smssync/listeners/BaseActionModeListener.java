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

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.LinkedHashSet;

/**
 * Base class for action mode listener
 */
public abstract class BaseActionModeListener implements ActionMode.Callback,
        AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    protected SherlockFragmentActivity host;

    public ActionMode activeMode;

    protected ListView modeView;

    private LinkedHashSet<Integer> mSelectedItemPositions = new LinkedHashSet<Integer>();

    protected final int contextMenuResId;

    /**
     * Set the title of the action mode.
     *
     * @param title Title string to set
     * @see #setTitle(int)
     */
    public abstract void setTitle(CharSequence title);

    /**
     * Set the title of the action mode. This method will have no visible effect if a custom view
     * has been set.
     *
     * @param resId Resource ID of a string to set as the title
     * @see #setTitle(CharSequence)
     */
    public abstract void setTitle(int resId);

    public BaseActionModeListener(final SherlockFragmentActivity host,
            ListView modeView, int contextMenuResId) {
        this.host = host;
        this.modeView = modeView;
        this.modeView.setOnItemClickListener(this);
        this.contextMenuResId = contextMenuResId;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> view, View row, int position,
            long id) {

        if (activeMode == null) {
            if (host != null) {
                activeMode = host.startActionMode(this);
            }
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
            setSelectedItemPositions(mSelectedItemPositions);
            setTitle(String.valueOf(mSelectedItemPositions.size()));
        }

    }

    public void setSelectedItemPositions(LinkedHashSet<Integer> selectedItemPositions) {
        mSelectedItemPositions = selectedItemPositions;
    }

    public LinkedHashSet<Integer> getSelectedItemPositions() {
        return mSelectedItemPositions;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        if (host != null) {
            if (contextMenuResId != 0) {
                new com.actionbarsherlock.view.MenuInflater(host)
                        .inflate(contextMenuResId, menu);
            }
        }
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        if (activeMode != null) {
            activeMode.finish();
        }
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        modeView.clearChoices();
        getSelectedItemPositions().clear();
        activeMode = null;

    }

}
