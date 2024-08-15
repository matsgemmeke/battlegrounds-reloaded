package nl.matsgemmeke.battlegrounds.item.controls;

import nl.matsgemmeke.battlegrounds.item.holder.ItemHolder;
import org.jetbrains.annotations.NotNull;

/**
 * A feature of an item that performs a certain task.
 *
 * @param <T> the required item holder for executing the function
 */
public interface ItemFunction<T extends ItemHolder> {

    /**
     * Gets whether the function is available to be performed.
     *
     * @return whether the function is available
     */
    boolean isAvailable();

    /**
     * Gets whether the function blocks other functions from being performed.
     *
     * @return whether the function is blocking other functions
     */
    boolean isBlocking();

    /**
     * Gets whether the function is currently performing.
     *
     * @return whether the function is performing
     */
    boolean isPerforming();

    /**
     * Attempts to cancel the execution.
     *
     * @return whether the operation was cancelled
     */
    boolean cancel();

    /**
     * Executes the function.
     *
     * @param holder the required item holder
     * @return whether the function performance has started
     */
    boolean perform(@NotNull T holder);
}
