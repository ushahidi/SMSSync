
package org.addhen.smssync.widget;

import java.io.File;
import java.util.ArrayList;

import org.addhen.smssync.MessagesTabActivity;
import org.addhen.smssync.R;
import org.addhen.smssync.Settings;
import org.addhen.smssync.MainApplication;
import org.addhen.smssync.data.Messages;
import org.addhen.smssync.data.Database;
import org.addhen.smssync.util.Util;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews;

public class SmsSyncAppWidgetProvider extends AppWidgetProvider {

    // log CLASS_TAG
    private static final String CLASS_TAG = SmsSyncAppWidgetProvider.class.getSimpleName();

    public static final String INTENT_TYPE = "type";

    public static final String INTENT_PREV = "PREV";

    public static final String INTENT_NEXT = "NEXT";

    public static final String INTENT_REFRESH = "REFRESH";

    public static File CACHE_DIR = null;

    static Context mContext;

    public static int WIDTH = 250;

    public static int HEIGHT = 160;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // why is appWidgetIds > 1
        for (int i = 0; i < appWidgetIds.length; ++i) {
            final int id = appWidgetIds[i];
            Intent intent = new Intent(context, SmsSyncAppWidgetService.class);
            intent.putExtra("id", id);
            context.startService(intent);
        }

