package nl.matsgemmeke.battlegrounds.item.mechanism.activation;

import nl.matsgemmeke.battlegrounds.item.holder.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.mechanism.ItemMechanism;
import org.jetbrains.annotations.NotNull;

/**
 * Object that tells an {@link ItemMechanism} when to activate.
 */
public interface ItemMechanismActivation {

    /**
     * Activates the mechanism instantly, overriding any other procedures.
     *
     * @param holder the holder who triggers the activation
     */
    void activate(@NotNull ItemHolder holder);

    /**
     * Gets whether the activation is primed.
     *
     * @return whether the activation is primed
     */
    boolean isPrimed();

    /**
     * Primes the activation for use.
     *
     * @param holder the holder who primes the activation
     */
    void prime(@NotNull ItemHolder holder);
}
