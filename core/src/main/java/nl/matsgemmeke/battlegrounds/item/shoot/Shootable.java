package nl.matsgemmeke.battlegrounds.item.shoot;

/**
 * An item that can shoot.
 */
public interface Shootable {

    /**
     * Attempts to cancel the current shooting cycle. Returns true {@code true} if the shooting cycle or cooldown was
     * cancelled, otherwise {@code false}.
     *
     * @return whether the shooting cycle was successfully cancelled
     */
    boolean cancelShootingCycle();

    /**
     * Gets whether the shooting mechanism of the item is capable of shooting.
     *
     * @return whether the item can shoot
     */
    boolean canShoot();

    /**
     * Gets whether the item is currently shooting.
     *
     * @return whether item is shooting
     */
    boolean isShooting();

    /**
     * Makes the item shoot.
     *
     * @return whether the item has shot
     */
    boolean shoot();

    /**
     * Makes the item shoot.
     *
     * @return whether the item has shot
     */
    boolean startShootCycle();
}
