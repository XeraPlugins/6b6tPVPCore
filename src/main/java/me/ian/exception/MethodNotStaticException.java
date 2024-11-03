package me.ian.exception;

import java.lang.reflect.Method;

/**
 * @author SevJ6
 */
public class MethodNotStaticException extends Exception {

    public MethodNotStaticException(Method method) {
        super(String.format("%s must be static!", method.toGenericString()));
    }
}
