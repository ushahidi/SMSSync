package org.addhen.smssync.tests.services;

import org.addhen.smssync.App;
import org.addhen.smssync.database.BaseDatabseHelper;
import org.addhen.smssync.models.Message;
import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.net.MessageSyncHttpClient;
import org.addhen.smssync.net.SyncScheme;
import org.addhen.smssync.tests.BaseTest;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;

import android.test.suitebuilder.annotation.SmallTest;

import java.util.Date;

/**
 *
 * Class: SyncSchemeTest
 * Description: Test all different sync schemes.
 * Author: Salama A.B. <devaksal@gmail.com>
 *
 */
public class SyncSchemeTest extends BaseTest {

    final String toNumber = "777777777";

    final String deviceId = "21";

    SyncUrl syncUrl;

    org.addhen.smssync.models.Message msg;

    @Override
    public void setUp() throws Exception{
        syncUrl = new SyncUrl();
        syncUrl.setKeywords("demo,ushahidi,smssync");
        syncUrl.setSecret("demo4");
        syncUrl.setTitle("ushahidi demo4");
        syncUrl.setUrl("http://demo.ushahidi.com/smssync4");

        msg = new Message();
        msg.setBody("TEST MESSAGE");
        msg.setPhoneNumber("555555555");
        msg.setUuid("312312");
        msg.setDate(new Date());

        super.setUp();
    }

    @Override
    public void tearDown() throws Exception{
        App.getDatabaseInstance().getSyncUrlInstance().deleteAllSyncUrl(new BaseDatabseHelper.DatabaseCallback<Void>() {
            @Override
            public void onFinished(Void result) {

            }

            @Override
            public void onError(Exception exception) {

            }
        });
        msg = null;
        super.tearDown();
    }

    @SmallTest
    public void testSyncWithPOSTAndURLEncoded(){
        syncUrl.setSyncScheme(new SyncScheme());

        MessageSyncHttpClient client = new MessageSyncHttpClient(
                getContext(), syncUrl, msg, toNumber, deviceId
        );
        HttpUriRequest req = null;
        try {
            req = client.getRequest();
        } catch (Exception e) {}

        assertNotNull(req);

        assertEquals(HttpPost.class.getCanonicalName(), req.getClass().getCanonicalName());

        HttpEntity entity = ((HttpPost) req).getEntity();

        assertNotNull(entity);

        assertEquals(UrlEncodedFormEntity.class.getCanonicalName(),
                entity.getClass().getCanonicalName());
    }

    @SmallTest
    public void testSyncWithPOSTAndJSON(){
        syncUrl.setSyncScheme(
                new SyncScheme(SyncScheme.SyncMethod.POST, SyncScheme.SyncDataFormat.JSON));

        MessageSyncHttpClient client = new MessageSyncHttpClient(
                getContext(), syncUrl, msg, toNumber, deviceId
        );
        HttpUriRequest req = null;
        try {
            req = client.getRequest();
        } catch (Exception e) {}

        assertNotNull(req);

        assertEquals(HttpPost.class.getCanonicalName(), req.getClass().getCanonicalName());

        HttpEntity entity = ((HttpPost) req).getEntity();

        assertNotNull(entity);

        assertEquals(StringEntity.class.getCanonicalName(), entity.getClass().getCanonicalName());
    }

    @SmallTest
    public void testSyncWithPOSTAndXML(){
        syncUrl.setSyncScheme(new SyncScheme(SyncScheme.SyncMethod.POST, SyncScheme.SyncDataFormat.XML));

        MessageSyncHttpClient client = new MessageSyncHttpClient(
                getContext(), syncUrl, msg, toNumber, deviceId
        );
        HttpUriRequest req = null;
        try {
            req = client.getRequest();
        } catch (Exception e) {}

        assertNotNull(req);

        assertEquals(HttpPost.class.getCanonicalName(), req.getClass().getCanonicalName());

        HttpEntity entity = ((HttpPost) req).getEntity();

        assertNotNull(entity);

        assertEquals(StringEntity.class.getCanonicalName(), entity.getClass().getCanonicalName());
    }

    @SmallTest
    public void testSyncWithPUTAndJSON(){
        syncUrl.setSyncScheme(new SyncScheme(SyncScheme.SyncMethod.PUT, SyncScheme.SyncDataFormat.JSON));

        MessageSyncHttpClient client = new MessageSyncHttpClient(
                getContext(), syncUrl, msg, toNumber, deviceId
        );
        HttpUriRequest req = null;
        try {
            req = client.getRequest();
        } catch (Exception e) {}

        assertNotNull(req);

        assertEquals(HttpPut.class.getCanonicalName(), req.getClass().getCanonicalName());

        HttpEntity entity = ((HttpPut) req).getEntity();

        assertNotNull(entity);

        assertEquals(StringEntity.class.getCanonicalName(), entity.getClass().getCanonicalName());
    }

    @SmallTest
    public void testSyncWithPUTAndXML(){
        syncUrl.setSyncScheme(new SyncScheme(SyncScheme.SyncMethod.PUT, SyncScheme.SyncDataFormat.XML));

        MessageSyncHttpClient client = new MessageSyncHttpClient(
                getContext(), syncUrl, msg, toNumber, deviceId
        );
        HttpUriRequest req = null;
        try {
            req = client.getRequest();
        } catch (Exception e) {}

        assertNotNull(req);

        assertEquals(HttpPut.class.getCanonicalName(), req.getClass().getCanonicalName());

        HttpEntity entity = ((HttpPut) req).getEntity();

        assertNotNull(entity);

        assertEquals(StringEntity.class.getCanonicalName(), entity.getClass().getCanonicalName());
    }

    @SmallTest
    public void testSyncWithPUTAndURLEncoded(){
        syncUrl.setSyncScheme(new SyncScheme(SyncScheme.SyncMethod.PUT, SyncScheme.SyncDataFormat.URLEncoded));

        MessageSyncHttpClient client = new MessageSyncHttpClient(
                getContext(), syncUrl, msg, toNumber, deviceId
        );
        HttpUriRequest req = null;
        try {
            req = client.getRequest();
        } catch (Exception e) {}

        assertNotNull(req);

        assertEquals(HttpPut.class.getCanonicalName(), req.getClass().getCanonicalName());

        HttpEntity entity = ((HttpPut) req).getEntity();

        assertNotNull(entity);

        assertEquals(UrlEncodedFormEntity.class.getCanonicalName(),
                entity.getClass().getCanonicalName());
    }

    @SmallTest
    public void testSyncWithBasicAuth(){
        syncUrl.setUrl("http://admin:123qwe!$@demo.ushahidi.com/smssync4");
        syncUrl.setSyncScheme(new SyncScheme(SyncScheme.SyncMethod.PUT, SyncScheme.SyncDataFormat.URLEncoded));

        MessageSyncHttpClient client = new MessageSyncHttpClient(
                getContext(), syncUrl, msg, toNumber, deviceId
        );

        HttpUriRequest req = null;
        try {
            req = client.getRequest();
        } catch (Exception e) {
        }

        Header header = req.getFirstHeader("Authorization");

        assertNotNull(req);
        assertNotNull(header);
        assertNotNull(req.getFirstHeader("Authorization"));

        assertEquals(
                req.getFirstHeader("Authorization").getValue(),
                "Basic YWRtaW46MTIzcXdlISQ="
        );
    }
}
