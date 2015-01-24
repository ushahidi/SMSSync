package org.addhen.smssync.listeners;

import org.addhen.smssync.state.LogEvent;

/**
 * Log listener
 */
public interface LogListener {

    public void reloadLog(LogEvent event);
}
