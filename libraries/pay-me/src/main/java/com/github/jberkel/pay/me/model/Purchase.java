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
 * Represents an in-app billing purchase.
 * <p/>
 * See also
 * {@link com.android.vending.billing.IInAppBillingService#getPurchases(int, String, String, String)}
 */
public class Purchase {

    private final String mOrderId;
    private final String mPackageName;
    private final String mSku;
    private final long mPurchaseTime;
    private final int mPurchaseState;
    private final String mDeveloperPayload;
    private final String mToken;

    private final ItemType mItemType;
    private final State mState;
    private final String mSignature;
    private final String mOriginalJson;


    /**
     * @param itemType the item type for this purchase, cannot be null.
     * @param jsonPurchaseInfo the JSON representation of this purchase
     * @param signature the signature
     * @throws JSONException if the purchase cannot be parsed or is invalid.
     */
    public Purchase(ItemType itemType, String jsonPurchaseInfo, String signature) throws JSONException {
        if (itemType == null) throw new IllegalArgumentException("itemType cannot be null");
        mItemType = itemType;
        final JSONObject json = new JSONObject(jsonPurchaseInfo);

        mOrderId = json.optString(ORDER_ID);
        mPackageName = json.optString(PACKAGE_NAME);
        mSku = json.optString(PRODUCT_ID);
        mPurchaseTime = json.optLong(PURCHASE_TIME);
        mPurchaseState = json.optInt(PURCHASE_STATE);
        mDeveloperPayload = json.optString(DEVELOPER_PAYLOAD);
        mToken = json.optString(TOKEN, json.optString(PURCHASE_TOKEN));

        mOriginalJson = jsonPurchaseInfo;
        mSignature = signature;
        mState = State.fromCode(mPurchaseState);

        if (TextUtils.isEmpty(mSku)) {
            throw new JSONException("SKU is empty");
        }
    }

    public ItemType getItemType() {
        return mItemType;
    }

    /**
     * @return A unique order identifier for the transaction. This corresponds to the Google Wallet Order ID.
     */
    public String getOrderId() {
        return mOrderId;
    }

    /**
     *
     * @return The application package from which the purchase originated.
     */
    public String getPackageName() {
        return mPackageName;
    }

    /**
     * @return The item's product identifier. Every item has a product ID, which you must specify in
     * the application's product list on the Google Play Developer Console.
     */
    public String getSku() {
        return mSku;
    }

    /**
     * @return The time the product was purchased, in milliseconds since the epoch (Jan 1, 1970).
     */
    public long getPurchaseTime() {
        return mPurchaseTime;
    }

    /**
     * @return The purchase state of the order. Possible values are 0 (purchased), 1 (canceled), or 2 (refunded).
     */
    public int getRawState() {
        return mPurchaseState;
    }

    /**
     * @return The parsed purchase state of the order.
     */
    public State getState() {
        return mState;
    }

    /**
     * @return A developer-specified string that contains supplemental information about an order. You
     * can specify a value for this field when you make a getBuyIntent request.
     */
    public String getDeveloperPayload() {
        return mDeveloperPayload;
    }

    /**
     * @return A token that uniquely identifies a purchase for a given item and user pair.
     */
    public String getToken() {
        return mToken;
    }

    /**
     * @return the original JSON response, as received from the billing service.
     */
    public String getOriginalJson() {
        return mOriginalJson;
    }

    /**
     * @return the signature, as received from the billing service.
     */
    public String getSignature() { return mSignature; }

    @Override
    public String toString() {
        return "Purchase(type:" + mItemType + "):" + mOriginalJson;
    }

    /**
     * The purchase state of the order.
     */
    public enum State {
        PURCHASED(0),
        CANCELED(1),
        REFUNDED(2),
        UNKNOWN(-1);

        final int code;

        State(int code) {
            this.code = code;
        }
        public static State fromCode(int code) {
            for (State s : values()) {
                if (s.code == code) return s;
            }
            return UNKNOWN;
        }
    }

    // fields used in service JSON response
    private static final String PACKAGE_NAME = "packageName";
    private static final String PRODUCT_ID = "productId";
    private static final String PURCHASE_TIME = "purchaseTime";
    private static final String PURCHASE_STATE = "purchaseState";
    private static final String DEVELOPER_PAYLOAD = "developerPayload";
    private static final String PURCHASE_TOKEN = "purchaseToken";
    private static final String TOKEN = "token";
    private static final String ORDER_ID = "orderId";
}
