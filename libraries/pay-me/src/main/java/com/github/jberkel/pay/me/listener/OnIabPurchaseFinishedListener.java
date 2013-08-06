package com.github.jberkel.pay.me.listener;

import com.github.jberkel.pay.me.IabResult;
import com.github.jberkel.pay.me.model.Purchase;

/**
 * Callback that notifies when a purchase is finished.
 */
public interface OnIabPurchaseFinishedListener {
    /**
     * Called to notify that an in-app purchase finished. If the purchase was successful,
     * then the sku parameter specifies which item was purchased. If the purchase failed,
     * the sku and extraData parameters may or may not be null, depending on how far the purchase
     * process went.
     *
     * @param result The result of the purchase.
     * @param purchase The purchase information (null if purchase failed)
     */
    public void onIabPurchaseFinished(IabResult result, Purchase purchase);
}
