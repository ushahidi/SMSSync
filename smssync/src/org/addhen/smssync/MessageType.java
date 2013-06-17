
package org.addhen.smssync;

public enum MessageType {
    SMS("sms"),
    PENDING("pending"),
    TASK("task");

    private final String type;

    private MessageType(String type) {
        this.type = type;
    }

}
