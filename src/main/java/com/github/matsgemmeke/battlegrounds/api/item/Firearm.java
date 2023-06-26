package com.github.matsgemmeke.battlegrounds.api.item;

public interface Firearm extends Weapon {

    /**
     * Gets the amount of ammo in the magazine of the firearm.
     *
     * @return the magazine ammo
     */
    int getMagazineAmmo();

    /**
     * Sets the amount of ammo in the magazine of the firearm.
     *
     * @param magazineAmmo the magazine ammo
     */
    void setMagazineAmmo(int magazineAmmo);

//    /**
//     * Gets the maximum amount of ammo a player can carry along with the firearm.
//     *
//     * @return the maximum amount of ammo
//     */
//    int getMaxAmmo();
//
//    /**
//     * Sets the maximum amount of ammo a player can carry along with the firearm.
//     *
//     * @param maxAmmo the maximum amount of ammo
//     */
//    void setMaxAmmo(int maxAmmo);

    /**
     * Gets the amount of reserve ammo the firearm has available.
     *
     * @return the amount of reserve ammo
     */
    int getReserveAmmo();

    /**
     * Sets the amount of reserve ammo the firearm has available.
     *
     * @param reserveAmmo the amount of reserve ammo
     */
    void setReserveAmmo(int reserveAmmo);

    /**
     * Reloads the firearm.
     */
    void reload();

    /**
     * Makes the firearm shoot one of its projectiles.
     */
    void shoot();
}
