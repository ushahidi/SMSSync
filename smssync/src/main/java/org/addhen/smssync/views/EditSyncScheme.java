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

import org.addhen.smssync.App;
import org.addhen.smssync.R;
import org.addhen.smssync.database.BaseDatabseHelper;
import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.net.SyncScheme;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Class: EditSyncScheme Description: Update sync scheme to SyncUrl Author: Salama A.B.
 * <devaksal@gmail.com>
 */
public class EditSyncScheme {

    public Spinner methods, dataFormats;

    public EditText keySecret;

    public EditText keyFrom;

    public EditText keyMessage;

    public EditText keySentTimeStamp;

    public EditText keySentTo;

    public EditText keyMessageID;

    public EditText keyDeviceID;

    /**
     * Handles views for the add dialog box
     */
    public EditSyncScheme(final android.view.View dialogViews) {

        methods = (Spinner) dialogViews.findViewById(R.id.sync_method);
        dataFormats = (Spinner) dialogViews.findViewById(R.id.sync_data_format);

        keySecret = (EditText) dialogViews.findViewById(R.id.sync_kSecret);
        keyFrom = (EditText) dialogViews.findViewById(R.id.sync_kFrom);
        keyMessage = (EditText) dialogViews.findViewById(R.id.sync_kMessage);
        keySentTimeStamp = (EditText) dialogViews.findViewById(R.id.sync_kSentTimestamp);
        keySentTo = (EditText) dialogViews.findViewById(R.id.sync_kSentTo);
        keyMessageID = (EditText) dialogViews.findViewById(R.id.sync_kMessageID);
        keyDeviceID = (EditText) dialogViews.findViewById(R.id.sync_kDeviceID);

    }


    /**
     * Add/Update Sync Scheme
     *
     * @return boolean
     */
    public boolean updateSyncScheme(SyncUrl syncUrl) {

        SyncScheme scheme = new SyncScheme();

        SyncScheme.SyncMethod method = SyncScheme.SyncMethod
                .valueOf(methods.getSelectedItem().toString());
        SyncScheme.SyncDataFormat format = SyncScheme.SyncDataFormat
                .valueOf(dataFormats.getSelectedItem().toString());
        scheme.init(method, format,
                keySecret.getText().toString(), keyFrom.getText().toString(),
                keyMessage.getText().toString(), keyMessageID.getText().toString(),
                keySentTimeStamp.getText().toString(), keySentTo.getText().toString(),
                keyDeviceID.getText().toString());

        syncUrl.setSyncScheme(scheme);
        App.getDatabaseInstance().getSyncUrlInstance().put(syncUrl, new BaseDatabseHelper.DatabaseCallback<Void>() {
            @Override
            public void onFinished(Void result) {
                //Do nothing
            }

            @Override
            public void onError(Exception exception) {
                //Do nothing
            }
        });

        return true;
    }

    /**
     * Validate dialog entries
     *
     * @return true if valid false otherwise
     */
    public boolean validEntries() {

        //TODO: Add adequate sanitation

        return !TextUtils.isEmpty(keyMessage.getText().toString()) &&
                !TextUtils.isEmpty(keyFrom.getText().toString()) &&
                !TextUtils.isEmpty(keyMessageID.getText().toString()) &&
                !TextUtils.isEmpty(keySecret.getText().toString()) &&
                !TextUtils.isEmpty(keySentTo.getText().toString()) &&
                !TextUtils.isEmpty(keySentTimeStamp.getText().toString()) &&
                !TextUtils.isEmpty(keyDeviceID.getText().toString());
    }
}
