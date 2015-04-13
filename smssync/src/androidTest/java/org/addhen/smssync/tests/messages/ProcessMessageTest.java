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

package org.addhen.smssync.tests.messages;

import org.addhen.smssync.messages.ProcessMessage;
import org.addhen.smssync.messages.ProcessSms;
import org.addhen.smssync.models.Message;
import org.addhen.smssync.models.SmssyncResponse;
import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.net.MessageSyncHttpClient;
import org.addhen.smssync.prefs.Prefs;
import org.addhen.smssync.tests.CustomAndroidTestCase;
import org.mockito.Mock;

import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test Process message
 */
public class ProcessMessageTest extends CustomAndroidTestCase {

    @Mock
    Message mockMessage;

    @Mock
    ProcessSms mockProcessSms;

    @Mock
    SmssyncResponse mockSmssyncResponse;

    @Mock
    SyncUrl mockSyncUrl;

    @Mock
    SmssyncResponse.Payload mockPayload;

    @Mock
    Message mockMsg;

    Prefs spyPrefs;

    private List<Message> msgs;

    private ProcessMessage mProcessMessage;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        initMocks(this);
        mProcessMessage = new ProcessMessage(getContext(), mockProcessSms);
        spyPrefs = spy(new Prefs(getContext()));
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @SmallTest
    public void testShouldSendResponseFromServerAsSms() throws Exception {
        stubNeedMethodsForSyncOperation();

        // Enable reply from server
        spyPrefs.enableReplyFrmServer().set(true);

        mProcessMessage.smsServerResponse(mockSmssyncResponse);
        verifySendSmsIsRun2x();

    }

    @SmallTest
    public void testShouldSendResponseConfiguredOnPhoneAsSms() throws Exception {
        stubNeedMethodsForSyncOperation();

        // Enable reply from server
        spyPrefs.enableReply().set(true);

        mProcessMessage.routeSms(mockMessage);
        verify(mockProcessSms, times(1)).sendSms(mockMsg);
    }

    @MediumTest
    public void testShouldSuccessfullySyncReceivedSmsWithNoInstantResponseFromServer()
            throws Exception {
        syncSmsToSyncUrl(false);

        verify(mockProcessSms, never()).sendSms(mockMsg);

    }

    @MediumTest
    public void testShouldSuccessfullySyncReceivedSmsWithInstantResponseFromServer()
            throws Exception {
        syncSmsToSyncUrl(true);

        verifySendSmsIsRun2x();
    }

    private void verifySendSmsIsRun2x() {
        verify(mockProcessSms, times(2)).sendSms(mockMsg);
    }

    private void syncSmsToSyncUrl(boolean postResponseToServer) {
        MessageSyncHttpClient client = mock(MessageSyncHttpClient.class);

        when(client.getSyncUrl()).thenReturn(mockSyncUrl);
        when(mockSyncUrl.getUrl()).thenReturn("http://www.dummyurl.com");
        when(client.postSmsToWebService()).thenReturn(true);

        stubNeedMethodsForSyncOperation();

        // Enable reply from server
        spyPrefs.enableReplyFrmServer().set(postResponseToServer);

        when(client.getServerSuccessResp()).thenReturn(mockSmssyncResponse);

        final boolean posted = mProcessMessage.syncReceivedSms(mockMessage, client);
        verify(client).postSmsToWebService();
        assertTrue(posted);
    }

    private void stubNeedMethodsForSyncOperation() {
        msgs = Arrays.asList(mockMsg, mockMsg);

        when(mockSmssyncResponse.getPayload()).thenReturn(mockPayload);
        when(mockPayload.getMessages()).thenReturn(msgs);
    }
}
