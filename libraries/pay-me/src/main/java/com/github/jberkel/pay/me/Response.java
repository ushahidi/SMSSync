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

public enum Response {
    // response codes from the IAB service
    OK                 (0, R.string.pay_me_response_ok, "OK"),
    USER_CANCELED      (1, R.string.pay_me_response_user_canceled, "User Canceled"),
    BILLING_UNAVAILABLE(3, R.string.pay_me_response_billing_unavailable, "Billing Unavailable"),
    ITEM_UNAVAILABLE   (4, R.string.pay_me_response_item_unavailable, "Item Unavailable"),
    DEVELOPER_ERROR    (5, R.string.pay_me_response_developer_error, "Developer Error"),
    ERROR              (6, R.string.pay_me_response_error, "Error"),
    ITEM_ALREADY_OWNED (7, R.string.pay_me_response_item_already_owned, "Item Already Owned"),
    ITEM_NOT_OWNED     (8, R.string.pay_me_response_item_not_owned, "Item not owned"),

    // internal response codes
    IABHELPER_REMOTE_EXCEPTION           (-1001, R.string.pay_me_response_remote_exception, "Remote exception during initialization"),
    IABHELPER_BAD_RESPONSE               (-1002, R.string.pay_me_response_bad_response, "Bad response received"),
    IABHELPER_VERIFICATION_FAILED        (-1003, R.string.pay_me_response_signature_verification_failed, "Purchase signature verification failed"),
    IABHELPER_SEND_INTENT_FAILED         (-1004, R.string.pay_me_response_send_intent_failed, "Send intent failed"),
    IABHELPER_UNKNOWN_PURCHASE_RESPONSE  (-1006, R.string.pay_me_response_unknown_purchase_response, "Unknown purchase response"),
    IABHELPER_MISSING_TOKEN              (-1007, R.string.pay_me_response_missing_token, "Missing token"),
    IABHELPER_UNKNOWN_ERROR              (-1008, R.string.pay_me_response_unknown_error, "Unknown error"),
    IABHELPER_SUBSCRIPTIONS_NOT_AVAILABLE(-1009, R.string.pay_me_response_subscriptions_not_available,  "Subscriptions not available"),
    IABHELPER_INVALID_CONSUMPTION        (-1010, R.string.pay_me_response_invalid_consumption, "Invalid consumption attempt"),
    IABHELPER_DISPOSED                   (-1011, R.string.pay_me_response_disposed, "The helper was already disposed of");

    /** the error code */
    public final int code;

    /** a resource id which can be used to look up the localized response message */
    public final int stringId;

    /** short description of the response */
    public final String description;

    Response(int code, int stringId, String description) {
        this.code = code;
        this.stringId = stringId;
        this.description = description;
    }

    public static Response fromCode(int code) {
        for (Response s : Response.values()) {
            if (s.code == code) return s;
        }
        return IABHELPER_UNKNOWN_ERROR;
    }

    public static String getDescription(int code) {
        return fromCode(code).description;
    }
}
