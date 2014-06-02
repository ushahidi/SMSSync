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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import org.addhen.smssync.Prefs;
import org.addhen.smssync.R;
import org.addhen.smssync.adapters.FilterAdapter;
import org.addhen.smssync.listeners.WhitelistActionModeListener;
import org.addhen.smssync.models.Filter;
import org.addhen.smssync.tasks.ProgressTask;
import org.addhen.smssync.tasks.Task;
import org.addhen.smssync.views.AddPhoneNumber;
import org.addhen.smssync.views.WhitelistView;

import java.util.LinkedHashSet;
import java.util.List;

import static org.addhen.smssync.models.Filter.Status.WHITELIST;

public class WhitelistFragment extends
        BaseListFragment<WhitelistView, Filter, FilterAdapter> implements
        View.OnClickListener, OnItemClickListener {

    private Filter model;

    private int id = 0;

    private boolean edit = false;

    private List<Filter> filters;

    private LinkedHashSet<Integer> mSelectedItemsPositions;

    private WhitelistActionModeListener multichoiceActionModeListener;

    public WhitelistFragment() {
        super(WhitelistView.class, FilterAdapter.class, R.layout.whitelist,
                R.menu.filter_menu, android.R.id.list);
        model = new Filter();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        Prefs.loadPreferences(getActivity());
        multichoiceActionModeListener = new WhitelistActionModeListener(this,
                listView);

        listView.setItemsCanFocus(false);
        listView.setLongClickable(true);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemLongClickListener(multichoiceActionModeListener);
        listView.setOnItemClickListener(this);

        view.enableWhitelist.setChecked(Prefs.enableWhitelist);
        view.enableWhitelist.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        loadInBackground();
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

    public boolean performAction(MenuItem item) {

        if (item.getItemId() == R.id.context_delete) {
            mSelectedItemsPositions = multichoiceActionModeListener.getSelectedItemPositions();
            if (Prefs.enableWhitelist && (adapter.getCount() == 1
                    || adapter.getCount() == mSelectedItemsPositions.size())) {
                showMessage(R.string.disable_whitelist);
            } else {
                performDeleteById();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.add_phone_number) {
            edit = false;
            addPhoneNumber();
            return (true);
        } else if (item.getItemId() == R.id.delete_all_phone_numbers) {
            // load all blacklisted phone numbers
            if (Prefs.enableWhitelist) {
                showMessage(R.string.disable_whitelist);
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

    public void addPhoneNumber() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View textEntryView = factory.inflate(R.layout.add_phone_number, null);
        final AddPhoneNumber addPhoneNumber = new AddPhoneNumber(textEntryView);
        // if edit was selected at the context menu, populate fields
        // with existing sync URL details
        if (edit) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    model.loadById(id);
                    filters = model.getFilterList();
                    if (filters != null && filters.size() > 0) {
                        addPhoneNumber.phoneNumber.setText(filters.get(0).getPhoneNumber());
                    }
                }
            });

        }

        final AlertDialog.Builder addBuilder = new AlertDialog.Builder(
                getActivity());
        addBuilder
                .setTitle(R.string.add_phone_number_list)
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

        final AlertDialog addDialog = addBuilder.create();
        addDialog.show();

        addDialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        // edit was selected
                        if (edit) {
                            AddPhoneNumberTask updateTask = new AddPhoneNumberTask(getActivity(),
                                    addPhoneNumber);
                            updateTask.editPhoneNumber = true;
                            updateTask.execute((String) null);

                        } else {
                            // add a new entry
                            AddPhoneNumberTask addTask = new AddPhoneNumberTask(getActivity(),
                                    addPhoneNumber);
                            addTask.execute((String) null);
                        }
                        addDialog.dismiss();
                    }


                });

    }

    // Display pending messages.
    public void loadInBackground() {
        new LoadingTask(getActivity()).execute((String) null);
    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {

        if (adapter.getCount() > 0) {

            load();
            if (model.getFilterList() != null && model.getFilterList().size() > 0) {
                if (view.enableWhitelist.isChecked()) {
                    Prefs.enableWhitelist = true;
                    view.enableWhitelist.setChecked(true);
                } else {

                    Prefs.enableWhitelist = false;
                    view.enableWhitelist.setChecked(false);
                }
            } else {
                toastLong(R.string.no_phone_number_to_enable_whitelist);
                Prefs.enableWhitelist = false;
                view.enableWhitelist.setChecked(false);
            }

        } else {
            toastLong(R.string.no_phone_number_to_enable_whitelist);
            Prefs.enableWhitelist = false;
            view.enableWhitelist.setChecked(false);
        }

        Prefs.savePreferences(getActivity());
    }

    private boolean load() {
        return model.loadByStatus(WHITELIST);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.id = adapter.getItem(position).getId();
        edit = true;
        addPhoneNumber();
    }

    private class LoadingTask extends ProgressTask {

        public LoadingTask(Activity activity) {
            super(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.cancel();
            view.emptyView.setVisibility(View.GONE);
        }

        @Override
        protected Boolean doInBackground(String... args) {
            return load();
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            view.listLoadingProgress.setVisibility(View.GONE);
            view.emptyView.setVisibility(View.VISIBLE);
            if (success) {

                adapter.setItems(model.getFilterList());
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
                    for (Integer position : mSelectedItemsPositions) {
                        model.deleteById(adapter.getItem(position).getId());
                    }
                } else {
                    model.deleteAll();
                }
                deleted = 2;
            }
            return load();
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            view.emptyView.setVisibility(View.VISIBLE);
            if (success) {
                if (deleted == 1) {
                    toastLong(R.string.no_phone_number_to_delete);
                } else {
                    if (deleted == 2) {
                        toastLong(R.string.phone_number_deleted);

                    } else {
                        toastLong(R.string.deleting_phone_number_failed);
                    }

                }
                adapter.setItems(model.getFilterList());
                if (multichoiceActionModeListener.activeMode != null) {
                    multichoiceActionModeListener.activeMode.finish();
                    multichoiceActionModeListener.getSelectedItemPositions().clear();
                }
            }
        }
    }

    private class AddPhoneNumberTask extends Task<String, String, Boolean> {

        protected boolean editPhoneNumber = false;

        private AddPhoneNumber addPhoneNumber;

        private boolean status = false;

        protected AddPhoneNumberTask(Activity activity, AddPhoneNumber addPhoneNumber) {
            super(activity);
            this.addPhoneNumber = addPhoneNumber;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            if (editPhoneNumber) {

                status = addPhoneNumber.update(id, WHITELIST);
            } else {
                status = addPhoneNumber.add(WHITELIST);
            }
            load();
            return status;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                adapter.setItems(model.getFilterList());
            } else {
                if (editPhoneNumber) {
                    toastLong(R.string.failed_to_update_phone_number);
                } else {
                    toastLong(R.string.failed_to_add_phone_number);
                }
            }
        }
    }

}
