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

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.android.vending.billing.IInAppBillingService;
import com.github.jberkel.pay.me.listener.OnConsumeFinishedListener;
import com.github.jberkel.pay.me.listener.OnConsumeMultiFinishedListener;
import com.github.jberkel.pay.me.listener.OnIabPurchaseFinishedListener;
import com.github.jberkel.pay.me.listener.OnIabSetupFinishedListener;
import com.github.jberkel.pay.me.listener.QueryInventoryFinishedListener;
import com.github.jberkel.pay.me.model.Inventory;
import com.github.jberkel.pay.me.model.ItemType;
import com.github.jberkel.pay.me.model.Purchase;
import com.github.jberkel.pay.me.model.SkuDetails;
import com.github.jberkel.pay.me.validator.DefaultSignatureValidator;
import com.github.jberkel.pay.me.validator.SignatureValidator;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.github.jberkel.pay.me.IabConsts.*;
import static com.github.jberkel.pay.me.Response.*;
import static com.github.jberkel.pay.me.model.ItemType.INAPP;
import static com.github.jberkel.pay.me.model.ItemType.SUBS;

/**
 * Provides convenience methods for in-app billing. You can create one instance of this
 * class for your application and use it to process in-app billing operations.
 * It provides synchronous (blocking) and asynchronous (non-blocking) methods for
 * many common in-app billing operations, as well as automatic signature
 * verification.
 * <p/>
 * After instantiating, you must perform setup in order to start using the object.
 * To perform setup, call the {@link #startSetup} method and provide a listener;
 * that listener will be notified when setup is complete, after which (and not before)
 * you may call other methods.
 * <p/>
 * After setup is complete, you will typically want to request an inventory of owned
 * items and subscriptions. See {@link #queryInventory}, {@link #queryInventoryAsync}
 * and related methods.
 * <p/>
 * When you are done with this object, don't forget to call {@link #dispose}
 * to ensure proper cleanup. This object holds a binding to the in-app billing
 * service, which will leak unless you dispose of it correctly. If you created
 * the object on an Activity's onCreate method, then the recommended
 * place to dispose of it is the Activity's onDestroy method.
 * <p/>
 * A note about threading: When using this object from a background thread, you may
 * call the blocking versions of methods; when using from a UI thread, call
 * only the asynchronous versions and handle the results via callbacks.
 * Also, notice that you can only call one asynchronous operation at a time;
 * attempting to start a second asynchronous operation while the first one
 * has not yet completed will result in an exception being thrown.
 *
 * @author Bruno Oliveira (Google)
 * @author Jan Berkel
 */
public class IabHelper {
    /* package */ static final Intent BIND_BILLING_SERVICE = new Intent("com.android.vending.billing.InAppBillingService.BIND");

    private Context mContext;
    private IInAppBillingService mService;
    private BillingServiceConnection mServiceConn;

    private PurchaseFlowState mPurchaseFlowState = PurchaseFlowState.NONE;
    private SignatureValidator mSignatureValidator;

    private boolean mSetupDone, mDisposed;
    private boolean mSubscriptionsSupported, mInAppSupported;

    private boolean mDebugLog;
    private String mDebugTag = "IabHelper";
    public boolean mAsyncInProgress;
    // if mAsyncInProgress == true, what asynchronous operation is in progress? (for logging/debugging)
    private String mAsyncOperation = "";

    /**
     * Creates an instance. After creation, it will not yet be ready to use. You must perform
     * setup by calling {@link #startSetup} and wait for setup to complete. This constructor does not
     * block and is safe to call from a UI thread.
     *
     * @param ctx             Your application or Activity context. Needed to bind to the in-app billing service.
     * @param base64PublicKey Your application's public key, encoded in base64.
     *                        This is used for verification of purchase signatures. You can find your app's base64-encoded
     *                        public key in your application's page on Google Play Developer Console. Note that this
     *                        is NOT your "developer public key".
     */
    public IabHelper(Context ctx, String base64PublicKey) {
        this(ctx, new DefaultSignatureValidator(base64PublicKey));
    }

