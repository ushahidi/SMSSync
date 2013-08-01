/*****************************************************************************
 ** Copyright (c) 2010 - 2012 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 **
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.
 **
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 **
 *****************************************************************************/

package org.addhen.smssync.views;

import org.addhen.smssync.R;

import android.app.Activity;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PendingMessagesView extends View {

    @Widget(R.id.status_label)
    public TextView status;

    @Widget(R.id.details_sync_label)
    public TextView details;

    @Widget(R.id.details_sync_progress)
    public ProgressBar progressStatus;

    @Widget(R.id.sync_button)
    public Button sync;

    public PendingMessagesView(Activity activity) {
        super(activity);
    }

}
