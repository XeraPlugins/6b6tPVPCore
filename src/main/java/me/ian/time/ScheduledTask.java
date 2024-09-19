package me.ian.time;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author SevJ6
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ScheduledTask {
    long delay() default 1000L;
}
