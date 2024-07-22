package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface EntityRegistry<T extends Entity, S extends GameEntity> {

    boolean isRegistered(T entity);

    @NotNull
    S registerEntity(T entity);
}
