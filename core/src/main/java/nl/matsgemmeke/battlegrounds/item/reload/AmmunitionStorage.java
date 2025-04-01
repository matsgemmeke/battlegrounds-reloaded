package nl.matsgemmeke.battlegrounds.item.reload;

/**
 * Describes how much ammunition an item, that is able to carry ammunition, stores.
 */
public class AmmunitionStorage {

    private int magazineAmmo;
    private int magazineSize;
    private int maxAmmo;
    private int reserveAmmo;

    public AmmunitionStorage(int magazineAmmo, int magazineSize, int reserveAmmo, int maxAmmo) {
        this.magazineAmmo = magazineAmmo;
        this.magazineSize = magazineSize;
        this.reserveAmmo = reserveAmmo;
        this.maxAmmo = maxAmmo;
    }

    public int getMagazineAmmo() {
        return magazineAmmo;
    }

    public void setMagazineAmmo(int magazineAmmo) {
        this.magazineAmmo = magazineAmmo;
    }

    public int getMagazineSize() {
        return magazineSize;
    }

    public void setMagazineSize(int magazineSize) {
        this.magazineSize = magazineSize;
    }

    public int getMaxAmmo() {
        return maxAmmo;
    }

    public void setMaxAmmo(int maxAmmo) {
        this.maxAmmo = maxAmmo;
    }

    public int getReserveAmmo() {
        return reserveAmmo;
    }

    public void setReserveAmmo(int reserveAmmo) {
        this.reserveAmmo = reserveAmmo;
    }
}