    /**
     * Alternative constructor if you want to perform your own signature validation by
     * implementing a {@link SignatureValidator}.
     *
     * @param ctx       Your application or Activity context. Needed to bind to the in-app billing service.
     * @param validator the validator to be used for verifying the signature.
     */
    public IabHelper(Context ctx, SignatureValidator validator) {
        if (ctx == null) throw new IllegalArgumentException("need non-null context");
        if (validator == null) throw new IllegalArgumentException("need non-null validator");
        mContext = ctx.getApplicationContext();
        mSignatureValidator = validator;
        logDebug("IAB helper created.");
    }

    /**
     * Starts the setup process. This will start up the setup process asynchronously.
     * You will be notified through the listener when the setup process is complete.
     * This method is safe to call from a UI thread.
     *
     * @param listener The listener to notify when the setup process is complete.
     */
    public void startSetup(final OnIabSetupFinishedListener listener) {
        checkNotDisposedAndThrow();

        if (mSetupDone) throw new IllegalStateException("IAB helper is already set up.");
        logDebug("Starting in-app billing setup.");

        if (!mContext.getPackageManager().queryIntentServices(BIND_BILLING_SERVICE, 0).isEmpty()) {
            // service available to handle that Intent
            mServiceConn = new BillingServiceConnection(listener);
            // Needed for Lollipop or higher
            BIND_BILLING_SERVICE.setPackage("com.android.vending");
            if (!mContext.bindService(BIND_BILLING_SERVICE, mServiceConn, Context.BIND_AUTO_CREATE)) {
                logWarn("Could not bind to service");
            }
        } else {
            // no service available to handle that Intent
            mSetupDone = true;
            if (listener != null) {
                listener.onIabSetupFinished(new IabResult(BILLING_UNAVAILABLE));
            }
        }
    }

    /**
     * Dispose of object, releasing resources. It's very important to call this
     * method when you are done with this object. It will release any resources
     * used by it such as service connections. Naturally, once the object is
     * disposed of, it can't be used again.
     */
    public void dispose() {
        logDebug("Disposing.");
        mSetupDone = false;
        if (mServiceConn != null) {
            logDebug("Unbinding from service.");
            if (mContext != null) mContext.unbindService(mServiceConn);
        }
        mDisposed = true;
        mContext = null;
        mServiceConn = null;
        mService = null;
        mPurchaseFlowState = PurchaseFlowState.NONE;
    }

    /**
     * Initiate the UI flow for an in-app purchase. Call this method to initiate an in-app purchase,
     * which will involve bringing up the Google Play screen. The calling activity will be paused while
     * the user interacts with Google Play, and the result will be delivered via the activity's
     * {@link android.app.Activity#onActivityResult} method, at which point you must call
     * this object's {@link #handleActivityResult} method to continue the purchase flow. This method
     * MUST be called from the UI thread of the Activity.
     *
     * @param activity         The calling activity.
     * @param sku              The sku of the item to purchase.
     * @param itemType         indicates if it's a product or a subscription
     * @param requestCode      A request code (to differentiate from other responses --
     *                         as in {@link android.app.Activity#startActivityForResult}).
     * @param listener         The listener to notify when the purchase process finishes, can be null
     * @param developerPayload Extra data (developer payload), which will be returned with the purchase data
     *                         when the purchase completes. This extra data will be permanently bound to that purchase
     *                         and will always be returned when the purchase is queried.
     */
    public void launchPurchaseFlow(final Activity activity,
                                   final String sku,
                                   final ItemType itemType,
                                   final int requestCode,
                                   final OnIabPurchaseFinishedListener listener,
                                   final String developerPayload) {
        if (TextUtils.isEmpty(sku)) throw new IllegalArgumentException("Empty sku");
        if (itemType == null) throw new IllegalArgumentException("Empty itemType");

        if (isDisposed()) {
            if (listener != null) {
                listener.onIabPurchaseFinished(new IabResult(Response.IABHELPER_DISPOSED), null);
            }
            return;
        }

        checkSetupDone("launchPurchaseFlow");
        flagStartAsync("launchPurchaseFlow");

        if (itemType == SUBS && !mSubscriptionsSupported || itemType == INAPP && !mInAppSupported) {
            flagEndAsync();
            if (listener != null) {
                listener.onIabPurchaseFinished(
                        new IabResult(itemType == INAPP ? BILLING_UNAVAILABLE : IABHELPER_SUBSCRIPTIONS_NOT_AVAILABLE),
                        null);
            }
            return;
        }

        try {
            logDebug("Constructing buy intent for " + sku + ", item type: " + itemType);
            Bundle buyIntentBundle = mService.getBuyIntent(API_VERSION, mContext.getPackageName(), sku,
                    itemType.toString(), developerPayload);
            int response = getResponseCodeFromBundle(buyIntentBundle);
            if (response != OK.code) {
                logError("Unable to buy item, Error response: " + getDescription(response));
                flagEndAsync();
                if (listener != null) {
                    listener.onIabPurchaseFinished(
                            new IabResult(response, "Unable to buy item"), null);
                }
                return;
            }

            PendingIntent pendingIntent = buyIntentBundle.getParcelable(RESPONSE_BUY_INTENT);
            if (pendingIntent == null) {
                throw new SendIntentException("No pending intent");
            }

            logDebug("Launching buy intent for " + sku + ". Request code: " + requestCode);
            mPurchaseFlowState = new PurchaseFlowState(requestCode, itemType, listener);
            activity.startIntentSenderForResult(pendingIntent.getIntentSender(),
                    requestCode, new Intent(),
                    0, 0, 0);
        } catch (SendIntentException e) {
            logError("SendIntentException while launching purchase flow for sku " + sku, e);
            flagEndAsync();
            if (listener != null)
                listener.onIabPurchaseFinished(new IabResult(IABHELPER_SEND_INTENT_FAILED), null);
        } catch (RemoteException e) {
            logError("RemoteException while launching purchase flow for sku " + sku, e);
            flagEndAsync();
            if (listener != null)
                listener.onIabPurchaseFinished(new IabResult(IABHELPER_REMOTE_EXCEPTION), null);
        }
    }

