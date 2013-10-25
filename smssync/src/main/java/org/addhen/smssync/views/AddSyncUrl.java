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
import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.net.SyncScheme;

import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;

public class AddSyncUrl {

    public EditText title;

    public EditText keywords;

    public EditText url;

    public EditText secret;

    public int status = 0;

    /**
     * Handles views for the add dialog box
     */
    public AddSyncUrl(final android.view.View dialogViews) {
        title = (EditText) dialogViews.findViewById(R.id.sync_url_title);
        keywords = (EditText) dialogViews.findViewById(R.id.sync_url_keyword);
        secret = (EditText) dialogViews.findViewById(R.id.sync_url_secret);
        url = (EditText) dialogViews.findViewById(R.id.sync_url);
        url.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                if (TextUtils.isEmpty(url.getText().toString())) {
                    url.setText(dialogViews.getContext().getString(
                            R.string.http_text));
                }

                return false;
            }

        });

    }

    /**
     * Add Sync URL to database
     *
     * @return boolean
     */
    public boolean addSyncUrl() {
        SyncUrl syncUrl = new SyncUrl();
        syncUrl.setKeywords(keywords.getText().toString());
        syncUrl.setSecret(secret.getText().toString());
        syncUrl.setTitle(title.getText().toString());
        syncUrl.setUrl(url.getText().toString());
        syncUrl.setStatus(0);
        syncUrl.setSyncScheme(new SyncScheme());
        return syncUrl.save();

    }

    /**
     * Add Sync URL to database
     *
     * @return boolean
     */
    public boolean updateSyncUrl(int id, SyncScheme scheme) {
        SyncUrl syncUrl = new SyncUrl();
        syncUrl.setId(id);
        syncUrl.setKeywords(keywords.getText().toString());
        syncUrl.setSecret(secret.getText().toString());
        syncUrl.setTitle(title.getText().toString());
        syncUrl.setUrl(url.getText().toString());
        syncUrl.setStatus(status);
        syncUrl.setSyncScheme(scheme);
        return syncUrl.update();
    }
}
