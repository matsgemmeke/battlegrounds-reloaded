package nl.matsgemmeke.battlegrounds.game.component.targeting.condition;

import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import org.bukkit.Location;

/**
 * A target condition implementation that selects targets whose hitboxes intersect the origin location.
 */
public class HitboxTargetCondition implements TargetCondition {

    @Override
    public boolean test(DamageTarget target, Location origin) {
        return target.getHitbox().intersects(origin);
    }
}