    /**
     * Handles an activity result that's part of the purchase flow in in-app billing. If you
     * are calling {@link #launchPurchaseFlow}, then you must call this method from your
     * Activity's {@link Activity#onActivityResult} method. This method
     * MUST be called from the UI thread of the Activity.
     *
     * @param requestCode The requestCode as you received it.
     * @param intentResultCode  The resultCode as you received it.
     * @param intent        The data (Intent) as you received it.
     * @return Returns true if the result was related to a purchase flow and was handled;
     *         false if the result was not related to a purchase, in which case you should
     *         handle it normally.
     */
    public boolean handleActivityResult(int requestCode, int intentResultCode, Intent intent) {
        if (mPurchaseFlowState == PurchaseFlowState.NONE) return false; // no prior launchPurchaseFlow
        else if (requestCode != mPurchaseFlowState.requestCode) return false;
        checkSetupDone("handleActivityResult");

        // end of async purchase operation that started on launchPurchaseFlow
        flagEndAsync();

        if (intent == null) {
            logError("Null data in IAB activity result.");
            mPurchaseFlowState.onIabPurchaseFinished(new IabResult(IABHELPER_BAD_RESPONSE, "Null data in IAB result"), null);
            return true;
        } else {
            int responseCode = getResponseCodeFromBundle(intent.getExtras());
            logDebug("handleActivityResult: resultCode="+intentResultCode+", response code="+responseCode);

            if (intentResultCode == RESULT_OK) {
                if (responseCode == OK.code) {
                    handlePurchaseResult(
                            intent.getStringExtra(RESPONSE_INAPP_PURCHASE_DATA),
                            intent.getStringExtra(RESPONSE_INAPP_SIGNATURE),
                            mPurchaseFlowState);
                } else {
                    logDebug("Result code was OK but in-app billing response was not OK: " + getDescription(responseCode));
                    mPurchaseFlowState.onIabPurchaseFinished(
                            new IabResult(responseCode, "Problem purchasing item."), null);

                }
                return true;
            } else if (intentResultCode == RESULT_CANCELED) {
                logDebug("Purchase canceled - Response: " + getDescription(responseCode));
                mPurchaseFlowState.onIabPurchaseFinished(new IabResult(responseCode, null), null);
                return true;
            } else { // weird intent result code
                logError("Purchase failed. Result code: " + Integer.toString(intentResultCode)
                        + ". Response: " + getDescription(responseCode));
                mPurchaseFlowState.onIabPurchaseFinished(new IabResult(IABHELPER_UNKNOWN_PURCHASE_RESPONSE), null);
                return false;
            }
        }
    }


