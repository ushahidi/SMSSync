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

package org.addhen.smssync.tests.models;

import com.google.gson.Gson;

import org.addhen.smssync.models.SmssyncResponse;
import org.addhen.smssync.tests.BaseTest;

import android.test.suitebuilder.annotation.SmallTest;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class SmssyncResponseTest extends BaseTest {

    private final String JSON_STRING = "{\n"
            + "    \"payload\": {\n"
            + "        \"success\": true,\n"
            + "        \"task\": \"send\",\n"
            + "        \"messages\": [\n"
            + "            {\n"
            + "                \"to\": \"+000-000-0000\",\n"
            + "                \"message\": \"the message goes here\",\n"
            + "                \"uuid\": \"042b3515-ef6b-f424-c4qd\"\n"
            + "            },\n"
            + "            {\n"
            + "                \"to\": \"+000-000-0000\",\n"
            + "                \"message\": \"the message goes here\",\n"
            + "                \"uuid\": \"026b3515-ef6b-f424-c4qd\"\n"
            + "            },\n"
            + "            {\n"
            + "                \"to\": \"+000-000-0000\",\n"
            + "                \"message\": \"the message goes here\",\n"
            + "                \"uuid\": \"096b3515-ef6b-f424-c4qd\"\n"
            + "            }\n"
            + "        ]\n"
            + "    }\n"
            + "}";

    private final String JSON_STRING_NO_MSG = "{\n"
            + "    \"payload\": {\n"
            + "        \"success\": true,\n"
            + "        \"error\": null\n"
            + "    }\n"
            + "}";

    private final String TASK_JSON_STRING
            = "{\"payload\":{\"task\":\"send\",\"secret\":\"coconut\",\"messages\":[{\"to\":\"+XXXXXXXX\",\"message\":\"from couch to you homes\",\"uuid\":\"aeefa9195f734d32b7e0f9d62d327f6d\"}]}}";

    private Gson mGson;

    private SmssyncResponse mSmssyncResponse;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mGson = new Gson();
    }

    @SmallTest
    public void testShouldDeserializeSmsResponse() throws Exception {
        mSmssyncResponse = mGson.fromJson(JSON_STRING, SmssyncResponse.class);
        assertNotNull(mSmssyncResponse);
        assertNotNull(mSmssyncResponse.getPayload());
        assertTrue(mSmssyncResponse.getPayload().isSuccess());
        assertEquals("send", mSmssyncResponse.getPayload().getTask());
        assertNotNull(mSmssyncResponse.getPayload().getMessages());
        assertEquals("+000-000-0000",
                mSmssyncResponse.getPayload().getMessages().get(0).getPhoneNumber());
        assertEquals("the message goes here",
                mSmssyncResponse.getPayload().getMessages().get(0).getBody());
        assertEquals("042b3515-ef6b-f424-c4qd",
                mSmssyncResponse.getPayload().getMessages().get(0).getUuid());
    }

    @SmallTest
    public void testShouldDeserializeSmsResponseWithNoMessage() throws Exception {
        mSmssyncResponse = mGson.fromJson(JSON_STRING_NO_MSG, SmssyncResponse.class);
        assertNotNull(mSmssyncResponse);
        assertNotNull(mSmssyncResponse.getPayload());
        assertTrue(mSmssyncResponse.getPayload().isSuccess());
        assertNull(mSmssyncResponse.getPayload().getTask());
        assertNull(mSmssyncResponse.getPayload().getMessages());
        System.out.println("messages:" + mSmssyncResponse.toString());
    }

    @SmallTest
    public void testShouldSerializeTaskResponseWithNoSuccessProperty() throws Exception {
        mSmssyncResponse = mGson.fromJson(TASK_JSON_STRING, SmssyncResponse.class);
        assertNotNull(mSmssyncResponse);
        assertNotNull(mSmssyncResponse.getPayload());
        assertEquals("send", mSmssyncResponse.getPayload().getTask());
        assertEquals("coconut", mSmssyncResponse.getPayload().getSecret());
        assertNotNull(mSmssyncResponse.getPayload().getMessages());
        assertEquals("+XXXXXXXX",
                mSmssyncResponse.getPayload().getMessages().get(0).getPhoneNumber());
        assertEquals("from couch to you homes",
                mSmssyncResponse.getPayload().getMessages().get(0).getBody());
        assertEquals("aeefa9195f734d32b7e0f9d62d327f6d",
                mSmssyncResponse.getPayload().getMessages().get(0).getUuid());
    }
}
