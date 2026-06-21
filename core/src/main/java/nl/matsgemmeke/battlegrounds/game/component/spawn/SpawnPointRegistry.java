package nl.matsgemmeke.battlegrounds.game.component.spawn;

import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPoint;

import java.util.Optional;
import java.util.UUID;

/**
 * Represents a game component responsible for managing and assigning spawn points for entities.
 */
public interface SpawnPointRegistry {

    /**
     * Gets the custom spawn point set by an entity. The returned optional empty when the entity has no custom spawn
     * point set up.
     *
     * @param entityId the entity id
     * @return an optional containing the spawn point object or an empty optional if none exists
     */
    Optional<SpawnPoint> getCustomSpawnPoint(UUID entityId);

    /**
     * Assigns a custom spawn point to the specified entity. If the {@code spawnPoint} parameter is {@code null}, the
     * custom spawn point for the entity is removed, and the default spawn logic will be applied.
     *
     * @param entityId the unique identifier of the entity
     * @param spawnPoint the custom spawn point to assign, or {@code null} to remove an existing spawn point
     */
    void setCustomSpawnPoint(UUID entityId, SpawnPoint spawnPoint);
}
