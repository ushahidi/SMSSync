/** 
 ** Copyright (c) 2010 Ushahidi Inc
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
 **/

package org.addhen.smssync;

import java.util.ArrayList;
import java.util.List;

import org.addhen.smssync.data.Messages;
import org.addhen.smssync.data.SmsSyncDatabase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This class test various aspects of task that needs be executed for pending
 * messages to show. 1. The submits dummy messages to the pending database. 2.
 * Shows the messages that failed to be sent 3. Synchronizes the pending
 * messages. This class shows list of pending messages. Allows deletion and
 * synchronization of pending messages.
 * 
 * @author eyedol
 */
public class SmsSyncOutbox extends Activity {

    /** Called when the activity is first created. */
    private int messageId = 0;

    private int listItemPosition = 0;

    private static ListView listMessages = null;

    private static List<Messages> mOldMessages;

    private static ListMessagesAdapter ila;

    private static TextView emptyListText;

    // Menu items
    private static final int SMSSYNC_SYNC_ALL = Menu.FIRST + 1;

    private static final int MESSAGES_IMPORT = Menu.FIRST + 2;

    private static final int DELETE_ALL = Menu.FIRST + 3;

    private static final int SETTINGS = Menu.FIRST + 4;

    // Context menu items
    private static final int DELETE = Menu.FIRST + 5;

    private static final int SMSSYNC_SYNC = Menu.FIRST + 6;

    private final Handler mHandler = new Handler();

    public static SmsSyncDatabase mDb;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setTitle(R.string.outbox);
        setContentView(R.layout.list_messages);
        SmsSyncPref.loadPreferences(SmsSyncOutbox.this);

        // show notification
        if (SmsSyncPref.enabled) {
            Util.showNotification(SmsSyncOutbox.this);
        }

        listMessages = (ListView)findViewById(R.id.view_messages);
        emptyListText = (TextView)findViewById(R.id.empty);

        mOldMessages = new ArrayList<Messages>();
        ila = new ListMessagesAdapter(SmsSyncOutbox.this);
        registerForContextMenu(listMessages);

