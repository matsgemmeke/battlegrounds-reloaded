package nl.matsgemmeke.battlegrounds.item.equipment.mechanism;

public interface EquipmentMechanism {

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
