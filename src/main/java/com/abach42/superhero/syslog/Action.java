package com.abach42.superhero.syslog;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

@Entity
@DiscriminatorValue("1")
public class Action extends SysLog {
    public static final String ACTOR = "ACTION";

    @Transient
    private String actor = ACTOR;

    //should be
    public String getActor() {
        return actor;
    }
}

