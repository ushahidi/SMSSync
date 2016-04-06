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

package org.addhen.smssync.presentation.presenter;

import org.addhen.smssync.R;
import org.addhen.smssync.data.PrefsFactory;
import org.addhen.smssync.data.cache.FileManager;
import org.addhen.smssync.data.entity.Message;
import org.addhen.smssync.data.message.PostMessage;
import org.addhen.smssync.data.net.AppHttpClient;
import org.addhen.smssync.domain.entity.WebServiceEntity;
import org.addhen.smssync.domain.repository.WebServiceRepository;
import org.addhen.smssync.presentation.model.MessageModel;
import org.addhen.smssync.presentation.util.Utility;
import org.addhen.smssync.smslib.model.SmsMessage;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class DebugPresenter {

    private PrefsFactory mPrefsFactory;

    private AppHttpClient mAppHttpClient;

    private PostMessage mProcessMessage;

    private WebServiceRepository mWebServiceRepository;

    private FileManager mFileManager;

    @Inject
    public DebugPresenter(PrefsFactory prefsFactory, AppHttpClient appHttpClient,
            PostMessage processMessage, WebServiceRepository webServiceRepository,
            FileManager fileManager) {
        mPrefsFactory = prefsFactory;
        mAppHttpClient = appHttpClient;
        mProcessMessage = processMessage;
        mWebServiceRepository = webServiceRepository;
        mFileManager = fileManager;
    }

    public String isServerOKRequest(Context context) {
        int responseCode = 0;
        String message = "";
        List<WebServiceEntity> webServiceEntities = mWebServiceRepository
                .syncGetByStatus(WebServiceEntity.Status.ENABLED);
        for (WebServiceEntity webServiceEntity : webServiceEntities) {
            try {
                mAppHttpClient.execute();
            } catch (Exception e) {
                mFileManager.append(e.getMessage());
            }

            if (responseCode != 0) {
                message = message + context
                        .getString(R.string.server_respond_message, webServiceEntity.getTitle(),
                                responseCode);
            } else {
                message = message + context.getResources()
                        .getString(R.string.unsuccessful_server_connection_message,
                                webServiceEntity.getTitle());
            }

        }
        return message;
    }


    private String isCellReceptionOKRequest(Context context) {
        return context.getResources().getString(R.string.reception_ok_message);
    }


    private String getBatteryLevelRequest(Context context) {
        return context.getResources()
                .getString(R.string.battery_level_message, getBatteryLevel(context));
    }


    private String getStatusRequest(Context context) {
        return isServerOKRequest(context) + "\n" +
                getBatteryLevelRequest(context) + "\n" +
                isCellReceptionOKRequest(context);
    }

    public boolean handleStatusMessage(final SmsMessage sms, final Context context) {
        boolean isStatusMessage = true;
        Runnable runnable = null;
        switch (sms.body) {
            case StatusSMS.CELL_RECEPTION_CODE:
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        sendSms(isCellReceptionOKRequest(context));
                    }
                };
                break;
            case StatusSMS.SERVER_OK_CODE:
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        sendSms(isServerOKRequest(context));
                    }
                };
                break;
            case StatusSMS.BATTERY_LEVEL_CODE:
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        sendSms(getBatteryLevelRequest(context));
                    }
                };
                break;
            case StatusSMS.GET_STATUS_CODE:
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        sendSms(getStatusRequest(context));
                    }
                };
                break;
            default:
                isStatusMessage = false;
                break;
        }
        if (runnable != null) {
            new Thread(runnable).start();
        }

        return isStatusMessage;
    }

    private int getBatteryLevel(Context context) {
        Intent batteryIntent = context
                .registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        return Utility.calculateBatteryLevel(level, scale);
    }


    private void sendSms(String body) {
        final Long timeMills = System.currentTimeMillis();
        Message message = new Message();
        message.setMessageBody(body);
        message.setMessageDate(new Date(timeMills));
        message.setMessageFrom(mPrefsFactory.alertPhoneNumber().get());
        message.setMessageUuid(mProcessMessage.getProcessSms().getUuid());
        message.setMessageType(Message.Type.ALERT);
        MessageModel smsMessage = mProcessMessage.map(message);
        mProcessMessage.getProcessSms().sendSms(smsMessage, false);
    }

    protected interface StatusSMS {

        String CELL_RECEPTION_CODE = "@10";
        String SERVER_OK_CODE = "@20";
        String BATTERY_LEVEL_CODE = "@30";
        String GET_STATUS_CODE = "@40";
    }
}
