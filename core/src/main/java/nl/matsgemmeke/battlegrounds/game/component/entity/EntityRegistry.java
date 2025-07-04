package nl.matsgemmeke.battlegrounds.game.component.entity;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public interface EntityRegistry<T extends GameEntity, S extends Entity> {

    @Nullable
    T findByEntity(@NotNull S entity);

    @Nullable
    T findByUUID(@NotNull UUID uuid);

    @NotNull
    Collection<T> getAll();

    boolean isRegistered(@NotNull S entity);

    boolean isRegistered(@NotNull UUID uuid);

    @NotNull
    T registerEntity(@NotNull S entity);
}
