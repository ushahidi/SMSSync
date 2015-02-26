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

import org.addhen.smssync.App;
import org.addhen.smssync.R;
import org.addhen.smssync.UiThread;
import org.addhen.smssync.adapters.FilterAdapter;
import org.addhen.smssync.database.BaseDatabseHelper;
import org.addhen.smssync.listeners.BlacklistActionModeListener;
import org.addhen.smssync.models.Filter;
import org.addhen.smssync.tasks.Task;
import org.addhen.smssync.util.Logger;
import org.addhen.smssync.views.AddPhoneNumber;
import org.addhen.smssync.views.BlacklistView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.LinkedHashSet;
import java.util.List;

import static org.addhen.smssync.models.Filter.Status.BLACKLIST;

public class BlacklistFragment extends
        BaseListFragment<BlacklistView, Filter, FilterAdapter> implements
        View.OnClickListener, AdapterView.OnItemClickListener {


    private Long id;

    private boolean edit = false;


    private LinkedHashSet<Integer> mSelectedItemsPositions;

    private BlacklistActionModeListener multichoiceActionModeListener;

    public BlacklistFragment() {
        super(BlacklistView.class, FilterAdapter.class, R.layout.list_filter,
                R.menu.filter_menu, android.R.id.list);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        multichoiceActionModeListener = new BlacklistActionModeListener(this,
                listView);
        listView.setItemsCanFocus(false);
        listView.setLongClickable(true);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemLongClickListener(multichoiceActionModeListener);
        listView.setOnItemClickListener(this);

        view.enableBlacklist.setChecked(prefs.enableBlacklist().get());
        view.enableBlacklist.setOnClickListener(this);

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
            if (prefs.enableBlacklist().get() && (adapter.getCount() == 1
                    || adapter.getCount() == mSelectedItemsPositions.size())) {
                showMessage(R.string.disable_blacklist);
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
            if (prefs.enableBlacklist().get()) {
                showMessage(R.string.disable_blacklist);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
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
                                deleteTask(false);

                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Delete message by it's id
     */
    public void performDeleteById() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BlacklistFragment.this.getActivity());
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
                                deleteTask(true);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
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
        LayoutInflater factory = LayoutInflater.from(this.getActivity());
        final View textEntryView = factory.inflate(R.layout.add_phone_number, null);
        final AddPhoneNumber addPhoneNumber = new AddPhoneNumber(textEntryView);
        // if edit was selected at the context menu, populate fields
        // with existing sync URL details
        if (edit) {
            App.getDatabaseInstance().getFilterInstance().fetchById(id, new BaseDatabseHelper.DatabaseCallback<Filter>() {
                @Override
                public void onFinished(final Filter result) {
                    UiThread.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            if(result !=null) {
                                addPhoneNumber.phoneNumber.setText(result.getPhoneNumber());
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
                this.getActivity());
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
                            AddPhoneNumberTask updateTask = new AddPhoneNumberTask(
                                    BlacklistFragment.this.getActivity(),
                                    addPhoneNumber);
                            updateTask.editPhoneNumber = true;
                            updateTask.execute((String) null);

                        } else {
                            // add a new entry
                            AddPhoneNumberTask addTask = new AddPhoneNumberTask(
                                    BlacklistFragment.this.getActivity(),
                                    addPhoneNumber);
                            addTask.execute((String) null);
                        }
                        addDialog.dismiss();
                    }


                });

    }

    // Display pending messages.
    public void loadInBackground() {
        load();
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
            App.getDatabaseInstance().getFilterInstance().fetchByStatus(Filter.Status.BLACKLIST,
                    new BaseDatabseHelper.DatabaseCallback<List<Filter>>() {
                @Override
                public void onFinished(final List<Filter> result) {
                    UiThread.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            if(result!=null && result.size() > 0 ) {
                                if (view.enableBlacklist.isChecked()) {
                                    // start sms receiver

                                    prefs.enableBlacklist().set(true);
                                    view.enableBlacklist.setChecked(true);


                                } else {

                                    prefs.enableBlacklist().set(false);
                                    view.enableBlacklist.setChecked(false);
                                }
                            } else {
                                toastLong(R.string.no_phone_number_to_enable_blacklist);
                                prefs.enableBlacklist().set(false);
                                view.enableBlacklist.setChecked(false);
                            }
                        }
                    });

                }

                @Override
                public void onError(Exception exception) {

                }
            });

        } else {
            toastLong(R.string.no_phone_number_to_enable_blacklist);
            prefs.serviceEnabled().set(false);
            view.enableBlacklist.setChecked(false);
        }
    }

    private void load() {
        view.emptyView.setVisibility(View.GONE);
        App.getDatabaseInstance().getFilterInstance().fetchByStatus(Filter.Status.BLACKLIST,
                new BaseDatabseHelper.DatabaseCallback<List<Filter>>() {
            @Override
            public void onFinished(final List<Filter> result) {
                UiThread.getInstance().post(new Runnable() {
                    @Override
                    public void run() {
                        view.listLoadingProgress.setVisibility(View.GONE);
                        view.emptyView.setVisibility(View.VISIBLE);
                        adapter.setItems(result);
                    }
                });

            }

            @Override
            public void onError(Exception exception) {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.id = adapter.getItem(position).getId();
        edit = true;
        addPhoneNumber();
    }

    private void deleteTask(boolean deleteByUuid) {
        getActivity().setProgressBarIndeterminate(true);
        if(adapter.getCount() == 0) {
            toastLong(R.string.no_phone_number_to_delete);
        } else {
            if(deleteByUuid) {
                for(final Integer position: mSelectedItemsPositions) {
                    App.getDatabaseInstance().getFilterInstance().deleteById(adapter.getItem(position).getId(),new BaseDatabseHelper.DatabaseCallback<Void>() {
                        @Override
                        public void onFinished(Void result) {
                            UiThread.getInstance().post(new Runnable() {
                                @Override
                                public void run() {
                                    toastLong(R.string.phone_number_deleted);
                                    load();
                                    if (multichoiceActionModeListener.activeMode != null) {
                                        multichoiceActionModeListener.activeMode.finish();
                                        multichoiceActionModeListener.getSelectedItemPositions().clear();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onError(Exception exception) {

                        }
                    });
                }


            } else {
                App.getDatabaseInstance().getFilterInstance().deleteAllBlackList(
                        new BaseDatabseHelper.DatabaseCallback<Void>() {
                            @Override
                            public void onFinished(Void result) {
                                UiThread.getInstance().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        toastLong(R.string.phone_number_deleted);
                                        load();
                                        if (multichoiceActionModeListener.activeMode != null) {
                                            multichoiceActionModeListener.activeMode.finish();
                                            multichoiceActionModeListener.getSelectedItemPositions()
                                                    .clear();
                                        }
                                    }
                                });

                            }

                            @Override
                            public void onError(Exception exception) {
                                UiThread.getInstance().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        toastLong(R.string.deleting_phone_number_failed);
                                    }
                                });

                            }
                        });
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

                status = addPhoneNumber.update(id, BLACKLIST);
            } else {
                status = addPhoneNumber.add(BLACKLIST);
            }

            return status;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                load();
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