    /**
     * Queries the inventory. This will query all owned items from the server, as well as
     * information on additional skus, if specified. This method may block or take long to execute.
     * Do not call from a UI thread. For that, use the non-blocking version {@link #queryInventoryAsync(boolean, List, List, QueryInventoryFinishedListener)}.
     *
     * @param querySkuDetails if true, SKU details (price, description, etc) will be queried as well
     *                        as purchase information.
     * @param moreItemSkus    additional PRODUCT skus to query information on, regardless of ownership.
     *                        Ignored if null or if querySkuDetails is false.
     * @param moreSubsSkus    additional SUBSCRIPTIONS skus to query information on, regardless of ownership.
     *                        Ignored if null or if querySkuDetails is false.
     * @throws IabException if a problem occurs while refreshing the inventory.
     */
    public Inventory queryInventory(boolean querySkuDetails,
                                    List<String> moreItemSkus,
                                    List<String> moreSubsSkus) throws IabException {
        checkNotDisposed();
        checkSetupDone("queryInventory");
        try {
            final Inventory inventory = new Inventory();

            queryPurchasesAndDetails(INAPP, inventory, querySkuDetails, moreItemSkus);
            if (subscriptionsSupported()) {
                queryPurchasesAndDetails(SUBS, inventory, querySkuDetails, moreSubsSkus);
            }
            return inventory;
        } catch (RemoteException e) {
            throw new IabException(IABHELPER_REMOTE_EXCEPTION, "Remote exception while refreshing inventory.", e);
        } catch (JSONException e) {
            throw new IabException(IABHELPER_BAD_RESPONSE, "Error parsing JSON response while refreshing inventory.", e);
        }
    }


    /**
     * Asynchronous wrapper for inventory query. This will perform an inventory
     * query as described in {@link #queryInventory}, but will do so asynchronously
     * and call back the specified listener upon completion. This method is safe to
     * call from a UI thread.
     *
     * @param querySkuDetails as in {@link #queryInventory}
     * @param moreSkus        as in {@link #queryInventory}
     * @param listener        The listener to notify when the refresh operation completes.
     */
    public void queryInventoryAsync(final boolean querySkuDetails,
                                    final List<String> moreSkus,
                                    final List<String> moreSubSkus,
                                    final QueryInventoryFinishedListener listener) {
        if (isDisposed()) {
            if (listener != null) {
                listener.onQueryInventoryFinished(new IabResult(Response.IABHELPER_DISPOSED), null);
            }
            return;
        }

        checkSetupDone("queryInventory");
        new QueryInventoryTask(this, listener).execute(new QueryInventoryTask.Args(querySkuDetails, moreSkus, moreSubSkus));
    }

    /**
     * Convenience method which queries for all purchased items, including details.
     * See {@link #queryInventoryAsync(boolean, java.util.List, java.util.List, QueryInventoryFinishedListener)}
     * @param listener The listener to notify when the refresh operation completes.
     */
    public void queryInventoryAsync(final QueryInventoryFinishedListener listener) {
        queryInventoryAsync(true, null, null, listener);
    }

    /**
     * Consumes a given in-app product. Consuming can only be done on an item
     * that's owned, and as a result of consumption, the user will no longer own it.
     * This method may block or take long to return. Do not call from the UI thread.
     * For that, see {@link #consumeAsync}.
     *
     * @param purchase The PurchaseInfo that represents the item to consume.
     * @throws IabException if there is a problem during consumption.
     */
    public void consume(Purchase purchase) throws IabException {
        checkNotDisposed();
        checkSetupDone("consume");

        if (purchase.getItemType() != INAPP) {
            throw new IabException(IABHELPER_INVALID_CONSUMPTION,
                    "Items of type '" + purchase.getItemType() + "' can't be consumed.");
        }
        try {
            String token = purchase.getToken();
            String sku = purchase.getSku();
            if (token == null || token.equals("")) {
                logError("Can't consume " + sku + ". No token.");
                throw new IabException(IABHELPER_MISSING_TOKEN, "PurchaseInfo is missing token for sku: "
                        + sku + " " + purchase);
            }

            logDebug("Consuming sku: " + sku + ", token: " + token);
            int response = mService.consumePurchase(API_VERSION, mContext.getPackageName(), token);
            if (response == OK.code) {
                logDebug("Successfully consumed sku: " + sku);
            } else {
                logDebug("Error consuming consuming sku " + sku + ". " + getDescription(response));
                throw new IabException(response, "Error consuming sku " + sku);
            }
        } catch (RemoteException e) {
            throw new IabException(IABHELPER_REMOTE_EXCEPTION, "Remote exception while consuming. PurchaseInfo: " + purchase, e);
        }
    }

