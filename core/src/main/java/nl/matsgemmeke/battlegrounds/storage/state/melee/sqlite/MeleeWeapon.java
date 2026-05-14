package nl.matsgemmeke.battlegrounds.storage.state.melee.sqlite;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "melee_weapon")
public class MeleeWeapon {

    @DatabaseField(columnName = "id", generatedId = true)
    private int id;
    @DatabaseField(columnName = "player_uuid", canBeNull = false)
    private String playerUuid;
    @DatabaseField(columnName = "melee_weapon_name", canBeNull = false)
    private String meleeWeaponName;
    @DatabaseField(columnName = "loaded_amount", canBeNull = false, defaultValue = "0")
    private int loadedAmount;
    @DatabaseField(columnName = "reserve_amount", canBeNull = false, defaultValue = "0")
    private int reserveAmount;
    @DatabaseField(columnName = "item_slot", canBeNull = false, defaultValue = "0")
    private int itemSlot;

    public MeleeWeapon() {
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

    public String getMeleeWeaponName() {
        return meleeWeaponName;
    }

    public void setMeleeWeaponName(String meleeWeaponName) {
        this.meleeWeaponName = meleeWeaponName;
    }

    public int getLoadedAmount() {
        return loadedAmount;
    }

    public void setLoadedAmount(int loadedAmount) {
        this.loadedAmount = loadedAmount;
    }

    public int getReserveAmount() {
        return reserveAmount;
    }

    public void setReserveAmount(int reserveAmount) {
        this.reserveAmount = reserveAmount;
    }

    public int getItemSlot() {
        return itemSlot;
    }

    public void setItemSlot(int itemSlot) {
        this.itemSlot = itemSlot;
    }
}
