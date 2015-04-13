/*
 * Copyright (c) 2010 - 2015 Ushahidi Inc
 * All rights reserved
 * Contact: team@ushahidi.com
 * Website: http://www.ushahidi.com
 * GNU Lesser General Public License Usage
 * This file may be used under the terms of the GNU Lesser
 * General Public License version 3 as published by the Free Software
 * Foundation and appearing in the file LICENSE.LGPL included in the
 * packaging of this file. Please review the following information to
 * ensure the GNU Lesser General Public License version 3 requirements
 * will be met: http://www.gnu.org/licenses/lgpl.html.
 *
 * If you have questions regarding the use of this file, please contact
 * Ushahidi developers at team@ushahidi.com.
 */

package org.addhen.smssync.tasks;

import android.app.Activity;
import android.app.ProgressDialog;

import org.addhen.smssync.R;

/**
 * ProgressTask Parent class for all AsyncTasks that need to show ProgressDialog while executing
 */
public abstract class ProgressTask extends Task<String, String, Boolean> {

    protected final ProgressDialog dialog;

    protected ProgressCallback callback;

    protected ProgressTask(Activity activity) {
        this(activity, R.string.loading);
    }

    protected ProgressTask(Activity activity, int message) {
        super(activity);
        this.dialog = new ProgressDialog(activity);
        this.dialog.setCancelable(false);
        this.dialog.setIndeterminate(true);
        this.dialog.setMessage(activity.getString(message));
    }

    public void register(ProgressCallback callback) {
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.show();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        if (values != null && values.length > 0) {
            dialog.setMessage(values[0]);
        }
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        dialog.dismiss();
        if (callback != null) {
            callback.execute();
        }
    }
}