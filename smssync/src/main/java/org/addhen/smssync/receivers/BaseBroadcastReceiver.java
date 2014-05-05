package org.addhen.smssync.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.widget.Toast;

import org.addhen.smssync.Prefs;
import org.addhen.smssync.util.LogUtil;
import org.addhen.smssync.util.Logger;

/**
 * Created by Tomasz Stalka(tstalka@soldevelo.com) on 5/5/14.
 */
public class BaseBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

    }

    protected void logActivities(String message, Context context) {
        if (Prefs.enableLog) {
            new LogUtil(DateFormat.getDateFormatOrder(context)).appendAndClose(message);
        }
    }

    protected void toastLong(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    protected void log(String message) {
        Logger.log(getClass().getName(), message);
    }
}
