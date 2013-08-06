package com.github.jberkel.pay.me.listener;

import com.github.jberkel.pay.me.IabResult;
import com.github.jberkel.pay.me.model.Purchase;

import java.util.List;

/**
 * Callback that notifies when a multi-item consumption operation finishes.
 */
public interface OnConsumeMultiFinishedListener {
    /**
     * Called to notify that a consumption of multiple items has finished.
     *
     * @param purchases The purchases that were (or were to be) consumed.
     * @param results The results of each consumption operation, corresponding to each
     *     sku.
     */
    public void onConsumeMultiFinished(List<Purchase> purchases, List<IabResult> results);
}
