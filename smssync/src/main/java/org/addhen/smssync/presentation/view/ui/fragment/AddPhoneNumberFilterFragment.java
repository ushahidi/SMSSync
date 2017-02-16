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
import org.addhen.smssync.presentation.di.component.FilterComponent;
import org.addhen.smssync.presentation.model.FilterModel;
import org.addhen.smssync.presentation.model.WebServiceModel;
import org.addhen.smssync.presentation.presenter.filter.AddFilterPresenter;
import org.addhen.smssync.presentation.presenter.filter.DeleteFilterPresenter;
import org.addhen.smssync.presentation.presenter.filter.ListFilterPresenter;
import org.addhen.smssync.presentation.view.filter.AddFilterView;
import org.addhen.smssync.presentation.view.filter.DeleteFilterView;
import org.addhen.smssync.presentation.view.filter.ListFilterView;
import org.addhen.smssync.presentation.view.ui.widget.KeywordView;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class AddPhoneNumberFilterFragment extends BaseFragment implements AddFilterView,
        DeleteFilterView, ListFilterView {

    @Inject
    ListFilterPresenter mListFilterPresenter;

    @Inject
    DeleteFilterPresenter mDeleteFilterPresenter;

    @Inject
    AddFilterPresenter mAddFilterPresenter;

    @BindView(R.id.filter_white_list_container)
    KeywordView mWhiteListKeywordView;

    @BindView(R.id.filter_black_list_container)
    KeywordView mBlackListKeywordView;

    private List<FilterModel> mFilterModels;

    /**
     * BaseFragment
     */
    public AddPhoneNumberFilterFragment() {
        super(R.layout.fragment_add_phone_numbers, 0);
    }

    public static AddPhoneNumberFilterFragment newInstance() {
        return new AddPhoneNumberFilterFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getComponent(FilterComponent.class).inject(this);
        initialize();
    }

    private void initialize() {
        mListFilterPresenter.setView(this);
        mAddFilterPresenter.setView(this);
        mDeleteFilterPresenter.setView(this);
        initViews();
    }

    private void initViews() {
        mWhiteListKeywordView.setOnTagSelectListener((view, tag, position) -> {
            mWhiteListKeywordView.removeTag(view, position);
            mDeleteFilterPresenter.deleteFilters(mFilterModels.get(position)._id);
        });
        mBlackListKeywordView.setOnTagSelectListener((view, tag, position) -> {
            mBlackListKeywordView.removeTag(view, position);
            mDeleteFilterPresenter.deleteFilters(mFilterModels.get(position)._id);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mListFilterPresenter.loadFilters();
        mDeleteFilterPresenter.resume();
        mAddFilterPresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mListFilterPresenter.pause();
        mDeleteFilterPresenter.pause();
        mAddFilterPresenter.pause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mListFilterPresenter.destroy();
        mDeleteFilterPresenter.destroy();
        mAddFilterPresenter.destroy();
    }

    @OnClick(R.id.add_white_list_numbers_btn)
    void onWhiteListClicked() {
        showDialog(R.string.whitelist, true);
    }

    @OnClick(R.id.add_black_list_numbers_btn)
    void onBlackListClicked() {
        showDialog(R.string.blacklist, false);
    }

    @Override
    public void onAdded(Long row) {
        // Do nothing
        setKeywords();
    }

    @Override
    public void onDeleted(Long row) {
        // Do nothing
        //setKeywords();
    }

    @Override
    public void showFilters(List<FilterModel> filterModelList) {
        mFilterModels = filterModelList;
        setKeywords();
    }

    @Override
    public void showCustomWebService(List<WebServiceModel> webServiceModels) {
        // Do nothing
    }

    @Override
    public void showError(String message) {
        showSnackbar(getView(), message);
    }

    @Override
    public Context getAppContext() {
        return getContext().getApplicationContext();
    }

    private void setKeywords() {
        mBlackListKeywordView.clearTags();
        mWhiteListKeywordView.clearTags();
        for (FilterModel filterModel : mFilterModels) {
            if (filterModel.getStatus().equals(FilterModel.Status.WHITELIST)) {

                KeywordView.Tag tag = new KeywordView.Tag(filterModel._id,
                        filterModel.getPhoneNumber(),
                        org.addhen.smssync.presentation.util.Utility.keywordColor(),
                        org.addhen.smssync.presentation.util.Utility.keywordIcon());
                mWhiteListKeywordView.add(tag);
            } else {
                KeywordView.Tag tag = new KeywordView.Tag(filterModel._id,
                        filterModel.getPhoneNumber(),
                        org.addhen.smssync.presentation.util.Utility.keywordColor(),
                        org.addhen.smssync.presentation.util.Utility.keywordIcon());
                mBlackListKeywordView.add(tag);
            }
        }
        mWhiteListKeywordView.setTags();
        mBlackListKeywordView.setTags();
    }

    private void showDialog(@StringRes int titleResId, boolean isWhiteList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_keyword, null);
        AppCompatTextView title = ButterKnife.findById(view, R.id.add_filter_title);
        title.setText(titleResId);
        EditText keywordEditText = ButterKnife.findById(view, R.id.add_keyword_text);
        keywordEditText.setHint(R.string.add_phone_number);
        builder.setView(view).setPositiveButton(R.string.add,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        final FilterModel filterModel = new FilterModel();
                        filterModel.setStatus(FilterModel.Status.BLACKLIST);
                        if (isWhiteList) {
                            filterModel.setStatus(FilterModel.Status.WHITELIST);
                        }
                        filterModel.setPhoneNumber(keywordEditText.getText().toString());
                        mAddFilterPresenter.addFilter(filterModel);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }
}
