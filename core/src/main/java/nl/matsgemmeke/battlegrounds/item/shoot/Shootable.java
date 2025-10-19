package nl.matsgemmeke.battlegrounds.item.shoot;

import org.jetbrains.annotations.NotNull;

/**
 * An item that can shoot.
 */
public interface Shootable {

    /**
     * Attempts to cancel the current shooting cycle.
     */
    void cancelShooting();

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
     * @param performer the entity who performs the shot
     */
    void shoot(ShotPerformer performer);
}
