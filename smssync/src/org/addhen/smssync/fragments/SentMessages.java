package org.addhen.smssync.fragments;

import org.addhen.smssync.Prefs;
import org.addhen.smssync.R;
import org.addhen.smssync.Settings;
import org.addhen.smssync.adapters.SentMessagesAdapter;
import org.addhen.smssync.models.SentMessagesModel;
import org.addhen.smssync.util.ServicesConstants;
import org.addhen.smssync.util.Util;
import org.addhen.smssync.views.SentMessagesView;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;

import com.actionbarsherlock.view.MenuItem;

public class SentMessages
		extends
		BaseListFragment<SentMessagesView, SentMessagesModel, SentMessagesAdapter> {

	private int messageId = 0;

	private final Handler mHandler = new Handler();

	private SentMessagesModel model;

	private MenuItem refresh;

	private boolean refreshState = false;

	public SentMessages() {
		super(SentMessagesView.class, SentMessagesAdapter.class,
				R.layout.sent_messages, R.menu.sent_messages_menu,
				android.R.id.list);

		model = new SentMessagesModel();

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Prefs.loadPreferences(getActivity());

		// show notification
		if (Prefs.enabled) {
			Util.showNotification(getActivity());
		}
		registerForContextMenu(listView);
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().registerReceiver(broadcastReceiver,
				new IntentFilter(ServicesConstants.AUTO_SYNC_ACTION));
		mHandler.post(mDisplayMessages);
	}

	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(broadcastReceiver);
		mHandler.post(mDisplayMessages);
	}

	// Context Menu Stuff
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		new MenuInflater(getActivity()).inflate(
				R.menu.sent_messages_context_menu, menu);

	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		messageId = adapter.getItem(info.position).getMessageId();
		boolean result = performAction(item, info.position);

		if (!result) {
			result = super.onContextItemSelected(item);
		}

		return result;
	}

	public boolean performAction(android.view.MenuItem item, int position) {

		if (item.getItemId() == R.id.context_delete) {
			// Delete by ID
			performDeleteById();
			return (true);
		} else if (item.getItemId() == R.id.context_delete_all) {
			performDeleteAll();
			return (true);
		}
		return (false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;

		if (item.getItemId() == R.id.delete) {
			refresh = item;
			performDeleteAll();
		} else if (item.getItemId() == R.id.settings) {
			intent = new Intent(getActivity(), Settings.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	// Display pending messages.
	final Runnable mDisplayMessages = new Runnable() {
		public void run() {
			adapter.refresh();
		}
	};

	/**
	 * Delete all messages. 0 - Successfully deleted. 1 - There is nothing to be
	 * deleted.
	 */
	final Runnable mDeleteAllSentMessages = new Runnable() {
		public void run() {

			boolean result = false;

			int deleted = 0;

			if (adapter.getCount() == 0) {
				deleted = 1;
			} else {
				result = model.deleteAllSentMessages();
			}

			try {

				if (deleted == 1) {
					toastLong(R.string.no_messages_to_delete);
				} else {
					if (result) {
						toastLong(R.string.messages_deleted);
						adapter.refresh();
					} else {
						toastLong(R.string.messages_deleted_failed);
					}
				}
				refreshState = false;
				updateRefreshStatus();

			} catch (Exception e) {
				return;
			}
		}
	};

	/**
	 * Delete individual messages 0 - Successfully deleted. 1 - There is nothing
	 * to be deleted.
	 */
	final Runnable mDeleteMessagesById = new Runnable() {
		public void run() {
			getActivity().setProgressBarIndeterminateVisibility(true);
			boolean result = false;

			int deleted = 0;

			if (adapter.getCount() == 0) {
				deleted = 1;
			} else {
				result = model.deleteSentMessagesById(messageId);
			}

			try {
				if (deleted == 1) {
					toastLong(R.string.no_messages_to_delete);

				} else {

					if (result) {
						toastLong(R.string.messages_deleted);
						refreshState = false;

					} else {
						toastLong(R.string.messages_deleted_failed);

					}
				}
				refreshState = true;
				updateRefreshStatus();
			} catch (Exception e) {
				return;
			}
		}
	};

	/**
	 * Delete all messages
	 */
	public void performDeleteAll() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(getString(R.string.confirm_message))
				.setCancelable(false)
				.setNegativeButton(getString(R.string.confirm_no),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						})
				.setPositiveButton(getString(R.string.confirm_yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// delete all messages
								refreshState = true;
								updateRefreshStatus();
								mHandler.post(mDeleteAllSentMessages);
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Delete message by it's id
	 */
	public void performDeleteById() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(getString(R.string.confirm_message))
				.setCancelable(false)
				.setNegativeButton(getString(R.string.confirm_no),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						})
				.setPositiveButton(getString(R.string.confirm_yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// Delete by ID
								refreshState = true;
								updateRefreshStatus();
								mHandler.post(mDeleteMessagesById);
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void updateRefreshStatus() {
		if (refresh != null) {
			if (refreshState)
				refresh.setActionView(R.layout.indeterminate_progress_action);
			else
				refresh.setActionView(null);
		}

	}

	@Override
	protected void onLoaded(boolean success) {
		// TODO Auto-generated method stub

	}

	/**
	 * This will refresh content of the listview aka the pending messages when
	 * smssync syncs pending messages.
	 */
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {
				int status = intent.getIntExtra("status", 2);

				if (status == 0) {

					toastLong(R.string.sending_succeeded);
				} else if (status == 1) {
					toastLong(R.string.sync_failed);
				} else {
					toastLong(R.string.no_messages_to_sync);
				}
				adapter.refresh();
			}
		}
	};

}
