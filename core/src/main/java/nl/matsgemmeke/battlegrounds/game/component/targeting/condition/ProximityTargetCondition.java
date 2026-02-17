package nl.matsgemmeke.battlegrounds.game.component.targeting.condition;

import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import org.bukkit.Location;

/**
 * A target condition implementation that selects targets that are located in a given proximity of the origin location.
 */
public class ProximityTargetCondition implements TargetCondition {

    private final double maxDistanceSquared;

    public ProximityTargetCondition(double maxDistance) {
        this.maxDistanceSquared = maxDistance * maxDistance;
    }

    @Override
    public boolean test(DamageTarget target, Location origin) {
        return target.getLocation().distanceSquared(origin) <= maxDistanceSquared;
    }
}
