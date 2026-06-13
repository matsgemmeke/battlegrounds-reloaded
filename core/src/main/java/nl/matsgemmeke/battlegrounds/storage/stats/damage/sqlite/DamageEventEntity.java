package nl.matsgemmeke.battlegrounds.storage.stats.damage.sqlite;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

@DatabaseTable(tableName = "damage_event")
public class DamageEventEntity {

    @DatabaseField(columnName = "id", generatedId = true)
    private int id;
    @DatabaseField(columnName = "game_key", canBeNull = false)
    private String gameKey;
    @DatabaseField(columnName = "source_id", canBeNull = false)
    private UUID sourceId;
    @DatabaseField(columnName = "source_entity_key", canBeNull = false)
    private String sourceEntityKey;
    @DatabaseField(columnName = "target_id", canBeNull = false)
    private UUID targetId;
    @DatabaseField(columnName = "target_entity_key", canBeNull = false)
    private String targetEntityKey;
    @DatabaseField(columnName = "item", canBeNull = false)
    private String item;
    @DatabaseField(columnName = "damage_amount", canBeNull = false)
    private double damageAmount;
    @DatabaseField(columnName = "damage_type", canBeNull = false)
    private String damageType;
    @DatabaseField(columnName = "hitbox", canBeNull = false)
    private String hitbox;
    @DatabaseField(columnName = "distance", canBeNull = false)
    private double distance;
    @DatabaseField(columnName = "kill", canBeNull = false)
    private boolean kill;
    @DatabaseField(columnName = "friendly_fire", canBeNull = false)
    private boolean friendlyFire;
    @DatabaseField(columnName = "timestamp", canBeNull = false)
    private String timestamp;

    public DamageEventEntity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGameKey() {
        return gameKey;
    }

    public void setGameKey(String gameKey) {
        this.gameKey = gameKey;
    }

    public UUID getSourceId() {
        return sourceId;
    }

    public void setSourceId(UUID sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceEntityKey() {
        return sourceEntityKey;
    }

    public void setSourceEntityKey(String sourceEntityKey) {
        this.sourceEntityKey = sourceEntityKey;
    }

    public UUID getTargetId() {
        return targetId;
    }

    public void setTargetId(UUID targetId) {
        this.targetId = targetId;
    }

    public String getTargetEntityKey() {
        return targetEntityKey;
    }

    public void setTargetEntityKey(String targetEntityKey) {
        this.targetEntityKey = targetEntityKey;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public double getDamageAmount() {
        return damageAmount;
    }

    public void setDamageAmount(double damageAmount) {
        this.damageAmount = damageAmount;
    }

    public String getDamageType() {
        return damageType;
    }

    public void setDamageType(String damageType) {
        this.damageType = damageType;
    }

    public String getHitbox() {
        return hitbox;
    }

    public void setHitbox(String hitbox) {
        this.hitbox = hitbox;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public boolean isKill() {
        return kill;
    }

    public void setKill(boolean kill) {
        this.kill = kill;
    }

    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
