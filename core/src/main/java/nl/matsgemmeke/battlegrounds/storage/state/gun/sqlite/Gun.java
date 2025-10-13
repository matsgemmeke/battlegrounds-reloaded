package nl.matsgemmeke.battlegrounds.storage.state.gun.sqlite;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "gun")
public class Gun {

    @DatabaseField(columnName = "id", generatedId = true)
    private int id;
    @DatabaseField(columnName = "player_uuid", canBeNull = false)
    private String playerUuid;
    @DatabaseField(columnName = "gun_name", canBeNull = false)
    private String gunName;
    @DatabaseField(columnName = "magazine_ammo", canBeNull = false, defaultValue = "0")
    private int magazineAmmo;
    @DatabaseField(columnName = "reserve_ammo", canBeNull = false, defaultValue = "0")
    private int reserveAmmo;
    @DatabaseField(columnName = "item_slot", canBeNull = false, defaultValue = "0")
    private int itemSlot;

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

    public String getGunName() {
        return gunName;
    }

    public void setGunName(String gunName) {
        this.gunName = gunName;
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

    public int getItemSlot() {
        return itemSlot;
    }

    public void setItemSlot(int itemSlot) {
        this.itemSlot = itemSlot;
    }
}
