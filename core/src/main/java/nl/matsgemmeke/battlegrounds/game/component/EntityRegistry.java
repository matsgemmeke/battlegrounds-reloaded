package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface EntityRegistry<T extends GameEntity, S extends Entity> {

    @Nullable
    T findByEntity(S entity);

    boolean isRegistered(S entity);

    @NotNull
    T registerEntity(S entity);
}
