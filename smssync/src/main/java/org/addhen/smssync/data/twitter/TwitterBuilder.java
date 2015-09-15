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

package org.addhen.smssync.data.twitter;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Builds the Twitter service
 *
 * @author Henry Addo
 */
public class TwitterBuilder {

    private String mConsumerKey;

    private String mConsumerSecret;

    private Context mContext;

    public TwitterBuilder(Context context, @NonNull String consumerKey,
            @NonNull String consumerSecret) {
        mContext = context;
        mConsumerKey = consumerKey;
        mConsumerSecret = consumerSecret;
    }

    public TwitterClient build() {
        return new TwitterClient(mContext, new TwitterAuthConfig(mConsumerKey, mConsumerSecret));
    }
}
