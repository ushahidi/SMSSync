
package org.addhen.smssync.tasks.state;

import org.addhen.smssync.exceptions.ConnectivityException;
import org.addhen.smssync.exceptions.I8nException;

import android.content.res.Resources;

import java.util.EnumSet;

/**
 * Base class to provide state of a running task
 *
 * @author eyedol
 */
public abstract class State {

    public final SyncState state;

    public final Exception exception;

    /**
     * Default constructor
     *
     * @param state     The state of the task
     * @param exception Exception to throw.
     */
    public State(SyncState state, Exception exception) {

        this.state = state;
        this.exception = exception;
    }

    public boolean isInitialState() {
        return state == SyncState.INITIAL;
    }

    public boolean isRunning() {
        return EnumSet.of(SyncState.SYNC).contains(state);
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

    /**
     * Get any error state of the task under execution.
     */
    public String getError(Resources resources) {
        // exception shouldn't be null
        if (exception == null) {
            return null;
        }

        // Is the exception message localizable
        if (exception instanceof I8nException) {
            // return any localizable string
            return resources.getString(((I8nException) exception).resId());
        } else {
            // return an exception message based on the active locale of the
            // system
            return exception.getLocalizedMessage();
        }
    }

    /**
     * Pass notification messages to the main UI
     */
    public String getNotificationMessage(Resources resources) {
        switch (state) {
            case ERROR:
                return getError(resources);
            default:
                return null;
        }
    }

}
