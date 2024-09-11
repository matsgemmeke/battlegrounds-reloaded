package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A façade interface whose responsible is to identify targets inside a game context.
 */
public interface CollisionDetector {

    /**
     * Looks for potential targets for a {@link GameEntity} around a specific {@link Location}.
     *
     * @param gameEntity the entity
     * @param location the location
     * @param range the range
     * @return all targets inside the range
     */
    @NotNull
    List<GameEntity> findTargets(@NotNull GameEntity gameEntity, @NotNull Location location, double range);

    /**
     * Verifies if the given location produces a collision with a block.
     *
     * @param location the location
     * @return whether a block collision is produced
     */
    boolean producesBlockCollisionAt(@NotNull Location location);
}
