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
     * Reloads the firearm.
     */
    void reload();

    /**
     * Makes the firearm shoot one of its projectiles.
     */
    void shoot();
}
