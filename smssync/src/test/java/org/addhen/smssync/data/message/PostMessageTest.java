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

package org.addhen.smssync.data.message;

import com.addhen.android.raiburari.data.pref.BooleanPreference;
import com.addhen.android.raiburari.data.pref.StringPreference;

import org.addhen.smssync.data.PrefsFactory;
import org.addhen.smssync.data.cache.FileManager;
import org.addhen.smssync.data.entity.Message;
import org.addhen.smssync.data.entity.SyncUrl;
import org.addhen.smssync.data.net.MessageHttpClient;
import org.addhen.smssync.data.repository.datasource.filter.FilterDataSource;
import org.addhen.smssync.data.repository.datasource.message.MessageDataSource;
import org.addhen.smssync.data.repository.datasource.webservice.WebServiceDataSource;
import org.addhen.smssync.smslib.sms.ProcessSms;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Test PostMessage
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class PostMessageTest {

    private static final String DEVICE_ID = "ID";

    private static final String FROM = "000";

    private PostMessage mPostMessage;

    @Mock
    private Context mMockContext;

    @Mock
    private MessageHttpClient mMockMessageHttpClient;

    @Mock
    private MessageDataSource mMockMessageDataSource;

    @Mock
    private WebServiceDataSource mMockWebServiceDataSource;

    @Mock
    private FilterDataSource mMockFilterDataSource;

    @Mock
    private ProcessSms mMockProcessSms;

    @Mock
    private FileManager mMockFileManager;

    @Mock
    private ProcessMessageResult mMockProcessMessageResult;

    @Mock
    private SharedPreferences mMockSharedPreferences;

    @Mock
    private PrefsFactory mMockPrefsFactory;

    @Mock
    private Message mMockMessage;


    @Mock
    private SyncUrl mMockSyncUrl;

    @Mock
    private BooleanPreference mMockBooleanPreference;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mPostMessage = spy(new PostMessage(mMockContext, mMockPrefsFactory, mMockMessageHttpClient,
                mMockMessageDataSource, mMockWebServiceDataSource, mMockFilterDataSource,
                mMockProcessSms, mMockFileManager, mMockProcessMessageResult));
    }

    @Test
    public void shouldSuccessfullyRouteSMSToASingleWebService() throws IOException {
        List<SyncUrl> syncUrls = Arrays.asList(mMockSyncUrl);

        stubNeedMethodsForSyncOperation(syncUrls);
        verify(mMockMessageHttpClient, times(1))
                .postSmsToWebService(syncUrls.get(0), mMockMessage, FROM, DEVICE_ID);
    }

    @Test
    public void shouldSuccessfullyRouteSMSToTwoWebServices() throws IOException {
        List<SyncUrl> syncUrls = Arrays.asList(mMockSyncUrl, mMockSyncUrl);

        stubNeedMethodsForSyncOperation(syncUrls);
        verify(mMockMessageHttpClient, times(2))
                .postSmsToWebService(syncUrls.get(0), mMockMessage, FROM, DEVICE_ID);
    }

    @Test
    public void shouldSuccessfullyRouteSMSToSingleWebServiceWithServerResponse() {

    }

    private void stubNeedMethodsForSyncOperation(List<SyncUrl> syncUrls) {

        // Enable SMSsync service
        BooleanPreference serviceEnabled = mock(BooleanPreference.class);
        given(mMockPrefsFactory.serviceEnabled())
                .willReturn(serviceEnabled);
        given(mMockPrefsFactory.serviceEnabled().get()).willReturn(true);

        // Disable AutoReply
        BooleanPreference booleanPreference = mock(BooleanPreference.class);
        given(mMockPrefsFactory.enableReply())
                .willReturn(booleanPreference);
        given(mMockPrefsFactory.enableReply().get()).willReturn(false);
        doReturn(true).when(mPostMessage).isConnected();

        given(mMockWebServiceDataSource.get(SyncUrl.Status.ENABLED)).willReturn(syncUrls);

        // Don't process whitelist
        BooleanPreference enableWhitelist = mock(BooleanPreference.class);
        given(mMockPrefsFactory.enableWhitelist()).willReturn(enableWhitelist);
        given(mMockPrefsFactory.enableWhitelist().get()).willReturn(false);

        // Don't process blacklist
        BooleanPreference enableBlackList = mock(BooleanPreference.class);
        given(mMockPrefsFactory.enableBlacklist()).willReturn(enableBlackList);
        given(mMockPrefsFactory.enableBlacklist().get()).willReturn(false);

        given(mMockMessage.getMessageFrom()).willReturn(FROM);
        //Get UniqueID
        StringPreference getUniquePreference = mock(StringPreference.class);
        given(mMockPrefsFactory.uniqueId()).willReturn(getUniquePreference);
        given(mMockPrefsFactory.uniqueId().get()).willReturn(DEVICE_ID);

        given(mMockMessage.getMessageType()).willReturn(Message.Type.PENDING);
        // Disable replyFromServer
        BooleanPreference enableReplyFromServer = mock(BooleanPreference.class);
        given(mMockPrefsFactory.enableReplyFrmServer()).willReturn(enableReplyFromServer);
        // Don't delete from server
        BooleanPreference deleteFromInbox = mock(BooleanPreference.class);
        given(mMockPrefsFactory.autoDelete()).willReturn(deleteFromInbox);
        given(mMockPrefsFactory.autoDelete().get()).willReturn(false);
        doReturn(FROM).when(mPostMessage).getPhoneNumber();
        given(mMockMessageHttpClient
                .postSmsToWebService(mMockSyncUrl, mMockMessage, FROM,
                        mMockPrefsFactory.uniqueId().get())).willReturn(true);

        mPostMessage.routeSms(mMockMessage);
        verify(mMockWebServiceDataSource).get(SyncUrl.Status.ENABLED);
    }
}
