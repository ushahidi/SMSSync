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

package org.addhen.smssync.presentation.view.ui.fragment;

import com.addhen.android.raiburari.presentation.ui.fragment.BaseFragment;

import org.addhen.smssync.R;
import org.addhen.smssync.presentation.di.component.WebServiceComponent;
import org.addhen.smssync.presentation.model.SyncSchemeModel;
import org.addhen.smssync.presentation.model.WebServiceModel;
import org.addhen.smssync.presentation.presenter.webservice.AddWebServicePresenter;
import org.addhen.smssync.presentation.util.Utility;
import org.addhen.smssync.presentation.view.ui.activity.QrcodeReaderActivity;
import org.addhen.smssync.presentation.view.webservice.AddWebServiceView;
import org.addhen.smssync.presentation.view.webservice.TestWebServiceView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTouch;

/**
 * Fragment for adding a new webService
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class AddWebServiceFragment extends BaseFragment implements AddWebServiceView {

    private static final int MIN_TEXT_LENGTH = 3;

    @BindView(R.id.add_custom_web_service_title)
    EditText mEditTextTitle;

    @BindView(R.id.add_custom_web_service_url)
    EditText mEditTextUrl;

    @BindView(R.id.add_custom_web_service_secret)
    EditText mEditTextSecret;

    // SyncScheme
    @BindView(R.id.sync_method)
    Spinner mSpinnerMethods;

    @BindView(R.id.sync_data_format)
    Spinner mSpinnerDataFormats;

    @BindView(R.id.sync_k_secret)
    EditText mKeySecret;

    @BindView(R.id.sync_k_from)
    EditText mKeyFrom;

    @BindView(R.id.sync_k_message)
    EditText mKeyMessage;

    @BindView(R.id.sync_k_sent_timestamp)
    EditText mKeySentTimeStamp;

    @BindView(R.id.sync_k_sent_to)
    EditText mKeySentTo;

    @BindView(R.id.sync_k_message_id)
    EditText mKeyMessageID;

    @BindView(R.id.sync_k_device_id)
    EditText mKeyDeviceID;

    // Input layout for handling error messages
    @BindView(R.id.service_title_text_input_layout)
    TextInputLayout mTitleTextInputLayout;

    @BindView(R.id.service_url_text_input_layout)
    TextInputLayout mUrlTextInputLayout;

    @BindView(R.id.service_secret_text_input_layout)
    TextInputLayout mSecretTextInputLayout;

    @BindView(R.id.service_k_secret_text_input_layout)
    TextInputLayout mKSecretTextIputLayout;

    @BindView(R.id.service_k_from_text_input_layout)
    TextInputLayout mKFromTextInputLayout;

    @BindView(R.id.service_k_message_id_text_input_layout)
    TextInputLayout mKMessageIdTextInputLayout;

    @BindView(R.id.service_k_message_text_input_layout)
    TextInputLayout mKMessageTextInputLayout;

    @BindView(R.id.service_k_sent_timestamp_text_input_layout)
    TextInputLayout mKSentTimestampTextInputLayout;

    @BindView(R.id.service_k_sent_to_text_input_layout)
    TextInputLayout mKSentToTextInputLayout;

    @BindView(R.id.service_k_device_id_text_input_layout)
    TextInputLayout mKDeviceIdTextInputLayout;

    @BindView(R.id.test_progress_bar)
    ProgressBar mProgressBar;

    @Inject
    AddWebServicePresenter mAddWebServicePresenter;

    /**
     * Add WebService  Fragment
     */
    public AddWebServiceFragment() {
        super(R.layout.fragment_add_web_service, 0);
    }

    public static AddWebServiceFragment newInstance() {
        AddWebServiceFragment addWebServiceFragment = new AddWebServiceFragment();
        return addWebServiceFragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getComponent(WebServiceComponent.class).inject(this);
        initialize();
    }

    private boolean setHttpProtocol() {
        if (TextUtils.isEmpty(mEditTextUrl.getText().toString())) {
            mEditTextUrl.setText("http://");
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAddWebServicePresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mAddWebServicePresenter.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAddWebServicePresenter.destroy();
    }

    private void initialize() {
        mAddWebServicePresenter.setView(this, new TestWebServiceView() {
            @Override
            public void webServiceTested(boolean status) {
                if (status) {
                    showSnackbar(getView(), R.string.valid_web_service);
                } else {
                    showSnackbar(getView(), R.string.failed_to_test_web_service);
                }
            }

            @Override
            public void showLoading() {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void hideLoading() {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void showRetry() {

            }

            @Override
            public void hideRetry() {

            }

            @Override
            public void showError(String s) {
                showSnackbar(getView(), s);
            }

            @Override
            public Context getAppContext() {
                return getActivity();
            }
        });
    }

    @Override
    public Context getAppContext() {
        return getActivity();
    }

    @Override
    public void showError(String message) {
        showToast(message);
    }

    @OnClick(R.id.add_custom_web_service_add)
    public void onClickValidate() {
        submit();
    }

    @OnTouch(R.id.add_custom_web_service_url)
    boolean onTouch() {
        setHttpProtocol();
        return false;
    }

    @OnEditorAction(R.id.add_custom_web_service_add)
    boolean onEditorAction(TextView textView, int actionId) {
        if (textView == mEditTextUrl) {
            switch (actionId) {
                case EditorInfo.IME_ACTION_DONE:
                    submit();
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    private void submit() {
        if (!validateFields()) {
            return;
        }
        WebServiceModel webServiceModel = getWebServiceModel();
        mAddWebServicePresenter.addWebService(webServiceModel);
    }

    @NonNull
    private WebServiceModel getWebServiceModel() {
        SyncSchemeModel syncSchemeModel = new SyncSchemeModel();
        SyncSchemeModel.SyncMethod syncMethod = SyncSchemeModel.SyncMethod
                .valueOf(mSpinnerMethods.getSelectedItem().toString());
        SyncSchemeModel.SyncDataFormat dataFormat = SyncSchemeModel.SyncDataFormat
                .valueOf(mSpinnerDataFormats.getSelectedItem().toString());
        syncSchemeModel.init(syncMethod, dataFormat,
                mKeySecret.getText().toString(), mKeyFrom.getText().toString(),
                mKeyMessage.getText().toString(), mKeyMessageID.getText().toString(),
                mKeySentTimeStamp.getText().toString(), mKeySentTo.getText().toString(),
                mKeyDeviceID.getText().toString());
        WebServiceModel webServiceModel = new WebServiceModel();
        webServiceModel.setTitle(mEditTextTitle.getText().toString());
        webServiceModel.setUrl(mEditTextUrl.getText().toString());
        webServiceModel.setSecret(mEditTextSecret.getText().toString());
        webServiceModel.setSyncScheme(syncSchemeModel);
        webServiceModel.setStatus(WebServiceModel.Status.ENABLED);
        return webServiceModel;
    }

    @OnClick(R.id.add_custom_web_service_cancel)
    public void onClickCancel() {
        getActivity().finish();
    }

    @OnClick(R.id.qr_code_scanner)
    public void onQrCodeScannerClick() {
        // Use this fragment to start the QR code scanner so the fragment's
        // onActivityResult method would be called
        startActivityForResult(QrcodeReaderActivity.getIntent(getActivity()),
                QrcodeReaderActivity.QRCODE_READER_REQUEST_CODE);
    }

    @OnClick(R.id.test_integration)
    public void testIntegration() {
        final String url = mEditTextUrl.getText().toString();
        if (!TextUtils.isEmpty(url)) {
            WebServiceModel webServiceModel = getWebServiceModel();
            mAddWebServicePresenter.testWebService(webServiceModel);
        }
    }

    @OnCheckedChanged(R.id.add_custom_web_service_show_password)
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mEditTextSecret.setInputType(
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            return;
        }
        mEditTextSecret
                .setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    private boolean validateFields() {
        mTitleTextInputLayout.setError(null);
        final int titleLength = mEditTextTitle.getText().length();
        if (titleLength > 0 && titleLength < MIN_TEXT_LENGTH) {
            mTitleTextInputLayout
                    .setError(getString(R.string.custom_web_service_title_validation_message));
            return false;
        }

        mUrlTextInputLayout.setError(null);
        if (!Utility.validateUrl(mEditTextUrl.getText().toString())) {
            mUrlTextInputLayout.setError(getString(R.string.validation_message_invalid_url));
            return false;
        }

        mKDeviceIdTextInputLayout.setError(null);
        if (TextUtils.isEmpty(mKeyDeviceID.getText().toString())) {
            mKDeviceIdTextInputLayout
                    .setError(getString(R.string.valid_sync_scheme_enter_key_for_device_id));
            return false;
        }

        mKFromTextInputLayout.setError(null);
        if (TextUtils.isEmpty(mKeyFrom.getText().toString())) {
            mKFromTextInputLayout
                    .setError(getString(R.string.valid_sync_scheme_enter_key_for_from));
            return false;
        }
        mKMessageIdTextInputLayout.setError(null);
        if (TextUtils.isEmpty(mKeyMessageID.getText().toString().toString())) {
            mKMessageIdTextInputLayout
                    .setError(getString(R.string.valid_sync_scheme_enter_key_for_message_id));
            return false;
        }

        mKMessageTextInputLayout.setError(null);
        if (TextUtils.isEmpty(mKeyMessage.getText().toString())) {
            mKSentTimestampTextInputLayout
                    .setError(getString(R.string.valid_sync_scheme_enter_key_for_sent_timestamp));
            return false;
        }
        mKSentToTextInputLayout.setError(null);
        if (TextUtils.isEmpty(mKeySentTo.getText().toString())) {
            mKSentToTextInputLayout
                    .setError(getString(R.string.valid_sync_scheme_enter_key_for_sent_to));
            return false;
        }
        mKSecretTextIputLayout.setError(null);
        if (TextUtils.isEmpty(mKeySecret.getText().toString())) {
            mKSecretTextIputLayout
                    .setError(getString(R.string.valid_sync_scheme_enter_key_for_secret));
            return false;
        }
        return true;
    }

    public void setWebService(@NonNull WebServiceModel webServiceModel) {
        if (webServiceModel != null) {
            mEditTextTitle.setText(webServiceModel.getTitle());
            mEditTextUrl.setText(webServiceModel.getUrl());
            mEditTextSecret.setText(webServiceModel.getSecret());

            SyncSchemeModel syncSchemeModel = webServiceModel.getSyncScheme();
            mKeyMessage.setText(syncSchemeModel.getKey(SyncSchemeModel.SyncDataKey.MESSAGE));
            mKeyMessageID.setText(syncSchemeModel.getKey(SyncSchemeModel.SyncDataKey.MESSAGE_ID));
            mKeyFrom.setText(syncSchemeModel.getKey(SyncSchemeModel.SyncDataKey.FROM));
            mKeySecret.setText(syncSchemeModel.getKey(SyncSchemeModel.SyncDataKey.SECRET));
            mKeySentTimeStamp
                    .setText(syncSchemeModel.getKey(SyncSchemeModel.SyncDataKey.SENT_TIMESTAMP));
            mKeySentTo.setText(syncSchemeModel.getKey(SyncSchemeModel.SyncDataKey.SENT_TO));
            mKeyDeviceID.setText(syncSchemeModel.getKey(SyncSchemeModel.SyncDataKey.DEVICE_ID));
            mSpinnerMethods.setSelection(syncSchemeModel.getMethod().ordinal());
            mSpinnerDataFormats.setSelection(syncSchemeModel.getDataFormat().ordinal());
        }
    }

    @Override
    public void onWebServiceSuccessfullyAdded(Long row) {
        getActivity().finish();
    }

    @Override
    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showRetry() {
        // Do nothing
    }

    @Override
    public void hideRetry() {
        // Do nothing
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == QrcodeReaderActivity.QRCODE_READER_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                final WebServiceModel webServiceModel = (WebServiceModel) data.getParcelableExtra(
                        QrcodeReaderActivity.INTENT_EXTRA_PARAM_BARCODE_WEB_SERVICE_MODEL);
                setWebService(webServiceModel);
                return;
            }
            showToast(getString(R.string.invalid_qr_code_string));
        }
    }
}