    /**
     * Asynchronous wrapper to item consumption. Works like {@link #consume}, but
     * performs the consumption in the background and notifies completion through
     * the provided listener. This method is safe to call from a UI thread.
     *
     * @param purchase The purchase to be consumed.
     * @param listener The listener to notify when the consumption operation finishes.
     */
    public void consumeAsync(Purchase purchase, OnConsumeFinishedListener listener) {
        if (isDisposed()) {
            if (listener != null) listener.onConsumeFinished(null, new IabResult(IABHELPER_DISPOSED));
            return;
        }
        checkSetupDone("consume");
        consumeAsyncInternal(Arrays.asList(purchase), listener, null);
    }

    /**
     * Same as {@link #consumeAsync(Purchase, OnConsumeFinishedListener)}, but for multiple items at once.
     *
     * @param purchases The list of PurchaseInfo objects representing the purchases to consume.
     * @param listener  The listener to notify when the consumption operation finishes.
     */
    public void consumeAsync(List<Purchase> purchases, OnConsumeMultiFinishedListener listener) {
        if (isDisposed()) {
            if (listener != null) {
                listener.onConsumeMultiFinished(new ArrayList<Purchase>(), new ArrayList<IabResult>());
            }
            return;
        }
        checkSetupDone("consume");
        consumeAsyncInternal(purchases, null, listener);
    }

    /**
     * Returns whether subscriptions are supported.
     */
    public boolean subscriptionsSupported() {
        return !isDisposed() && mSubscriptionsSupported;
    }

    /**
     * Enables or disable debug logging through LogCat.
     */
    public void enableDebugLogging(boolean enable, String tag) {
        checkNotDisposedAndThrow();
        enableDebugLogging(enable);
        mDebugTag = tag;
    }

    public void enableDebugLogging(boolean enable) {
        checkNotDisposedAndThrow();
        mDebugLog = enable;
    }

    // Checks that setup was done; if not, throws an exception.
    /* package */ void checkSetupDone(String operation) {
        if (!mSetupDone) {
            logError("Illegal state for operation (" + operation + "): IAB helper is not set up.");
            throw new IllegalStateException("IAB helper is not set up. Can't perform operation: " + operation);
        }
    }

    /* package */ int getResponseCodeFromBundle(Bundle bundle) {
        Object o;
        if (bundle == null || (o = bundle.get(RESPONSE_CODE)) == null) {
            logDebug("Bundle with null response code, assuming OK (known issue)");
            return OK.code;
        } else if (o instanceof Integer) return (Integer) o;
            // Workaround to bug where sometimes response codes come as Long instead of Integer
        else if (o instanceof Long) return (int) ((Long) o).longValue();
        else {
            logError("Unexpected type for bundle response code.");
            logError(o.getClass().getName());
            throw new RuntimeException("Unexpected type for bundle response code: " + o.getClass().getName());
        }
    }

    /* package */ void flagStartAsync(String operation) {
        if (mAsyncInProgress) throw new IllegalStateException("Can't start async operation (" +
                operation + ") because another async operation(" + mAsyncOperation + ") is in progress.");
        mAsyncOperation = operation;
        mAsyncInProgress = true;
        logDebug("Starting async operation: " + operation);
    }

    /* package */ void flagEndAsync() {
        logDebug("Ending async operation: " + mAsyncOperation);
        mAsyncOperation = "";
        mAsyncInProgress = false;
    }

    /* package */  boolean isDisposed() {
        return mDisposed;
    }

    private void checkNotDisposed() throws IabException {
        if (isDisposed()) {
            throw new IabException(IABHELPER_DISPOSED, null);
        }
    }

    private void checkNotDisposedAndThrow() throws IllegalStateException {
        if (isDisposed()) {
            throw new IllegalStateException("IabHelper was disposed of, so it cannot be used.");
        }
    }

