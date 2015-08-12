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

package org.addhen.smssync.presentation.view.ui.activity;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import com.addhen.android.raiburari.presentation.ui.activity.BaseActivity;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import org.addhen.smssync.R;
import org.addhen.smssync.presentation.model.WebServiceModel;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.text.TextUtils;

import butterknife.Bind;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class QrcodeReaderActivity extends BaseActivity
        implements QRCodeReaderView.OnQRCodeReadListener {

    /** Intent extra's name to be used to retrieved the shared {@link WebServiceModel} */
    public static final String INTENT_EXTRA_PARAM_BARCODE_WEB_SERVICE_MODEL
            = "org.addhen.smssync.INTENT_PARAM_BARCODE_DEPLOYMENT_MODEL";

    /** The request code number to determine if the result was sent by this activity */
    public static final int QRCODE_READER_REQUEST_CODE = 1;

    @Bind(R.id.qrdecoderview)
    QRCodeReaderView mQRCodeReaderView;

    public QrcodeReaderActivity() {
        super(R.layout.activity_barcode_reader, 0);
    }

    /**
     * Provides {@link Intent} launching this activity
     *
     * @param context The calling context
     * @return The intent to be launched
     */
    public static Intent getIntent(final Context context) {
        return new Intent(context, QrcodeReaderActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mQRCodeReaderView.setOnQRCodeReadListener(this);
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        if (!TextUtils.isEmpty(text)) {
            Gson gson = new Gson();
            WebServiceModel webServiceModel = null;
            try {
                webServiceModel = gson.fromJson(text, WebServiceModel.class);
            } catch (JsonSyntaxException e) {
                // Do nothing
            }

            Intent returnIntent = new Intent();
            if (webServiceModel != null) {
                returnIntent
                        .putExtra(INTENT_EXTRA_PARAM_BARCODE_WEB_SERVICE_MODEL, webServiceModel);
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
