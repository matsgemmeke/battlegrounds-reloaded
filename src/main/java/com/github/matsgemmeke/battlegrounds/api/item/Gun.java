package com.github.matsgemmeke.battlegrounds.api.item;

public interface Gun extends Weapon {

    /**
     * Gets the amount of ammo in the magazine of the gun.
     *
     * @return the magazine ammo
     */
    int getMagazineAmmo();

    /**
     * Sets the amount of ammo in the magazine of the gun.
     *
     * @param magazineAmmo the magazine ammo
     */
    void setMagazineAmmo(int magazineAmmo);

    /**
     * Gets the amount of ammo in the magazines of the gun.
     *
     * @return the magazine size
     */
    int getMagazineSize();

    /**
     * Sets the amount of ammo in the magazines of the gun.
     *
     * @param magazineSize the magazine size
     */
    void setMagazineSize(int magazineSize);

//    /**
//     * Gets the maximum amount of ammo a player can carry along with the gun.
//     *
//     * @return the maximum amount of ammo
//     */
//    int getMaxAmmo();
//
//    /**
//     * Sets the maximum amount of ammo a player can carry along with the gun.
//     *
//     * @param maxAmmo the maximum amount of ammo
//     */
//    void setMaxAmmo(int maxAmmo);

    /**
     * Gets the amount of reserve ammo the gun has available.
     *
     * @return the amount of reserve ammo
     */
    int getReserveAmmo();

    /**
     * Sets the amount of reserve ammo the gun has available.
     *
     * @param reserveAmmo the amount of reserve ammo
     */
    void setReserveAmmo(int reserveAmmo);

    /**
     * Gets whether the gun is currently being reloaded.
     *
     * @return whether the gun is reloading
     */
    boolean isReloading();

    /**
     * Sets whether the gun is currently being reloaded.
     *
     * @param reloading whether the gun is reloading
     */
    void setReloading(boolean reloading);

    /**
     * Reloads the gun.
     *
     * @return whether the gun was reloaded
     */
    boolean reload();

    /**
     * Makes the gun shoot one of its projectiles.
     *
     * @return whether the gun has shot
     */
    boolean shoot();
}
