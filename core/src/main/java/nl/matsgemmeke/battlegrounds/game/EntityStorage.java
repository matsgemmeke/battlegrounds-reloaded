package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class EntityStorage<T extends GameEntity> {

    @NotNull
    private Set<T> entities;

    public EntityStorage() {
        this.entities = new HashSet<>();
    }

    public void addEntity(@NotNull T entity) {
        entities.add(entity);
    }

    @Nullable
    public T getEntity(@NotNull Entity entity) {
        for (T gameEntity : entities) {
            if (gameEntity.getEntity() == entity) {
                return gameEntity;
            }
        }
        return null;
    }

    @NotNull
    public Set<T> getEntities() {
        return entities;
    }

    public void removeEntity(@NotNull T entity) {
        entities.remove(entity);
    }
}
