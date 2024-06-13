package nl.matsgemmeke.battlegrounds.item.equipment.activation;

/**
 * Object that tells an equipment item when to activate its mechanism.
 */
public interface EquipmentActivation {

    /**
     * Gets whether the mechanism is primed and about to be used.
     *
     * @return whether the mechanism is primed
     */
    boolean isPrimed();

    /**
     * Primes the mechanism for use.
     */
    void prime();
}
