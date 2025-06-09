package nl.matsgemmeke.battlegrounds.storage.state.equipment.sqlite;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "equipment")
public class Equipment {

    @DatabaseField(columnName = "id", generatedId = true)
    private int id;
    @DatabaseField(columnName = "player_uuid", canBeNull = false)
    private String playerUuid;
    @DatabaseField(columnName = "equipment_id", canBeNull = false)
    private String equipmentId;
    @DatabaseField(columnName = "item_slot", canBeNull = false, defaultValue = "0")
    private int itemSlot;

    public Equipment() {
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

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public int getItemSlot() {
        return itemSlot;
    }

    public void setItemSlot(int itemSlot) {
        this.itemSlot = itemSlot;
    }
}