    private void handlePurchaseResult(String purchaseData,
                                      String dataSignature,
                                      PurchaseFlowState purchaseState) {
        if (purchaseData == null || dataSignature == null) {
            logError("BUG: either purchaseData or dataSignature is null." +
                    " data="+purchaseData+", signature="+dataSignature);
            purchaseState.onIabPurchaseFinished(
                new IabResult(IABHELPER_UNKNOWN_ERROR, "IAB returned null purchaseData or dataSignature"), null);
            return;
        }
        try {
            Purchase purchase = new Purchase(purchaseState.itemType, purchaseData, dataSignature);
            if (!mSignatureValidator.validate(purchaseData, dataSignature)) {
                logError("Purchase signature verification FAILED for " + purchase);
                purchaseState.onIabPurchaseFinished(
                        new IabResult(IABHELPER_VERIFICATION_FAILED, "Signature verification failed for purchase " + purchase),
                        purchase);
                return;
            }
            logDebug("Purchase signature successfully verified.");
            purchaseState.onIabPurchaseFinished(new IabResult(OK), purchase);
        } catch (JSONException e) {
            logError("Failed to parse purchase data.", e);
            purchaseState.onIabPurchaseFinished(
                new IabResult(IABHELPER_BAD_RESPONSE, "Failed to parse purchase data."), null);
        }
    }

    private void queryPurchasesAndDetails(ItemType itemType,
                                          Inventory inventory,
                                          boolean queryDetails,
                                          List<String> extraSkus) throws JSONException, RemoteException, IabException {
        checkNotDisposed();

        int result = queryPurchases(inventory, itemType);
        if (result != OK.code) {
            throw new IabException(result, "Error querying purchases for "+itemType);
        }
        if (queryDetails) {
            result = querySkuDetails(itemType, inventory, extraSkus);
            if (result != OK.code) {
                throw new IabException(result, "Error querying purchase details for "+itemType);
            }
        }
    }

    private int queryPurchases(Inventory inv, ItemType itemType) throws JSONException, RemoteException, IabException {
        checkNotDisposed();

        logDebug("Querying owned items, item type: " + itemType);
        boolean verificationFailed = false;
        String continueToken = null;
        do {
            Bundle ownedItems = mService.getPurchases(API_VERSION, mContext.getPackageName(),
                    itemType.toString(), continueToken);

            int response = getResponseCodeFromBundle(ownedItems);
            if (response != OK.code) {
                logDebug("getPurchases() failed: " + getDescription(response));
                return response;
            }
            if (ownedItems == null
                    || !ownedItems.containsKey(RESPONSE_INAPP_ITEM_LIST)
                    || !ownedItems.containsKey(RESPONSE_INAPP_PURCHASE_DATA_LIST)
                    || !ownedItems.containsKey(RESPONSE_INAPP_SIGNATURE_LIST)) {
                logError("Bundle returned from getPurchases() doesn't contain required fields.");
                return IABHELPER_BAD_RESPONSE.code;
            }

            List<String> ownedSkus = ownedItems.getStringArrayList(RESPONSE_INAPP_ITEM_LIST);
            List<String> purchaseDataList = ownedItems.getStringArrayList(RESPONSE_INAPP_PURCHASE_DATA_LIST);
            List<String> signatureList = ownedItems.getStringArrayList(RESPONSE_INAPP_SIGNATURE_LIST);

            if (signatureList.size() < purchaseDataList.size()
                    || ownedSkus.size() < purchaseDataList.size()) {
                logError("invalid data returned by service");
                return ERROR.code;
            }

            for (int i = 0; i < purchaseDataList.size(); i++) {
                String purchaseData = purchaseDataList.get(i);
                String signature = signatureList.get(i);
                if (mSignatureValidator.validate(purchaseData, signature)) {
                    Purchase purchase = new Purchase(itemType, purchaseData, signature);

                    if (TextUtils.isEmpty(purchase.getToken())) {
                        logWarn("BUG: empty/null token!");
                        logDebug("Purchase data: " + purchaseData);
                    }

                    inv.addPurchase(purchase);
                } else {
                    logWarn("Purchase signature verification **FAILED**. Not adding item.");
                    logDebug("   Purchase data: " + purchaseData);
                    logDebug("   Signature: " + signature);
                    verificationFailed = true;
                }
            }
            continueToken = ownedItems.getString(INAPP_CONTINUATION_TOKEN);
            logDebug("Continuation token: " + continueToken);
        } while (!TextUtils.isEmpty(continueToken));

        return verificationFailed ? IABHELPER_VERIFICATION_FAILED.code : OK.code;
    }

