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

package com.github.jberkel.pay.me.validator;

/**
 * Validates signatures returned by the billing services.
 */
public interface SignatureValidator {
    /**
     * Validates that the specified signature matches the computed signature on
     * the specified signed data. Returns true if the data is correctly signed.
     *
     * @param signedData signed data
     * @param signature signature
     * @return true if the data and signature match, false otherwise.
     */
    boolean validate(String signedData, String signature);
}
