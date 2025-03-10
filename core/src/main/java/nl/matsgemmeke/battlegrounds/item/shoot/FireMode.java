package nl.matsgemmeke.battlegrounds.item.shoot;

public interface FireMode {

    /**
     * Activates a shooting cycle.
     *
     * @return whether the cycle was activated
     */
    boolean activateCycle();

    /**
     * Attempts to cancel the current shooting cycle.
     *
     * @return whether the cycle was cancelled
     */
    boolean cancelCycle();

    /**
     * Gets the rate of fire of the firemode in rounds per minute.
     *
     * @return the firemode's rate of fire
     */
    int getRateOfFire();

    /**
     * Gets whether the fire mode is performing a cycle.
     *
     * @return whether the fire mode is cycling
     */
    boolean isCycling();
}
