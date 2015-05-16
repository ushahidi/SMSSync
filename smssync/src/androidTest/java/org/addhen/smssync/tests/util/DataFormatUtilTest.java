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

package org.addhen.smssync.tests.util;

import android.test.suitebuilder.annotation.SmallTest;

import org.addhen.smssync.tests.BaseTest;
import org.addhen.smssync.util.DataFormatUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Class: DataFormatUtilTest
 * Description: Test DataFormatUtil class.
 * Author: Salama A.B. <devaksal@gmail.com>
 *
 */
public class DataFormatUtilTest extends BaseTest {

    List<NameValuePair> pairs;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("a","b"));
        pairs.add(new BasicNameValuePair("c","d"));

    }

    /**
     * Test make json string from List of pair values
     */
    @SmallTest
    public void testMakeJSONString() throws JSONException {
        String actual = DataFormatUtil.makeJSONString(pairs);
        String expected = "{\"c\":\"d\",\"a\":\"b\"}";
        assertNotNullOrEqual("JSON cannot be null and must be formatted correctly", expected, actual);

    }

    /**
     * Test make xml string from List of pair values
     */
    @SmallTest
    public void testMakeXMLString() throws IOException {
        String actual = DataFormatUtil.makeXMLString(pairs, "zz", HTTP.UTF_8);
        String expected = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?><zz><a>b</a><c>d</c></zz>";
        assertNotNullOrEqual("XML cannot be null and must be formatted correctly", expected, actual);
    }


    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        pairs = null;
    }
}
