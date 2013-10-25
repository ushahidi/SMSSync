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

package org.addhen.smssync.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

/**
 * BaseTask
 *
 * Parent class for all AsyncTasks
 */
public abstract class Task<A, P, R> extends AsyncTask<A, P, R> {

    protected Activity activity;

    protected Task(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        log("onPreExecute");
    }

    @Override
    protected void onPostExecute(R result) {
        log("onPostExecute %s", result);
    }

    protected void log(String message) {
        Log.i(getClass().getName(), message);
    }

    protected void log(String format, Object... args) {
        Log.i(getClass().getName(), String.format(format, args));
    }

    protected void log(String message, Exception ex) {
        Log.e(getClass().getName(), message, ex);
    }
}