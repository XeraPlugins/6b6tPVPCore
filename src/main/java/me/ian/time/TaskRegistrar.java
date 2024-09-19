package me.ian.time;

import lombok.SneakyThrows;
import me.ian.PVPHelper;
import me.ian.util.Utils;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author SevJ6
 */
public class TaskRegistrar {

    @SneakyThrows
    public static void register(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(ScheduledTask.class)) continue;
                ScheduledTask task = method.getAnnotation(ScheduledTask.class);
                PVPHelper.EXECUTOR_SERVICE.scheduleWithFixedDelay(() -> Utils.invokeMethodUnderBukkit(method, PVPHelper.INSTANCE), 0L, task.delay(), TimeUnit.MILLISECONDS);
            }
        }
    }
}
