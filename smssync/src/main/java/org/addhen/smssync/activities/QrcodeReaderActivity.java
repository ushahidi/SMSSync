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

package org.addhen.smssync.activities;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import org.addhen.smssync.R;
import org.addhen.smssync.models.SyncUrl;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;

/**
 * Reads a barcode
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class QrcodeReaderActivity extends ActionBarActivity
        implements QRCodeReaderView.OnQRCodeReadListener {

    public static final String INTENT_EXTRA_PARAM_BARCODE_SYNC_URL
            = "org.addhen.smssync.activities.INTENT_PARAM_BARCODE_DEPLOYMENT_MODEL";

    public static final int QRCODE_READER_REQUEST_CODE = 1;

    private QRCodeReaderView mQRCodeReaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_reader);
        mQRCodeReaderView = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        mQRCodeReaderView.setOnQRCodeReadListener(this);
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        if (!TextUtils.isEmpty(text)) {
            Gson gson = new Gson();
            SyncUrl syncUrl = null;
            try {
                syncUrl = gson.fromJson(text, SyncUrl.class);
            } catch (JsonSyntaxException e) {
                // Do nothing
            }

            Intent returnIntent = new Intent();
            if (syncUrl != null) {
                returnIntent.putExtra(INTENT_EXTRA_PARAM_BARCODE_SYNC_URL, syncUrl);
                setResult(RESULT_OK, returnIntent);
            } else {
                setResult(RESULT_CANCELED, returnIntent);
            }
        }
        finish();
    }

    // Called when your device have no camera
    @Override
    public void cameraNotFound() {
        finish();
    }

    // Called when there's no QR codes in the camera preview image
    @Override
    public void QRCodeNotFoundOnCamImage() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        mQRCodeReaderView.getCameraManager().startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mQRCodeReaderView.getCameraManager().stopPreview();
    }
}
