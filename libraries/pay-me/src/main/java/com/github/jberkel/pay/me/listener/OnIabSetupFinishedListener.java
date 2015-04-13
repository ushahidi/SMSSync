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

/**
 * Callback for setup process. This listener's {@link #onIabSetupFinished} method is called
 * when the setup process is complete.
 */
public interface OnIabSetupFinishedListener {
    /**
     * Called to notify that setup is complete.
     *
     * @param result The result of the setup process.
     */
    public void onIabSetupFinished(IabResult result);
}
