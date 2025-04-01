package nl.matsgemmeke.battlegrounds.game.component.spawn;

import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPoint;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents a game component responsible for managing and assigning spawn points for entities.
 */
public interface SpawnPointProvider {

    /**
     * Checks whether the specified entity has an assigned spawn point.
     *
     * @param entityId the unique identifier of the entity
     * @return {@code true} if the entity has a custom spawn point assigned, {@code false} otherwise
     */
    boolean hasSpawnPoint(@NotNull UUID entityId);

    /**
     * Respawns the given entity at its assigned spawn point. If a custom spawn point is set, the entity will be
     * respawned there. Otherwise, the default spawn location will be used.
     *
     * @param entity the entity to be respawned
     * @return the location where the entity is respawned
     */
    @NotNull
    Location respawnEntity(@NotNull Entity entity);

    /**
     * Assigns a custom spawn point to the specified entity. If the {@code spawnPoint} parameter is {@code null}, the
     * custom spawn point for the entity is removed, and the default spawn logic will be applied.
     *
     * @param entityId the unique identifier of the entity
     * @param spawnPoint the custom spawn point to assign, or {@code null} to remove an existing spawn point
     */
    void setCustomSpawnPoint(@NotNull UUID entityId, @Nullable SpawnPoint spawnPoint);
}
