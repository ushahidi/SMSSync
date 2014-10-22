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

package org.addhen.smssync.fragments;

import org.addhen.smssync.Prefs;
import org.addhen.smssync.R;
import org.addhen.smssync.adapters.SyncUrlAdapter;
import org.addhen.smssync.listeners.SyncUrlActionModeListener;
import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.net.SyncScheme;
import org.addhen.smssync.receivers.SmsReceiver;
import org.addhen.smssync.services.CheckTaskScheduledService;
import org.addhen.smssync.services.CheckTaskService;
import org.addhen.smssync.tasks.ProgressTask;
import org.addhen.smssync.tasks.Task;
import org.addhen.smssync.util.RunServicesUtil;
import org.addhen.smssync.util.Util;
import org.addhen.smssync.views.AddSyncUrl;
import org.addhen.smssync.views.EditSyncScheme;
import org.addhen.smssync.views.SyncUrlView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.List;

public class SyncUrlFragment extends
        BaseListFragment<SyncUrlView, SyncUrl, SyncUrlAdapter> implements
        View.OnClickListener {

    private SyncUrl model;

    private int id = 0;

    private boolean edit = false;

    private List<SyncUrl> syncUrl;

    private PackageManager pm;

    private ComponentName smsReceiverComponent;

    public SyncUrlFragment() {
        super(SyncUrlView.class, SyncUrlAdapter.class, R.layout.list_sync_url,
                R.menu.sync_url_menu, android.R.id.list);
        model = new SyncUrl();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        Prefs.loadPreferences(getActivity());
        pm = getActivity().getPackageManager();
        smsReceiverComponent = new ComponentName(getActivity(),
                SmsReceiver.class);

        listView.setItemsCanFocus(false);
        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new SyncUrlActionModeListener(this,
                listView));
        view.enableSmsSync.setChecked(Prefs.enabled);
        view.enableSmsSync.setOnClickListener(this);

        // registerForContextMenu(listView);

    }

    @Override
    public void onResume() {
        super.onResume();
        loadSyncUrlInBackground();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public boolean performAction(MenuItem item, int position) {

        id = adapter.getItem(position).getId();

        if (item.getItemId() == R.id.sync_url_context_edit_sync_url) {
            edit = true;
            addSyncUrl();
            return (true);
        } else if (item.getItemId() == R.id.sync_url_context_delete_sync_url) {
            if (adapter.getItem(position).getStatus() == 1) {
                showMessage(R.string.disable_to_delete_syncurl);
            } else {
                performDeleteById();
            }
            return (true);
        } else if (item.getItemId() == R.id.sync_url_context_sync_scheme) {
            editSyncScheme();
            return true;
        }
        return (false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.add_sync_url) {
            edit = false;
            addSyncUrl();
            return (true);
        } else if (item.getItemId() == R.id.delete_all_sync_url) {
            // load all checked syncurl
            loadByStatus();
            if (syncUrl != null && syncUrl.size() > 0) {
                showMessage(R.string.disable_to_delete_all_syncurl);

                // check if a service is running
            } else if (Prefs.enabled) {
                showMessage(R.string.disable_smssync_service);
            } else {
                performDeleteAll();
            }
        }
        return super.onOptionsItemSelected(item);
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
                                new DeleteTask(getActivity()).execute((String) null);
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Validates the Sync URL to be added
     *
     * @return boolean
     */
    public boolean validateSyncUrlEntry(AddSyncUrl addSyncUrl) {
        boolean noError = false;
        if (addSyncUrl != null) {
            if (TextUtils.isEmpty(addSyncUrl.title.getText().toString())) {
                toastLong(R.string.empty_sync_url_title);
            } else if (Util.validateCallbackUrl(addSyncUrl.url.getText()
                    .toString()) == 1) {
                toastLong(R.string.valid_sync_url);
            } else {
                noError = true;
            }
        }
        return noError;
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
                                DeleteTask deleteById = new DeleteTask(getActivity());
                                deleteById.deletebyUuid = true;
                                deleteById.execute((String) null);
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Show prompt message
     *
     * @param message The resource string which is the message to show to the user.
     * @return void
     */
    public void showMessage(int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(message))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Delete by ID
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void addSyncUrl() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View textEntryView = factory.inflate(R.layout.add_sync_url, null);
        final AddSyncUrl addSyncUrl = new AddSyncUrl(textEntryView);
        // if edit was selected at the context menu, populate fields
        // with existing sync URL details
        if (edit) {
            final List<SyncUrl> listSyncUrl = model.loadById(id);
            if (listSyncUrl != null && listSyncUrl.size() > 0) {
                addSyncUrl.title.setText(listSyncUrl.get(0).getTitle());
                addSyncUrl.url.setText(listSyncUrl.get(0).getUrl());
                addSyncUrl.secret.setText(listSyncUrl.get(0).getSecret());
                addSyncUrl.keywords.setText(listSyncUrl.get(0).getKeywords());
                addSyncUrl.status = listSyncUrl.get(0).getStatus();

            }
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

                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                dialog.dismiss();
                            }
                        });

        final AlertDialog deploymentDialog = addBuilder.create();
        deploymentDialog.show();

        deploymentDialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // validate entry
                        if (validateSyncUrlEntry(addSyncUrl)) {
                            // edit was selected
                            if (edit) {
                                AddSyncUrlTask updateTask = new AddSyncUrlTask(getActivity(),
                                        addSyncUrl);
                                updateTask.editSyncUrl = true;
                                updateTask.execute((String) null);

                            } else {
                                // add a new entry
                                AddSyncUrlTask addTask = new AddSyncUrlTask(getActivity(),
                                        addSyncUrl);
                                addTask.execute((String) null);
                            }
                            deploymentDialog.dismiss();
                        }

                    }
                });

    }

    private void editSyncScheme() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View textEntryView = factory.inflate(R.layout.edit_sync_url_scheme, null);
        final EditSyncScheme editScheme = new EditSyncScheme(textEntryView);

        final List<SyncUrl> listSyncUrl = model.loadById(id);
        if (listSyncUrl != null && listSyncUrl.size() > 0) {
            SyncScheme scheme = listSyncUrl.get(0).getSyncScheme();
            editScheme.keyMessage.setText(scheme.getKey(SyncScheme.SyncDataKey.MESSAGE));
            editScheme.keyMessageID.setText(scheme.getKey(SyncScheme.SyncDataKey.MESSAGE_ID));
            editScheme.keyFrom.setText(scheme.getKey(SyncScheme.SyncDataKey.FROM));
            editScheme.keySecret.setText(scheme.getKey(SyncScheme.SyncDataKey.SECRET));
            editScheme.keySentTimeStamp
                    .setText(scheme.getKey(SyncScheme.SyncDataKey.SENT_TIMESTAMP));
            editScheme.keySentTo.setText(scheme.getKey(SyncScheme.SyncDataKey.SENT_TO));
            editScheme.keyDeviceID.setText(scheme.getKey(SyncScheme.SyncDataKey.DEVICE_ID));
            editScheme.methods.setSelection(scheme.getMethod().ordinal());
            editScheme.dataFormats.setSelection(scheme.getDataFormat().ordinal());
        } else {
            //SHOULD NOT GET HERE!!
            return;
        }

        final AlertDialog.Builder addBuilder = new AlertDialog.Builder(
                getActivity());
        addBuilder
                .setTitle(R.string.sync_sheme)
                .setView(textEntryView)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                dialog.dismiss();
                            }
                        });

        final AlertDialog deploymentDialog = addBuilder.create();
        deploymentDialog.show();

        deploymentDialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //TODO: validate entry
                        if (editScheme.validEntries()) {
                            editScheme.updateSyncScheme(listSyncUrl.get(0));
                            deploymentDialog.dismiss();
                        } else {
                            toastLong(R.string.all_fields_are_required);
                        }
                    }
                });
    }

    // Display pending messages.
    public void loadSyncUrlInBackground() {
        new LoadingTask(getActivity()).execute((String) null);
    }

    public void loadByStatus() {
        syncUrl = model.loadByStatus(1);
    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {

        if (adapter.getCount() > 0) {
            // check if there are any enabled sync urls
            // load all checked syncurl
            loadByStatus();
            if (syncUrl != null && syncUrl.size() > 0) {
                if (view.enableSmsSync.isChecked()) {

                    if (Util.isDefaultSmsApp(this.getActivity())) {
                        // start sms receiver
                        pm.setComponentEnabledSetting(smsReceiverComponent,
                                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                                PackageManager.DONT_KILL_APP);

                        Prefs.enabled = true;
                        view.enableSmsSync.setChecked(true);
                        // because the services to be run depends on the state of the service so save the
                        // changes first
                        Prefs.savePreferences(getActivity());
                        // run auto sync service
                        RunServicesUtil.runAutoSyncService(getActivity());

                        // run check task service
                        RunServicesUtil.runCheckTaskService(getActivity());

                        // show notification
                        Util.showNotification(getActivity());
                    } else {
                        view.enableSmsSync.setChecked(false);
                        Prefs.enabled = false;
                        Util.makeDefaultSmsApp(this.getActivity());
                    }

                } else {
                    // stop sms receiver
                    pm.setComponentEnabledSetting(smsReceiverComponent,
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP);

                    RunServicesUtil.stopCheckTaskService(getActivity());
                    RunServicesUtil.stopAutoSyncService(getActivity());

                    // stop check task schedule
                    getActivity().stopService(
                            new Intent(getActivity(),
                                    CheckTaskScheduledService.class));
                    getActivity().stopService(
                            new Intent(getActivity(), CheckTaskService.class));

                    Util.clearNotify(getActivity());
                    Prefs.enabled = false;
                    view.enableSmsSync.setChecked(false);
                }
            } else {
                toastLong(R.string.no_enabled_sync_url);
                Prefs.enabled = false;
                view.enableSmsSync.setChecked(false);
            }

        } else {
            toastLong(R.string.no_sync_url_added);
            Prefs.enabled = false;
            view.enableSmsSync.setChecked(false);
        }
        Prefs.savePreferences(getActivity());
    }


    private class LoadingTask extends ProgressTask {

        protected boolean loadSyncUrlByStatus = false;

        public LoadingTask(Activity activity) {
            super(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.cancel();
            view.emptyView.setVisibility(android.view.View.GONE);
        }

        @Override
        protected Boolean doInBackground(String... args) {
            if (loadSyncUrlByStatus) {
                syncUrl = model.loadByStatus(1);
                return true;
            } else {
                return model.load();
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            view.listLoadingProgress.setVisibility(android.view.View.GONE);
            view.emptyView.setVisibility(View.VISIBLE);
            if (success) {

                adapter.setItems(model.getSyncUrlList());
                listView.setAdapter(adapter);
            }
        }
    }


    protected class DeleteTask extends ProgressTask {

        protected boolean deletebyUuid = false;

        protected int deleted = 0;

        public DeleteTask(Activity activity) {
            super(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.cancel();
            activity.setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Boolean doInBackground(String... args) {
            if (adapter.getCount() == 0) {
                deleted = 1;
            } else {
                if (deletebyUuid) {
                    model.deleteSyncUrlById(id);
                } else {
                    model.deleteAllSyncUrl();
                }
                deleted = 2;
            }
            model.load();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            view.emptyView.setVisibility(View.VISIBLE);
            if (success) {
                if (deleted == 1) {
                    toastLong(R.string.no_sync_url_to_delete);
                } else {
                    if (deleted == 2) {
                        toastLong(R.string.sync_url_deleted);

                    } else {
                        toastLong(R.string.sync_url_deleted_failed);
                    }

                }
                adapter.setItems(model.getSyncUrlList());
                listView.setAdapter(adapter);
            }
        }
    }

    private class AddSyncUrlTask extends Task<String, String, Boolean> {

        protected boolean editSyncUrl = false;

        private AddSyncUrl addSyncUrl;

        private boolean status = false;

        protected AddSyncUrlTask(Activity activity, AddSyncUrl addSyncUrl) {
            super(activity);
            this.addSyncUrl = addSyncUrl;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            if (editSyncUrl) {
                final List<SyncUrl> listSyncUrl = model.loadById(id);
                status = addSyncUrl.updateSyncUrl(id, listSyncUrl.get(0).getSyncScheme());
            } else {
                status = addSyncUrl.addSyncUrl();
            }
            model.load();
            return status;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                adapter.setItems(model.getSyncUrlList());
                listView.setAdapter(adapter);
            } else {
                if (editSyncUrl) {
                    toastLong(R.string.failed_to_update_sync_url);
                } else {
                    toastLong(R.string.failed_to_add_sync_url);
                }
            }
        }
    }

}
