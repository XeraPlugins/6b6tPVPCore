package me.ian.time;

import lombok.SneakyThrows;
import me.ian.PVPHelper;
import me.ian.exception.MethodNotStaticException;
import me.ian.utils.Utils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * @author SevJ6
 */
public class TaskManager {

    @SneakyThrows
    public static void register(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(ScheduledTask.class)) continue;
                if (!Modifier.isStatic(method.getModifiers())) {
                    PVPHelper.INSTANCE.getLogger().log(Level.SEVERE, "Could not register method " + method.getName() + " from " + clazz.getName());
                    throw new MethodNotStaticException(method);
                }
                ScheduledTask task = method.getAnnotation(ScheduledTask.class);
                PVPHelper.EXECUTOR_SERVICE.scheduleWithFixedDelay(() -> Utils.invokeMethodUnderBukkit(method, PVPHelper.INSTANCE), 0L, task.delay(), TimeUnit.MILLISECONDS);
            }
        }
    }
}
