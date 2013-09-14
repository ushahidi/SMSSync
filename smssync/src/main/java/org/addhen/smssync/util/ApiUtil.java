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

import org.json.JSONException;
import org.json.JSONObject;

public class ApiUtil {

    private JSONObject jsonObject;


    private boolean processingResult;

    public ApiUtil(String jsonString) {
        processingResult = true;

        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            processingResult = false;
        }
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public boolean getProcessingResult() {
        return processingResult;
    }

}
