package com.github.jberkel.pay.me;

class IabConsts {
    static final int API_VERSION = 3;

    // Keys for the responses from InAppBillingService
    static final String RESPONSE_CODE                 = "RESPONSE_CODE";
    static final String RESPONSE_GET_SKU_DETAILS_LIST = "DETAILS_LIST";
    static final String RESPONSE_BUY_INTENT           = "BUY_INTENT";
    static final String RESPONSE_INAPP_PURCHASE_DATA  = "INAPP_PURCHASE_DATA";
    static final String RESPONSE_INAPP_SIGNATURE      = "INAPP_DATA_SIGNATURE";
    static final String RESPONSE_INAPP_ITEM_LIST      = "INAPP_PURCHASE_ITEM_LIST";
    static final String RESPONSE_INAPP_PURCHASE_DATA_LIST = "INAPP_PURCHASE_DATA_LIST";
    static final String RESPONSE_INAPP_SIGNATURE_LIST = "INAPP_DATA_SIGNATURE_LIST";
    static final String INAPP_CONTINUATION_TOKEN      = "INAPP_CONTINUATION_TOKEN";

    // some fields on the getSkuDetails response bundle
    static final String GET_SKU_DETAILS_ITEM_LIST = "ITEM_ID_LIST";
}
