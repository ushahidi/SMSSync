package org.addhen.smssync.models;

import nl.qbusict.cupboard.annotation.Column;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class Filter extends Model {

    @Column("phone_number")
    private String phoneNumber;

    @Column("status")
    private Status status;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * The status of the filtered phone number.
     */
    public enum Status {
        WHITELIST,
        BLACKLIST;
    }

}
