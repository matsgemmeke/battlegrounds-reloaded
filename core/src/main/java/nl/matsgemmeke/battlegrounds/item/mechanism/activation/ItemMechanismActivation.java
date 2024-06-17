package nl.matsgemmeke.battlegrounds.item.mechanism.activation;

import nl.matsgemmeke.battlegrounds.item.mechanism.ItemMechanism;

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
     */
    void prime();
}
