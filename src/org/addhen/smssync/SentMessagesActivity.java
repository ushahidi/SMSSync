
package org.addhen.smssync;

import java.util.ArrayList;
import java.util.List;

import org.addhen.smssync.data.Messages;
import org.addhen.smssync.data.Database;
import org.addhen.smssync.util.ServicesConstants;
import org.addhen.smssync.util.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class SentMessagesActivity extends Activity {

    private int messageId = 0;

    private int listItemPosition = 0;

    private static ListView listMessages = null;

    private static List<Messages> mOldMessages;

    private static ListMessagesAdapter ila;

    private static TextView emptyListText;

    // Menu items
    private static final int DELETE_ALL = Menu.FIRST + 1;

    private static final int SETTINGS = Menu.FIRST + 2;

    // Context menu items
    private static final int DELETE = Menu.FIRST + 3;

    private final Handler mHandler = new Handler();

    public static Database mDb;

    public static final String CLASS_TAG = SentMessagesActivity.class.getSimpleName();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.sent_messages);
        Prefrences.loadPreferences(SentMessagesActivity.this);

        // show notification
        if (Prefrences.enabled) {
            Util.showNotification(SentMessagesActivity.this);
        }

        listMessages = (ListView)findViewById(R.id.view_sent_messages);
        emptyListText = (TextView)findViewById(R.id.empty_sent_messages);

        mOldMessages = new ArrayList<Messages>();
        ila = new ListMessagesAdapter(this);
        registerForContextMenu(listMessages);

        mHandler.post(mDisplayMessages);
        displayEmptyListText();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(ServicesConstants.AUTO_SYNC_ACTION));
        mHandler.post(mDisplayMessages);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        mHandler.post(mDisplayMessages);
    }

    public static void displayEmptyListText() {

        if (ila.getCount() == 0) {
            emptyListText.setVisibility(View.VISIBLE);
        } else {
            emptyListText.setVisibility(View.GONE);
        }

    }

    // menu stuff
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
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
        Log.i(CLASS_TAG, "delete sent messages by id via context menu " + messageId);
        switch (item.getItemId()) {
            // context menu selected
            case DELETE:
                // Delete by ID
                performDeleteById();
                return (true);

            case DELETE_ALL:
                performDeleteAll();
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

            case SETTINGS:

                intent = new Intent(this, Settings.class);
                startActivity(intent);
                return (true);

            case DELETE_ALL:

                performDeleteAll();
                return (true);

        }

        return (false);
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

    // Display pending messages.
    final Runnable mUpdateListView = new Runnable() {
        public void run() {
            updateListView();
        }
    };

    /**
     * Delete all messages. 0 - Successfully deleted. 1 - There is nothing to be
     * deleted.
     */
    final Runnable mDeleteAllSentMessages = new Runnable() {
        public void run() {
            setProgressBarIndeterminateVisibility(true);
            boolean result = false;

            int deleted = 0;

            if (MainApplication.mDb.fetchSentMessagesCount() == 0) {
                deleted = 1;
            } else {
                result = deleteAllSentMessages();
            }

            try {
                if (deleted == 1) {
                    Util.showToast(SentMessagesActivity.this, R.string.no_messages_to_delete);
                } else {
                    if (result) {
                        Util.showToast(SentMessagesActivity.this, R.string.messages_deleted);
                        ila.removeItems();
                        ila.notifyDataSetChanged();
                        displayEmptyListText();
                    } else {
                        Util.showToast(SentMessagesActivity.this, R.string.messages_deleted_failed);
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

            if (MainApplication.mDb.fetchSentMessagesCount() == 0) {
                deleted = 1;
            } else {
                result = deleteSentMessagesById(messageId);
            }

            try {
                if (deleted == 1) {
                    Util.showToast(SentMessagesActivity.this, R.string.no_messages_to_delete);
                } else {

                    if (result) {
                        Util.showToast(SentMessagesActivity.this, R.string.messages_deleted);
                        ila.removetItemAt(listItemPosition);
                        ila.notifyDataSetChanged();
                        showMessages();
                        displayEmptyListText();

                    } else {
                        Util.showToast(SentMessagesActivity.this, R.string.messages_deleted_failed);
                    }
                }
                setProgressBarIndeterminateVisibility(false);
            } catch (Exception e) {
                return;
            }
        }
    };

    /**
     * Delete all messages
     */
    public void performDeleteAll() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Get messages from the database.
     * 
     * @return void
     */
    public static void showMessages() {

        Cursor cursor;
        cursor = MainApplication.mDb.fetchAllSentMessages();

        String messagesFrom;
        String messagesDate;
        String messagesBody;
        int messageId;
        if (cursor.moveToFirst()) {
            int messagesIdIndex = cursor.getColumnIndexOrThrow(Database.SENT_MESSAGES_ID);
            int messagesFromIndex = cursor.getColumnIndexOrThrow(Database.SENT_MESSAGES_FROM);
            int messagesDateIndex = cursor.getColumnIndexOrThrow(Database.SENT_MESSAGES_DATE);

            int messagesBodyIndex = cursor.getColumnIndexOrThrow(Database.SENT_MESSAGES_BODY);

            if (ila != null) {
                ila.removeItems();
                ila.notifyDataSetChanged();
            }

            if (mOldMessages != null)
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
     * Delete all pending messages.
     * 
     * @return boolean
     */
    public boolean deleteAllSentMessages() {

        return MainApplication.mDb.deleteAllSentMessages();
    }

    /**
     * Delete sent messages by id
     * 
     * @param int messageId - Message to be deleted ID
     * @return boolean
     */
    public boolean deleteSentMessagesById(int messageId) {
        return MainApplication.mDb.deleteSentMessagesById(messageId);
    }

    /**
     * Get messages from the database.
     * 
     * @return void
     */
    public static void updateListView() {

        Cursor cursor;
        cursor = MainApplication.mDb.fetchAllSentMessages();

        String messagesFrom;
        String messagesDate;
        String messagesBody;
        int messageId;
        if (cursor.getCount() == 0) {
            ila.removeItems();
        }

        if (cursor.moveToFirst()) {
            int messagesIdIndex = cursor.getColumnIndexOrThrow(Database.SENT_MESSAGES_ID);
            int messagesFromIndex = cursor.getColumnIndexOrThrow(Database.SENT_MESSAGES_FROM);
            int messagesDateIndex = cursor.getColumnIndexOrThrow(Database.SENT_MESSAGES_DATE);

            int messagesBodyIndex = cursor.getColumnIndexOrThrow(Database.SENT_MESSAGES_BODY);

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
        displayEmptyListText();
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

                    Util.showToast(SentMessagesActivity.this, R.string.sending_succeeded);
                } else if (status == 1) {
                    Util.showToast(SentMessagesActivity.this, R.string.sync_failed);
                } else {
                    Util.showToast(SentMessagesActivity.this, R.string.no_messages_to_sync);
                }
                mHandler.post(mUpdateListView);
            }
        }
    };
}
