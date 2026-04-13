package nl.matsgemmeke.battlegrounds.item.controls;

import nl.matsgemmeke.battlegrounds.item.ItemUser;

/**
 * Represents discrete behaviour that can be performed on an item by an {@link ItemUser}.
 *
 * <p>Implementations of this interface should be concise and designed to activate only the intended feature without
 * triggering unrelated behaviors. This ensures a clear and modular interaction between item actions and their
 * corresponding effects.
 *
 * @param <T> the type of user that can interact with this function
 */
public interface Function<T extends ItemUser> {

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
     * @param user the required item user
     * @return     whether the function performance has started
     */
    FunctionResult perform(T user);
}
