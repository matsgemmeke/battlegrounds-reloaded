package nl.matsgemmeke.battlegrounds.item.shoot;

/**
 * An item that can shoot.
 */
public interface Shootable {

    /**
     * Gets whether the shooting mechanism of the item is capable of shooting.
     *
     * @return whether the item can shoot
     */
    boolean canShoot();

    /**
     * Makes the item shoot.
     *
     * @return whether the item has shot
     */
    boolean shoot();
}
