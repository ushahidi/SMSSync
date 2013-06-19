
package org.addhen.smssync.tasks.state;

import java.util.EnumSet;

import org.addhen.smssync.MessageType;
import org.addhen.smssync.exceptions.ConnectivityException;
import org.addhen.smssync.exceptions.I8nException;

import android.content.res.Resources;

public abstract class State {

    public final SyncState state;

    public final Exception exception;

    public final MessageType messageType;

    public State(SyncState state, MessageType messageType, Exception exception) {

        this.state = state;
        this.exception = exception;
        this.messageType = messageType;
    }

    public boolean isInitialState() {
        return state == SyncState.INITIAL;
    }

    public boolean isRunning() {
        return EnumSet.of(

                SyncState.SYNC,

                SyncState.UPDATING_THREADS).contains(state);
    }

    public abstract State transition(SyncState newState, Exception exception);

    public boolean isConnectivityError() {
        return exception instanceof ConnectivityException;
    }

    public boolean isError() {
        return state == SyncState.ERROR;
    }

    public boolean isCanceled() {
        return state == SyncState.CANCELED_SYNC;
    }

    public String getError(Resources resources) {
        if (exception == null)
            return null;

        if (exception instanceof I8nException) {
            return resources.getString(((I8nException) exception).resId());
        } else {
            return exception.getLocalizedMessage();
        }
    }

    public String getNotificationMessage(Resources resources) {
        switch (state) {
            case ERROR:
                return getError(resources);
            default:
                return null;
        }
    }

}
