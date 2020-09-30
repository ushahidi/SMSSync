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

import com.addhen.android.raiburari.presentation.ui.activity.BaseActivity;
import com.nineoldandroids.view.ViewHelper;

import org.addhen.smssync.R;
import org.addhen.smssync.presentation.view.ui.fragment.GettingStartedIntegrationFragment;
import org.addhen.smssync.presentation.view.ui.fragment.GettingStartedWelcomeFragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Provides cross fading between page slides. Code shamelessly lifted from: https://goo.gl/bkNXZQ
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class GettingStartedActivity extends BaseActivity {

    private static final String STATE_PARAM_SELECTED_PAGE = "org.addhen.smssync.SELECTED_PAGE";

    @BindView(R.id.getting_started_view_pager)
    ViewPager mViewPager;

    @BindView(R.id.getting_started_circles)
    LinearLayout mCircles;

    @BindView(R.id.getting_started_skip)
    Button mSkip;

    @BindView(R.id.getting_started_done)
    Button mDone;

    @BindView(R.id.getting_started_next)
    ImageButton mNext;

    private boolean mIsOpaque = true;

    private ViewPagerAdapter mPagerAdapter;

    private static int mCurrentItem;

    private int mNumOfPages = 0;

    public GettingStartedActivity() {
        super(R.layout.activity_getting_started, 0);
    }

    public static Intent getIntent(final Context context) {
        return new Intent(context, GettingStartedActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
        if (savedInstanceState != null) {
            mCurrentItem = savedInstanceState.getInt(STATE_PARAM_SELECTED_PAGE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mCurrentItem = mViewPager.getCurrentItem();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(STATE_PARAM_SELECTED_PAGE, mCurrentItem);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mViewPager != null) {
            mViewPager.clearOnPageChangeListeners();
            mCurrentItem = 0;
        }
    }

    private void initialize() {
        if (mViewPager != null) {
            setupViewPager(mViewPager);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), viewPager);
        mPagerAdapter.addFragment(GettingStartedWelcomeFragment.newInstance());
        mPagerAdapter.addFragment(GettingStartedIntegrationFragment.newInstance());
        mNumOfPages = mPagerAdapter.getCount();
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setPageTransformer(true, new CrossfadePageTransformer(viewPager));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels) {
                if (position == mNumOfPages - 1 && positionOffset > 0) {
                    if (mIsOpaque) {
                        mViewPager.setBackgroundColor(Color.TRANSPARENT);
                        mIsOpaque = false;
                    }
                } else {
                    if (!mIsOpaque) {
                        mViewPager.setBackgroundColor(
                                getResources().getColor(R.color.primary_material_light));
                        mIsOpaque = true;
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);
                if (position == mNumOfPages - 1) {
                    mSkip.setVisibility(View.GONE);
                    mNext.setVisibility(View.GONE);
                    mDone.setVisibility(View.VISIBLE);
                } else if (position < mNumOfPages - 1) {
                    mSkip.setVisibility(View.VISIBLE);
                    mNext.setVisibility(View.VISIBLE);
                    mDone.setVisibility(View.GONE);
                } else if (position == mNumOfPages - 1) {
                    finishScreen();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        renderViewPageIndicators();
    }

    @OnClick(R.id.getting_started_skip)
    void onSkipped() {
        finishScreen();
    }

    @OnClick(R.id.getting_started_next)
    void onNext() {
        mCurrentItem = mViewPager.getCurrentItem() + 1;
        mViewPager.setCurrentItem(mCurrentItem, true);
    }

    @OnClick(R.id.getting_started_done)
    void onDone() {
        finishScreen();
    }

    private void renderViewPageIndicators() {
        float scale = getResources().getDisplayMetrics().density;
        int padding = (int) (5 * scale + 0.5f);

        for (int i = 0; i < mNumOfPages - 1; i++) {
            ImageView circle = new ImageView(this);
            circle.setImageResource(R.drawable.ic_swipe_indicator_white_18dp);
            circle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            circle.setAdjustViewBounds(true);
            circle.setPadding(padding, 0, padding, 0);
            mCircles.addView(circle);
        }

        setIndicator(0);
    }

    private void setIndicator(int index) {
        if (index < mNumOfPages) {
            for (int i = 0; i < mNumOfPages - 1; i++) {
                ImageView circle = (ImageView) mCircles.getChildAt(i);
                if (i == index) {
                    circle.setColorFilter(getResources().getColor(R.color.grey_light));
                } else {
                    circle.setColorFilter(getResources().getColor(android.R.color.transparent));
                }
            }
        }
    }

    private void finishScreen() {
        finish();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
        }
    }

    private static class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private final List<Fragment> mFragments = new ArrayList<>();

        private ViewPager mViewPager;

        public ViewPagerAdapter(FragmentManager fm, ViewPager viewPager) {
            super(fm);
            mViewPager = viewPager;
        }

        /**
         * Adds a fragment to the view pager
         *
         * @param fragment The fragment to be added
         */
        public void addFragment(Fragment fragment) {
            mFragments.add(fragment);
            notifyDataSetChanged();
            mViewPager.setCurrentItem(mCurrentItem);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }

    private static class CrossfadePageTransformer implements ViewPager.PageTransformer {

        private ViewPager mViewPager;

        public CrossfadePageTransformer(ViewPager viewPager) {
            mViewPager = viewPager;
        }

        @Override
        public void transformPage(View page, float position) {
            int pageWidth = mViewPager.getWidth();
            View backgroundView = page.findViewById(R.id.getting_started_welcome_fragment);
            View textContent = page.findViewById(R.id.getting_started_welcome_content);
            View textHead = page.findViewById(R.id.getting_started_welcome_heading);

            if (0 <= position && position < 1) {
                ViewHelper.setTranslationX(page, pageWidth * -position);
            }
            if (-1 < position && position < 0) {
                ViewHelper.setTranslationX(page, pageWidth * -position);
            }
            if (!(position <= -1.0f) || !(position >= 1.0f)) {
                if (backgroundView != null) {
                    ViewHelper.setAlpha(backgroundView, 1.0f - Math.abs(position));
                }

                if (textHead != null) {
                    ViewHelper.setTranslationX(textHead, pageWidth * position);
                    ViewHelper.setAlpha(textHead, 1.0f - Math.abs(position));
                }

                if (textContent != null) {
                    ViewHelper.setTranslationX(textContent, pageWidth * position);
                    ViewHelper.setAlpha(textContent, 1.0f - Math.abs(position));
                }
            }
        }
    }
}
