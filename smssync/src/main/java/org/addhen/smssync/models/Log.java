package org.addhen.smssync.models;

import org.addhen.smssync.util.Util;

import java.util.Date;

/**
 * Log messages
 */
public class Log extends Model {

    public final static String FORMAT = "MM-dd kk:mm";

    public String message;

    public CharSequence timestamp;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CharSequence getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = Util.formatDateTime(timestamp, FORMAT);
    }

    @Override
    public boolean load() {
        return false;
    }

    @Override
    public boolean save() {
        return false;
    }

    @Override
    public String toString() {
        return "Log{" +
                "message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
