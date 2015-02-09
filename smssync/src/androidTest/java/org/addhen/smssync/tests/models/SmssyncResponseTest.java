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
                mSmssyncResponse.getPayload().getMessages().get(0).getMessage());
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
}
