package nl.matsgemmeke.battlegrounds.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class EntityKeyRegistry {

    private final Map<UUID, EntityKey> entityKeys;

    public EntityKeyRegistry() {
        this.entityKeys = new HashMap<>();
    }

    public Optional<EntityKey> getEntityKey(UUID uniqueId) {
        return Optional.ofNullable(entityKeys.get(uniqueId));
    }

    public void register(UUID uniqueId, EntityKey entityKey) {
        entityKeys.put(uniqueId, entityKey);
    }

    public void remove(UUID uniqueId) {
        entityKeys.remove(uniqueId);
    }
}
