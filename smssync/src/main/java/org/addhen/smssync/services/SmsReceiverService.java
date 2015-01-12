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

package org.addhen.smssync.services;

import com.squareup.otto.Produce;

import org.addhen.smssync.MainApplication;
import org.addhen.smssync.R;
import org.addhen.smssync.controllers.DebugCallbacks;
import org.addhen.smssync.messages.ProcessMessage;
import org.addhen.smssync.messages.ProcessSms;
import org.addhen.smssync.util.Logger;
import org.addhen.smssync.util.ServicesConstants;
import org.addhen.smssync.util.Util;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.telephony.SmsMessage;

import java.lang.ref.WeakReference;

public class SmsReceiverService extends Service {

    private static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    private static final Object mStartingServiceSync = new Object();

    private static final String CLASS_TAG = SmsReceiverService.class
            .getSimpleName();

    private static PowerManager.WakeLock mStartingService;

    private static WifiManager.WifiLock wifilock;

    private ServiceHandler mServiceHandler;

    private Looper mServiceLooper;

    private Context mContext;

    private String messagesBody = "";

    private String messagesUuid = "";

    private ProcessMessage mProcessMessage;

    private SmsMessage sms;

    private Intent statusIntent;

    synchronized protected static WifiManager.WifiLock getWifiLock(
            Context context) {
        // keep wifi alive
        if (wifilock == null) {
            WifiManager manager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            wifilock = manager.createWifiLock(CLASS_TAG);
            wifilock.setReferenceCounted(true);
        }
        return wifilock;
    }

    /**
     * Get the SMS message.
     *
     * @param intent - The SMS message intent.
     * @return SmsMessage
     */
    public static final SmsMessage[] getMessagesFromIntent(Intent intent) {

        new SmsReceiverService()
                .log("getMessagesFromIntent(): getting SMS message");

        Object[] messages = (Object[]) intent.getSerializableExtra("pdus");

        if (messages == null) {
            return null;
        }

        if (messages.length == 0) {
            return null;
        }

        byte[][] pduObjs = new byte[messages.length][];

        for (int i = 0; i < messages.length; i++) {
            pduObjs[i] = (byte[]) messages[i];
        }

        byte[][] pdus = new byte[pduObjs.length][];
        int pduCount = pdus.length;

        SmsMessage[] msgs = new SmsMessage[pduCount];
        for (int i = 0; i < pduCount; i++) {
            pdus[i] = pduObjs[i];
            msgs[i] = SmsMessage.createFromPdu(pdus[i]);
        }
        return msgs;
    }

    /**
     * Start the service to process the current event notifications, acquiring the wake lock before
     * returning to ensure that the service will run.
     *
     * @param context - The context of the calling activity.
     * @param intent  - The calling intent.
     * @return void
     */
    public static void beginStartingService(Context context, Intent intent) {
        synchronized (mStartingServiceSync) {

            if (mStartingService == null) {
                PowerManager pm = (PowerManager) context
                        .getSystemService(Context.POWER_SERVICE);
                mStartingService = pm.newWakeLock(
                        PowerManager.PARTIAL_WAKE_LOCK, CLASS_TAG);
                mStartingService.setReferenceCounted(false);
            }

            mStartingService.acquire();
            if (!getWifiLock(context).isHeld()) {
                getWifiLock(context).acquire();
            }
            context.startService(intent);
        }
    }

    /**
     * Called back by the service when it has finished processing notifications, releasing the wake
     * lock and wifi lock if the service is now stopping.
     *
     * @param service - The calling service.
     * @param startId - The service start id.
     * @return void
     */
    public static void finishStartingService(Service service, int startId) {

        synchronized (mStartingServiceSync) {

            if (mStartingService != null) {
                if (service.stopSelfResult(startId)) {
                    mStartingService.release();
                }
            }

        }
    }

