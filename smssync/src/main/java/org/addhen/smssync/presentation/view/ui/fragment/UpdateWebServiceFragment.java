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
import org.addhen.smssync.presentation.presenter.webservice.UpdateWebServicePresenter;
import org.addhen.smssync.presentation.validator.UrlValidator;
import org.addhen.smssync.presentation.view.ui.activity.QrcodeReaderActivity;
import org.addhen.smssync.presentation.view.webservice.TestWebServiceView;
import org.addhen.smssync.presentation.view.webservice.UpdateWebServiceView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
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

/**
 * Fragment for updating a existing webService. This needs to be merged with {@link
 * AddWebServiceFragment}
 * as they share so much code
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class UpdateWebServiceFragment extends BaseFragment implements UpdateWebServiceView {

    private static final String ARGUMENT_KEY_WEB_SERVICE_MODEL
            = "org.addhen.smssync.ARGUMENT_WEB_SERVICE_MODEL";

    private static final String BUNDLE_STATE_WEB_SERVICE_MODEL
            = "org.addhen.smssync.BUNDLE_STATE_WEB_SERVICE_MODEL";

    @BindView(R.id.add_custom_web_service_title)
    EditText mEditTextTitle;

    @BindView(R.id.add_custom_web_service_url)
    EditText mEditTextUrl;

    @BindView(R.id.add_custom_web_service_secret)
    EditText mEditTextSecret;


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

    @BindView(R.id.add_custom_web_service_add)
    Button mButton;

    @BindView(R.id.test_progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.button_container)
    ViewGroup mButtonViewGroup;

    @Inject
    UpdateWebServicePresenter mUpdateWebServicePresenter;

    private UpdateWebServiceListener mActionListener;

    private WebServiceModel mWebServiceModel;

    /**
     * Update WebService  Fragment
     */
    public UpdateWebServiceFragment() {
        super(R.layout.fragment_add_web_service, 0);
    }

    public static UpdateWebServiceFragment newInstance(WebServiceModel webService) {
        UpdateWebServiceFragment updateWebServiceFragment = new UpdateWebServiceFragment();
        Bundle argumentBundle = new Bundle();
        argumentBundle.putParcelable(ARGUMENT_KEY_WEB_SERVICE_MODEL, webService);
        updateWebServiceFragment.setArguments(argumentBundle);
        return updateWebServiceFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof UpdateWebServiceListener) {
            mActionListener = (UpdateWebServiceListener) activity;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
        mEditTextUrl.setOnTouchListener((view, event) -> setHttpProtocol());
        if (savedInstanceState != null) {
            mWebServiceModel = savedInstanceState.getParcelable(BUNDLE_STATE_WEB_SERVICE_MODEL);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(BUNDLE_STATE_WEB_SERVICE_MODEL, mWebServiceModel);
        super.onSaveInstanceState(savedInstanceState);
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
        showWebService(mWebServiceModel);
        mUpdateWebServicePresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mUpdateWebServicePresenter.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUpdateWebServicePresenter != null) {
            mUpdateWebServicePresenter.destroy();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActionListener = null;
    }

    private void initialize() {
        getComponent(WebServiceComponent.class).inject(this);
        mUpdateWebServicePresenter.setTestWebServiceView(new TestWebServiceView() {
            @Override
            public void webServiceTested(boolean status) {
                if (status) {
                    showSnackbar(mButtonViewGroup, R.string.valid_web_service);
                    return;
                }
                showSnackbar(mButtonViewGroup, R.string.failed_to_test_web_service);
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
                showSnackbar(mButtonViewGroup, s);
            }

            @Override
            public Context getAppContext() {
                return getActivity();
            }
        });
        mUpdateWebServicePresenter.setView(this);
        mButton.setText(R.string.update_btn);
    }

    @Override
    public Context getAppContext() {
        return getActivity();
    }

    @Override
    public void showError(String message) {
        showToast(message);
    }

    @OnClick(R.id.add_custom_web_service_cancel)
    public void onClickCancel() {
        if (mActionListener != null) {
            mActionListener.onCancelUpdate();
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

    @Override
    public void onWebServiceSuccessfullyUpdated(Long row) {
        if (mActionListener != null) {
            mActionListener.onUpdateNavigateOrReloadList();
        }
    }

    @Override
    public void showWebService(@NonNull WebServiceModel webServiceModel) {
        mWebServiceModel = webServiceModel;
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

    @OnClick(R.id.add_custom_web_service_add)
    public void onClickValidate() {
        submit();
    }

    @OnEditorAction(R.id.add_custom_web_service_url)
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
        mEditTextUrl.setError(null);
        if (TextUtils.isEmpty(mEditTextTitle.getText().toString())) {
            mEditTextTitle.setError(getString(R.string.validation_message_no_deployment_title));
            return;
        }
        if (!(new UrlValidator().isValid(mEditTextUrl.getText().toString()))) {
            mEditTextUrl.setError(getString(R.string.validation_message_invalid_url));
            return;
        }
        initWebServiceModel();
        mUpdateWebServicePresenter.updateWebService(mWebServiceModel);
    }

    private void initWebServiceModel() {
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

        mWebServiceModel.setTitle(mEditTextTitle.getText().toString());
        mWebServiceModel.setUrl(mEditTextUrl.getText().toString());
        mWebServiceModel.setSecret(mEditTextSecret.getText().toString());
        mWebServiceModel.setSyncScheme(syncSchemeModel);
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
            initWebServiceModel();
            mUpdateWebServicePresenter.testWebService(mWebServiceModel);
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
                showWebService(webServiceModel);
                return;
            }
            showToast(getString(R.string.invalid_qr_code_string));
        }
    }

    /**
     * Listens for Update WebService events
     */
    public interface UpdateWebServiceListener {

        /**
         * Executes when a button is pressed to either navigate away from the screen or reload an
         * existing list.
         */
        void onUpdateNavigateOrReloadList();

        /**
         * Executes when a button is pressed to either cancel.
         */
        void onCancelUpdate();
    }
}
