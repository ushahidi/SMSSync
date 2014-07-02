package org.addhen.smssync.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import org.addhen.smssync.controllers.DebugControllerRunnable;

/**
 * Created by Kamil Kalfas(kkalfas@soldevelo.com) on 17.06.14.
 */
public class BatteryLevelReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            context.unregisterReceiver(this);

            int extraLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            int level = -1;

            if (extraLevel >= 0 && scale > 0) {
                level = (extraLevel * 100) / scale;
            }

            new Thread(new DebugControllerRunnable(context) {
                @Override
                public void run() {

                }
            }).start();
    };
}
