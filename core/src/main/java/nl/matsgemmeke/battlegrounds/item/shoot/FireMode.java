package nl.matsgemmeke.battlegrounds.item.shoot;

import nl.matsgemmeke.battlegrounds.item.ItemMechanism;

public interface FireMode extends ItemMechanism {

    /**
     * Activates a shooting cycle.
     *
     * @return whether the cycle was activated
     */
    boolean activateCycle();

    /**
     * Gets whether the fire mode is performing a cycle.
     *
     * @return whether the fire mode is cycling
     */
    boolean isCycling();
}
