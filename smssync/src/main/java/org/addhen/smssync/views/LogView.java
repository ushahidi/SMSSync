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

package org.addhen.smssync.views;

import org.addhen.smssync.R;

import android.app.Activity;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 *
 */
public class LogView extends View {

    @Widget(R.id.start_logs)
    public CheckBox enableLogs;

    @Widget(R.id.loading_list_progress)
    public ProgressBar listLoadingProgress;

    @Widget(android.R.id.empty)
    public TextView emptyView;

    @Widget(R.id.data_connection_status)
    public TextView dataConnection;

    @Widget(R.id.phone_status_label)
    public TextView phoneStatusLable;

    @Widget(R.id.battery_level_status)
    public TextView batteryLevelStatus;

    @Widget(R.id.log_location)
    public TextView logLcation;

    public LogView(Activity activity) {
        super(activity);
        emptyView.setText(activity.getString(R.string.no_logs));
    }
}
