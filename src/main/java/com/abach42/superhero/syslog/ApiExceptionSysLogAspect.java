package com.abach42.superhero.syslog;

import com.abach42.superhero.shared.api.ApiException;
import com.abach42.superhero.syslog.message.IssueMessage;
import com.abach42.superhero.syslog.message.IssueMessageBuilder;
import com.abach42.superhero.syslog.message.Message;
import java.util.List;
import java.util.Optional;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
@ConditionalOnProperty(name = "iat.monitoring.syslog.enabled", havingValue = "true", matchIfMissing = true)
public class ApiExceptionSysLogAspect extends AbstractSysLogAspect {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public ApiExceptionSysLogAspect(SysLogService sysLogService) {
        super(sysLogService);
    }

    @AfterThrowing(pointcut = "execution(* (@org.springframework.stereotype.Service *).*(..)) " +
        "&& !execution(* com.abach42.superhero.syslog..*(..)) " +
        "|| execution(* (@org.springframework.web.bind.annotation.RestController *).*(..)) " +
        "&& !execution(* com.abach42.superhero.syslog..*(..)))", throwing = "apiException")
    public void logApiException(JoinPoint joinPoint, ApiException apiException) {
        try {
            SysLog issueSysLog = createIssueSysLog(joinPoint, apiException);

            sysLogService.persist(issueSysLog);
        } catch (Exception e) {
            log.error("System log not possible on {}", e.getMessage(), e);
        }
    }

    private SysLog createIssueSysLog(JoinPoint joinPoint, ApiException apiException) {
        SysLog issueSysLog = new Issue();

        issueSysLog.setLevel(Level.STANDARD); //todo receive

        issueSysLog.setTitle(joinPoint.getSourceLocation().getFileName());

        List<String> message = buildMessage(joinPoint, apiException);
        issueSysLog.setMessage(message);

        return issueSysLog;
    }

    private List<String> buildMessage(JoinPoint joinPoint, ApiException apiException) {
        IssueMessage issueMessage = new IssueMessageBuilder()
            .requestMethod(retrieveRequestMethod())
            .requestUrl(retrieveRequestUrl())
            .parametersCount(retrieveParameterCount(joinPoint))
            .parameters(listParameters(joinPoint))
            .httpStatus(retrieveHttpStatus(apiException))
            .message(retrieveMessage(apiException))
            .build();

        return Message.convertToListOfStrings(issueMessage.toString());
    }

    private String retrieveHttpStatus(ApiException apiException) {
        return Optional.of(apiException.getStatusCode()).orElse(null).toString();
    }

    private String retrieveMessage(ApiException apiException) {
        return apiException.getMessage();
    }


}
