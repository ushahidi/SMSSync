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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import com.addhen.android.raiburari.presentation.ui.activity.BaseActivity;

import org.addhen.smssync.R;
import org.addhen.smssync.presentation.model.WebServiceModel;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class QrcodeReaderActivity extends BaseActivity
        implements ZXingScannerView.ResultHandler {

    /** Intent extra's name to be used to retrieved the shared {@link WebServiceModel} */
    public static final String INTENT_EXTRA_PARAM_BARCODE_WEB_SERVICE_MODEL
            = "org.addhen.smssync.INTENT_PARAM_BARCODE_WEB_SERVICE_MODEL";

    /** The request code number to determine if the result was sent by this activity */
    public static final int QRCODE_READER_REQUEST_CODE = 1;

    ZXingScannerView mScannerView;

    public QrcodeReaderActivity() {
        super(0, 0);
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
        mScannerView = new ZXingScannerView(this);
        setupFormats();
        setContentView(mScannerView);
        mUnbinder = ButterKnife.bind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
        mScannerView.setFlash(false);
        mScannerView.setAutoFocus(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!TextUtils.isEmpty(result.getText())) {
            Gson gson = new Gson();
            WebServiceModel webServiceModel = null;
            try {
                webServiceModel = gson.fromJson(result.getText(), WebServiceModel.class);
            } catch (JsonSyntaxException e) {
                Toast.makeText(this, getString(R.string.invalid_qr_code_string), Toast.LENGTH_LONG)
                        .show();
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
        mScannerView.startCamera();
    }

    public void setupFormats() {
        if (mScannerView != null) {
            List<BarcodeFormat> scannerFormats = new ArrayList<>();
            scannerFormats.add(BarcodeFormat.QR_CODE);
            mScannerView.setFormats(scannerFormats);
        }
    }
}
