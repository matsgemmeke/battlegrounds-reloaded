package nl.matsgemmeke.battlegrounds.event;

import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class EventDispatcher {

    @NotNull
    private Map<Class<? extends Event>, EventBus<?>> eventBuses;
    @NotNull
    private PluginManager pluginManager;

    public EventDispatcher(@NotNull PluginManager pluginManager) {
        this.pluginManager = pluginManager;
        this.eventBuses = new HashMap<>();
    }

    /**
     * Dispatches an event that gets sent to its corresponding {@link EventBus} and to the server as an outgoing event.
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
     * Dispatches an event that gets sent only to its corresponding {@link EventBus}.
     *
     * @param event the event to be dispatched
     */
    @SuppressWarnings("unchecked")
    public <T extends Event> void dispatchInternalEvent(@NotNull T event) {
        if (!eventBuses.containsKey(event.getClass())) {
            return;
        }

        EventBus<T> eventBus = (EventBus<T>) eventBuses.get(event.getClass());
        eventBus.passEvent(event);
    }

    /**
     * Gets whether the dispatcher has an {@link EventBus} registered for a specific event class.
     *
     * @param eventClass the event class
     * @return whether an {@link EventBus} is registered for the class
     */
    public boolean containsEventBusForClass(Class<? extends Event> eventClass) {
        return eventBuses.containsKey(eventClass);
    }

    /**
     * Registers an event channel.
     *
     * @param eventClass the event class that the event channel handles
     * @param eventBus the event bus
     */
    @SuppressWarnings("unchecked")
    public <T extends Event> void registerEventBus(@NotNull Class<T> eventClass, @NotNull EventBus<T> eventBus) {
        if (eventBuses.containsKey(eventClass)) {
            // If an event channel for the event class already exists, add the event handlers to the existing channel
            EventBus<T> existingBus = (EventBus<T>) eventBuses.get(eventClass);
            existingBus.addEventBus(eventBus);
        } else {
            eventBuses.put(eventClass, eventBus);
        }
    }

    /**
     * Unregisters an event bus.
     *
     * @param eventClass the event class that the event bus handles
     */
    public void unregisterEventBus(@NotNull Class<? extends Event> eventClass) {
        if (!eventBuses.containsKey(eventClass)) {
            return;
        }

        eventBuses.remove(eventClass);
    }
}
