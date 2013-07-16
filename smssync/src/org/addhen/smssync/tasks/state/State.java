
package org.addhen.smssync.tasks.state;

import java.util.EnumSet;

import org.addhen.smssync.R;
import org.addhen.smssync.exceptions.ConnectivityException;

import android.content.res.Resources;

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
     * @param state The state of the task
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
     * 
     * @param resources
     * @return
     */
    public String getError(Resources resources) {
        return resources.getString(R.string.unknown_issue);

    }

    /**
     * Pass notification messages to the main UI
     * 
     * @param resources
     * @return
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
