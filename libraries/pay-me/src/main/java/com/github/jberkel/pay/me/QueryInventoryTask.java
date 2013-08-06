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

