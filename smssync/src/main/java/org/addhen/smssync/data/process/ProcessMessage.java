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

package org.addhen.smssync.data.process;

import org.addhen.smssync.data.PrefsFactory;
import org.addhen.smssync.data.entity.Filter;
import org.addhen.smssync.data.entity.Message;
import org.addhen.smssync.data.entity.WebService;
import org.addhen.smssync.data.net.AppHttpClient;

import java.util.List;

import javax.inject.Inject;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class ProcessMessage {

    AppHttpClient mAppHttpClient;

    PrefsFactory mPrefsFactory;

    @Inject
    public ProcessMessage(PrefsFactory prefsFactory, AppHttpClient appHttpClient) {
        mPrefsFactory = prefsFactory;
        mAppHttpClient = appHttpClient;
    }

    public void postMessage(Message message, List<WebService> webServices, List<Filter> filters) {
        for(WebService webService: webServices) {
            // Process if whitelisting is enabled
            if(mPrefsFactory.enableWhitelist().get()) {
                for(Filter filter: filters) {
                    if(filter.phoneNumber.equals(message.messageFrom)){

                    }
                }
            }
        }
    }

    private void postMessage() {

    }
}
