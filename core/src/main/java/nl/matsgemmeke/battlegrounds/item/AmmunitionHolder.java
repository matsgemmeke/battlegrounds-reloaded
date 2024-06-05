package nl.matsgemmeke.battlegrounds.item;

/**
 * An item that holds ammunition.
 */
public interface AmmunitionHolder {

    /**
     * Gets the amount of ammo in the magazine of the item.
     *
     * @return the magazine ammo
     */
    int getMagazineAmmo();

    /**
     * Sets the amount of ammo in the magazine of the item.
     *
     * @param magazineAmmo the magazine ammo
     */
    void setMagazineAmmo(int magazineAmmo);

    /**
     * Gets the amount of ammo in the magazines of the item.
     *
     * @return the magazine size
     */
    int getMagazineSize();

    /**
     * Sets the amount of ammo in the magazines of the item.
     *
     * @param magazineSize the magazine size
     */
    void setMagazineSize(int magazineSize);

    /**
     * Gets the maximum amount of ammo the item's holder can carry along with the item.
     *
     * @return the maximum amount of ammo
     */
    int getMaxAmmo();

    /**
     * Sets the maximum amount of ammo the item's holder can carry along with the item.
     *
     * @param maxAmmo the maximum amount of ammo
     */
    void setMaxAmmo(int maxAmmo);

    /**
     * Gets the amount of reserve ammo the item has available.
     *
     * @return the amount of reserve ammo
     */
    int getReserveAmmo();

    /**
     * Sets the amount of reserve ammo the item has available.
     *
     * @param reserveAmmo the amount of reserve ammo
     */
    void setReserveAmmo(int reserveAmmo);

    /**
     * Updates the item's ammunition display.
     */
    void updateAmmoDisplay();
}
