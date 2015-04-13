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
import com.github.jberkel.pay.me.listener.QueryInventoryFinishedListener;
import com.github.jberkel.pay.me.model.Inventory;

import java.util.List;

import static com.github.jberkel.pay.me.Response.OK;

class QueryInventoryTask extends AsyncTask<QueryInventoryTask.Args, Void, Inventory> {
    private final IabHelper mIabHelper;
    private final QueryInventoryFinishedListener mListener;
    private IabResult mResult = new IabResult(OK);

    public QueryInventoryTask(IabHelper iabHelper, QueryInventoryFinishedListener listener) {
        mIabHelper = iabHelper;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        mIabHelper.flagStartAsync("refresh inventory");
    }

    @Override
    protected Inventory doInBackground(Args... args) {
        if (args == null || args.length == 0 || args[0] == null) throw new IllegalArgumentException("need args");
        final Args arg = args[0];
        try {
            return mIabHelper.queryInventory(arg.queryDetails, arg.skus, arg.subSkus);
        } catch (IabException ex) {
            mResult = ex.getResult();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Inventory inventory) {
        mIabHelper.flagEndAsync();
        if (mListener != null && !mIabHelper.isDisposed() && !isCancelled()) {
            mListener.onQueryInventoryFinished(mResult, inventory);
        }
    }

    static class Args {
        final boolean queryDetails;
        final List<String> skus;
        final List<String> subSkus;
        public Args(boolean querySkuDetails, List<String> moreSkus, List<String> moreSubSkus) {
            queryDetails = querySkuDetails;
            skus = moreSkus;
            subSkus = moreSubSkus;
        }
    }
}

