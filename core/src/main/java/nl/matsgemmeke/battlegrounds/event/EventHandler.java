package nl.matsgemmeke.battlegrounds.event;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * Handles logic execution based on a certain event.
 *
 * @param <T> the event the handler expects
 */
public interface EventHandler<T extends Event> {

    /**
     * Executes the event handler logic.
     *
     * @param event the event
     */
    void handle(@NotNull T event);
}