        mHandler.post(mDisplayMessages);
        displayEmptyListText();

    }

    public static void displayEmptyListText() {

        if (ila.getCount() == 0) {
            emptyListText.setVisibility(View.VISIBLE);
        } else {
            emptyListText.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.post(mDisplayMessages);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.post(mDisplayMessages);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // Display pending messages.
    final Runnable mDisplayMessages = new Runnable() {
        public void run() {
            setProgressBarIndeterminateVisibility(true);
            showMessages();
            try {
                setProgressBarIndeterminateVisibility(false);
            } catch (Exception e) {
                return; // means that the dialog is not showing, ignore please!
            }
        }
    };

    // Synchronize all pending messages.
    final Runnable mSyncMessages = new Runnable() {
        public void run() {
            SmsSyncPref.loadPreferences(SmsSyncOutbox.this);
            if (SmsSyncPref.enabled) {
                int result = syncMessages(false);
                try {
                    if (result == 0) {
                        Util.showToast(SmsSyncOutbox.this, R.string.sending_succeeded);

                    } else if (result == 1) {
                        Util.showToast(SmsSyncOutbox.this, R.string.sending_failed);
                    } else if (result == 2) {
                        Util.showToast(SmsSyncOutbox.this, R.string.no_messages_to_sync);
                    }
                } catch (Exception e) {
                    return;
                }
            } else {
                Util.showToast(SmsSyncOutbox.this, R.string.smssync_not_enabled);
            }
        }
    };

    /**
     * Synchronize all pending messages by message id. Which means it
     * synchronizes messages individually.
     */
    final Runnable mSyncMessagesById = new Runnable() {
        public void run() {
            SmsSyncPref.loadPreferences(SmsSyncOutbox.this);
            if (SmsSyncPref.enabled) {
                int result = syncMessages(true);
                try {
                    if (result == 0) {
                        Util.showToast(SmsSyncOutbox.this, R.string.sending_succeeded);

                    } else if (result == 1) {
                        Util.showToast(SmsSyncOutbox.this, R.string.sending_failed);
                    } else if (result == 2) {
                        Util.showToast(SmsSyncOutbox.this, R.string.no_messages_to_sync);
                    }
                } catch (Exception e) {
                    return;
                }
            } else {
                Util.showToast(SmsSyncOutbox.this, R.string.smssync_not_enabled);
            }
        }
    };

    /**
     * Delete all messages. 0 - Successfully deleted. 1 - There is nothing to be
     * deleted.
     */
    final Runnable mDeleteAllMessages = new Runnable() {
        public void run() {
            setProgressBarIndeterminateVisibility(true);
            boolean result = false;

            int deleted = 0;

            if (SmsSyncApplication.mDb.fetchMessagesCount() == 0) {
                deleted = 1;
            } else {
                result = deleteAllMessages();
            }

            try {
                if (deleted == 1) {
                    Util.showToast(SmsSyncOutbox.this, R.string.no_messages_to_delete);
                } else {
                    if (result) {
                        Util.showToast(SmsSyncOutbox.this, R.string.messages_deleted);
                        ila.removeItems();
                        ila.notifyDataSetChanged();
                        displayEmptyListText();
                    } else {
                        Util.showToast(SmsSyncOutbox.this, R.string.messages_deleted_failed);
                    }
                }
                setProgressBarIndeterminateVisibility(false);
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
            setProgressBarIndeterminateVisibility(true);
            boolean result = false;

            int deleted = 0;

            if (SmsSyncApplication.mDb.fetchMessagesCount() == 0) {
                deleted = 1;
            } else {
                result = deleteMessagesById(messageId);
            }

            try {
                if (deleted == 1) {
                    Util.showToast(SmsSyncOutbox.this, R.string.no_messages_to_delete);
                } else {

                    if (result) {
                        Util.showToast(SmsSyncOutbox.this, R.string.messages_deleted);
                        ila.removetItemAt(listItemPosition);
                        ila.notifyDataSetChanged();
                        showMessages();
                        displayEmptyListText();

                    } else {
                        Util.showToast(SmsSyncOutbox.this, R.string.messages_deleted_failed);
                    }
                }
                setProgressBarIndeterminateVisibility(false);
            } catch (Exception e) {
                return;
            }
        }
    };

    // menu stuff
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(Menu.NONE, SMSSYNC_SYNC, Menu.NONE, R.string.menu_sync);
        menu.add(Menu.NONE, SMSSYNC_SYNC_ALL, Menu.NONE, R.string.menu_sync_all);
        menu.add(Menu.NONE, DELETE, Menu.NONE, R.string.menu_delete);
        menu.add(Menu.NONE, DELETE_ALL, Menu.NONE, R.string.menu_delete_all);
    }

    // context menu stuff.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        populateMenu(menu);
        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return (applyMenuChoice(item) || super.onOptionsItemSelected(item));
    }

    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item
                .getMenuInfo();
        messageId = mOldMessages.get(info.position).getMessageId();
        listItemPosition = info.position;

        switch (item.getItemId()) {
            // context menu selected
            case DELETE:
                // Delete by ID
                mHandler.post(mDeleteMessagesById);
                return (true);

            case DELETE_ALL:
                mHandler.post(mDeleteAllMessages);
                return (true);

            case SMSSYNC_SYNC:
                // Synchronize by ID
                SyncTask syncTask = new SyncTask();
                syncTask.byId = true;
                syncTask.execute();
                return (true);

            case SMSSYNC_SYNC_ALL:
                SyncTask syncAllTask = new SyncTask();
                syncAllTask.byId = false;
                syncAllTask.execute();
                return (true);

        }
        return true;

    }

    /**
     * Generate menus
     * 
     * @param Menu menu
     * @return void
     */
    private void populateMenu(Menu menu) {
        MenuItem i;
        i = menu.add(Menu.NONE, SETTINGS, Menu.NONE, R.string.menu_settings);
        i.setIcon(android.R.drawable.ic_menu_preferences);

        i = menu.add(Menu.NONE, SMSSYNC_SYNC_ALL, Menu.NONE, R.string.menu_sync_all);
        i.setIcon(android.R.drawable.ic_menu_send);

        i = menu.add(Menu.NONE, MESSAGES_IMPORT, Menu.NONE, R.string.menu_import);
        i.setIcon(android.R.drawable.ic_input_get);

        i = menu.add(Menu.NONE, DELETE_ALL, Menu.NONE, R.string.menu_delete_all);
        i.setIcon(android.R.drawable.ic_menu_delete);

    }

    /**
     * Execute a task upon selection of a menu item.
     * 
     * @param MenuItem item - The selected menu item.
     * @return boolean
     */
    private boolean applyMenuChoice(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {

            case SMSSYNC_SYNC_ALL:

                SyncTask syncTask = new SyncTask();
                syncTask.execute();
                return (true);

            case MESSAGES_IMPORT:

                ImportMessagesTask importMessagesTask = new ImportMessagesTask();
                importMessagesTask.appContext = this;
                importMessagesTask.execute();
                return (true);

            case SETTINGS:

                intent = new Intent(SmsSyncOutbox.this, Settings.class);
                startActivity(intent);
                return (true);

            case DELETE_ALL:

                mHandler.post(mDeleteAllMessages);
                return (true);

        }

        return (false);
    }

    /**
     * Get messages from the database.
     * 
     * @return void
     */
    public static void showMessages() {

        Cursor cursor;
        cursor = SmsSyncApplication.mDb.fetchAllMessages();

        String messagesFrom;
        String messagesDate;
        String messagesBody;
        int messageId;
        if (cursor.moveToFirst()) {
            int messagesIdIndex = cursor.getColumnIndexOrThrow(SmsSyncDatabase.MESSAGES_ID);
            int messagesFromIndex = cursor.getColumnIndexOrThrow(SmsSyncDatabase.MESSAGES_FROM);
            int messagesDateIndex = cursor.getColumnIndexOrThrow(SmsSyncDatabase.MESSAGES_DATE);

            int messagesBodyIndex = cursor.getColumnIndexOrThrow(SmsSyncDatabase.MESSAGES_BODY);

            if (ila != null) {
                ila.removeItems();
                ila.notifyDataSetChanged();
            }

            mOldMessages.clear();

            do {

                Messages messages = new Messages();
                mOldMessages.add(messages);

                messageId = Util.toInt(cursor.getString(messagesIdIndex));
                messages.setMessageId(messageId);

                messagesFrom = Util.capitalizeString(cursor.getString(messagesFromIndex));
                messages.setMessageFrom(messagesFrom);

                messagesDate = cursor.getString(messagesDateIndex);
                messages.setMessageDate(messagesDate);

                messagesBody = cursor.getString(messagesBodyIndex);
                messages.setMessageBody(messagesBody);

                ila.addItem(new ListMessagesText(messagesFrom, messagesBody, messagesDate,
                        messageId));

            } while (cursor.moveToNext());
        }

        cursor.close();
        ila.notifyDataSetChanged();
        listMessages.setAdapter(ila);
        displayEmptyListText();
    }

    /**
     * Get messages from the db and push them to the configured callback URL
     * 
     * @param boolean byId
     * @return int
     */

    public int syncMessages(boolean byId) {

        Cursor cursor;

        // check if it should sync by id
        if (byId) {
            cursor = SmsSyncApplication.mDb.fetchMessagesById(messageId);
        } else {
            cursor = SmsSyncApplication.mDb.fetchAllMessages();
        }
        String messagesFrom;
        String messagesBody;
        String messagesDate;

        if (cursor.getCount() == 0) {
            return 2; // no pending messages to synchronize
        }

        int deleted = 0;
        mOldMessages.clear();
        if (cursor.moveToFirst()) {
            int messagesIdIndex = cursor.getColumnIndexOrThrow(SmsSyncDatabase.MESSAGES_ID);
            int messagesFromIndex = cursor.getColumnIndexOrThrow(SmsSyncDatabase.MESSAGES_FROM);

            int messagesBodyIndex = cursor.getColumnIndexOrThrow(SmsSyncDatabase.MESSAGES_BODY);

            int messagesDateIndex = cursor.getColumnIndexOrThrow(SmsSyncDatabase.MESSAGES_DATE);

            do {

                Messages messages = new Messages();
                mOldMessages.add(messages);

                int messageId = Util.toInt(cursor.getString(messagesIdIndex));
                messages.setMessageId(messageId);

                messagesFrom = Util.capitalizeString(cursor.getString(messagesFromIndex));
                messages.setMessageFrom(messagesFrom);

                messagesDate = cursor.getString(messagesDateIndex);
                messages.setMessageDate(messagesDate);

                messagesBody = cursor.getString(messagesBodyIndex);
                messages.setMessageBody(messagesBody);

                // post to web service
                if (Util.postToAWebService(messagesFrom, messagesBody, SmsSyncOutbox.this)) {
                    // if it successfully pushes a message, delete message from
                    // the db.
                    if (byId) {
                        ila.removetItemAt(listItemPosition);
                    } else {
                        ila.removeItems();
                    }
                    ila.notifyDataSetChanged();
                    SmsSyncApplication.mDb.deleteMessagesById(messageId);
                    deleted = 0; // successfully posted messages to the web
                                 // service.
                } else {
                    deleted = 1; // failed to post the messages to the web
                                 // service.
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
        ila.notifyDataSetChanged();
        return deleted;
    }

    /**
     * Delete all pending messages.
     * 
     * @return boolean
     */
    public boolean deleteAllMessages() {

        return SmsSyncApplication.mDb.deleteAllMessages();
    }

    /**
     * Delete messages by id
     * 
     * @param int messageId - Message to be deleted ID
     * @return boolean
     */
    public boolean deleteMessagesById(int messageId) {
        return SmsSyncApplication.mDb.deleteMessagesById(messageId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);
    }

    // Thread class to handle asynchronous task execution.
    private class SyncTask extends AsyncTask<Void, Void, Integer> {

        protected Integer status;

        protected Boolean byId = false;

        @Override
        protected void onPreExecute() {

            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Integer doInBackground(Void... params) {

            status = 0;

            if (byId) {
                mHandler.post(mSyncMessagesById);
            } else {
                mHandler.post(mSyncMessages);
            }
            return status;
        }

        @Override
        protected void onPostExecute(Integer result) {
            showMessages();
            setProgressBarIndeterminateVisibility(false);
        }
    }

    // Thread class to handle synchronous execution of message importation task.
    private class ImportMessagesTask extends AsyncTask<Void, Void, Integer> {

        protected Integer status;

        private ProgressDialog dialog;

        protected Context appContext;

        @Override
        protected void onPreExecute() {

            this.dialog = ProgressDialog.show(appContext, getString(R.string.please_wait),
                    getString(R.string.import_messages), false);
        }

        @Override
        protected Integer doInBackground(Void... params) {

            status = Util.importMessages(appContext);
            return status;
        }

        @Override
        protected void onPostExecute(Integer result) {

            if (result == 0) {
                this.dialog.cancel();
                showMessages();
            } else if (result == 1) {
                this.dialog.cancel();
                Util.showToast(SmsSyncOutbox.this, R.string.nothing_to_import);
            }
        }
    }

}
