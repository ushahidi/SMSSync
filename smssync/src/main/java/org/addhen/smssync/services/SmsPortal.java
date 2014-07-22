package org.addhen.smssync.services;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Messenger;

import org.addhen.smssync.Settings;

import java.util.ArrayList;

/**
 * Service used to integrate Sms Portal applications.
 */
public class SmsPortal {

    private enum version {
        ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN;
    }

    private int number;

    private ArrayList<Messenger> messengers;

    private ArrayList<ServiceConnection> serviceConnectionList;

    private Context context;

    public SmsPortal(Context c) {
        context = c;
        serviceConnectionList = new ArrayList<>();
        messengers = new ArrayList<>();
    }

    public void setNumber() {
        PackageManager pm = context.getPackageManager();
        int count = 0;
        for (PackageInfo packageInfo : pm.getInstalledPackages(1)) {
            if (packageInfo.packageName.contains("com.smssync.portal.")) {
                count++;
            }
        }
        number = count;
    }

    public void bindToSmsPortals() {
        for (int i = 0; i < number; i++ ) {
            Intent senderIntent = new Intent("com.smssync.portal." + version.values()[i].toString().toLowerCase() + ".action.SEND_SMS");
            serviceConnectionList.add(getServiceConnection(i));
            context.bindService(senderIntent, serviceConnectionList.get(i),
                    				Context.BIND_AUTO_CREATE);
        }
    }

    public void unbindFromSmsPortals() {
        for(ServiceConnection serviceConnection : serviceConnectionList) {
            context.unbindService(serviceConnection);
        }
        messengers.clear();
        Settings.currentConnectionIndex = -1;
    }

    private ServiceConnection getServiceConnection(final int number) {
        		ServiceConnection mConnection = new ServiceConnection() {
            		public void onServiceConnected(ComponentName className,
                                        IBinder service) {
                        Messenger messenger = new Messenger(service);
                        messengers.add(messenger);
                     }
                    public void onServiceDisconnected(ComponentName componentName) {

                        messengers.remove(number);
                        Settings.availableConnections.remove(number);
                    }
                };
        return mConnection;
    }

    public ArrayList<Messenger> getMessengers() {
        return messengers;
    }
}