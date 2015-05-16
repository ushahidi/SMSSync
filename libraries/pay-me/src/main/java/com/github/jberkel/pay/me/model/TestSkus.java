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


/**
 * To test your implementation with static responses, you make an In-app Billing request using a special
 * item that has a reserved product ID. Each reserved product ID returns a specific static response
 * from Google Play. No money is transferred when you make In-app Billing requests with the reserved product
 * IDs. Also, you cannot specify the form of payment when you make a billing request with a
 * reserved product ID.
 *
 * @see <a href="http://developer.android.com/google/play/billing/billing_testing.html">
 *     Testing in-app purchases with static responses
 *     </a>
 */
public final class TestSkus {
    static final String TEST_PREFIX = "android.test.";
    static final String TEST_PRICE = "0.00 USD";
    /**
     * When you make an In-app Billing request with this product ID, Google Play responds as though
     * you successfully purchased an item. The response includes a JSON string, which contains fake
     * purchase information (for example, a fake order ID). In some cases, the JSON string is signed
     * and the response includes the signature so you can test your signature verification implementation
     * using these responses.
     */
    public static final SkuDetails PURCHASED =
        new SkuDetails(ItemType.INAPP, TEST_PREFIX+"purchased", TEST_PRICE, "Test (purchased)", "Purchased");


    /**
     * When you make an In-app Billing request with this product ID Google Play responds as though
     * the purchase was canceled. This can occur when an error is encountered in the order process,
     * such as an invalid credit card, or when you cancel a user's order before it is charged.
     */
    public static final SkuDetails CANCELED =
        new SkuDetails(ItemType.INAPP, TEST_PREFIX+"canceled", TEST_PRICE, "Test (canceled)", "Canceled");

    /**
     * When you make an In-app Billing request with this product ID, Google Play responds as though
     * the purchase was refunded. Refunds cannot be initiated through Google Play's in-app billing service.
     * Refunds must be initiated by you (the merchant). After you process a refund request through your
     * Google Wallet merchant account, a refund message is sent to your application by Google Play.
     * This occurs only when Google Play gets notification from Google Wallet that a refund has been made.
     * For more information about refunds, see
     * <a href="http://developer.android.com/google/play/billing/v2/api.html#billing-action-notify">
     *     Handling IN_APP_NOTIFY messages</a> and
     *  <a href="http://support.google.com/googleplay/android-developer/bin/answer.py?hl=en&answer=1153485">
     *         In-app Billing Pricing
     *  </a>.
     */
    public static final SkuDetails REFUNDED =
        new SkuDetails(ItemType.INAPP, TEST_PREFIX+"refunded", TEST_PRICE, "Test (refunded)", "Refunded");

    /**
     * When you make an In-app Billing request with this product ID, Google Play responds as though
     * the item being purchased was not listed in your application's product list.
     */
    public static final SkuDetails UNAVAILABLE =
        new SkuDetails(ItemType.INAPP, TEST_PREFIX+"item_unavailable", TEST_PRICE, "Test (unavailable)", "Unavailable");
}
