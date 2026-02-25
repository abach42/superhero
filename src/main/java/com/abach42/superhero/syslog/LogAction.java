package com.abach42.superhero.syslog;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark methods for logging purposes. The annotated method logs
 * relevant action details such as severity level, custom message, and title.
 * This is commonly used in conjunction with AOP to capture runtime information
 * for auditing or monitoring purposes.
 Usage: <code>@LogAction(value = "My important action", level = Level.ESSENTIAL, messsage = "my additional message")
 </code>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogAction {
    
    /**
     * Custom title for the logged action
     */
    String value();

    /**
     * Alias for value() - Custom title for the logged action
     */
    String title() default "";

    /**
     * Severity level of the action, default is STANDARD
     */
    Level level() default Level.STANDARD;

    /**
     * Additional message to be logged (optional)
     */
    String message() default "";
}