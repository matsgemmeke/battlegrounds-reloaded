package nl.matsgemmeke.battlegrounds.game.component.entity;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface EntityRegistry<T extends GameEntity, S extends Entity> {

    Optional<T> findByEntity(S entity);

    Optional<T> findByUniqueId(UUID uuid);

    Collection<T> getAll();

    boolean isRegistered(S entity);

    boolean isRegistered(UUID uuid);

    T registerEntity(S entity);
}