    private int querySkuDetails(ItemType itemType, Inventory inv, List<String> moreSkus)
            throws RemoteException, JSONException {
        logDebug("Querying SKU details.");
        ArrayList<String> skuList = new ArrayList<String>();
        skuList.addAll(inv.getAllOwnedSkus(itemType));
        if (moreSkus != null) {
            for (String sku : moreSkus) {
                if (!skuList.contains(sku)) {
                    skuList.add(sku);
                }
            }
        }
        if (skuList.isEmpty()) {
            logDebug("querySkuDetails: nothing to do because there are no SKUs.");
            return OK.code;
        }

        // TODO: check for 20 SKU limit + add batching ?
        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList(GET_SKU_DETAILS_ITEM_LIST, skuList);
        Bundle skuDetails = mService.getSkuDetails(API_VERSION, mContext.getPackageName(), itemType.toString(), querySkus);
        if (skuDetails == null) return IABHELPER_BAD_RESPONSE.code;

        if (!skuDetails.containsKey(RESPONSE_GET_SKU_DETAILS_LIST)) {
            int response = getResponseCodeFromBundle(skuDetails);
            if (response != OK.code) {
                logWarn("getSkuDetails() failed: " + getDescription(response));
                return response;
            } else {
                logError("getSkuDetails() returned a bundle with neither an error nor a detail list.");
                return IABHELPER_BAD_RESPONSE.code;
            }
        }
        ArrayList<String> responseList = skuDetails.getStringArrayList(RESPONSE_GET_SKU_DETAILS_LIST);
        for (String json : responseList) {
            inv.addSkuDetails(new SkuDetails(json));
        }
        return OK.code;
    }

    private ConsumeTask consumeAsyncInternal(final List<Purchase> purchases,
                                             final OnConsumeFinishedListener singleListener,
                                             final OnConsumeMultiFinishedListener multiListener) {
        return (ConsumeTask) new ConsumeTask(this, singleListener, multiListener).execute(purchases.toArray(new Purchase[purchases.size()]));
    }

    private void logDebug(String msg) {
        if (mDebugLog) Log.d(mDebugTag, msg);
    }

    private void logError(String msg) {
        logError(msg, null);
    }

    private void logError(String msg, Throwable t) {
        Log.e(mDebugTag, "In-app billing error: " + msg, t);
    }

    private void logWarn(String msg) {
        Log.w(mDebugTag, "In-app billing warning: " + msg);
    }

    // for testing
    /* package */ IInAppBillingService getInAppBillingService(IBinder service) {
        return IInAppBillingService.Stub.asInterface(service);
    }

    private class BillingServiceConnection implements ServiceConnection {
        private OnIabSetupFinishedListener listener;

        BillingServiceConnection(OnIabSetupFinishedListener listener) {
            this.listener = listener;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            logDebug("Billing service disconnected.");
            mService = null;
            listener = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (isDisposed()) return;
            logDebug("Billing service connected.");
            mService = getInAppBillingService(service);

            String packageName = mContext.getPackageName();
            IabResult result = new IabResult(OK);
            try {
                logDebug("Checking for in-app billing 3 support.");
                int response = mService.isBillingSupported(API_VERSION, packageName, INAPP.toString());
                if (response == OK.code) {
                    logDebug("In-app billing version 3 supported for " + packageName);
                    mInAppSupported = true;
                    logDebug("Checking for in-app billing 3 subscription support.");
                    response = mService.isBillingSupported(API_VERSION, packageName, SUBS.toString());
                    if (response == OK.code) {
                        logDebug("Subscriptions AVAILABLE.");
                        mSubscriptionsSupported = true;
                    } else {
                        logDebug("Subscriptions NOT AVAILABLE. Response: " + response + " " + Response.getDescription(response));
                    }
                } else {
                    result = new IabResult(response, null);
                }
            } catch (RemoteException e) {
                result = new IabResult(IABHELPER_REMOTE_EXCEPTION);
                Log.e(mDebugTag, "RemoteException while setting up in-app billing.", e);
            } finally {
                mSetupDone = true;
                if (listener != null) {
                    listener.onIabSetupFinished(result);
                }
            }
        }
    }
}
