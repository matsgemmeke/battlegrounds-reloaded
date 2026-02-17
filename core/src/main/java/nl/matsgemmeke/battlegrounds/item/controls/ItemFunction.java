package nl.matsgemmeke.battlegrounds.item.controls;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;

/**
 * Represents a functional link between a performed action on an item and a specific feature of that item.
 * <p>
 * Implementations of this interface should be concise and designed to activate only the intended feature without
 * triggering unrelated behaviors. This ensures a clear and modular interaction between item actions and their
 * corresponding effects.
 * </p>
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
    boolean perform(T holder);
}
