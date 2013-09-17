package org.addhen.smssync.controllers;

import org.addhen.smssync.models.PhoneStatusInfo;
import org.addhen.smssync.views.ILogView;

/**
 * Log controller
 */
public class LogController {

    private ILogView mView;

    public void setView(ILogView view) {
        mView = view;
    }

    protected ILogView getView() {
        return mView;
    }

    public void setPhoneStatusInfo(PhoneStatusInfo info) {
        getView().setPhoneStatus(info);
    }
}
