package nl.matsgemmeke.battlegrounds.event;

import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

public class EventDispatcher {

    @NotNull
    private Logger logger;
    @NotNull
    private Map<Class<? extends Event>, List<EventHandlerMethod>> eventMethods;
    @NotNull
    private PluginManager pluginManager;

    public EventDispatcher(@NotNull PluginManager pluginManager, @NotNull Logger logger) {
        this.pluginManager = pluginManager;
        this.logger = logger;
        this.eventMethods = new HashMap<>();
    }

    /**
     * Dispatches an event to be handled by its corresponding {@link EventHandler} instances as well as the server as
     * an outgoing event.
     *
     * @param event the event to be dispatched
     */
    public void dispatchExternalEvent(@NotNull Event event) {
        // Dispatch the event internally so the plugin does not have to listen for its own events
        this.dispatchInternalEvent(event);
        // Call the outgoing event to the plugin manager
        pluginManager.callEvent(event);
    }

    /**
     * Dispatches an event to be handled by its corresponding {@link EventHandler} instances.
     *
     * @param event the event to be dispatched
     */
    public <T extends Event> void dispatchInternalEvent(@NotNull T event) {
        for (Entry<Class<? extends Event>, List<EventHandlerMethod>> entry : eventMethods.entrySet()) {
            Class<? extends Event> eventClass = entry.getKey();

            if (eventClass.isAssignableFrom(event.getClass())) {
                for (EventHandlerMethod method : entry.getValue()) {
                    try {
                        method.invoke(event);
                    } catch (IllegalAccessException e) {
                        logger.severe("Cannot invoke inaccessible handle method for event " + eventClass.getSimpleName());
                    } catch (IllegalArgumentException e) {
                        logger.severe("Cannot invoke handle method for event " + eventClass.getSimpleName() + " with given arguments");
                    } catch (InvocationTargetException e) {
                        logger.severe("Error occurred while invoking handle method for event " + eventClass.getSimpleName());
                    }
                }
            }
        }
    }

    /**
     * Registers a new event handler instance.
     *
     * @param eventClass the event class
     * @param eventHandler the event handler
     * @param <T> the event type
     */
    public <T extends Event> void registerEventHandler(@NotNull Class<T> eventClass, @NotNull EventHandler<T> eventHandler) {
        Method method;

        try {
            method = eventHandler.getClass().getDeclaredMethod("handle", eventClass);
        } catch (NoSuchMethodException e) {
            logger.severe("Cannot register event handler for event " + eventClass.getSimpleName() + " without handle method");
            return;
        }

        EventHandlerMethod eventMethod = new EventHandlerMethod(eventHandler, method);

        if (eventMethods.containsKey(eventClass)) {
            eventMethods.get(eventClass).add(eventMethod);
        } else {
            List<EventHandlerMethod> list = new ArrayList<>();
            list.add(eventMethod);

            eventMethods.put(eventClass, list);
        }
    }
}
