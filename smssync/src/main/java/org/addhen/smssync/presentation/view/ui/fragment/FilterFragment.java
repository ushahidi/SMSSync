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
import org.addhen.smssync.data.PrefsFactory;
import org.addhen.smssync.data.twitter.TwitterClient;
import org.addhen.smssync.presentation.di.component.FilterComponent;
import org.addhen.smssync.presentation.model.FilterModel;
import org.addhen.smssync.presentation.model.WebServiceModel;
import org.addhen.smssync.presentation.presenter.filter.ListFilterPresenter;
import org.addhen.smssync.presentation.presenter.webservice.UpdateWebServiceKeywordsPresenter;
import org.addhen.smssync.presentation.util.Utility;
import org.addhen.smssync.presentation.view.filter.ListFilterView;
import org.addhen.smssync.presentation.view.ui.activity.MainActivity;
import org.addhen.smssync.presentation.view.ui.navigation.Launcher;
import org.addhen.smssync.presentation.view.ui.widget.FilterKeywordsView;
import org.addhen.smssync.presentation.view.webservice.UpdateWebServiceKeywordsView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.widget.LinearLayout;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class FilterFragment extends BaseFragment implements ListFilterView,
        UpdateWebServiceKeywordsView {

    @Inject
    ListFilterPresenter mListFilterPresenter;

    @Inject
    UpdateWebServiceKeywordsPresenter mUpdateWebServiceKeywordsPresenter;

    @Inject
    Launcher mLauncher;

    @Inject
    PrefsFactory mPrefsFactory;

    @Inject
    TwitterClient mTwitterClient;

    @BindView(R.id.custom_integration_filter_container)
    LinearLayout mFilterViewGroup;

    @BindView(R.id.black_list)
    FilterKeywordsView mBlackListFilterKeywordsView;

    @BindView(R.id.white_list)
    FilterKeywordsView mWhiteListFilterKeywordsView;

    public FilterFragment() {
        super(R.layout.fragment_filter_list, 0);
    }

    public static FilterFragment newInstance() {
        return new FilterFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getFilterComponent(FilterComponent.class).inject(this);
        initialize();
    }

    @Override
    public void onResume() {
        super.onResume();
        mFilterViewGroup.removeAllViews();
        initTwitterView();
        mListFilterPresenter.resume();
        mUpdateWebServiceKeywordsPresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mListFilterPresenter.pause();
        mUpdateWebServiceKeywordsPresenter.pause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mListFilterPresenter != null) {
            mListFilterPresenter.destroy();
        }
        if (mUpdateWebServiceKeywordsPresenter != null) {
            mUpdateWebServiceKeywordsPresenter.destroy();
        }
    }

    @Override
    public void showFilters(List<FilterModel> filterModelList) {
        int whiteListCount = 0;
        int blackListCount = 0;
        if (!Utility.isEmpty(filterModelList)) {
            for (FilterModel filterModel : filterModelList) {
                if (filterModel.getStatus().equals(FilterModel.Status.WHITELIST)) {
                    whiteListCount += 1;
                } else {
                    blackListCount += 1;
                }
            }
        }
        initBlackListFilters(String.valueOf(blackListCount));
        initWhiteListFilters(String.valueOf(whiteListCount));
    }

    @Override
    public void showCustomWebService(List<WebServiceModel> webServiceModels) {
        if (!Utility.isEmpty(webServiceModels)) {
            for (WebServiceModel webServiceModel : webServiceModels) {
                int keywordSize = 0;
                if (!TextUtils.isEmpty(webServiceModel.getKeywords())) {
                    keywordSize = webServiceModel.getKeywords().split(",").length;
                }
                initCustomIntegrationFilters(webServiceModel, R.drawable.ic_web_grey_900_24dp,
                        String.valueOf(keywordSize));
            }
        }
    }

    @Override
    public void showError(String s) {
        showSnackbar(getView(), s);
    }

    @Override
    public Context getAppContext() {
        return getContext().getApplicationContext();
    }

    protected <C> C getFilterComponent(Class<C> componentType) {
        return componentType.cast(((MainActivity) getActivity()).getFilterComponent());
    }

    private void initialize() {
        mListFilterPresenter.setView(this);
        mUpdateWebServiceKeywordsPresenter.setView(this);
    }

    private void initTwitterView() {
        if ((mTwitterClient != null) && (mTwitterClient.getSessionManager().getActiveSession()
                != null)) {
            FilterKeywordsView filterKeywordsView = new FilterKeywordsView(
                    getContext());

            final SwitchCompat filterKeywordsSwitch = filterKeywordsView
                    .getSwitchCompat();
            filterKeywordsSwitch.setChecked(mPrefsFactory.enableTwitterKeywords().get());
            if (filterKeywordsSwitch.isChecked()) {
                filterKeywordsView.visible(true);
            }
            filterKeywordsView.setSwitchListener(v -> {
                if (filterKeywordsSwitch.isChecked()) {
                    filterKeywordsSwitch.setChecked(true);
                    mPrefsFactory.enableTwitterKeywords().set(true);
                } else {
                    filterKeywordsSwitch.setChecked(false);
                    mPrefsFactory.enableTwitterKeywords().set(false);
                }
            });

            filterKeywordsView.setFilterItemListener(v -> {
                mLauncher.launchAddTwitterKeyword();
            });

            final AppCompatTextView title = filterKeywordsView.getTitle();
            title.setText(R.string.twitter);
            final Drawable customWebServiceDrawable = ContextCompat.getDrawable(getContext(),
                    R.drawable.ic_twitter_blue_24dp);
            title.setCompoundDrawablesWithIntrinsicBounds(customWebServiceDrawable, null, null,
                    null);
            AppCompatTextView filterKeywordCount = filterKeywordsView
                    .getFilterKeywordCount();
            if (mPrefsFactory.twitterKeywords().get() != null && TextUtils
                    .isEmpty(mPrefsFactory.twitterKeywords().get())) {
                String[] keywords = mPrefsFactory.twitterKeywords().get().split(",");
                filterKeywordCount.setText(keywords.length);
            } else {
                filterKeywordCount.setText(0);
            }
            mFilterViewGroup.addView(filterKeywordsView);
        }
    }

    private void initBlackListFilters(String count) {
        final SwitchCompat filterKeywordsSwitch = mBlackListFilterKeywordsView
                .getSwitchCompat();
        filterKeywordsSwitch.setChecked(mPrefsFactory.enableBlacklist().get());
        if (filterKeywordsSwitch.isChecked()) {
            mBlackListFilterKeywordsView.visible(true);
        }
        mBlackListFilterKeywordsView.setSwitchListener(v -> {
            if (filterKeywordsSwitch.isChecked()) {
                filterKeywordsSwitch.setChecked(true);
                mPrefsFactory.enableBlacklist().set(true);
            } else {
                filterKeywordsSwitch.setChecked(false);
                mPrefsFactory.enableBlacklist().set(false);
            }
        });

        mBlackListFilterKeywordsView.setFilterItemListener(v -> {
            mLauncher.launchAddPhoneNumber();
        });

        AppCompatTextView filterKeywordCount = mBlackListFilterKeywordsView.getFilterKeywordCount();
        filterKeywordCount.setText(count);
        AppCompatTextView filterKeyword = mBlackListFilterKeywordsView.getFilterKeyword();
        filterKeyword.setText(R.string.phone_numbers);
        mBlackListFilterKeywordsView.getFilterKeywordCount().setText(count);
    }

    private void initWhiteListFilters(String count) {
        final SwitchCompat filterKeywordsSwitch = mWhiteListFilterKeywordsView
                .getSwitchCompat();
        filterKeywordsSwitch.setChecked(mPrefsFactory.enableWhitelist().get());
        if (filterKeywordsSwitch.isChecked()) {
            mWhiteListFilterKeywordsView.visible(true);
        }
        mWhiteListFilterKeywordsView.setSwitchListener(v -> {
            if (filterKeywordsSwitch.isChecked()) {
                filterKeywordsSwitch.setChecked(true);
                mPrefsFactory.enableWhitelist().set(true);
            } else {
                filterKeywordsSwitch.setChecked(false);
                mPrefsFactory.enableWhitelist().set(false);
            }
        });
        mWhiteListFilterKeywordsView.setFilterItemListener(v -> {
            mLauncher.launchAddPhoneNumber();
        });
        AppCompatTextView filterKeywordCount = mWhiteListFilterKeywordsView.getFilterKeywordCount();
        filterKeywordCount.setText(count);

        AppCompatTextView filterKeyword = mWhiteListFilterKeywordsView.getFilterKeyword();
        filterKeyword.setText(R.string.phone_numbers);
        mWhiteListFilterKeywordsView.getFilterKeywordCount().setText(count);
    }

    private void initCustomIntegrationFilters(WebServiceModel webServiceModel,
            @DrawableRes int integrationDrawableResId, String count) {
        FilterKeywordsView filterKeywordsView = new FilterKeywordsView(getContext());
        final SwitchCompat filterKeywordsSwitch = filterKeywordsView.getSwitchCompat();
        filterKeywordsSwitch.setChecked(
                webServiceModel.getKeywordStatus() == WebServiceModel.KeywordStatus.ENABLED);
        if (filterKeywordsSwitch.isChecked()) {
            filterKeywordsView.visible(true);
        }
        filterKeywordsView.setSwitchListener(v -> {
            if (filterKeywordsSwitch.isChecked()) {
                filterKeywordsSwitch.setChecked(true);
                webServiceModel.setKeywordStatus(WebServiceModel.KeywordStatus.ENABLED);
            } else {
                filterKeywordsSwitch.setChecked(false);
                webServiceModel.setKeywordStatus(WebServiceModel.KeywordStatus.ENABLED);
            }
            mUpdateWebServiceKeywordsPresenter.updateWebService(webServiceModel);
        });

        filterKeywordsView.setFilterItemListener(v -> {
            mLauncher.launchAddKeyword(webServiceModel);
        });

        final AppCompatTextView title = filterKeywordsView.getTitle();
        title.setText(webServiceModel.getTitle());
        final Drawable customWebServiceDrawable = ContextCompat.getDrawable(getContext(),
                integrationDrawableResId);
        title.setCompoundDrawablesWithIntrinsicBounds(customWebServiceDrawable, null, null, null);
        AppCompatTextView filterKeyword = filterKeywordsView.getFilterKeyword();
        filterKeyword.setText(R.string.keywords);
        AppCompatTextView filterKeywordCount = filterKeywordsView.getFilterKeywordCount();
        filterKeywordCount.setText(count);
        mFilterViewGroup.addView(filterKeywordsView);
    }

    @Override
    public void onWebServiceSuccessfullyUpdated(Long row) {
        // Do nothing
    }
}
