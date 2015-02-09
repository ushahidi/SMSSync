package org.addhen.smssync.tests.models;

import com.google.gson.Gson;

import org.addhen.smssync.models.SmssyncResponse;
import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.tests.BaseTest;

import android.test.suitebuilder.annotation.SmallTest;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class SmssyncResponseTest extends BaseTest {

    private Gson mGson;

    private SmssyncResponse mSmssyncResponse;

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
    }
}
