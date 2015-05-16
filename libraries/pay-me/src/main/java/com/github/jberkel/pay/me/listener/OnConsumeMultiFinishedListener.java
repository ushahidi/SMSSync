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
