package org.addhen.smssync;

import android.os.Handler;
import android.os.Looper;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class UiThread {

    private final Handler handler;

    private UiThread() {
        this.handler = new Handler(Looper.getMainLooper());
    }

    public static UiThread getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * Causes the Runnable r to be added to the message queue. The runnable will be run on the main
     * thread.
     *
     * @param runnable {@link Runnable} to be executed.
     */
    public void post(Runnable runnable) {
        handler.post(runnable);
    }

    private static class LazyHolder {

        private static final UiThread INSTANCE = new UiThread();
    }
}
