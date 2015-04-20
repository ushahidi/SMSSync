/*
 * Copyright (c) 2010 - 2015 Ushahidi Inc
 * All rights reserved
 * Contact: team@ushahidi.com
 * Website: http://www.ushahidi.com
 * GNU Lesser General Public License Usage
 * This file may be used under the terms of the GNU Lesser
 * General Public License version 3 as published by the Free Software
 * Foundation and appearing in the file LICENSE.LGPL included in the
 * packaging of this file. Please review the following information to
 * ensure the GNU Lesser General Public License version 3 requirements
 * will be met: http://www.gnu.org/licenses/lgpl.html.
 *
 * If you have questions regarding the use of this file, please contact
 * Ushahidi developers at team@ushahidi.com.
 */

package org.addhen.smssync.fragments;

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

import org.addhen.smssync.App;
import org.addhen.smssync.R;
import org.addhen.smssync.UiThread;
import org.addhen.smssync.adapters.SyncUrlAdapter;
import org.addhen.smssync.listeners.SyncUrlActionModeListener;
import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.net.SyncScheme;
import org.addhen.smssync.receivers.SmsReceiver;
import org.addhen.smssync.services.CheckTaskScheduledService;
import org.addhen.smssync.services.CheckTaskService;
import org.addhen.smssync.tasks.Task;
import org.addhen.smssync.util.RunServicesUtil;
import org.addhen.smssync.util.Util;
import org.addhen.smssync.views.AddSyncUrl;
import org.addhen.smssync.views.EditSyncScheme;
import org.addhen.smssync.views.SyncUrlView;

import java.util.List;

import static org.addhen.smssync.database.BaseDatabseHelper.DatabaseCallback;

