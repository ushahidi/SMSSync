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

package org.addhen.smssync.widget;

import org.addhen.smssync.App;
import org.addhen.smssync.R;
import org.addhen.smssync.Settings;
import org.addhen.smssync.activities.FilterTabActivity;
import org.addhen.smssync.models.Message;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;

public class SmsSyncAppWidgetProvider extends AppWidgetProvider {

    // log CLASS_TAG
    private static final String CLASS_TAG = SmsSyncAppWidgetProvider.class
            .getSimpleName();

    public static final String INTENT_TYPE = "type";

    public static final String INTENT_PREV = "PREV";

    public static final String INTENT_NEXT = "NEXT";

    public static final String INTENT_REFRESH = "REFRESH";

    private static final int LIMIT = 5;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {

        // why is appWidgetIds > 1
        for (int i = 0; i < appWidgetIds.length; ++i) {
            final int id = appWidgetIds[i];
            Intent intent = new Intent(context, SmsSyncAppWidgetService.class);
            intent.putExtra("id", id);
            context.startService(intent);
        }
    }

    @Override
    public void onReceive(Context ctxt, Intent intent) {
        final String action = intent.getAction();
        Log.i(CLASS_TAG, "onReceive:action=" + action);

        if (INTENT_PREV.equals(action) || INTENT_NEXT.equals(action)
                || INTENT_REFRESH.equals(action)) {
            Intent prevNextIntent = new Intent(ctxt,
                    SmsSyncAppWidgetService.class);
            prevNextIntent.setAction(action);
            ctxt.startService(prevNextIntent);
        } else if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
            final int appWidgetId = intent.getExtras().getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                this.onDeleted(ctxt, new int[]{appWidgetId});
            }
        } else {
            super.onReceive(ctxt, intent);
        }
    }

    public static List<Message> pendingMsgs = new ArrayList<Message>();

    public static List<Integer> pendingMsgIds;

    public static int pendingMsgIndex = 0;

    // implement next screen
    public static Message getNextPendingMessages() {
        if (pendingMsgs != null && pendingMsgs.size() > 0) {
            pendingMsgIndex = (pendingMsgIndex + 1) % pendingMsgs.size();
            return pendingMsgs.get(pendingMsgIndex);
        }
        return null;
    }

    // implement previous screen.
    public static Message getPrevPendingMessages() {
        if (pendingMsgs != null && pendingMsgs.size() > 0) {
            pendingMsgIndex = pendingMsgIndex - 1;
            pendingMsgIndex = pendingMsgIndex < 0 ? pendingMsgs.size() - 1
                    : pendingMsgIndex;
            return pendingMsgs.get(pendingMsgIndex);
        }
        return null;
    }

    // implement current screen
    public static Message getCurrentPendingMessages() {

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
            ComponentName me = new ComponentName(this,
                    SmsSyncAppWidgetProvider.class);
            AppWidgetManager mgr = AppWidgetManager.getInstance(this);
            mgr.updateAppWidget(me, updateDisplay(intent, startId));

            if (startId != null) {
                stopSelfResult(startId);
            }
        }

        private RemoteViews updateDisplay(Intent intent, Integer startId) {
            Log.i(CLASS_TAG, "Updating display");
            RemoteViews views = new RemoteViews(getPackageName(),
                    R.layout.appwidget);
            Message mgs;
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
            PendingIntent settingsAction = PendingIntent.getActivity(this, 0,
                    settingsScreen, 0);
            views.setOnClickPendingIntent(R.id.appwidget_settings,
                    settingsAction);

            Intent pendingMessages = new Intent(this, FilterTabActivity.class);
            pendingMessages.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent mainAction = PendingIntent.getActivity(this, 0,
                    pendingMessages, 0);
            views.setOnClickPendingIntent(R.id.appwidget_logo, mainAction);

            if (mgs != null) {
                Log.i(CLASS_TAG, "messages are not null " + mgs.getBody());
                // make views visible
                views.setViewVisibility(R.id.msg_number, View.VISIBLE);
                views.setViewVisibility(R.id.msg_date, View.VISIBLE);
                views.setViewVisibility(R.id.msg_desc, View.VISIBLE);

                // initialize views

                views.setTextViewText(R.id.msg_number, mgs.getPhoneNumber());
                views.setTextViewText(R.id.msg_date, Util.formatDateTime(mgs.getDate().getTime(),
                        "hh:mm a"));
                views.setTextViewText(R.id.msg_desc, mgs.getBody());

                // make all the views clickable
                views.setOnClickPendingIntent(R.id.msg_number, mainAction);
                views.setOnClickPendingIntent(R.id.msg_date, mainAction);
                views.setOnClickPendingIntent(R.id.msg_desc, mainAction);
                views.setViewVisibility(R.id.appwidget_empty_list,
                        View.INVISIBLE);

            } else {
                Log.i(CLASS_TAG, "messages are null ");
                views.setViewVisibility(R.id.msg_number, View.INVISIBLE);
                views.setViewVisibility(R.id.msg_date, View.INVISIBLE);
                views.setViewVisibility(R.id.msg_desc, View.INVISIBLE);
                views.setViewVisibility(R.id.appwidget_empty_list, View.VISIBLE);
                views.setOnClickPendingIntent(R.id.appwidget_empty_list,
                        mainAction);
            }

            Intent prevIntent = new Intent(this, SmsSyncAppWidgetProvider.class);
            prevIntent.setAction(INTENT_PREV);
            PendingIntent pendingPrevIntent = PendingIntent.getBroadcast(this,
                    0, prevIntent, 0);
            views.setOnClickPendingIntent(R.id.appwidget_prev,
                    pendingPrevIntent);

            Intent nextIntent = new Intent(this, SmsSyncAppWidgetProvider.class);
            nextIntent.setAction(INTENT_NEXT);
            PendingIntent pendingNextIntent = PendingIntent.getBroadcast(this,
                    0, nextIntent, 0);
            views.setOnClickPendingIntent(R.id.appwidget_next,
                    pendingNextIntent);

            Intent refreshIntent = new Intent(this,
                    SmsSyncAppWidgetProvider.class);
            refreshIntent.setAction(INTENT_REFRESH);
            PendingIntent pendingRefreshIntent = PendingIntent.getBroadcast(
                    this, 0, refreshIntent, 0);
            views.setOnClickPendingIntent(R.id.appwidget_refresh,
                    pendingRefreshIntent);

            return views;
        }

        @Override
        public void onStart(final Intent intent, final int startId) {
            final Runnable updateUI = new Runnable() {
                public void run() {
                    String action = intent.getAction();

                    if (INTENT_PREV.equals(action)
                            || INTENT_NEXT.equals(action)) {
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
            ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null
                    && cm.getActiveNetworkInfo().isConnected()) {
                new Thread(updateUI).start();
            } else {
                BroadcastReceiver networkChange = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        try {
                            ConnectivityManager connectivityManager = (ConnectivityManager) context
                                    .getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo ni = connectivityManager
                                    .getActiveNetworkInfo();
                            if (ni != null && ni.isConnected()) {
                                new Thread(updateUI).start();
                                SmsSyncAppWidgetService.this
                                        .unregisterReceiver(this);
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

    public static List<Message> showMessages() {

        final List<Message> messages = App.getDatabaseInstance().getMessageInstance().fetchByLimit(LIMIT);
        if (messages != null) {
            if (messages.size() == 0) {
                pendingMsgs.clear();
            }
            pendingMsgs = messages;

        }
        return pendingMsgs;
    }

}