        mContext = context;
        CACHE_DIR = context.getCacheDir();
        Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        WIDTH = display.getWidth();
        WIDTH = WIDTH - (WIDTH / 16); // tiles are 300/320 wide, so subtract a
                                      // sixteenth of the width to fetch right
                                      // image
        HEIGHT = display.getHeight();

    }

    @Override
    public void onReceive(Context ctxt, Intent intent) {
        final String action = intent.getAction();
        Log.i(CLASS_TAG, "onReceive:action=" + action);

        if (INTENT_PREV.equals(action) || INTENT_NEXT.equals(action)
                || INTENT_REFRESH.equals(action)) {
            Intent prevNextIntent = new Intent(ctxt, SmsSyncAppWidgetService.class);
            prevNextIntent.setAction(action);
            ctxt.startService(prevNextIntent);
        } else if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
            final int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                this.onDeleted(ctxt, new int[] {
                    appWidgetId
                });
            }
        } else {
            super.onReceive(ctxt, intent);
        }
    }

    public static ArrayList<Messages> pendingMsgs = new ArrayList<Messages>();

    public static ArrayList<Integer> pendingMsgIds;

    public static int pendingMsgIndex = 0;

    // implement next screen
    public static Messages getNextPendingMessages() {
        if (pendingMsgs != null && pendingMsgs.size() > 0) {
            pendingMsgIndex = (pendingMsgIndex + 1) % pendingMsgs.size();
            return pendingMsgs.get(pendingMsgIndex);
        }
        return null;
    }

    // implement previous screen.
    public static Messages getPrevPendingMessages() {
        if (pendingMsgs != null && pendingMsgs.size() > 0) {
            pendingMsgIndex = pendingMsgIndex - 1;
            pendingMsgIndex = pendingMsgIndex < 0 ? pendingMsgs.size() - 1 : pendingMsgIndex;
            return pendingMsgs.get(pendingMsgIndex);
        }
        return null;
    }

    // implement current screen
    public static Messages getCurrentPendingMessages() {

        if (pendingMsgs != null && pendingMsgs.size() > 0) {
            pendingMsgIndex = pendingMsgIndex < 0 ? 0 : pendingMsgIndex;
            return pendingMsgs.get(pendingMsgIndex);
        }
        return null;
    }

    public static class SmsSyncAppWidgetService extends IntentService {

        public SmsSyncAppWidgetService() {
            super(CLASS_TAG);
        }

        @Override
        public void onHandleIntent(Intent intent) {
            buildUpdate(intent, null);
        }

        private void buildUpdate(Intent intent, Integer startId) {
            ComponentName me = new ComponentName(this, SmsSyncAppWidgetProvider.class);
            AppWidgetManager mgr = AppWidgetManager.getInstance(this);
            mgr.updateAppWidget(me, updateDisplay(intent, startId));

            if (startId != null) {
                stopSelfResult(startId);
            }
        }

        private RemoteViews updateDisplay(Intent intent, Integer startId) {
            Log.i(CLASS_TAG, "Updating display");
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.appwidget);
            Messages mgs;
            String action = intent.getAction();
            if (INTENT_NEXT.equals(action)) {
                mgs = getNextPendingMessages();
            } else if (INTENT_PREV.equals(action)) {
                mgs = getPrevPendingMessages();
            } else {
                mgs = getCurrentPendingMessages();
            }
            
            // go to settings screen when configure icon is pressed
            Intent settingsScreen = new Intent(this, Settings.class);
            settingsScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent settingsAction = PendingIntent.getActivity(this, 0, settingsScreen, 0);
            views.setOnClickPendingIntent(R.id.appwidget_logo, settingsAction);

            Intent pendingMessages = new Intent(this, MessagesTabActivity.class);
            pendingMessages.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent mainAction = PendingIntent.getActivity(this, 0, pendingMessages, 0);
            views.setOnClickPendingIntent(R.id.appwidget_item, mainAction);

            if (mgs != null) {
                Log.i(CLASS_TAG, "messages are not null "+mgs.getMessageBody());
                // set number
                views.setViewVisibility(R.id.linear_pending_msg, View.VISIBLE);
                views.setTextViewText(R.id.msg_number, mgs.getMessageFrom());
                views.setTextViewText(R.id.msg_date, mgs.getMessageDate());
                views.setTextViewText(R.id.msg_desc, mgs.getMessageBody());

                // make all the views clickable
                views.setOnClickPendingIntent(R.id.msg_number, mainAction);
                views.setOnClickPendingIntent(R.id.msg_date, mainAction);
                views.setOnClickPendingIntent(R.id.msg_desc, mainAction);
                views.setViewVisibility(R.id.appwidget_empty_list, View.INVISIBLE);

            } else {
                Log.i(CLASS_TAG, "messages are null ");
                views.setViewVisibility(R.id.linear_pending_msg, View.INVISIBLE);
                views.setViewVisibility(R.id.appwidget_empty_list, View.VISIBLE);
                views.setOnClickPendingIntent(R.id.appwidget_empty_list, mainAction);
            }

            Intent prevIntent = new Intent(this, SmsSyncAppWidgetProvider.class);
            prevIntent.setAction(INTENT_PREV);
            PendingIntent pendingPrevIntent = PendingIntent.getBroadcast(this, 0, prevIntent, 0);
            views.setOnClickPendingIntent(R.id.appwidget_prev, pendingPrevIntent);

            Intent nextIntent = new Intent(this, SmsSyncAppWidgetProvider.class);
            nextIntent.setAction(INTENT_NEXT);
            PendingIntent pendingNextIntent = PendingIntent.getBroadcast(this, 0, nextIntent, 0);
            views.setOnClickPendingIntent(R.id.appwidget_next, pendingNextIntent);

            Intent refreshIntent = new Intent(this, SmsSyncAppWidgetProvider.class);
            refreshIntent.setAction(INTENT_REFRESH);
            PendingIntent pendingRefreshIntent = PendingIntent.getBroadcast(this, 0, refreshIntent,
                    0);
            views.setOnClickPendingIntent(R.id.appwidget_refresh, pendingRefreshIntent);

            return views;
        }

        @Override
        public void onStart(final Intent intent, final int startId) {
            final Runnable updateUI = new Runnable() {
                public void run() {
                    String action = intent.getAction();

                    if (INTENT_PREV.equals(action) || INTENT_NEXT.equals(action)) {
                        buildUpdate(intent, startId);
                    } else {
                        if (INTENT_REFRESH.equals(action)) {
                            pendingMsgIndex = 0;
                        }
                        pendingMsgs = showMessages();
                        buildUpdate(intent, startId);
                    }
                }
            };
            ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {
                new Thread(updateUI).start();
            } else {
                BroadcastReceiver networkChange = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        try {
                            NetworkInfo ni = intent
                                    .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                            if (ni != null && ni.isConnected()) {
                                new Thread(updateUI).start();
                                SmsSyncAppWidgetService.this.unregisterReceiver(this);
                            }
                        } catch (Exception e) {
                            Log.e(CLASS_TAG, "receiver error", e);
                        }
                    }
                };
                registerReceiver(networkChange, new IntentFilter(
                        ConnectivityManager.CONNECTIVITY_ACTION));
            }
        }
    }

    public static ArrayList<Messages> showMessages() {

        Cursor cursor;
        cursor = MainApplication.mDb.fetchMessagesByLimit(5);

        int messageId;
        String messagesFrom;
        String messagesDate;
        String messagesBody;

        if (cursor != null) {
            if (cursor.getCount() == 0) {
                pendingMsgs.clear();
            }
            Log.d(CLASS_TAG, "Got messages from Inbox");
            if (cursor.moveToFirst()) {
                int messagesIdIndex = cursor.getColumnIndexOrThrow(Database.MESSAGES_ID);
                int messagesFromIndex = cursor.getColumnIndexOrThrow(Database.MESSAGES_FROM);
                int messagesDateIndex = cursor.getColumnIndexOrThrow(Database.MESSAGES_DATE);

                int messagesBodyIndex = cursor.getColumnIndexOrThrow(Database.MESSAGES_BODY);
                do {
                    Messages messages = new Messages();
                    pendingMsgs.add(messages);
                    messageId = Util.toInt(cursor.getString(messagesIdIndex));
                    messages.setMessageId(messageId);

                    messagesFrom = Util.capitalizeString(cursor.getString(messagesFromIndex));
                    messages.setMessageFrom(messagesFrom);

                    messagesDate = cursor.getString(messagesDateIndex);
                    messages.setMessageDate(messagesDate);

                    messagesBody = cursor.getString(messagesBodyIndex);
                    messages.setMessageBody(messagesBody);

                } while (cursor.moveToNext());
            }

            cursor.close();

        }
        return pendingMsgs;
    }

}
