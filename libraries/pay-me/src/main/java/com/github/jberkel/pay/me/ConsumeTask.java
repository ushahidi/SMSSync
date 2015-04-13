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

package com.github.jberkel.pay.me;

import android.os.AsyncTask;
import com.github.jberkel.pay.me.listener.OnConsumeFinishedListener;
import com.github.jberkel.pay.me.listener.OnConsumeMultiFinishedListener;
import com.github.jberkel.pay.me.model.Purchase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.jberkel.pay.me.Response.OK;

class ConsumeTask extends AsyncTask<Purchase, Void, List<IabResult>> {
    private final IabHelper mIabHelper;
    private final OnConsumeFinishedListener mSingleListener;
    private final OnConsumeMultiFinishedListener mMultiListener;
    private List<Purchase> mPurchases;

    public ConsumeTask(IabHelper iabHelper,
                       OnConsumeFinishedListener singleListener,
                       OnConsumeMultiFinishedListener multiListener) {
        mIabHelper = iabHelper;
        mSingleListener = singleListener;
        mMultiListener = multiListener;
    }

    @Override
    protected void onPreExecute() {
        mIabHelper.flagStartAsync("consume");
    }

    @Override
    protected List<IabResult> doInBackground(final Purchase... purchases) {
        if (purchases == null || purchases.length == 0) throw new IllegalArgumentException("no purchases");

        mPurchases = new ArrayList<Purchase>(purchases.length);
        Collections.addAll(mPurchases, purchases);

        final List<IabResult> results = new ArrayList<IabResult>(purchases.length);
        for (Purchase purchase : purchases) {
            try {
                mIabHelper.consume(purchase);
                results.add(new IabResult(OK, "Successful consume of sku " + purchase.getSku()));
            } catch (IabException ex) {
                results.add(ex.getResult());
            }
        }
        return results;
    }

    @Override
    protected void onPostExecute(List<IabResult> results) {
        mIabHelper.flagEndAsync();
        if (mIabHelper.isDisposed() || isCancelled()) return;

        if (mSingleListener != null) {
            mSingleListener.onConsumeFinished(mPurchases.get(0), results.get(0));
        }

        if (mMultiListener != null) {
            mMultiListener.onConsumeMultiFinished(mPurchases, results);
        }
    }
}
