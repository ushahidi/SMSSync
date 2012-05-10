/**
 * 
 */
package org.addhen.smssync.listeners;

import org.addhen.smssync.fragments.PendingMessages;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

/**
 * @author eyedol
 * 
 */
public class PendingMessagesActionModeListener implements
		ListView.MultiChoiceModeListener {

	private int lastPosition = -1;

	private ListView modeView;

	private PendingMessages host;

	public PendingMessagesActionModeListener(PendingMessages host,
			ListView modeView) {
		this.modeView = modeView;
		this.host = host;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onItemCheckedStateChanged(ActionMode mode, int position,
			long id, boolean checked) {
		// TODO Auto-generated method stub
	}

}
