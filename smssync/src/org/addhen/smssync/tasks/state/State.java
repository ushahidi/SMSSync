
package org.addhen.smssync.tasks.state;

import java.util.EnumSet;

import android.content.res.Resources;

public abstract class State {

    public final SyncState state;

    public final Exception exception;

    public State(SyncState state, Exception exception) {

        this.state = state;
        this.exception = exception;
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

    public boolean isError() {
        return state == SyncState.ERROR;
    }

    public boolean isCanceled() {
        return state == SyncState.CANCELED_SYNC;
    }

    public String getErrorMessage(Resources resources, int resourceId) {
        if (exception == null)
            return null;

        return resources.getString(resourceId);
    }

}
