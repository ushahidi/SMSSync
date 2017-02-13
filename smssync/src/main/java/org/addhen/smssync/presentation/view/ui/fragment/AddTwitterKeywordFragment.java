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
import org.addhen.smssync.data.util.Utility;
import org.addhen.smssync.presentation.view.ui.widget.KeywordView;
import org.addhen.smssync.presentation.view.webservice.UpdateWebServiceKeywordsView;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class AddTwitterKeywordFragment extends BaseFragment implements
        UpdateWebServiceKeywordsView {

    private static AddTwitterKeywordFragment mAddTwitterKeywordFragment;

    @BindView(R.id.filter_keyword_integration_title)
    AppCompatTextView mWebServiceTitleTextView;

    @BindView(R.id.keywords_container)
    KeywordView mKeywordsView;

    private PrefsFactory mPrefsFactory;

    private List<String> mKeywords;


    /**
     * BaseFragment
     */
    public AddTwitterKeywordFragment() {
        super(R.layout.fragment_add_keywords, 0);
    }

    public static AddTwitterKeywordFragment newInstance() {
        if (mAddTwitterKeywordFragment == null) {
            mAddTwitterKeywordFragment = new AddTwitterKeywordFragment();
        }
        return mAddTwitterKeywordFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        initialize();
        return view;
    }

    @OnClick(R.id.add_keyword_btn)
    void onAddKeywordClicked() {
        showDialog();
    }

    private void initialize() {
        mWebServiceTitleTextView.setText(R.string.twitter);
        initKeywords();
    }

    private void initKeywords() {
        if (mPrefsFactory.twitterKeywords() != null && !TextUtils
                .isEmpty(mPrefsFactory.twitterKeywords().get())) {
            mKeywords = new ArrayList(
                    Arrays.asList(mPrefsFactory.twitterKeywords().get().split(",")));
            setKeywords();
            mKeywordsView.setOnTagSelectListener(new KeywordView.OnTagSelectListener() {
                @Override
                public void onTagSelected(View view, KeywordView.Tag tag, int position) {
                    mKeywords.remove(tag.getTag());
                    final String keyword = TextUtils.join(",", mKeywords);
                    mPrefsFactory.twitterKeywords().set(keyword);
                }
            });
        }
    }

    private void setKeywords() {
        for (int i = 0; i < mKeywords.size(); i++) {
            KeywordView.Tag tag = new KeywordView.Tag(i, mKeywords.get(i),
                    org.addhen.smssync.presentation.util.Utility.keywordColor(),
                    org.addhen.smssync.presentation.util.Utility.keywordIcon());
            mKeywordsView.add(tag);
        }
        mKeywordsView.setTags();
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_keyword, null);
        EditText keywordEditText = ButterKnife.findById(view, R.id.add_keyword_text);
        builder.setView(view).setPositiveButton(R.string.add,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (Utility.isEmpty(mKeywords)) {
                            mKeywords = new ArrayList<String>();
                            mKeywords.add(keywordEditText.getText().toString());
                        } else {
                            mKeywords.add(keywordEditText.getText().toString());
                        }
                        mPrefsFactory.twitterKeywords().set(TextUtils.join(",", mKeywords));
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }

    @Override
    public void onWebServiceSuccessfullyUpdated(Long row) {
        setKeywords();
    }

    @Override
    public void showError(String message) {
        showSnackbar(getView(), message);
    }

    @Override
    public Context getAppContext() {
        return getContext().getApplicationContext();
    }

    public void setPrefsFactory(PrefsFactory prefsFactory) {
        mPrefsFactory = prefsFactory;
    }
}
