package nl.matsgemmeke.battlegrounds.game.component.entity;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import org.bukkit.entity.Entity;

import java.util.Optional;
import java.util.UUID;

public interface EntityRegistry {

    Optional<GameEntity> findByUniqueId(UUID uniqueId);

    GameEntity register(Entity entity);
}
