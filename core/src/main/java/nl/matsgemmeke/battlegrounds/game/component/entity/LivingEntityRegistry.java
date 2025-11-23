package nl.matsgemmeke.battlegrounds.game.component.entity;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import org.bukkit.entity.LivingEntity;

import java.util.Optional;
import java.util.UUID;

public interface LivingEntityRegistry {

    Optional<GameEntity> findByUniqueId(UUID uniqueId);

    /**
     * Attempts to register a new instance for the given entity. When an instance already exists, it returns the
     * existing one instead.
     *
     * @param entity the entity
     * @return       the newly created instance or the existing one if it already exists
     */
    GameEntity register(LivingEntity entity);
}
