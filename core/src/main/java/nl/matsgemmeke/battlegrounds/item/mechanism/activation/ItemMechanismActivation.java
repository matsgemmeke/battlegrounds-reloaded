package nl.matsgemmeke.battlegrounds.item.mechanism.activation;

import nl.matsgemmeke.battlegrounds.entity.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.mechanism.ItemMechanism;
import org.jetbrains.annotations.NotNull;

/**
 * Object that tells an {@link ItemMechanism} when to activate.
 */
public interface ItemMechanismActivation {

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
