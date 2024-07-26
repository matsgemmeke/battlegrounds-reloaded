package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EntityStorage<T extends GameEntity> {

    @NotNull
    private Map<UUID, T> entities;

    public EntityStorage() {
        this.entities = new HashMap<>();
    }

    public void addEntity(@NotNull T entity) {
        UUID uuid = entity.getEntity().getUniqueId();
        entities.put(uuid, entity);
    }

    @Nullable
    public T getEntity(@NotNull Entity entity) {
        for (T gameEntity : entities.values()) {
            if (gameEntity.getEntity() == entity) {
                return gameEntity;
            }
        }
        return null;
    }

    @Nullable
    public T getEntity(@NotNull UUID uuid) {
        return entities.get(uuid);
    }

    @NotNull
    public Collection<T> getEntities() {
        return entities.values();
    }

    public void removeEntity(@NotNull UUID uuid) {
        entities.remove(uuid);
    }
}
