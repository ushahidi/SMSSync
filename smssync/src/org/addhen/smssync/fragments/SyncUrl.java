package org.addhen.smssync.fragments;

import org.addhen.smssync.Prefs;
import org.addhen.smssync.R;
import org.addhen.smssync.Settings;
import org.addhen.smssync.adapters.SyncUrlAdapter;
import org.addhen.smssync.models.SyncUrlModel;
import org.addhen.smssync.services.SyncPendingMessagesService;
import org.addhen.smssync.util.ServicesConstants;
import org.addhen.smssync.views.AddSyncUrl;
import org.addhen.smssync.views.SyncUrlView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.actionbarsherlock.view.MenuItem;

public class SyncUrl extends
		BaseListFragment<SyncUrlView, SyncUrlModel, SyncUrlAdapter> {

	private Intent syncPendingMessagesServiceIntent;

	private Intent statusIntent;

	private final Handler mHandler;

	private SyncUrlModel model;

	private int messageId = 0;

	private MenuItem refresh;

	private boolean refreshState = false;

	public SyncUrl() {
		super(SyncUrlView.class, SyncUrlAdapter.class, R.layout.list_sync_url,
				R.menu.sync_url_menu, android.R.id.list);
		mHandler = new Handler();
		model = new SyncUrlModel();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		registerForContextMenu(listView);
		setEmptyText(getString(R.string.no_sync_url));
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setItemsCanFocus(false);
	}

	@Override
	public void onResume() {
		super.onResume();
		mHandler.post(mUpdateListView);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onPause() {
		super.onPause();
		mHandler.post(mUpdateListView);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	// Context Menu Stuff
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		new MenuInflater(getActivity()).inflate(R.menu.sync_url_context_menu,
				menu);

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

		if (item.getItemId() == R.id.context_edit_sync_url) {
			// Delete by ID
			performDeleteById();
			return (true);
		} else if (item.getItemId() == R.id.context_delete_sync_url) {
			performDeleteAll();
			return (true);
		}
		return (false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		if (item.getItemId() == R.id.add_sync_url) {
			addSyncUrl();
			return (true);
		} else if (item.getItemId() == R.id.delete_sync_url) {
			performDeleteById();
		} else if (item.getItemId() == R.id.delete_all_sync_url) {
			performDeleteAll();
		} else if (item.getItemId() == R.id.settings) {
			intent = new Intent(getActivity(), Settings.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateRefreshStatus() {
		if (refresh != null) {
			if (refreshState)
				refresh.setActionView(R.layout.indeterminate_progress_action);
			else
				refresh.setActionView(null);
		}

	}

	/**
	 * Delete all messages
	 */
	private void performDeleteAll() {
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
								mHandler.post(mDeleteAllMessages);
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
								mHandler.post(mDeleteSyncById);
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void addSyncUrl() {
		LayoutInflater factory = LayoutInflater.from(getActivity());
		final View textEntryView = factory.inflate(R.layout.add_sync_url, null);
		final AddSyncUrl addMapView = new AddSyncUrl(textEntryView);
		final boolean edit = false;
		// if edit was selected at the context menu, populate fields
		// with existing map details
		if (edit) {
			// TODO edit stuff
		}

		final AlertDialog.Builder addBuilder = new AlertDialog.Builder(
				getActivity());

		addBuilder
				.setTitle(R.string.add_sync_url)
				.setView(textEntryView)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// edit was selected
								if (edit) {

									// update
								} else {
									// add a new entry
								}

							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.cancel();
							}
						});

		AlertDialog deploymentDialog = addBuilder.create();
		deploymentDialog.show();

	}

	// Display pending messages.
	final Runnable mUpdateListView = new Runnable() {
		public void run() {
			showSyncUrl();
		}
	};

	// Synchronize all pending messages.
	final Runnable mSyncMessages = new Runnable() {
		public void run() {
			Prefs.loadPreferences(getActivity());
			if (Prefs.enabled) {
				int result = 0;
				try {
					if (result == 0) {

						toastLong(R.string.sending_succeeded);
					} else if (result == 1) {
						toastLong(R.string.sending_failed);
					} else if (result == 2) {
						toastLong(R.string.no_messages_to_sync);
					}

				} catch (Exception e) {
					return;
				}
			} else {
				toastLong(R.string.smssync_not_enabled);
			}
		}
	};

	/**
	 * Synchronize all pending messages by message id. Which means it
	 * synchronizes messages individually.
	 */
	final Runnable mSyncMessagesById = new Runnable() {
		public void run() {
			Prefs.loadPreferences(getActivity());
			if (Prefs.enabled) {
				int result = 0;
				try {
					if (result == 0) {
						toastLong(R.string.sending_succeeded);
						showSyncUrl();
					} else if (result == 1) {
						toastLong(R.string.sync_failed);
					} else if (result == 2) {
						toastLong(R.string.no_messages_to_sync);
					}
				} catch (Exception e) {
					return;
				}
			} else {
				toastLong(R.string.smssync_not_enabled);
			}
		}
	};

	/**
	 * Delete all messages. 0 - Successfully deleted. 1 - There is nothing to be
	 * deleted.
	 */
	final Runnable mDeleteAllMessages = new Runnable() {
		public void run() {

			boolean result = false;

			int deleted = 0;

			if (adapter.getCount() == 0) {
				deleted = 1;
			} else {
				result = model.deleteAllSyncUrl();
			}

			try {
				if (deleted == 1) {
					toastLong(R.string.no_messages_to_delete);
				} else {
					if (result) {

						toastLong(R.string.messages_deleted);
						showSyncUrl();
					} else {
						toastLong(R.string.messages_deleted_failed);
					}
				}

			} catch (Exception e) {
				return;
			}
		}
	};

	/**
	 * Delete individual messages 0 - Successfully deleted. 1 - There is nothing
	 * to be deleted.
	 */
	final Runnable mDeleteSyncById = new Runnable() {
		public void run() {

			boolean result = false;

			int deleted = 0;

			if (adapter.getCount() == 0) {
				deleted = 1;
			} else {
				result = model.deleteSyncUrlById(messageId);
			}

			try {
				if (deleted == 1) {
					toastLong(R.string.no_messages_to_delete);
				} else {

					if (result) {
						toastLong(R.string.messages_deleted);
						showSyncUrl();

					} else {
						toastLong(R.string.messages_deleted_failed);
					}
				}

			} catch (Exception e) {
				return;
			}
		}
	};

	/**
	 * Get sync url from the database.
	 * 
	 * @return void
	 */
	public void showSyncUrl() {
		if (adapter != null) {
			adapter.refresh();
		}
	}

	@Override
	protected void onLoaded(boolean success) {
		// TODO Auto-generated method stub
	}

	/**
	 * Get messages from the db and push them to the configured callback URL
	 * 
	 * @param int messagesId
	 * @return int
	 */

	public void syncMessages(int messagesId) {
		statusIntent.putExtra("status", 3);
		getActivity().sendBroadcast(statusIntent);
		syncPendingMessagesServiceIntent = new Intent(getActivity(),
				SyncPendingMessagesService.class);
		syncPendingMessagesServiceIntent.putExtra(
				ServicesConstants.MESSEAGE_ID, messagesId);
		getActivity().startService(syncPendingMessagesServiceIntent);
	}

}
