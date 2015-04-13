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
