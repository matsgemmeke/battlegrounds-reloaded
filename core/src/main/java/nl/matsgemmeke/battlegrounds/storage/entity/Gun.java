package nl.matsgemmeke.battlegrounds.storage.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "gun")
public class Gun {

    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
    private int id;
    @DatabaseField(canBeNull = false)
    private String playerUuid;
    @DatabaseField(canBeNull = false)
    private String gunId;
    @DatabaseField(canBeNull = false, defaultValue = "0")
    private int magazineAmmo;
    @DatabaseField(canBeNull = false, defaultValue = "0")
    private int reserveAmmo;

    public Gun() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlayerUuid() {
        return playerUuid;
    }

    public void setPlayerUuid(String playerUuid) {
        this.playerUuid = playerUuid;
    }

    public String getGunId() {
        return gunId;
    }

    public void setGunId(String gunId) {
        this.gunId = gunId;
    }

    public int getMagazineAmmo() {
        return magazineAmmo;
    }

    public void setMagazineAmmo(int magazineAmmo) {
        this.magazineAmmo = magazineAmmo;
    }

    public int getReserveAmmo() {
        return reserveAmmo;
    }

    public void setReserveAmmo(int reserveAmmo) {
        this.reserveAmmo = reserveAmmo;
    }
}
