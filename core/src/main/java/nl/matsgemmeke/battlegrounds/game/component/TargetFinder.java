package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A façade interface whose responsibility is to find potential targets in a game context.
 */
public interface TargetFinder {

    /**
     * Finds deployment objects that are considered hostile to the given entity, within a specified range around a
     * location. The returned list will only contain instances of {@link DeploymentObject}.
     *
     * @param gameEntity the entity
     * @param location the location
     * @param range the range
     * @return a list of deployment object that are considered hostile to the given entity within the specified range
     */
    @NotNull
    List<DeploymentObject> findDeploymentObjects(@NotNull GameEntity gameEntity, @NotNull Location location, double range);

    /**
     * Finds targets that are considered hostile to the given entity, within a specified range around a location.
     *
     * @param gameEntity the entity
     * @param location the location
     * @param range the range
     * @return a list of game entities that are considered enemy targets within the specified range
     */
    @NotNull
    List<GameEntity> findEnemyTargets(@NotNull GameEntity gameEntity, @NotNull Location location, double range);

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
}
