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

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.PropertyValuesHolder;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import org.addhen.smssync.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * An animated view for displaying filtered keywords
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class KeywordView extends LinearLayout {

    private List<Tag> mTagList = new ArrayList<>();

    private OnTagSelectListener mSelectListener;

    private String mViewTagPrefix = "tag_";

    /** view size param */
    private int mWidth;

    private int mHeight;

    private static final int LAYOUT_WIDTH_OFFSET = 3;

    /** Attributes **/

    private static final int DEFAULT_CORNER_RADIUS = 6;

    private static final boolean DEFAULT_UPPERCASE = false;

    private static final int DEFAULT_TEXT_SIZE = 14;

    private int mTagMargin = 10;

    private int mTextPaddingTop = 5;

    private int mTextPaddingBottom = 5;

    private int mTextPaddingLeft = 8;

    private int mTextPaddingRight = 8;

    private int mTagDrawablePadding = 5;

    private int mTagTextColor = android.R.color.white;

    private int mTagCornerRadius;

    private boolean mUppercaseTags = DEFAULT_UPPERCASE;

    private float mTagTextSize;

    private boolean mIsAnimated = true;

    private Context mContext;

    private LinearLayout mRow;

    /**
     * Layout initialize flag
     */
    private boolean mInitialized = false;

    public KeywordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        mContext = context;
        TypedArray attributesArray = context.obtainStyledAttributes(attrs, R.styleable.TagView);
        mTagMargin = (int) attributesArray
                .getDimension(R.styleable.TagView_tagMargin,
                        dipToPixels(mTagMargin));
        mTextPaddingLeft = (int) attributesArray
                .getDimension(R.styleable.TagView_textPaddingLeft,
                        dipToPixels(mTextPaddingLeft));
        mTextPaddingRight = (int) attributesArray
                .getDimension(R.styleable.TagView_textPaddingRight,
                        dipToPixels(mTextPaddingRight));
        mTextPaddingTop = (int) attributesArray
                .getDimension(R.styleable.TagView_textPaddingTop,
                        dipToPixels(mTextPaddingTop));
        mTextPaddingBottom = (int) attributesArray
                .getDimension(R.styleable.TagView_textPaddingBottom,
                        dipToPixels(mTextPaddingBottom));
        mTagCornerRadius = attributesArray
                .getDimensionPixelSize(R.styleable.TagView_tagCornerRadius,
                        dipToPixels(DEFAULT_CORNER_RADIUS));
        mUppercaseTags = attributesArray
                .getBoolean(R.styleable.TagView_tagUppercase, DEFAULT_UPPERCASE);
        mTagDrawablePadding = (int) attributesArray
                .getDimension(R.styleable.TagView_tagDrawablePadding,
                        dipToPixels(mTagDrawablePadding));
        mTagTextSize = attributesArray.getDimension(
                R.styleable.TagView_tagTextSize, DEFAULT_TEXT_SIZE);
        mTagTextColor = attributesArray.getColor(R.styleable.TagView_tagTextColor,
                mContext.getResources().getColor(mTagTextColor));
        mIsAnimated = attributesArray.getBoolean(
                R.styleable.TagView_isActionAnimated, mIsAnimated);
        attributesArray.recycle();
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        getViewTreeObserver().addOnGlobalLayoutListener(
                () -> {
                    if (!mInitialized) {
                        mInitialized = true;
                        setTags();
                    }
                });

    }

    public void setTags() {
        if (!mInitialized) {
            return;
        }
        removeAllViews();
        mRow = null;
        float totalPadding = getPaddingLeft() + getPaddingRight();
        int indexFrontView = 0;
        LayoutParams itemParams = getItemLayoutParams();
        for (int i = 0; i < mTagList.size(); i++) {
            final int position = i;
            final Tag tag = mTagList.get(i);
            String tagContent = mUppercaseTags ? tag.getTag().toUpperCase() : tag.getTag();
            final AppCompatTextView textView = new AppCompatTextView(mContext);
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, tag.getTagDrawableResId(), 0);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTagTextSize);
            textView.setText(tagContent);

            int r = dipToPixels(mTagCornerRadius);
            float[] outerR = new float[]{r, r, r, r, r, r, r, r};
            ShapeDrawable colorDrawable = new ShapeDrawable(
                    new RoundRectShape(outerR, null, null));
            colorDrawable.getPaint().setAntiAlias(true);
            colorDrawable.getPaint().setStyle(Paint.Style.FILL);
            colorDrawable.getPaint().setColor(tag.getColor());
            if (isJellyBeanAndAbove()) {
                textView.setBackground(colorDrawable);
            } else {
                textView.setBackgroundDrawable(colorDrawable);
            }

            textView.setCompoundDrawablePadding(mTagDrawablePadding);
            textView.setOnClickListener(v -> {
                animateView(v);
                if (mSelectListener != null) {
                    mSelectListener.onTagSelected(textView, tag, position);
                }
            });
            textView.setPadding(mTextPaddingLeft, mTextPaddingTop, mTextPaddingRight,
                    mTextPaddingBottom);
            textView.setTextColor(mTagTextColor);

            float itemWidth = textView.getPaint().measureText(tag.getTag()) + mTextPaddingLeft
                    + mTextPaddingRight;

            itemWidth += dipToPixels(25) + mTextPaddingLeft + mTextPaddingRight;

            if (mWidth <= totalPadding + itemWidth + dipToPixels(LAYOUT_WIDTH_OFFSET)) {
                totalPadding = getPaddingLeft() + getPaddingRight();
                indexFrontView = i;
                textView.setTag(getViewTag(tag.getTagId()));
                addItemView(textView, itemParams, true, i);
            } else {
                if (i != indexFrontView) {
                    itemParams.leftMargin = mTagMargin;
                    totalPadding += mTagMargin;
                }
                textView.setTag(getViewTag(tag.getTagId()));
                addItemView(textView, itemParams, false, i);
            }
            totalPadding += itemWidth;
        }
    }


    private LayoutParams getItemLayoutParams() {
        LayoutParams itemParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        itemParams.bottomMargin = mTagMargin / 2;
        itemParams.topMargin = mTagMargin / 2;

        return itemParams;
    }

    private String getViewTag(long tagId) {
        return mViewTagPrefix + tagId;
    }

    private void addItemView(View itemView, ViewGroup.LayoutParams chipParams, boolean newLine,
            int position) {
        if (mRow == null || newLine) {
            mRow = new LinearLayout(getContext());
            mRow.setOrientation(HORIZONTAL);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mRow.setLayoutParams(params);
            addView(mRow);
        }
        mRow.addView(itemView, chipParams);
        animateItemView(itemView, position);
    }

    public void clearTags() {
        mTagList.clear();
    }

    /**
     * Remove Tag
     */
    public void removeTag(View view, final int position) {
        LinearLayout rowFound = null;
        for (int i = 0; i < getChildCount(); i++) {
            final LinearLayout row = (LinearLayout) getChildAt(i);
            if (row.findViewWithTag(getViewTag(mTagList.get(position).mTagId)) != null) {
                rowFound = row;
                break;
            }
        }

        final View v = rowFound.findViewWithTag(getViewTag(mTagList.get(position).mTagId));
        rowFound.removeView(v);

        if (rowFound.getChildCount() > 0) {
            animateScaleDownAndFadeOut(v, position);
            rowFound.removeView(
                    rowFound.findViewWithTag(getViewTag(mTagList.get(position).mTagId)));
        } else {
            removeView(rowFound);
        }
    }

    private int dipToPixels(float dipValue) {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        mWidth = width;
        mHeight = height;
    }

    /**
     * Get layout's width
     *
     * @return layout width
     */
    public int width() {
        return mWidth;
    }

    /**
     * Get Layout height
     *
     * @return int layout height
     */
    public int height() {
        return mHeight;
    }

    /**
     * Add a tag Tag
     */
    public void add(Tag tag) {
        mTagList.add(tag);
    }

    public List<Tag> getTags() {
        return mTagList;
    }

    private boolean isJellyBeanAndAbove() {
        return android.os.Build.VERSION.SDK_INT >= 16;
    }

    private void animateView(final View view) {
        if (!mIsAnimated) {
            return;
        }
        ViewPropertyAnimator.animate(view)
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(100)
                .setStartDelay(0)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        reverseAnimation(view);
                    }

                    @Override
                    public void onAnimationCancel(
                            Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(
                            Animator animation) {
                    }
                })
                .start();
    }

    private void reverseAnimation(View view) {
        if (!mIsAnimated) {
            return;
        }
        ViewHelper.setScaleY(view, 1.2f);
        ViewHelper.setScaleX(view, 1.2f);
        ViewPropertyAnimator.animate(view)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(100)
                .setListener(null)
                .start();
    }

    // For older android devices
    private void animateItemView(View view, int position) {
        if (!mIsAnimated) {
            return;
        }
        long animationDelay = 600;

        animationDelay += position * 30;
        ViewHelper.setScaleY(view, 0);
        ViewHelper.setScaleX(view, 0);
        ViewPropertyAnimator.animate(view)
                .scaleY(1)
                .scaleX(1)
                .setDuration(200)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(null)
                .setStartDelay(animationDelay)
                .start();
    }

    private void animateScaleDownAndFadeOut(View view, int position) {
        if (!mIsAnimated) {
            return;
        }
        long animationDelay = 600;

        animationDelay += position * 30;
        ViewHelper.setScaleY(view, 1f);
        ViewHelper.setScaleX(view, 1f);
        ObjectAnimator scaleDown
                = ObjectAnimator.ofPropertyValuesHolder(view,
                PropertyValuesHolder.ofFloat("scaleX", 1.15f, 1.0f),
                PropertyValuesHolder.ofFloat("scaleY", 1.15f, 1.0f));
        ObjectAnimator alpha1
                = ObjectAnimator.ofFloat(view, "alpha", 0.5f);
        alpha1.setDuration(1000);

        ObjectAnimator alpha2
                = ObjectAnimator.ofFloat(view, "alpha", 0);
        alpha2.setDuration(2000);
        AnimatorSet animset
                = new AnimatorSet();
        animset.play(scaleDown).before(alpha1).with(alpha2);
        animset.setStartDelay(animationDelay);
        animset.start();
    }

    /**
     * Setter for OnTagSelectListener
     */
    public void setOnTagSelectListener(OnTagSelectListener selectListener) {
        mSelectListener = selectListener;
    }

    public static class Tag {

        private final String mTagText;

        private final int mTagColor;

        private final int mTagDrawableResId;

        private final long mTagId;

        public Tag(long tagId, String tagText, int tagColor) {
            this(tagId, tagText, tagColor, 0);
        }

        public Tag(long tagId, String tagText, int tagColor, @DrawableRes int tagDrawableResId) {
            mTagId = tagId;
            mTagText = tagText;
            mTagColor = tagColor;
            mTagDrawableResId = tagDrawableResId;
        }

        public String getTag() {
            return mTagText;
        }

        public int getColor() {
            return mTagColor;
        }

        public int getTagDrawableResId() {
            return mTagDrawableResId;
        }

        public long getTagId() {
            return mTagId;
        }
    }

    /**
     * Listener to be called when tag is selected
     */
    public interface OnTagSelectListener {

        void onTagSelected(View view, Tag tag, int position);
    }
}