public class SyncUrlFragment extends
        BaseListFragment<SyncUrlView, SyncUrl, SyncUrlAdapter> implements
        View.OnClickListener {

    private Long id;

    private boolean edit = false;

    private PackageManager pm;

    private ComponentName smsReceiverComponent;

    private SyncUrl mSyncUrl;

    public SyncUrlFragment() {
        super(SyncUrlView.class, SyncUrlAdapter.class, R.layout.list_sync_url,
                R.menu.sync_url_menu, android.R.id.list);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        pm = getActivity().getPackageManager();
        smsReceiverComponent = new ComponentName(getActivity(),
                SmsReceiver.class);

        listView.setItemsCanFocus(false);
        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new SyncUrlActionModeListener(this,
                listView));
        view.enableSmsSync.setChecked(prefs.serviceEnabled().get());
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
            if (adapter.getItem(position).getStatus() == SyncUrl.Status.ENABLED) {
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
            App.getDatabaseInstance().getSyncUrlInstance().fetchSyncUrlByStatus(
                    SyncUrl.Status.ENABLED, new DatabaseCallback<List<SyncUrl>>() {
                        @Override
                        public void onFinished(final List<SyncUrl> result) {
                            UiThread.getInstance().post(new Runnable() {

                                @Override
                                public void run() {
                                    if (result != null && result.size() > 0) {
                                        showMessage(R.string.disable_to_delete_all_syncurl);

                                        // check if a service is running
                                    } else if (prefs.serviceEnabled().get()) {
                                        showMessage(R.string.disable_smssync_service);
                                    } else {
                                        performDeleteAll();
                                    }
                                }
                            });

                        }

                        @Override
                        public void onError(Exception exception) {
                            // Do nothing
                        }
                    });

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
                                new DeleteTask().execute(false);
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
                                DeleteTask deleteById = new DeleteTask();
                                deleteById.execute(true);
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
        final SyncUrl editSyncUrl = new SyncUrl();
        // if edit was selected at the context menu, populate fields
        // with existing sync URL details
        if (edit) {
            App.getDatabaseInstance().getSyncUrlInstance()
                    .fetchSyncUrlById(id, new DatabaseCallback<SyncUrl>() {
                        @Override
                        public void onFinished(final SyncUrl syncUrl) {
                            UiThread.getInstance().post(new Runnable() {

                                @Override
                                public void run() {
                                    if (syncUrl != null) {
                                        addSyncUrl.title.setText(syncUrl.getTitle());
                                        addSyncUrl.url.setText(syncUrl.getUrl());
                                        addSyncUrl.secret.setText(syncUrl.getSecret());
                                        addSyncUrl.keywords.setText(syncUrl.getKeywords());
                                        addSyncUrl.status = syncUrl.getStatus();
                                        editSyncUrl.setSyncScheme(syncUrl.getSyncScheme());
                                    }
                                }
                            });

                        }

                        @Override
                        public void onError(Exception exception) {

                        }
                    });

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
                                        addSyncUrl, editSyncUrl.getSyncScheme());
                                updateTask.editSyncUrl = true;
                                updateTask.execute();

                            } else {
                                // add a new entry
                                AddSyncUrlTask addTask = new AddSyncUrlTask(getActivity(),
                                        addSyncUrl, null);
                                addTask.editSyncUrl = false;
                                addTask.execute();
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
        App.getDatabaseInstance().getSyncUrlInstance().fetchSyncUrlById(id, new DatabaseCallback<SyncUrl>() {
            @Override
            public void onFinished(final SyncUrl result) {
                if (result != null) {
                    SyncScheme scheme = result.getSyncScheme();
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
                    mSyncUrl = result;
                }
            }

            @Override
            public void onError(Exception exception) {

            }
        });

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
                            editScheme.updateSyncScheme(mSyncUrl);
                            deploymentDialog.dismiss();
                        } else {
                            toastLong(R.string.all_fields_are_required);
                        }
                    }
                });

    }

    // Display pending messages.
    public void loadSyncUrlInBackground() {
        loadTask(false);
    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {

        if (adapter.getCount() > 0) {
            // check if there are any enabled sync urls
            startServiceForEnabledSyncUrl();
        } else {
            toastLong(R.string.no_sync_url_added);
            prefs.serviceEnabled().set(false);
            view.enableSmsSync.setChecked(false);
        }
    }

    private void startServiceForEnabledSyncUrl() {
        // load all checked syncurl
        App.getDatabaseInstance().getSyncUrlInstance().fetchSyncUrlByStatus(
                SyncUrl.Status.ENABLED, new DatabaseCallback<List<SyncUrl>>() {
                    @Override
                    public void onFinished(final List<SyncUrl> result) {
                        UiThread.getInstance().post(new Runnable() {
                            @Override
                            public void run() {
                                if (result != null && result.size() > 0) {

                                    RunServicesUtil runServicesUtil = new RunServicesUtil(prefs);
                                    if (view.enableSmsSync.isChecked()) {

                                        if (Util.isDefaultSmsApp(getActivity())) {
                                            // start sms receiver
                                            pm.setComponentEnabledSetting(smsReceiverComponent,
                                                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                                                    PackageManager.DONT_KILL_APP);

                                            prefs.serviceEnabled().set(true);
                                            view.enableSmsSync.setChecked(true);
                                            // because the services to be run depends on the state of the service so save the
                                            // changes first
                                            // run auto sync service
                                            runServicesUtil.runAutoSyncService();

                                            // run check task service
                                            runServicesUtil.runCheckTaskService();

                                            // show notification
                                            Util.showNotification(getActivity());
                                        } else {
                                            view.enableSmsSync.setChecked(false);
                                            prefs.serviceEnabled().set(false);
                                            Util.makeDefaultSmsApp(getActivity());
                                        }

                                    } else {
                                        // stop sms receiver
                                        pm.setComponentEnabledSetting(smsReceiverComponent,
                                                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                                PackageManager.DONT_KILL_APP);

                                        runServicesUtil.stopCheckTaskService();
                                        runServicesUtil.stopAutoSyncService();

                                        // stop check task schedule
                                        getActivity().stopService(
                                                new Intent(getActivity(),
                                                        CheckTaskScheduledService.class));
                                        getActivity().stopService(
                                                new Intent(getActivity(), CheckTaskService.class));

                                        Util.clearNotify(getActivity());
                                        prefs.serviceEnabled().set(false);
                                        view.enableSmsSync.setChecked(false);
                                    }
                                } else {
                                    toastLong(R.string.no_enabled_sync_url);
                                    prefs.serviceEnabled().set(false);
                                    view.enableSmsSync.setChecked(false);
                                }
                            }
                        });

                    }

                    @Override
                    public void onError(Exception exception) {
                        UiThread.getInstance().post(new Runnable() {
                            @Override
                            public void run() {
                                view.enableSmsSync.setChecked(false);
                            }
                        });

                    }
                });

    }

    public void loadTask(boolean loadSyncUrlByStatus) {
        view.emptyView.setVisibility(android.view.View.GONE);
        if (loadSyncUrlByStatus) {
            App.getDatabaseInstance().getSyncUrlInstance().fetchSyncUrlByStatus(
                    SyncUrl.Status.ENABLED, new DatabaseCallback<List<SyncUrl>>() {
                        @Override
                        public void onFinished(final List<SyncUrl> result) {
                            UiThread.getInstance().post(new Runnable() {
                                @Override
                                public void run() {
                                    view.listLoadingProgress.setVisibility(android.view.View.GONE);
                                    view.emptyView.setVisibility(View.VISIBLE);
                                    adapter.setItems(result);
                                    listView.setAdapter(adapter);
                                }
                            });

                        }

                        @Override
                        public void onError(Exception exception) {

                        }
                    });
        } else {
            App.getDatabaseInstance().getSyncUrlInstance().fetchSyncUrl(new DatabaseCallback<List<SyncUrl>>() {
                @Override
                public void onFinished(final List<SyncUrl> result) {
                    UiThread.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            view.listLoadingProgress.setVisibility(android.view.View.GONE);
                            view.emptyView.setVisibility(View.VISIBLE);
                            adapter.setItems(result);
                            listView.setAdapter(adapter);
                        }
                    });

                }

                @Override
                public void onError(Exception exception) {

                }
            });
        }
    }

    private void loadSyncUrls() {
        App.getDatabaseInstance().getSyncUrlInstance().fetchSyncUrl(new DatabaseCallback<List<SyncUrl>>() {
            @Override
            public void onFinished(final List<SyncUrl> result) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setItems(result);
                        listView.setAdapter(adapter);
                    }
                });

            }

            @Override
            public void onError(Exception exception) {

            }
        });
    }

    protected class DeleteTask {

        public void execute(boolean deletebyUuid) {
            getActivity().setProgressBarIndeterminateVisibility(true);
            if (adapter.getCount() == 0) {
                toastLong(R.string.no_sync_url_to_delete);
            } else {
                if (deletebyUuid) {
                    App.getDatabaseInstance().getSyncUrlInstance().deleteSyncUrlById(id,
                            new DatabaseCallback<Void>() {
                                @Override
                                public void onFinished(Void result) {
                                    UiThread.getInstance().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            view.emptyView.setVisibility(View.VISIBLE);
                                            toastLong(R.string.sync_url_deleted);
                                            loadSyncUrls();
                                        }
                                    });

                                }

                                @Override
                                public void onError(Exception exception) {
                                    UiThread.getInstance().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            view.emptyView.setVisibility(View.VISIBLE);
                                            toastLong(R.string.sync_url_deleted_failed);
                                        }
                                    });

                                }
                            });
                } else {
                    App.getDatabaseInstance().getSyncUrlInstance().deleteAllSyncUrl(new DatabaseCallback<Void>() {
                        @Override
                        public void onFinished(Void result) {
                            UiThread.getInstance().post(new Runnable() {
                                @Override
                                public void run() {
                                    view.emptyView.setVisibility(View.VISIBLE);
                                    toastLong(R.string.sync_url_deleted);
                                    loadSyncUrls();
                                }
                            });
                        }

                        @Override
                        public void onError(Exception exception) {
                            UiThread.getInstance().post(new Runnable() {
                                @Override
                                public void run() {
                                    view.emptyView.setVisibility(View.VISIBLE);
                                    toastLong(R.string.sync_url_deleted_failed);
                                }
                            });
                        }
                    });
                }
            }
        }

    }

    private class AddSyncUrlTask extends Task<String, String, Boolean> {

        protected boolean editSyncUrl = false;
        SyncScheme scheme;
        private AddSyncUrl addSyncUrl;
        private boolean status = false;

        protected AddSyncUrlTask(Activity activity, AddSyncUrl addSyncUrl, SyncScheme scheme) {
            super(activity);
            this.addSyncUrl = addSyncUrl;
            this.scheme = scheme;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            if (editSyncUrl && scheme != null) {

                status = addSyncUrl.updateSyncUrl(id, scheme);
            } else {
                status = addSyncUrl.addSyncUrl();
            }
            return status;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                loadSyncUrlInBackground();
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
