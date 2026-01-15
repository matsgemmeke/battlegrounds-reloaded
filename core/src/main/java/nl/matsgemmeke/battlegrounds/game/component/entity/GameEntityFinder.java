package nl.matsgemmeke.battlegrounds.game.component.entity;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * Component that provides methods for finding game entities.
 */
public interface GameEntityFinder {

    Optional<GameEntity> findGameEntityByUniqueId(UUID uniqueId);
}
