package com.abach42.superhero.syslog;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;
import org.aspectj.lang.JoinPoint;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class AbstractSysLogAspect {
    protected final SysLogService sysLogService;

    public AbstractSysLogAspect(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }

    protected String retrieveRequestMethod() {
        HttpServletRequest request = getCurrentRequest();

        if (request == null) {
            return null;
        }

        return request.getMethod();
    }

    protected String retrieveRequestUrl() {
        HttpServletRequest request = getCurrentRequest();

        if (request == null) {
            return null;
        }

        return request.getRequestURI();
    }

    protected Integer retrieveParameterCount(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        return args.length;
    }

    protected String retrieveQueryStrings() {
        HttpServletRequest request = getCurrentRequest();

        if (request == null) {
            return null;
        }

        return request.getQueryString();
    }

    private static HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes requestAttributes =
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (requestAttributes == null) {
            return null;
        }

        return requestAttributes.getRequest();
    }

    protected String listParameters(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        if (args.length == 0) {
            return null;
        }

        String parameters = Arrays.stream(args)
            .map(Object::toString).reduce((a, b) -> a + ", " + b).orElse("");

        String queryString = retrieveQueryStrings();

        if (queryString == null) {
            return parameters;
        }

        return parameters + ", " +  queryString;
    }
}
