package org.addhen.smssync.tests.services;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import android.test.suitebuilder.annotation.SmallTest;

import org.addhen.smssync.models.Message;
import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.net.BaseHttpClient;
import org.addhen.smssync.net.MessageSyncHttpClient;
import org.addhen.smssync.net.SyncScheme;
import org.addhen.smssync.tests.BaseTest;

/**
 *
 * Class: SyncSchemeTest
 * Description: Test all different sync schemes.
 * Author: Salama A.B. <devaksal@gmail.com>
 *
 */
public class SyncSchemeTest extends BaseTest {

    SyncUrl syncUrl;
    org.addhen.smssync.models.Message msg;
    final String toNumber = "777777777";
    final String deviceId = "21";

    private static final MediaType CONTENT_TYPE
            = MediaType.parse("application/x-www-form-urlencoded");

    @Override
    public void setUp() throws Exception{
        syncUrl = new SyncUrl();
        syncUrl.setKeywords("demo,ushahidi,smssync");
        syncUrl.setSecret("demo4");
        syncUrl.setTitle("ushahidi demo4");
        syncUrl.setUrl("http://demo.ushahidi.com/smssync4");

        msg = new Message();
        msg.setMessage("TEST MESSAGE");
        msg.setPhoneNumber("555555555");
        msg.setUuid("312312");
        msg.setTimestamp("0");

        super.setUp();
    }

    @Override
    public void tearDown() throws Exception{
        syncUrl.deleteAllSyncUrl();
        msg = null;
        super.tearDown();
    }

    @SmallTest
    public void testSetPOSTJSONSyncScheme() {
        SyncScheme syncScheme = new SyncScheme(SyncScheme.SyncMethod.POST, SyncScheme.SyncDataFormat.JSON);
        assertNotNull(syncScheme);
        assertEquals(syncScheme.getMethod(),SyncScheme.SyncMethod.POST);
        assertEquals(syncScheme.getDataFormat(),SyncScheme.SyncDataFormat.JSON);
    }

    @SmallTest
    public void testSetPOSTXMLSyncScheme() {
        SyncScheme syncScheme = new SyncScheme(SyncScheme.SyncMethod.POST, SyncScheme.SyncDataFormat.XML);
        assertNotNull(syncScheme);
        assertEquals(syncScheme.getMethod(),SyncScheme.SyncMethod.POST);
        assertEquals(syncScheme.getDataFormat(),SyncScheme.SyncDataFormat.XML);
    }

    @SmallTest
    public void testSyncWithPOSTAndURLEncoded(){
        syncUrl.setSyncScheme(new SyncScheme());

        MessageSyncHttpClient client = new MessageSyncHttpClient(
            getContext(), syncUrl, msg, toNumber, deviceId
        );

        Request req = null;
        try {
            client.execute();
            req = client.getRequest();
        } catch (Exception e) {}

        assertNotNull(req);

        assertEquals(BaseHttpClient.HttpMethod.POST.value(),client.getRequest().method());
        RequestBody body = client.getRequest().body();

        assertNotNull(body);
        assertEquals(CONTENT_TYPE, body.contentType());
    }

    @SmallTest
    public void testSyncWithPOSTAndJSON(){
        SyncScheme syncScheme = new SyncScheme(SyncScheme.SyncMethod.POST, SyncScheme.SyncDataFormat.JSON);
        syncUrl.setSyncScheme(syncScheme);

        MessageSyncHttpClient client = new MessageSyncHttpClient(
            getContext(), syncUrl, msg, toNumber, deviceId
        );

        Request req = null;
        try {
            client.execute();
            req = client.getRequest();
        } catch (Exception e) {}

        assertNotNull(req);

        assertEquals(BaseHttpClient.HttpMethod.POST.value(),client.getRequest().method());
        RequestBody body = client.getRequest().body();

        assertNotNull(body);
        assertEquals(BaseHttpClient.JSON, body.contentType());
    }

