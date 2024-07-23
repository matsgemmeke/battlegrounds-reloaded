package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface EntityRegistry<T extends Entity, S extends GameEntity> {

    @Nullable
    S findByEntity(T entity);

    boolean isRegistered(T entity);

    @NotNull
    S registerEntity(T entity);
}
