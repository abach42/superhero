package com.abach42.superhero.syslog;

import com.abach42.superhero.syslog.message.ActionMessage;
import com.abach42.superhero.syslog.message.ActionMessageBuilder;
import com.abach42.superhero.syslog.message.Message;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Spring AOP Aspect that intercepts annotated methods in REST controllers. See: LogAction.
 */
@Aspect
@Component
@Order(2)
@ConditionalOnProperty(name = "iat.monitoring.syslog.enabled", havingValue = "true", matchIfMissing = true)
public class ActionSysLogAspect extends AbstractSysLogAspect {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public ActionSysLogAspect(SysLogService sysLogService) {
        super(sysLogService);
    }

    @AfterReturning("@annotation(logAction) "
        + "&& execution(* (@org.springframework.web.bind.annotation.RestController *).*(..)) "
        + "&& !execution(* com.abach42.superhero.syslog..*(..)) ")
    public void logUserAction(JoinPoint joinPoint, LogAction logAction) {
        try {
            SysLog actionSysLog = createActionSysLog(joinPoint, logAction);

            sysLogService.persist(actionSysLog);
        } catch (Exception e) {
            log.error("System log not possible on {}", e.getMessage(), e);
        }
    }

    private SysLog createActionSysLog(JoinPoint joinPoint, LogAction logAction) {
        SysLog actionSysLog = new Action();

        Level level = logAction.level();
        actionSysLog.setLevel(level);

        String title = resolveTitle(logAction);
        actionSysLog.setTitle(title);

        List<String> message = buildMessage(joinPoint, logAction);

        actionSysLog.setMessage(message);

        return actionSysLog;
    }

    private List<String> buildMessage(JoinPoint joinPoint, LogAction logAction) {
        ActionMessage actionMessage = new ActionMessageBuilder()
            .requestMethod(retrieveRequestMethod())
            .requestUrl(retrieveRequestUrl())
            .parametersCount(retrieveParameterCount(joinPoint))
            .parameters(listParameters(joinPoint))
            .controller(retrieveController(joinPoint))
            .action(retrieveMethodName(joinPoint))
            .message(addCustomMessage(logAction))
            .build();

        return Message.convertToListOfStrings(actionMessage.toString());
    }

    private String resolveTitle(LogAction logUserAction) {
        if (logUserAction.title().trim().isEmpty()) {
            return logUserAction.value();
        }

        return logUserAction.title();
    }

    private String retrieveController(JoinPoint joinPoint) {
        return joinPoint.getTarget().getClass().getSimpleName();
    }

    private String retrieveMethodName(JoinPoint joinPoint) {
        return joinPoint.getSignature().getName();
    }

    private String addCustomMessage(LogAction logUserAction) {
        if (logUserAction.message().trim().isEmpty()) {
            return null;
        }

       return logUserAction.message();
    }
}