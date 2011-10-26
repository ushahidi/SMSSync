
package org.addhen.smssync.services;

import org.addhen.smssync.Prefrences;
import org.addhen.smssync.receivers.ConnectivityChangedReceiver;
import org.addhen.smssync.util.Util;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.util.Log;

/**
 * Service that queries the underlying web service to retrieve the full details
 * for the specified place / venue. This Service is called by the
 * {@link PlacesUpdateService} to prefetch details for the nearby venues, or by
 * the {@link PlacesActivity} and {@link PlaceDetailsFragment} to retrieve the
 * details for the selected place. TODO Replace the URL and XML parsing to match
 * the details available from your service.
 */

public abstract class SmsSyncServices extends IntentService {

    protected static String CLASS_TAG = SmsSyncServices.class.getSimpleName();

    protected static PowerManager.WakeLock mStartingService = null;

    protected static WifiManager.WifiLock wifilock = null;

    protected NotificationManager notificationManager;

    protected static final Object mStartingServiceSync = new Object();

    public SmsSyncServices(String name) {
        super(name);
    }

    synchronized private static PowerManager.WakeLock getPhoneWakeLock(Context context) {
        if (mStartingService == null) {
            PowerManager mgr = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
            mStartingService = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, CLASS_TAG);
        }
        return mStartingService;
    }

    synchronized private static WifiManager.WifiLock getPhoneWifiLock(Context context) {
        if (wifilock == null) {

            WifiManager manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            wifilock = manager.createWifiLock(WifiManager.WIFI_MODE_FULL, CLASS_TAG);
        }
        return wifilock;
    }

    protected static void sendWakefulTask(Context context, Intent i) {
        getPhoneWakeLock(context.getApplicationContext()).acquire();
        getPhoneWifiLock(context.getApplicationContext()).acquire();
        context.startService(i);
    }

    public static void sendWakefulTask(Context context, Class<?> classService) {
        sendWakefulTask(context, new Intent(context, classService));
    }

    /*
     * Subclasses must implement this method so it executes any tasks
     * implemented in it.
     */
    protected abstract void executeTask(Intent intent);

    @Override
    public void onCreate() {
        super.onCreate();
        // load setting. Just in case someone changes a setting
        Prefrences.loadPreferences(this);
    }

    /**
     * {@inheritDoc} Perform a task as implemented by the executeTask()
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(CLASS_TAG, "onHandleIntent(): running service");
        try {

            boolean isConnected = Util.isConnected(this);

            // check if we have internet
            if (!isConnected) {
                // Enable the Connectivity Changed Receiver to listen for
                // connection
                // to a network
                // so we can execute pending messages.
                PackageManager pm = getPackageManager();
                ComponentName connectivityReceiver = new ComponentName(this,
                        ConnectivityChangedReceiver.class);
                pm.setComponentEnabledSetting(connectivityReceiver,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);

            } else {

                // execute the scheduled task
                executeTask(intent);
            }
        } finally {

            if (getPhoneWakeLock(this.getApplicationContext()).isHeld()
                    && getPhoneWakeLock(this.getApplicationContext()) != null) {
                getPhoneWakeLock(this.getApplicationContext()).release();
            }
            
            if (getPhoneWifiLock(this.getApplicationContext()).isHeld()
                    && getPhoneWifiLock(this.getApplicationContext()) != null) {
                getPhoneWifiLock(this.getApplicationContext()).release();
            }
        }
    }

    @Override
   public void onDestroy() {
        //release resources
        if (getPhoneWifiLock(this.getApplicationContext()).isHeld()
                && getPhoneWifiLock(this.getApplicationContext()) != null) {
            getPhoneWifiLock(this.getApplicationContext()).release();
        }
        
        if (getPhoneWakeLock(this.getApplicationContext()).isHeld()
                && getPhoneWakeLock(this.getApplicationContext()) != null) {
            getPhoneWakeLock(this.getApplicationContext()).release();
        }

    }
}
