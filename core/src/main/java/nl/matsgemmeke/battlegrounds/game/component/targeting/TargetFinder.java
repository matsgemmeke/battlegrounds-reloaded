package nl.matsgemmeke.battlegrounds.game.component.targeting;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.entity.PotionEffectReceiver;
import nl.matsgemmeke.battlegrounds.entity.damage.DamageTarget;
import org.bukkit.Location;

import java.util.List;
import java.util.UUID;

/**
 * Represents a game component whose responsibility is to find potential targets.
 */
public interface TargetFinder {

    /**
     * Finds targets that are considered hostile to the given entity, within a specified range around a location.
     *
     * @param entityId the entity id
     * @param location the location
     * @param range the range
     * @return a list of game entities that are considered enemy targets within the specified range
     */
    @Deprecated
    List<GameEntity> findEnemyTargets(UUID entityId, Location location, double range);

    /**
     * Finds targets that can receive potion effects within a specified range around a location.
     *
     * @param location the location
     * @param range    the range
     * @return         a list of game entities that are able to receive potion effects, within the given range
     */
    List<PotionEffectReceiver> findPotionEffectReceivers(Location location, double range);

    /**
     * Looks for potential targets for the given entity around a specific location.
     *
     * @param entityId the entity id
     * @param location the location
     * @param range the range
     * @return all targets inside the range
     */
    @Deprecated
    List<GameEntity> findTargets(UUID entityId, Location location, double range);

    List<DamageTarget> findTargets(TargetQuery query);
}
