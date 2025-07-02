package com.abach42.superhero.config.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.annotation.Secured;

//TODO get rid of "SCOPE_" prefix
//https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html#oauth2resourceserver-jwt-authorization-extraction
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Secured({SecuredAdmin.ROLE_ADMIN})
public @interface SecuredAdmin {

    String ROLE_ADMIN = "SCOPE_ROLE_ADMIN";
}