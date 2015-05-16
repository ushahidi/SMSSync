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

package com.github.jberkel.pay.me.model;

import android.text.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents an in-app product's listing details.
 * <p/>
 * See also
 * {@link com.android.vending.billing.IInAppBillingService#getSkuDetails(int, String, String, android.os.Bundle)}
 */
public class SkuDetails {
    private final String mSku;
    private final String mType;
    private final String mPrice;
    private final String mTitle;
    private final String mDescription;

    private String mJson;
    private final ItemType mItemType;

    public SkuDetails(String jsonSkuDetails) throws JSONException {
        mJson = jsonSkuDetails;
        JSONObject json = new JSONObject(mJson);
        mSku = json.optString(PRODUCT_ID);
        if (TextUtils.isEmpty(mSku)) {
            throw new JSONException("SKU cannot be empty");
        }
        mType = json.optString(TYPE);
        mPrice = json.optString(PRICE);
        mTitle = json.optString(TITLE);
        mDescription = json.optString(DESCRIPTION);
        mItemType = ItemType.fromString(mType);
    }

    // package constructor for TestSkus
    SkuDetails(ItemType itemType,
               String sku,
               String price,
               String title,
               String description) {
        if (itemType == null) throw new IllegalArgumentException("itemType cannot be null");
        if (TextUtils.isEmpty(sku)) {
            throw new IllegalArgumentException("SKU cannot be empty");
        }
        mItemType = itemType;
        mType = itemType.toString();
        mSku = sku;
        mPrice = price;
        mTitle = title;
        mDescription = description;
    }

    /**
     * @return The product ID for the product.
     */
    public String getSku() {
        return mSku;
    }

    /**
     * @return Formatted price of the item, including its currency sign. The price does not include tax.
     */
    public String getPrice() {
        return mPrice;
    }

    /**
     * @return Title of the product.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * @return Description of the product.
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * @return Value must be “inapp” for an in-app product or "subs" for subscriptions.
     */
    public String getRawType() {
        return mType;
    }

    /**
     * @return parsed representation of {@link #getRawType()}.
     */
    public ItemType getType() {
        return mItemType;
    }

    @Override
    public String toString() {
        return "SkuDetails{" +
                "mItemType='" + mItemType + '\'' +
                ", mSku='" + mSku + '\'' +
                ", mType='" + mType + '\'' +
                ", mPrice='" + mPrice + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", mJson='" + mJson + '\'' +
                '}';
    }

    public boolean isTestSku() {
        return mSku.startsWith(TestSkus.TEST_PREFIX);
    }

    // fields used in service JSON response
    private static final String PRODUCT_ID = "productId";
    private static final String TYPE = "type";
    private static final String PRICE = "price";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
}