    @Override
    public void onCreate() {

        HandlerThread thread = new HandlerThread(CLASS_TAG,
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mContext = getApplicationContext();
        mProcessMessage = new ProcessMessage(mContext, new ProcessSms(mContext));

        statusIntent = new Intent(ServicesConstants.AUTO_SYNC_ACTION);
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(this, mServiceLooper);
        MainApplication.bus.register(this);

    }

    @Override
    public void onStart(Intent intent, int startId) {
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);

    }

    @Override
    public void onDestroy() {
        mServiceLooper.quit();
        MainApplication.bus.unregister(this);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Handle receiving SMS message
     */
    protected void handleSmsReceived(Intent intent) {

        String body;
        Bundle bundle = intent.getExtras();
        //TODO:: refactor to use a different name for this
        org.addhen.smssync.models.Message msg = new org.addhen.smssync.models.Message();

        log("handleSmsReceived() bundle " + bundle);

        if (bundle != null) {
            SmsMessage[] messages = getMessagesFromIntent(intent);
            sms = messages[0];
            if (messages != null) {

                //if received sms is status message do appropriate action
                // and there is no need to pass it further
                if (DebugCallbacks.handleStatusMessage(sms, mContext)) {
                    return;
                }

                // extract message details. phone number and the message body
                msg.setPhoneNumber(sms.getOriginatingAddress());
                msg.setTimestamp(String.valueOf(sms.getTimestampMillis()));

                if (messages.length == 1 || sms.isReplace()) {
                    body = sms.getDisplayMessageBody();

                } else {
                    StringBuilder bodyText = new StringBuilder();
                    for (int i = 0; i < messages.length; i++) {
                        bodyText.append(messages[i].getMessageBody());
                    }
                    body = bodyText.toString();
                }
                msg.setMessage(body);
                msg.setUuid(new ProcessSms(mContext).getUuid());
            }
        }

        log("handleSmsReceived() messagesUuid: " + messagesUuid);
        // Log received SMS

        Util.logActivities(this, getString(R.string.received_msg, msg.getMessage(), msg.getPhoneNumber()));

        // route the sms
        boolean sent = mProcessMessage.routeSms(msg);
        if (!sent) {
            Util.showFailNotification(this, messagesBody,
                    getString(R.string.sending_failed));

            statusIntent = new Intent(ServicesConstants.FAILED_ACTION);
            statusIntent.putExtra("failed", 0);
            sendBroadcast(statusIntent);
        } else {
            Util.showFailNotification(this, messagesBody,
                    getString(R.string.sending_succeeded));
            Util.logActivities(this, getString(R.string.sending_succeeded));
            statusIntent.putExtra("sentstatus", 0);
            sendBroadcast(statusIntent);
        }
    }

    protected void log(String message) {
        Logger.log(getClass().getName(), message);
    }

    protected void log(String format, Object... args) {
        Logger.log(getClass().getName(), format, args);
    }

    protected void log(String message, Exception ex) {
        Logger.log(getClass().getName(), message, ex);
    }

    @Produce
    public boolean reloadLog() {
        return true;
    }

    private static class ServiceHandler extends Handler {

        private final WeakReference<SmsReceiverService> mSmsReceiverService;

        public ServiceHandler(SmsReceiverService mSmsReceiverService,
                Looper looper) {
            super(looper);
            this.mSmsReceiverService = new WeakReference<SmsReceiverService>(
                    mSmsReceiverService);
        }

        @Override
        public void handleMessage(Message msg) {
            SmsReceiverService smsReceiverService = mSmsReceiverService.get();
            if (smsReceiverService != null) {
                int serviceId = msg.arg1;
                Intent intent = (Intent) msg.obj;
                if (intent != null) {
                    String action = intent.getAction();

                    if (ACTION_SMS_RECEIVED.equals(action)) {
                        smsReceiverService.handleSmsReceived(intent);
                    }
                }
                finishStartingService(smsReceiverService, serviceId);
            }
        }
    }
}
