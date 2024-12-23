package nl.matsgemmeke.battlegrounds.game.spawn;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * A specific location used by games to spawn certain entities.
 */
public interface SpawnPoint {

    /**
     * Gets the location of the spawn point.
     *
     * @return the spawn point location
     */
    @NotNull
    Location getLocation();

    /**
     * Called when a {@link GameEntity} is spawned on this spawn point.
     *
     * @param gameEntity the entity that was spawned on the spawn point
     */
    void onSpawn(@NotNull GameEntity gameEntity);
}
