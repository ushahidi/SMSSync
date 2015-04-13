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

package org.addhen.smssync;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import net.smssync.survey.dialog.UrlHelper;

public class UrlHelperImpl implements UrlHelper {

    private String mUrl;

    public UrlHelperImpl(String url) {
        mUrl = url;
    }

    public void goToUrl(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getUrl()));
        context.startActivity(intent);
    }

    @Override
    public String getUrl() {
        return mUrl;
    }
}