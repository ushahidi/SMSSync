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
import org.addhen.smssync.presentation.presenter.webservice.UpdateWebServicePresenter;
import org.addhen.smssync.presentation.validator.UrlValidator;
import org.addhen.smssync.presentation.view.webservice.UpdateWebServiceView;

import android.app.Activity;
import android.content.Context;
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
 * Fragment for updating a existing webService
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class UpdateWebServiceFragment extends BaseFragment implements UpdateWebServiceView {

    private static final String ARGUMENT_KEY_WEB_SERVICE_MODEL
            = "org.addhen.smssync.ARGUMENT_WEB_SERVICE_MODEL";

    @Bind(R.id.add_custom_web_service_title)
    EditText title;

    @Bind(R.id.add_custom_web_service_url)
    EditText url;

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
        mUpdateWebServicePresenter.destroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActionListener = null;
    }

    private void initialize() {
        getComponent(WebServiceComponent.class).inject(this);
        mUpdateWebServicePresenter.setView(this);
    }

    @Override
    public Context getAppContext() {
        return getActivity().getApplication();
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

    @Override
    public void onWebServiceSuccessfullyUpdated(Long row) {
        if (mActionListener != null) {
            mActionListener.onUpdateNavigateOrReloadList();
        }
    }

    @Override
    public void showWebService(@NonNull WebServiceModel webServiceModel) {
        mWebServiceModel = webServiceModel;
        title.setText(webServiceModel.getTitle());
        url.setText(webServiceModel.getUrl());
    }

    @OnClick(R.id.add_custom_web_service_add)
    public void onClickValidate() {
        submit();
    }

    @OnEditorAction(R.id.add_custom_web_service_url)
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
        if (TextUtils.isEmpty(title.getText().toString())) {
            title.setError(getString(R.string.validation_message_no_deployment_title));
            return;
        }
        if (!(new UrlValidator().isValid(url.getText().toString()))) {
            url.setError(getString(R.string.validation_message_invalid_url));
            return;
        }
        mWebServiceModel.setTitle(title.getText().toString());
        mWebServiceModel.setUrl(url.getText().toString());
        mUpdateWebServicePresenter.updateWebService(mWebServiceModel);
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
