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

package org.addhen.smssync.fragments;

import com.github.jberkel.pay.me.IabHelper;
import com.github.jberkel.pay.me.IabResult;
import com.github.jberkel.pay.me.listener.OnConsumeFinishedListener;
import com.github.jberkel.pay.me.listener.OnIabPurchaseFinishedListener;
import com.github.jberkel.pay.me.listener.OnIabSetupFinishedListener;
import com.github.jberkel.pay.me.listener.QueryInventoryFinishedListener;
import com.github.jberkel.pay.me.model.Inventory;
import com.github.jberkel.pay.me.model.ItemType;
import com.github.jberkel.pay.me.model.Purchase;
import com.github.jberkel.pay.me.model.SkuDetails;
import com.github.jberkel.pay.me.model.TestSkus;

import org.addhen.smssync.BuildConfig;
import org.addhen.smssync.R;
import org.addhen.smssync.adapters.DonationAdapter;
import org.addhen.smssync.models.Donation;
import org.addhen.smssync.views.DonationView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.github.jberkel.pay.me.Response.BILLING_UNAVAILABLE;
import static org.addhen.smssync.util.DonationConstants.Billing.ALL_SKUS;
import static org.addhen.smssync.util.DonationConstants.Billing.DONATION_PREFIX;
import static org.addhen.smssync.util.DonationConstants.Billing.PUBLIC_KEY;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class DonationFragment extends BaseListFragment<DonationView, Donation, DonationAdapter>
        implements
        QueryInventoryFinishedListener,
        OnIabPurchaseFinishedListener,
        AdapterView.OnItemClickListener {

    private static final int PURCHASE_REQUEST = 1;

    private static boolean DEBUG_IAB = BuildConfig.DEBUG;

    private IabHelper mIabHelper;

    private List<Donation> mSkuDetailsList = new ArrayList<>();


    /**
     * BaseListActivity
     */
    public DonationFragment() {
        super(DonationView.class, DonationAdapter.class, R.layout.list_donation, 0,
                android.R.id.list);
    }

    private static boolean userHasDonated(Inventory inventory) {
        for (String sku : ALL_SKUS) {
            if (inventory.hasPurchase(sku)) {
                return true;
            }
        }
        return false;
    }

    public static void checkUserHasDonated(Context c, final DonationStatusListener l) {
        if (Build.VERSION.SDK_INT < 8) {
            l.userDonationState(DonationStatusListener.State.NOT_AVAILABLE);
            return;
        }

        final IabHelper helper = new IabHelper(c, PUBLIC_KEY);
        helper.startSetup(new OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (result.isSuccess()) {
                    helper.queryInventoryAsync(new QueryInventoryFinishedListener() {
                        @Override
                        public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                            try {
                                if (result.isSuccess()) {
                                    final DonationStatusListener.State s = userHasDonated(inv)
                                            ? DonationStatusListener.State.DONATED
                                            : DonationStatusListener.State.NOT_DONATED;
                                    l.userDonationState(s);
                                } else {
                                    l.userDonationState(DonationStatusListener.State.UNKNOWN);
                                }
                            } finally {
                                helper.dispose();
                            }
                        }
                    });
                } else {
                    l.userDonationState(
                            result.getResponse() == BILLING_UNAVAILABLE
                                    ? DonationStatusListener.State.NOT_AVAILABLE
                                    : DonationStatusListener.State.UNKNOWN);
                    helper.dispose();
                }
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView.setOnItemClickListener(this);
        setupInAppPurchase();
    }

    private void setupInAppPurchase() {
        mIabHelper = new IabHelper(getActivity(), PUBLIC_KEY);
        mIabHelper.enableDebugLogging(DEBUG_IAB);

        mIabHelper.startSetup(new OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    String message;
                    switch (result.getResponse()) {
                        case BILLING_UNAVAILABLE:
                            message = getString(R.string.donation_error_iab_unavailable);
                            break;
                        default:
                            message = result.getMessage();
                    }

                    toastLong(message);
                    log("Problem setting up in-app billing: " + result);

                    getActivity().finish();
                } else if (mIabHelper != null) {
                    List<String> moreSkus = new ArrayList<>();
                    Collections.addAll(moreSkus, ALL_SKUS);
                    mIabHelper.queryInventoryAsync(true, moreSkus, null, DonationFragment.this);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mIabHelper != null) {
            mIabHelper.dispose();
            mIabHelper = null;
        }
    }

    @Override
    public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
        log("onQueryInventoryFinished(" + result + ", " + inventory + ")");
        if (result.isFailure()) {
            log("failed to query inventory: " + result);
            return;
        }

        for (SkuDetails d : inventory.getSkuDetails()) {
            if (d.getSku().startsWith(DONATION_PREFIX)) {
                mSkuDetailsList.add(new Donation(d));
            }
        }
        if (DEBUG_IAB) {
            Purchase testPurchase = inventory.getPurchase(TestSkus.PURCHASED.getSku());
            if (testPurchase != null) {
                mIabHelper.consumeAsync(testPurchase, new OnConsumeFinishedListener() {
                    @Override
                    public void onConsumeFinished(Purchase purchase, IabResult result) {
                        log("onConsumeFinished:" + purchase + ", " + result);
                    }
                });
            }
        }

        if (!getActivity().isFinishing() && !userHasDonated(inventory)) {
            setupDonations();
        } else {
            getActivity().finish();
        }
    }

    private void setupDonations() {
        Collections.sort(mSkuDetailsList, SkuComparator.INSTANCE);
        //noinspection ConstantConditions
        if (DEBUG_IAB) {

            mSkuDetailsList.add(new Donation(TestSkus.PURCHASED));
            mSkuDetailsList.add(new Donation(TestSkus.CANCELED));
            mSkuDetailsList.add(new Donation(TestSkus.UNAVAILABLE));
            mSkuDetailsList.add(new Donation(TestSkus.REFUNDED));
        }
        adapter.setItems(mSkuDetailsList);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (!mIabHelper.mAsyncInProgress) {
            launchPurchase(position);
        }
    }

    private void launchPurchase(int position) {
        mIabHelper.launchPurchaseFlow(getActivity(),
                adapter.getItem(position).getSkuDetails().getSku(),
                ItemType.INAPP,
                PURCHASE_REQUEST,
                DonationFragment.this,
                null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        logger("onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (!mIabHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
            // Refresh list
            setupDonations();
        } else {
            logger("onActivityResult handled by IABUtil.");
        }
    }

    @Override
    public void onIabPurchaseFinished(IabResult result, Purchase info) {
        logger("onIabPurchaseFinished(" + result + ", " + info);
        if (result.isSuccess()) {
            toastLong(R.string.ui_donation_success_message);

        } else {
            String message;
            switch (result.getResponse()) {
                case ITEM_UNAVAILABLE:
                    message = getString(R.string.donation_error_unavailable);
                    break;
                case ITEM_ALREADY_OWNED:
                    message = getString(R.string.donation_error_already_owned);
                    break;
                case USER_CANCELED:
                    message = getString(R.string.donation_error_canceled);
                    break;

                default:
                    message = result.getMessage();
            }

            toastLong(getString(R.string.ui_donation_failure_message, message));
        }
        getActivity().finish();
    }

    private void logger(String s) {
        if (DEBUG_IAB) {
            log(s);
        }
    }

    public interface DonationStatusListener {

        void userDonationState(State s);

        enum State {
            DONATED,
            NOT_DONATED,
            UNKNOWN,
            NOT_AVAILABLE
        }
    }

    private static class SkuComparator implements Comparator<Donation> {

        static final SkuComparator INSTANCE = new SkuComparator();

        @Override
        public int compare(Donation lhs, Donation rhs) {
            if (lhs.getSkuDetails().getPrice() != null && rhs.getSkuDetails().getPrice() != null) {
                return lhs.getSkuDetails().getPrice().compareTo(rhs.getSkuDetails().getPrice());
            } else if (lhs.getSkuDetails().getTitle() != null
                    && rhs.getSkuDetails().getTitle() != null) {
                return lhs.getSkuDetails().getTitle().compareTo(rhs.getSkuDetails().getTitle());
            } else if (lhs.getSkuDetails().getSku() != null
                    && rhs.getSkuDetails().getSku() != null) {
                return lhs.getSkuDetails().getSku().compareTo(rhs.getSkuDetails().getSku());
            } else {
                return 0;
            }
        }
    }
}
