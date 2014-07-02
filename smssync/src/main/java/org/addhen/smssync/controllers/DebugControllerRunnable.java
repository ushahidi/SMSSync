package org.addhen.smssync.controllers;

import android.content.Context;

/**
 * Created by Kamil Kalfas(kkalfas@soldevelo.com) on 16.06.14.
 */
public abstract class DebugControllerRunnable implements Runnable {
    private Context context;

    public DebugControllerRunnable(Context c) {
        super();
        this.context = c;
    }

}