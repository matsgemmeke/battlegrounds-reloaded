package nl.matsgemmeke.battlegrounds.event;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class EventBus<T extends Event> {

    @NotNull
    private Set<EventHandler<T>> eventHandlers;

    public EventBus() {
        this.eventHandlers = new HashSet<>();
    }

    public EventBus(@NotNull EventHandler<T>... eventHandlers) {
        this.eventHandlers = new HashSet<>(Arrays.asList(eventHandlers));
    }

    /**
     * Adds all event handlers of another {@link EventBus}, combining the two.
     *
     * @param eventBus the event channel to add
     * @return whether the event bus was combined
     */
    public boolean addEventBus(@NotNull EventBus<T> eventBus) {
        return eventHandlers.addAll(eventBus.eventHandlers);
    }

    /**
     * Adds an event handler to the channel.
     *
     * @param eventHandler the event handler to add
     * @return whether the event handler was added
     */
    public boolean addEventHandler(@NotNull EventHandler<T> eventHandler) {
        return eventHandlers.add(eventHandler);
    }

    /**
     * Passes an event to all event handlers.
     *
     * @param event the event to handle
     */
    public void passEvent(@NotNull T event) {
        for (EventHandler<T> eventHandler : eventHandlers) {
            eventHandler.handle(event);
        }
    }

    /**
     * Removes an event handler from the channel.
     *
     * @param eventHandler the event handler to remove
     */
    public boolean removeEventHandler(@NotNull EventHandler<T> eventHandler) {
        return eventHandlers.remove(eventHandler);
    }
}
