package com.abach42.superhero.config.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.access.annotation.Secured;

//TODO get rid of "SCOPE_" prefix
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Secured({SecuredUser.ROLE_USER, SecuredAdmin.ROLE_ADMIN})
public @interface SecuredUser{
    public static final String ROLE_USER = "SCOPE_ROLE_USER";
}