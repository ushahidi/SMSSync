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

package org.addhen.smssync.fragments;

import org.addhen.smssync.Prefs;
import org.addhen.smssync.ProcessSms;
import org.addhen.smssync.R;
import org.addhen.smssync.Settings;
import org.addhen.smssync.adapters.PendingMessagesAdapter;
import org.addhen.smssync.listeners.PendingMessagesActionModeListener;
import org.addhen.smssync.models.MessagesModel;
import org.addhen.smssync.services.SyncPendingMessagesService;
import org.addhen.smssync.tasks.ProgressTask;
import org.addhen.smssync.util.ServicesConstants;
import org.addhen.smssync.util.Util;
import org.addhen.smssync.util.Logger;
import org.addhen.smssync.views.PendingMessagesView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.widget.ListView;

import com.actionbarsherlock.view.MenuItem;

public class PendingMessages
		extends
		BaseListFragment<PendingMessagesView, MessagesModel, PendingMessagesAdapter> {

	private Intent syncPendingMessagesServiceIntent;

	private Intent statusIntent;

	private final Handler mHandler;

	private MessagesModel model;

	private String messageUuid;

	private MenuItem refresh;

	private boolean refreshState = false;

	private static final String STATE_CHECKED = "org.addhen.smssync.fragments.STATE_CHECKED";

	private static String CLASS_TAG = PendingMessages.class.getSimpleName();

	public PendingMessages() {
		super(PendingMessagesView.class, PendingMessagesAdapter.class,
				R.layout.list_messages, R.menu.pending_messages_menu,
				android.R.id.list);
		log("PendingMessages()");
		mHandler = new Handler();
		model = new MessagesModel();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		log("onActivityCreated()");
		super.onActivityCreated(savedInstanceState);

		setHasOptionsMenu(true);
		// adapter = new PendingMessagesAdapter(getActivity());
		Prefs.loadPreferences(getActivity());
		statusIntent = new Intent(ServicesConstants.AUTO_SYNC_ACTION);
		// show notification
		if (Prefs.enabled) {
			Util.showNotification(getActivity());
		}

		listView.setItemsCanFocus(false);
		listView.setLongClickable(true);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setOnItemLongClickListener(new PendingMessagesActionModeListener(
				this, listView));

		if (savedInstanceState != null) {
			int position = savedInstanceState.getInt(STATE_CHECKED, -1);

			if (position > -1) {
				listView.setItemChecked(position, true);
			}
		}

	}

	@Override
	public void onSaveInstanceState(Bundle state) {
		log("onSaveInstanceState()");
		super.onSaveInstanceState(state);
		state.putInt(STATE_CHECKED, listView.getCheckedItemPosition());
	}

	@Override
	public void onResume() {
		log("onResume()");
		super.onResume();
		getActivity().registerReceiver(broadcastReceiver,
				new IntentFilter(ServicesConstants.AUTO_SYNC_ACTION));

		getActivity().registerReceiver(failedReceiver,
				new IntentFilter(ServicesConstants.FAILED_ACTION));
		getActivity().registerReceiver(smsSentReceiver,
				new IntentFilter(ServicesConstants.SENT));
		getActivity().registerReceiver(smsDeliveredReceiver,
				new IntentFilter(ServicesConstants.DELIVERED));
		mHandler.post(mUpdateListView);
	}

	@Override
	public void onStart() {
		log("onStart()");
		super.onStart();
	}

	@Override
	public void onPause() {
		log("onPause()");
		super.onPause();
		getActivity().unregisterReceiver(broadcastReceiver);
		getActivity().unregisterReceiver(failedReceiver);
		getActivity().unregisterReceiver(smsSentReceiver);
		getActivity().unregisterReceiver(smsDeliveredReceiver);
		mHandler.post(mUpdateListView);

	}

	@Override
	public void onDestroy() {
		log("onDestroy()");
		super.onDestroy();
	}

	public boolean performAction(MenuItem item, int position) {
		log("performAction()");
		messageUuid = adapter.getItem(position).getMessageUuid();
		if (item.getItemId() == R.id.context_delete) {

			performDeleteById();
			return (true);

		} else if (item.getItemId() == R.id.context_sync) {
			// Synchronize by ID
			refresh = item;
			refreshState = true;
			updateRefreshStatus();
			syncMessages(messageUuid);
		}
		return (false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		log("onOptionsItemSelected()");
		Intent intent;
		if (item.getItemId() == R.id.sync) {
			refresh = item;
			refreshState = true;
			updateRefreshStatus();
			syncMessages("");
			return (true);
		} else if (item.getItemId() == R.id.import_sms) {
			importAllSms();
		} else if (item.getItemId() == R.id.delete) {
			performDeleteAll();
		} else if (item.getItemId() == R.id.settings) {
			intent = new Intent(getActivity(), Settings.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateRefreshStatus() {
		log("updateRefreshStatus()");
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
		log("perofrmDeleteAll()");
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
								adapter.refresh();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Import all messages from the Android messaging inbox
	 */
	private void importAllSms() {
		log("importAllSms()");
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(getString(R.string.confirm_sms_import))
				.setCancelable(false)
				.setNegativeButton(getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						})
				.setPositiveButton(getString(R.string.ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								ImportMessagesTask importMessagesTask = new ImportMessagesTask(
										getActivity());
								importMessagesTask.execute();

							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Delete message by it's id
	 */
	public void performDeleteById() {
		log("performDeleteById()");
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
								mHandler.post(mDeleteMessagesById);
								adapter.refresh();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	// Display pending messages.
	final Runnable mUpdateListView = new Runnable() {
		public void run() {
			log("mUpdateListView()");
			showMessages();
		}
	};

	// Synchronize all pending messages.
	final Runnable mSyncMessages = new Runnable() {
		public void run() {
			log("mSyncMessages()");
			Prefs.loadPreferences(getActivity());
			if (Prefs.enabled) {
				int result = 0;
				try {
					if (result == 0) {
						toastLong(R.string.sending_succeeded);
					} else if (result == 1) {
						toastLong(R.string.sending_failed);
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
			log("mSyncMessagesById()");
			Prefs.loadPreferences(getActivity());
			if (Prefs.enabled) {
				int result = 0;
				try {
					if (result == 0) {
						toastLong(R.string.sending_succeeded);
						showMessages();
					} else if (result == 1) {
						toastLong(R.string.sync_failed);
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
			log("mDeleteAllMessages()");
			getActivity().setProgressBarIndeterminateVisibility(true);
			boolean result = false;

			int deleted = 0;

			if (adapter.getCount() == 0) {
				deleted = 1;
			} else {
				result = model.deleteAllMessages();
			}

			try {
				if (deleted == 1) {
					toastLong(R.string.no_messages_to_delete);
				} else {
					if (result) {

						toastLong(R.string.messages_deleted);
						showMessages();
					} else {
						toastLong(R.string.messages_deleted_failed);
					}
				}
				getActivity().setProgressBarIndeterminateVisibility(false);
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
			log("mDeleteMessagesById()");
			getActivity().setProgressBarIndeterminateVisibility(true);
			boolean result = false;

			int deleted = 0;

			if (adapter.getCount() == 0) {
				deleted = 1;
			} else {
				result = model.deleteMessagesByUuid(messageUuid);
			}

			try {
				if (deleted == 1) {
					toastLong(R.string.no_messages_to_delete);
				} else {

					if (result) {
						toastLong(R.string.messages_deleted);

					} else {
						toastLong(R.string.messages_deleted_failed);
					}
				}
				showMessages();
				getActivity().setProgressBarIndeterminateVisibility(false);
			} catch (Exception e) {
				return;
			}
		}
	};

	/**
	 * Get messages from the database.
	 * 
	 * @return void
	 */
	public void showMessages() {
		log("showMessages()");
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
	 * @param String messagesUuid
	 * @return int
	 */

	public void syncMessages(String messagesUuid) {
		log("syncMessages messagesUuid: " + messagesUuid);
		if (adapter != null && adapter.getCount() == 0) {
			statusIntent.putExtra("syncstatus", 2);
			getActivity().sendBroadcast(statusIntent);
		} else {
			syncPendingMessagesServiceIntent = new Intent(getActivity(),
					SyncPendingMessagesService.class);
			syncPendingMessagesServiceIntent.putExtra(
					ServicesConstants.MESSAGE_UUID, messagesUuid);
			getActivity().startService(syncPendingMessagesServiceIntent);
		}
	}

	// Thread class to handle synchronous execution of message importation task.
	private class ImportMessagesTask extends ProgressTask {

		protected Integer status;

		protected Context appContext;

		public ImportMessagesTask(Activity activity) {
			super(activity, R.string.please_wait);
			appContext = activity;
		}

		@Override
		protected Boolean doInBackground(String... args) {

			status = new ProcessSms(appContext).importMessages();
			return true;
		}

		@Override
		protected void onPostExecute(Boolean success) {
			super.onPostExecute(success);
			if (success) {
				if (status == 0) {
					showMessages();
				} else if (status == 1) {
					toastLong(R.string.nothing_to_import);
				}
			}
		}
	}

	/**
	 * This will refresh content of the listview aka the pending messages when
	 * smssync successfully syncs pending messages.
	 */
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {
				int status = intent.getIntExtra("syncstatus", 3);
				log("broadcastReceiver onReceive status: " + status);
				if (status == 0) {
					toastLong(R.string.sending_succeeded);
				} else if (status == 1) {
					toastLong(R.string.sync_failed);
				} else if (status == 2) {
					toastLong(R.string.no_messages_to_sync);
				}
				if (syncPendingMessagesServiceIntent != null) {
					getActivity().stopService(syncPendingMessagesServiceIntent);
				}

				refreshState = false;
				updateRefreshStatus();
				mHandler.post(mUpdateListView);
			}
		}
	};

	/**
	 * This will refresh content of the listview aka the pending messages when
	 * smssync successfully syncs pending messages.
	 */
	private BroadcastReceiver failedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {
				int status = intent.getIntExtra("failed", 2);
				log("failedReceiver onReceive status: " + status);
				if (status == 0) {
					mHandler.post(mUpdateListView);
				}
			}
		}
	};

	// when sms has been sent
	private BroadcastReceiver smsSentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int result = getResultCode();
			log("smsSentReceiver onReceive result: " + result);
			switch (result) {
			case Activity.RESULT_OK:
				toastLong(R.string.sms_status_success);
				break;
			case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
				toastLong(R.string.sms_delivery_status_failed);
				break;
			case SmsManager.RESULT_ERROR_NO_SERVICE:
				toastLong(R.string.sms_delivery_status_no_service);
				break;
			case SmsManager.RESULT_ERROR_NULL_PDU:
				toastLong(R.string.sms_delivery_status_null_pdu);
				break;
			case SmsManager.RESULT_ERROR_RADIO_OFF:
				toastLong(R.string.sms_delivery_status_radio_off);
				break;
			}
		}
	};

	// when sms has been delivered
	private BroadcastReceiver smsDeliveredReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			int result = getResultCode();
			log("smsDeliveredReceiver onReceive result: " + result);
			switch (result) {

			case Activity.RESULT_OK:
				toastLong(R.string.sms_delivered);
				break;
			case Activity.RESULT_CANCELED:
				toastLong(R.string.sms_not_delivered);
				break;
			}
		}
	};

}
