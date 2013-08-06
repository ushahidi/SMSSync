package com.github.jberkel.pay.me;

import com.github.jberkel.pay.me.listener.OnIabPurchaseFinishedListener;
import com.github.jberkel.pay.me.model.ItemType;
import com.github.jberkel.pay.me.model.Purchase;

import static com.github.jberkel.pay.me.model.ItemType.UNKNOWN;

class PurchaseFlowState implements OnIabPurchaseFinishedListener {
    static final PurchaseFlowState NONE = new PurchaseFlowState(-1, UNKNOWN, null);

    /** The request code used to launch purchase flow */
    final int requestCode;
    /** The item type of the current purchase flow */
    final ItemType itemType;
    /**  The listener registered on launchPurchaseFlow, which we have to call back when the purchase finishes */
    final OnIabPurchaseFinishedListener listener;

    PurchaseFlowState(int requestCode, ItemType itemType, OnIabPurchaseFinishedListener listener) {
        if (itemType == null) throw new IllegalArgumentException("itemType cannot be null");
        this.requestCode = requestCode;
        this.itemType = itemType;
        this.listener = listener;
    }

    @Override
    public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
        if (listener != null) {
            listener.onIabPurchaseFinished(result, purchase);
        }
    }
}
