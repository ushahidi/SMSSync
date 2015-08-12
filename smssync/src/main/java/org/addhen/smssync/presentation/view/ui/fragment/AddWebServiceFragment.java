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
import org.addhen.smssync.presentation.model.WebServiceModel;
import org.addhen.smssync.presentation.presenter.webservice.AddWebServicePresenter;
import org.addhen.smssync.presentation.view.ui.navigation.Launcher;
import org.addhen.smssync.presentation.view.webservice.AddWebServiceView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnEditorAction;

/**
 * Fragment for adding a new webService
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class AddWebServiceFragment extends BaseFragment implements AddWebServiceView {

    @Bind(R.id.add_custom_web_service_title)
    EditText title;

    @Bind(R.id.add_custom_web_service_url)
    EditText url;

    @Inject
    AddWebServicePresenter mAddWebServicePresenter;

    @Inject
    Launcher mLauncher;

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
        initialize();
        url.setOnTouchListener((view, event) -> setHttpProtocol());
    }

    private boolean setHttpProtocol() {
        if (TextUtils.isEmpty(url.getText().toString())) {
            url.setText("http://");
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
        getComponent(WebServiceComponent.class).inject(this);
        mAddWebServicePresenter.setView(this);
    }


    @Override
    public Context getAppContext() {
        return getActivity().getApplication();
    }

    @Override
    public void showError(String message) {
        showToast(message);
    }

    @OnClick(R.id.add_custom_web_service_add)
    public void onClickValidate() {
        submit();
    }

    @OnEditorAction(R.id.add_custom_web_service_add)
    boolean onEditorAction(TextView textView, int actionId) {
        if (textView == url) {
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
        url.setError(null);
        // TODO: Validate URL
        WebServiceModel webServiceModel = new WebServiceModel();
        webServiceModel.setTitle(title.getText().toString());
        webServiceModel.setUrl(url.getText().toString());
        mAddWebServicePresenter.addWebService(webServiceModel);
    }

    @OnClick(R.id.add_custom_web_service_cancel)
    public void onClickCancel() {
        getActivity().finish();
    }

    @OnClick(R.id.qr_code_scanner)
    public void onQrCodeScannerClick() {
        mLauncher.launchQrcodeReader();
    }

    public void setWebService(@NonNull WebServiceModel webServiceModel) {
        title.setText(webServiceModel.getTitle());
        url.setText(webServiceModel.getUrl());
    }

    @Override
    public void onWebServiceSuccessfullyAdded(Long row) {
        getActivity().finish();
    }

    @Override
    public void showLoading() {
        // Do nothing
    }

    @Override
    public void hideLoading() {
        // Do nothing
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
        // TODO: Implement QR code activity
    }
}
