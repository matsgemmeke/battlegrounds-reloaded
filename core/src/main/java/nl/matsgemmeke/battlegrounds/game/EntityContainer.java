package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import org.bukkit.entity.Entity;

import java.util.*;

public class EntityContainer<T extends GameEntity> {

    private final Map<UUID, T> entities;

    public EntityContainer() {
        this.entities = new HashMap<>();
    }

    public void addEntity(T entity) {
        UUID uniqueId = entity.getUniqueId();
        entities.put(uniqueId, entity);
    }

    public Optional<T> getEntity(Entity entity) {
        for (T gameEntity : entities.values()) {
            if (gameEntity.getEntity() == entity) {
                return Optional.of(gameEntity);
            }
        }

        return Optional.empty();
    }

    public Optional<T> getEntity(UUID uniqueId) {
        return Optional.ofNullable(entities.get(uniqueId));
    }

    public Collection<T> getEntities() {
        return entities.values();
    }

    public void removeEntity(UUID uniqueId) {
        entities.remove(uniqueId);
    }
}
