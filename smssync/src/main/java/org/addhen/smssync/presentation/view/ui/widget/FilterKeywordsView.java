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

package org.addhen.smssync.presentation.view.ui.widget;

import org.addhen.smssync.R;
import org.addhen.smssync.presentation.util.Utility;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class FilterKeywordsView extends LinearLayout {

    private static final int DEFAULT_TEXT_SIZE = 15;

    private AppCompatTextView mTitleTextView;

    private SwitchCompat mSwitchCompat;

    private AppCompatTextView mFilterKeyword;

    private AppCompatTextView mFilterKeywordCount;

    private int mKeywordDrawablePadding = 24;

    private int mKeywordCounterDrawablePadding = 16;

    private int mKeywordTextColor = getResources().getColor(android.R.color.black);

    private int mKeywordCounterTextColor = getResources().getColor(android.R.color.black);

    private float mKeywordCounterTextSize = DEFAULT_TEXT_SIZE;

    private float mKeywordTextSize = DEFAULT_TEXT_SIZE;

    private ViewGroup mKeywordsContainer;

    private Drawable mKeywordIcon;

    private Drawable mTitleIcon;

    private CharSequence mKeywordText;

    private CharSequence mTitle;

    private SwitchListener mSwitchListener;

    private FilterItemListener mFilterItemListener;

    public FilterKeywordsView(Context context) {
        super(context);
        initView();
    }

    public FilterKeywordsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rootView = inflater.inflate(R.layout.filter_keywords_view, this);
        mTitleTextView = (AppCompatTextView) rootView.findViewById(R.id.custom_web_service_filter);
        mKeywordsContainer = (ViewGroup) rootView.findViewById(R.id.filter_keywords_container);
        mSwitchCompat = (SwitchCompat) rootView.findViewById(
                R.id.custom_filter_keyword_custom_switch);
        mFilterKeyword = (AppCompatTextView) rootView.findViewById(R.id.filter_keyword);
        mFilterKeywordCount = (AppCompatTextView) rootView.findViewById(R.id.filter_keyword_count);
        mTitleTextView.setText(mTitle);
        mTitleTextView.setCompoundDrawablesWithIntrinsicBounds(mTitleIcon, null, null, null);
        mFilterKeyword.setCompoundDrawablePadding(mKeywordDrawablePadding);
        mFilterKeyword.setTextSize(mKeywordTextSize);
        mFilterKeyword.setTextColor(mKeywordTextColor);
        mFilterKeywordCount.setCompoundDrawablePadding(mKeywordCounterDrawablePadding);
        mFilterKeywordCount.setTextSize(mKeywordCounterTextSize);
        mFilterKeywordCount.setTextColor(mKeywordCounterTextColor);
        mFilterKeyword.setText(mKeywordText);
        mFilterKeyword.setCompoundDrawablesWithIntrinsicBounds(mKeywordIcon, null, null, null);
        mSwitchCompat.setOnClickListener(v -> {
            toggleFilters();
            if (mSwitchListener != null) {
                mSwitchListener.onSwitchClicked(v);
            }
        });
        mKeywordsContainer.setOnClickListener(v -> {
            if (mFilterItemListener != null) {
                mFilterItemListener.onFilterItemClicked(v);
            }
        });
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray attributesArray = getContext().obtainStyledAttributes(attrs,
                R.styleable.FilterKeywords);
        try {
            mKeywordDrawablePadding = (int) attributesArray
                    .getDimension(R.styleable.FilterKeywords_filterKeywordDrawablePadding,
                            Utility.dipToPixels(getContext(), mKeywordDrawablePadding));
            mKeywordCounterDrawablePadding = (int) attributesArray
                    .getDimension(R.styleable.FilterKeywords_filterKeywordCounterDrawablePadding,
                            Utility.dipToPixels(getContext(), mKeywordCounterDrawablePadding));
            mKeywordTextColor = attributesArray
                    .getColor(R.styleable.FilterKeywords_filterKeywordTextColor, mKeywordTextColor);
            mKeywordCounterTextColor = attributesArray.getColor(
                    R.styleable.FilterKeywords_filterKeywordCounterTextColor,
                    mKeywordCounterTextColor);

            mKeywordTextSize = attributesArray
                    .getDimensionPixelSize(R.styleable.FilterKeywords_filterKeywordTextSize,
                            DEFAULT_TEXT_SIZE);

            mKeywordCounterTextSize = attributesArray
                    .getDimensionPixelSize(R.styleable.FilterKeywords_filterKeywordTextSize,
                            DEFAULT_TEXT_SIZE);
            mKeywordText = attributesArray.getText(R.styleable.FilterKeywords_filterKeywordText);
            mKeywordIcon = attributesArray
                    .getDrawable(R.styleable.FilterKeywords_filterKeywordIcon);
            mTitle = attributesArray.getText(R.styleable.FilterKeywords_filterTitleText);
            mTitleIcon = attributesArray.getDrawable(R.styleable.FilterKeywords_filterTitleIcon);
        } finally {
            attributesArray.recycle();
        }

    }

    public void setSwitchListener(SwitchListener switchListener) {
        mSwitchListener = switchListener;
    }

    public void setFilterItemListener(FilterItemListener filterItemListener) {
        mFilterItemListener = filterItemListener;
    }

    public void toggleFilters() {
        if (mKeywordsContainer.getVisibility() == View.VISIBLE) {
            visible(false);
        } else {
            visible(true);
        }
    }

    public void visible(boolean isVisible) {
        if (isVisible) {
            Animation in = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
            mKeywordsContainer.startAnimation(in);
            mKeywordsContainer.setVisibility(View.VISIBLE);
        } else {
            Animation out = AnimationUtils.makeOutAnimation(getContext(), true);
            mKeywordsContainer.startAnimation(out);
            mKeywordsContainer.setVisibility(View.GONE);
        }
    }

    public AppCompatTextView getTitle() {
        return mTitleTextView;
    }

    public SwitchCompat getSwitchCompat() {
        return mSwitchCompat;
    }

    public AppCompatTextView getFilterKeyword() {
        return mFilterKeyword;
    }

    public AppCompatTextView getFilterKeywordCount() {
        return mFilterKeywordCount;
    }

    public CharSequence getText() {
        return mKeywordText;
    }

    public interface SwitchListener {

        void onSwitchClicked(View view);
    }

    public interface FilterItemListener {

        void onFilterItemClicked(View view);
    }
}
