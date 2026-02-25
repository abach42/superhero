package com.abach42.superhero.syslog.message;

public class IssueMessage extends Message {
    public String httpStatus;
    public String errorCode;

    public String additional;

    @Override
    public String toString() {
        return super.toString()
                + DELIMITER + "httpStatus: " + httpStatus
                + DELIMITER + "errorCode: " + errorCode
                + DELIMITER + "additional: " + additional;
    }
}