    @SmallTest
    public void testSyncWithPOSTAndXML(){
        syncUrl.setSyncScheme(new SyncScheme(SyncScheme.SyncMethod.POST, SyncScheme.SyncDataFormat.XML));

        MessageSyncHttpClient client = new MessageSyncHttpClient(
            getContext(), syncUrl, msg, toNumber, deviceId
        );
        Request req = null;
        try {
            client.execute();
            req = client.getRequest();
        } catch (Exception e) {}

        assertNotNull(req);

        assertEquals(BaseHttpClient.HttpMethod.POST.value(),client.getRequest().method());
        RequestBody body = client.getRequest().body();

        assertNotNull(body);
        assertEquals(BaseHttpClient.XML, body.contentType());
    }

    @SmallTest
    public void testSyncWithPUTAndJSON(){
        syncUrl.setSyncScheme(new SyncScheme(SyncScheme.SyncMethod.PUT, SyncScheme.SyncDataFormat.JSON));

        MessageSyncHttpClient client = new MessageSyncHttpClient(
            getContext(), syncUrl, msg, toNumber, deviceId
        );
        Request req = null;
        try {
            client.execute();
            req = client.getRequest();
        } catch (Exception e) {}

        assertNotNull(req);

        assertEquals(BaseHttpClient.HttpMethod.PUT.value(),client.getRequest().method());
        RequestBody body = client.getRequest().body();

        assertNotNull(body);
        assertEquals(BaseHttpClient.JSON, body.contentType());
    }

    @SmallTest
    public void testSyncWithPUTAndXML(){
        syncUrl.setSyncScheme(new SyncScheme(SyncScheme.SyncMethod.PUT, SyncScheme.SyncDataFormat.XML));

        MessageSyncHttpClient client = new MessageSyncHttpClient(
            getContext(), syncUrl, msg, toNumber, deviceId
        );
        Request req = null;
        try {
            client.execute();
            req = client.getRequest();
        } catch (Exception e) {}

        assertNotNull(req);

        assertEquals(BaseHttpClient.HttpMethod.PUT.value(),client.getRequest().method());
        RequestBody body = client.getRequest().body();

        assertNotNull(body);
        assertEquals(BaseHttpClient.XML, body.contentType());
    }

    @SmallTest
    public void testSyncWithPUTAndURLEncoded(){
        syncUrl.setSyncScheme(new SyncScheme(SyncScheme.SyncMethod.PUT, SyncScheme.SyncDataFormat.URLEncoded));

        MessageSyncHttpClient client = new MessageSyncHttpClient(
            getContext(), syncUrl, msg, toNumber, deviceId
        );
        Request req = null;
        try {
            client.execute();
            req = client.getRequest();
        } catch (Exception e) {}

        assertNotNull(req);

        assertEquals(BaseHttpClient.HttpMethod.PUT.value(),client.getRequest().method());
        RequestBody body = client.getRequest().body();

        assertNotNull(body);
        assertEquals(CONTENT_TYPE, body.contentType());
    }

    @SmallTest
    public void testSyncWithBasicAuth(){
        syncUrl.setUrl("http://admin:123qwe!$@demo.ushahidi.com/smssync4");
        syncUrl.setSyncScheme(new SyncScheme(SyncScheme.SyncMethod.PUT, SyncScheme.SyncDataFormat.URLEncoded));

        MessageSyncHttpClient client = new MessageSyncHttpClient(
            getContext(), syncUrl, msg, toNumber, deviceId
        );

        Request req = null;
        try {
            client.execute();
            req = client.getRequest();
        } catch (Exception e) {} 

        assertNotNull(req);
        String header = req.header("Authorization");

        assertNotNull(req);
        assertNotNull(header);assertEquals(
            header,
            "Basic YWRtaW46MTIzcXdlISQ="
        );
    }
}
