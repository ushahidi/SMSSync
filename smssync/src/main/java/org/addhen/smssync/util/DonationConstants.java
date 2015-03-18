/*******************************************************************************
 *  Copyright (c) 2010 - 2013 Ushahidi Inc
 *  All rights reserved
 *  Contact: team@ushahidi.com
 *  Website: http://www.ushahidi.com
 *  GNU Lesser General Public License Usage
 *  This file may be used under the terms of the GNU Lesser
 *  General Public License version 3 as published by the Free Software
 *  Foundation and appearing in the file LICENSE.LGPL included in the
 *  packaging of this file. Please review the following information to
 *  ensure the GNU Lesser General Public License version 3 requirements
 *  will be met: http://www.gnu.org/licenses/lgpl.html.
 *
 * If you have questions regarding the use of this file, please contact
 * Ushahidi developers at team@ushahidi.com.
 ******************************************************************************/

package org.addhen.smssync.util;

import org.addhen.smssync.BuildConfig;

/**
 * Handles donation constants
 */
public class DonationConstants {

    public static class Billing {

        public static final String PUBLIC_KEY = BuildConfig.PUBLIC_LICENSE_KEY;

        public static final String DONATION_PREFIX = "donation_level_";

        public static final String SKU_DONATION_1 = "donation_level_one";

        public static final String SKU_DONATION_2 = "donation_level_two";

        public static final String SKU_DONATION_3 = "donation_level_three";

        public static final String[] ALL_SKUS = new String[]{
                SKU_DONATION_1,
                SKU_DONATION_2,
                SKU_DONATION_3
        };
    }
}
