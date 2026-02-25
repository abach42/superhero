package com.abach42.superhero.syslog.message;

public class IssueMessageBuilder extends MessageBuilder<IssueMessage, IssueMessageBuilder> {
    public IssueMessageBuilder() {
        super(new IssueMessage());
    }

    public IssueMessageBuilder httpStatus(String httpStatus) {
        message.httpStatus = httpStatus;
        return this;
    }

    public IssueMessageBuilder additional(String additional) {
        message.additional = additional;
        return this;
    }

    @Override
    protected IssueMessageBuilder self() {
        return this;
    }
}