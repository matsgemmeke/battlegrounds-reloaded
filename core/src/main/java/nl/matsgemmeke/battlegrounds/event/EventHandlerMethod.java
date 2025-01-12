package nl.matsgemmeke.battlegrounds.event;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EventHandlerMethod {

    @NotNull
    private Object handler;
    @NotNull
    private Method method;

    public EventHandlerMethod(@NotNull Object handler, @NotNull Method method) {
        this.handler = handler;
        this.method = method;
    }

    public void invoke(@NotNull Object event) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        method.invoke(handler, event);
    }
}
