package nl.matsgemmeke.battlegrounds.entity;

import org.bukkit.entity.EntityType;

public final class EntityKey {

    private static final String MINECRAFT_PREFIX = "minecraft:";
    private static final String BATTLEGROUNDS_PREFIX = "battlegrounds:";

    private final String value;

    private EntityKey(String value) {
        this.value = value;
    }

    public static EntityKey fromEntityType(EntityType entityType) {
        return new EntityKey(MINECRAFT_PREFIX + entityType.getKey().getKey());
    }

    public static EntityKey custom(String type) {
        return new EntityKey(BATTLEGROUNDS_PREFIX + type);
    }

    public String getValue() {
        return value;
    }
}
